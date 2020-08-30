package com.example.lox.jlox;

import java.util.ArrayList;
import java.util.List;

import static com.example.lox.jlox.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens;
    private int start;
    private int current;
    private int line;

    public static Scanner apply(String source) {
        return new Scanner(source);
    }

    private Scanner(String source) {
        this.source = source;
        this.tokens = new ArrayList<>();
        this.start = 0;
        this.current = 0;
        this.line = 1;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(Token.of(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(' -> addToken(LEFT_PAREN);
            case ')' -> addToken(RIGHT_PAREN);
            case '{' -> addToken(LEFT_BRACE);
            case '}' -> addToken(RIGHT_BRACE);
            case ',' -> addToken(COMMA);
            case '.' -> addToken(DOT);
            case '-' -> addToken(MINUS);
            case '+' -> addToken(PLUS);
            case ';' -> addToken(SEMICOLON);
            case '*' -> addToken(STAR);
            case '!' -> addToken(match('=') ? BANG_EQUAL : BANG);
            case '=' -> addToken(match('=') ? EQUAL_EQUAL : EQUAL);
            case '<' -> addToken(match('=') ? LESS_EQUAL : LESS);
            case '>' -> addToken(match('=') ? GREATER_EQUAL : GREATER);
            case '/' -> {
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else {
                    addToken(SLASH);
                }
            }
            default -> LoxError.error(line, "Unexpected character: " + c);
        }
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        } else {
            return source.charAt(current);
        }
    }

    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    private boolean match(char expected) {
        if (isAtEnd()) {
            return false;
        } else if (source.charAt(current) != expected) {
            return false;
        } else {
            current++;
            return true;
        }
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void addToken(TokenType tokenType) { // Could return a token
        addToken(tokenType, null);
    }

    private void addToken(TokenType tokenType, Object literal) { // Could return a token
        String text = source.substring(start, current);
        tokens.add(Token.of(tokenType, text, literal, line));
    }
}
