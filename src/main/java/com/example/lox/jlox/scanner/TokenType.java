package com.example.lox.jlox.scanner;

import java.util.Map;

import static java.util.Map.entry;

public enum TokenType {
    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    // One or two character tokens.
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Literals.
    IDENTIFIER, STRING, NUMBER,

    // Keywords.
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
    RETURN, SUPER, THIS, TRUE, VAR, WHILE,

    EOF;

    private static final Map<String, TokenType> keywords = Map.ofEntries(
            entry("and", AND),
            entry("class", CLASS),
            entry("else", ELSE),
            entry("false", FALSE),
            entry("fun", FUN),
            entry("for", FOR),
            entry("if", IF),
            entry("nil", NIL),
            entry("or", OR),
            entry("return", RETURN),
            entry("super", SUPER),
            entry("this", THIS),
            entry("true", TRUE),
            entry("var", VAR),
            entry("while", WHILE));

    /**
     * Returns the keyword that matches the specified name,
     * otherwise returns null
     *
     * @param name token name
     * @return a token type or null
     */
    public static TokenType get(String name) {
        return keywords.get(name);
    }
}
