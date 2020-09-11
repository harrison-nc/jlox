package com.example.lox.jlox.parser;

import com.example.lox.jlox.Expr;
import com.example.lox.jlox.Expr.Binary;
import com.example.lox.jlox.Expr.Grouping;
import com.example.lox.jlox.Expr.Literal;
import com.example.lox.jlox.Expr.Unary;
import com.example.lox.jlox.Lox;
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

    public Expr parse() {
        try {
            return expression();
        } catch (ParserError e) {
            return Literal.of(EOF);
        }
    }

    public List<Expr> parseAll() {
        List<Expr> exprs = new ArrayList<>();

        while (!isAtEnd()) {
            Expr expr = parse();

            if (expr instanceof Literal) {
                var literal = (Literal) expr;
                if (literal.value() == EOF) {
                    advance();
                    continue;
                }
            }

            exprs.add(expr);
        }

        return exprs;
    }

    private Expr expression() {
        return equality();
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
        } else if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume();
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

    private Token consume() {
        if (check(TokenType.RIGHT_PAREN)) {
            return advance();
        } else {
            throw error(peek(), "Expect ')' after expression.");
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
        }

        advance();
    }

    public static class ParserError extends RuntimeException {
    }
}
