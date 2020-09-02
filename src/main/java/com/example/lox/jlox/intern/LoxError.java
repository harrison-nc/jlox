package com.example.lox.jlox.intern;

import com.example.lox.jlox.scanner.Token;

import java.util.ArrayList;
import java.util.List;

import static com.example.lox.jlox.scanner.TokenType.EOF;

public class LoxError {
    private static final List<Error> errors = new ArrayList<>();

    public static boolean hadError() {
        return !errors.isEmpty();
    }

    public static void error(int line, String message) {
        errors.add(Error.of(line, "", message));
    }

    public static void error(int line, String where, String message) {
        errors.add(Error.of(line, where, message));
    }

    public static void error(Token token, String message) {
        if (token.type() == EOF) {
            error(token.line(), "at end", message);
        } else {
            error(token.line(), "at '" + token.lexeme() + "'", message);
        }
    }

    public static void report() {
        for (Error error : errors) {
            System.out.println(error);
        }
    }

    public void clear() {
        errors.clear();
    }

    private LoxError() {
    }

    static class Error {
        private final int line;
        private final String where;
        private final String message;

        private Error(int line, String where, String message) {
            this.line = line;
            this.message = message;
            this.where = where;
        }

        static Error of(int line, String where, String message) {
            return new Error(line, where, message);
        }

        @Override
        public String toString() {
            return "[line " + line + "] Error" + where + " :" + message;
        }
    }
}
