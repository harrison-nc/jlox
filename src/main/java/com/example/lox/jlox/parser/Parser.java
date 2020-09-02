package com.example.lox.jlox.parser;

import com.example.lox.jlox.Expr;
import com.example.lox.jlox.intern.LoxError;
import com.example.lox.jlox.scanner.Token;
import com.example.lox.jlox.scanner.TokenType;

import java.util.ArrayList;
import java.util.List;

import static com.example.lox.jlox.Expr.*;
import static com.example.lox.jlox.scanner.TokenType.*;

public final class Parser {
    private final List<Token> tokens;
    private int current;

    public static Parser parseTokens(List<Token> tokens) {
        return new Parser(tokens);
    }

    private Parser(List<Token> tokens) {
        this.tokens = tokens;
        current = 0;
    }

    public Expr parse() {
        try {
            return expression();
        } catch (ParserError e) {
            return literalExpr(EOF);
        }
    }

    public List<Expr> parseAll() {
        List<Expr> exprList = new ArrayList<>();

        while (!isAtEnd()) {
            Expr expr = parse();
            exprList.add(expr);
        }

        return exprList;
    }

    private Expr expression() {
        return equality();
    }

    private Expr equality() {
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = binaryExpr(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = addition();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = addition();
            expr = binaryExpr(expr, operator, right);
        }

        return expr;
    }

    private Expr addition() {
        Expr expr = multiplication();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = multiplication();
            expr = binaryExpr(expr, operator, right);
        }

        return expr;
    }

    private Expr multiplication() {
        Expr expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = binaryExpr(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = primary();
            return unaryExpr(operator, right);
        }
        return primary();
    }

    private Expr primary() {
        if (match(FALSE)) {
            return literalExpr(false);
        } else if (match(TRUE)) {
            return literalExpr(true);
        } else if (match(NIL)) {
            return literalExpr(null);
        } else if (match(NUMBER, STRING)) {
            return literalExpr(previous().literal());
        } else if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return groupingExpr(expr);
        } else {
            throw ParserError.of(
                    "Expect an expression but found: "
                            + previous() + " at " + previous().line());
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
        LoxError.error(token, message);
        return ParserError.of(token + " : " + message);
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type() == SEMICOLON) {
                return;
            }

            switch (peek().type()) {
                case CLASS,
                        FUN,
                        VAR,
                        FOR,
                        IF,
                        WHILE,
                        PRINT,
                        RETURN -> {
                    return;
                }
            }

            advance();
        }
    }

    public static class ParserError extends RuntimeException {

        public static ParserError of(String message) {
            return new ParserError(message);
        }

        private ParserError(String message) {
            super(message);
        }
    }
}
