package com.example.lox.jlox;

import java.util.concurrent.atomic.AtomicBoolean;

public class LoxError {
    private static final AtomicBoolean hadError = new AtomicBoolean(false);

    static boolean hadError() {
        return hadError.get();
    }

    static void hadError(boolean value) {
        hadError.set(value);
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error " + where + ": " + message);
        hadError.set(true);
    }
}
