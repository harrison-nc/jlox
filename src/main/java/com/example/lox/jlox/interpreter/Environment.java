package com.example.lox.jlox.interpreter;

import com.example.lox.jlox.scanner.Token;

import java.util.HashMap;
import java.util.Map;

class Environment {
    private final Map<String, Object> values = new HashMap<>();

    void define(String name, Object value) {
        values.put(name, value);
    }

    void assign(Token name, Object value) {
        String lexeme = name.lexeme();

        if (values.containsKey(lexeme)) {
            values.put(lexeme, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable '" + lexeme + "'.");
    }

    Object get(Token name) {
        String lexeme = name.lexeme();

        if (values.containsKey(lexeme)) {
            return values.get(lexeme);
        }

        throw new RuntimeError(name, "Undefined variable '" + lexeme + "'.");
    }
}
