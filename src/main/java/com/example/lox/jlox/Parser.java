package com.example.lox.jlox;

import java.util.List;

class Parser {
    private final List<Token> tokens;
    private final int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }
}
