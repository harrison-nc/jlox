package com.example.lox.jlox;

public class Scanner {
    private final String source;

    public static Scanner apply(String source) {
        return new Scanner(source);
    }

    private Scanner(String source) {
        this.source = source;
    }
}
