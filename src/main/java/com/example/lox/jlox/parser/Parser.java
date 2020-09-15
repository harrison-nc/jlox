package com.example.lox.jlox.parser;

import com.example.lox.jlox.Expr;
import com.example.lox.jlox.Expr.*;
import com.example.lox.jlox.Lox;
import com.example.lox.jlox.Stmt;
import com.example.lox.jlox.Stmt.*;
import com.example.lox.jlox.scanner.Token;
import com.example.lox.jlox.scanner.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.example.lox.jlox.scanner.TokenType.*;

public final class Parser {
    private final List<Token> tokens;
    private int current;

    private Parser(List<Token> tokens) {
        this.tokens = tokens;
        current = 0;
    }

    public static Parser of(List<Token> tokens) {
        return new Parser(tokens);
    }

    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();

        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    private Stmt declaration() {
        try {
            if (match(VAR)) {
                return varDeclaration();
            }

            return statement();
        } catch (ParserError error) {
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expect variable name.");

        Expr initializer = null;
        if (match(EQUAL)) {
            initializer = expression();
        }

        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return Var.of(name, initializer);
    }

    private Stmt statement() {
        if (match(IF)) {
            return ifStatement();
        } else if (match(FOR)) {
            return forStatement();
        } else if (match(WHILE)) {
            return whileStatement();
        } else if (match(PRINT)) {
            return printStatement();
        } else if (match(LEFT_BRACE)) {
            return Block.of(block());
        } else {
            return expressionStatement();
        }
    }

    private Stmt forStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'for'.");

        Stmt initializer;
        if (match(SEMICOLON)) {
            initializer = null;
        } else if (match(VAR)) {
            initializer = varDeclaration();
        } else {
            initializer = expressionStatement();
        }

        Expr condition = null;
        if (!check(SEMICOLON)) {
            condition = expression();
        }
        consume(SEMICOLON, "Expect ';' after loop condition.");

        Expr increment = null;
        if (!check(RIGHT_PAREN)) {
            increment = expression();
        }
        consume(RIGHT_PAREN, "Expect ')' after for clauses.");

        Stmt body = statement();

        if (increment != null) {
            body = Block.of(List.of(
                    body,
                    Expression.of(increment)));
        }

        if (condition == null) {
            condition = Expr.Literal.of(true);
        }
        body = While.of(condition, body);

        if (initializer != null) {
            body = Block.of(List.of(initializer, body));
        }

        return body;
    }

    private Stmt whileStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after while condition.");
        Stmt body = statement();
        return While.of(condition, body);
    }

    private Stmt ifStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition.");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE)) {
            elseBranch = statement();
        }

        return If.of(condition, thenBranch, elseBranch);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();

        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return Print.of(value);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return Expression.of(expr);
    }

    private Expr expression() {
        return assignment();
    }

    private Expr assignment() {
        Expr expr = or();

        if (match(EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Variable) {
                Token name = ((Variable) expr).name();
                return Assign.of(name, value);
            }

            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private Expr or() {
        return logicalExpr(this::and, OR);
    }

    private Expr and() {
        return logicalExpr(this::equality, AND);
    }

    private Expr logicalExpr(Supplier<Expr> func, TokenType... types) {
        Expr expr = func.get();

        while (match(types)) {
            Token operator = previous();
            Expr right = func.get();
            expr = Logical.of(expr, operator, right);
        }

        return expr;
    }

    private Expr equality() {
        return leftAssocBinaryExpr(this::comparison, BANG_EQUAL, EQUAL_EQUAL);
    }

    private Expr comparison() {
        return leftAssocBinaryExpr(this::addition, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL);
    }

    private Expr addition() {
        return leftAssocBinaryExpr(this::multiplication, MINUS, PLUS);
    }

    private Expr multiplication() {
        return leftAssocBinaryExpr(this::unary, SLASH, STAR);
    }

    private Expr leftAssocBinaryExpr(Supplier<Expr> func, TokenType... types) {
        Expr expr = func.get();

        while (match(types)) {
            Token operator = previous();
            Expr right = func.get();
            expr = Binary.of(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = primary();
            return Unary.of(operator, right);
        }
        return primary();
    }

    private Expr primary() {
        if (match(FALSE)) {
            return Literal.of(false);
        } else if (match(TRUE)) {
            return Literal.of(true);
        } else if (match(NIL)) {
            return Literal.of(null);
        } else if (match(NUMBER, STRING)) {
            return Literal.of(previous().literal());
        } else if (match(IDENTIFIER)) {
            return Variable.of(previous());
        } else if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return Grouping.of(expr);
        } else {
            throw error(peek(), "Expect an expression");
        }
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        } else {
            throw error(peek(), message);
        }
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        } else {
            return peek().type() == type;
        }
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }

        return previous();
    }

    private boolean isAtEnd() {
        return peek().type() == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParserError error(Token token, String message) {
        Lox.error(token, message);
        return new ParserError();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type() == SEMICOLON) {
                return;
            }

            switch (peek().type()) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }

            advance();
        }
    }

    public static class ParserError extends RuntimeException {
    }
}
