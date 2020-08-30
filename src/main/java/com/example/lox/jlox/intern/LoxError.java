package com.example.lox.jlox.intern;

import java.util.ArrayList;
import java.util.List;

public class LoxError {
    private static final List<Error> errors = new ArrayList<>();

    public static boolean hadError() {
        return !errors.isEmpty();
    }

    public static void error(int line, String message) {
        errors.add(Error.of(line, message));
    }

    public static void report() {
        for (Error error : errors) {
            System.out.println(error);
        }
        errors.clear();
    }

    private LoxError() {
    }
}

class Error {
    private final int line;
    private final String message;

    private Error(int line, String message) {
        this.line = line;
        this.message = message;
    }

    static Error of(int line, String message) {
        return new Error(line, message);
    }

    @Override
    public String toString() {
        return "[line " + line + "] Error : " + message;
    }
}
