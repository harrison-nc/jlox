package com.example.lox.jlox.tool;

public final class Util {
    private Util() {
    }

    public static void print(Object message) {
        System.out.print(message);
    }

    public static void println(Object message) {
        System.out.println(message);
    }

    public static void printf(String format, Object... args) {
        System.out.printf(format, args);
    }

    public static void err(Object message) {
        System.err.println(message);
    }
}
