package com.example.lox.jlox.interpreter;

import com.example.lox.jlox.scanner.Token;

import java.util.HashMap;
import java.util.Map;

class Environment {
    private final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();

    Environment() {
        enclosing = null;
    }

    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    void define(String name, Object value) {
        values.put(name, value);
    }

    void assign(Token name, Object value) {
        String lexeme = name.lexeme();

        if (values.containsKey(lexeme)) {
            values.put(lexeme, value);
        } else if (null != enclosing) {
            enclosing.assign(name, value);
        } else {
            throw new RuntimeError(name, "Undefined variable '" + lexeme + "'.");
        }
    }

    Object get(Token name) {
        String lexeme = name.lexeme();

        if (values.containsKey(lexeme)) {
            return values.get(lexeme);
        } else if (null != enclosing) {
            return enclosing.get(name);
        } else {
            throw new RuntimeError(name, "Undefined variable '" + lexeme + "'.");
        }
    }
}
