package com.example.lox.jlox.tool;

import com.example.lox.jlox.Expr;
import com.example.lox.jlox.scanner.Token;

public final class Util {
    private Util() {
    }

    public static void print(Object message) {
        System.out.print(message);
    }

    public static void print(Expr expr) {
        print(expr, "");
    }

    public static void print(Expr expr, String args) {
        print(expr + args);
    }

    public static void print(Token token) {
        printf("%3d: %-15s %s", token.line(), token.type(), token.literal());
    }

    public static void println(Object message) {
        System.out.println(message);
    }

    public static void println() {
        System.out.println();
    }

    public static void printf(String format, Object... args) {
        System.out.printf(format, args);
    }

    public static void err(Object message) {
        System.err.println(message);
    }

    public static String stringify(Object object) {
        if (object == null) {
            return "nil";
        } else if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        } else {
            return object.toString();
        }
    }
}
