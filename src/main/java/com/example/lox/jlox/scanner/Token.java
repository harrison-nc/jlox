package com.example.lox.jlox.scanner;

public class Token {
    private final TokenType type;
    private final String lexeme;
    private final Object literal;
    private final int line;

    private Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public static Token of(TokenType type, String lexeme, Object literal, int line) {
        return new Token(type, lexeme, literal, line);
    }

    public TokenType type() {
        return type;
    }

    public String lexeme() {
        return lexeme;
    }

    public Object literal() {
        return literal;
    }

    public int line() {
        return line;
    }

    @Override
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
