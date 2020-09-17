package com.example.lox.jlox.interpreter;

import com.example.lox.jlox.scanner.Token;

public class Break extends RuntimeException {
    private final Token token;

    public Break(Token token) {
        super(null, null, false, false);
        this.token = token;
    }

    Token token() {
        return token;
    }
}
