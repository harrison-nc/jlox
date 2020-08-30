package com.example.lox.jlox;

import com.example.lox.jlox.scanner.Token;

import java.util.List;

public abstract class Expr {

    public interface Visitor<R> {

        R visitBinaryExpr(Binary expr);

        R visitGroupingExpr(Grouping expr);

        R visitLiteralExpr(Literal expr);

        R visitUnaryExpr(Unary expr);
    }

    public static Binary binaryExpr(Expr left, Token operator, Expr right) {
        return new Binary(left, operator, right);
    }

    public static class Binary extends Expr {

        private Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        private final Expr left;

        public Expr left() {
            return left;
        }

        private final Token operator;

        public Token operator() {
            return operator;
        }

        private final Expr right;

        public Expr right() {
            return right;
        }
    }

    public static Grouping groupingExpr(Expr expression) {
        return new Grouping(expression);
    }

    public static class Grouping extends Expr {

        private Grouping(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        private final Expr expression;

        public Expr expression() {
            return expression;
        }
    }

    public static Literal literalExpr(Object value) {
        return new Literal(value);
    }

    public static class Literal extends Expr {

        private Literal(Object value) {
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        private final Object value;

        public Object value() {
            return value;
        }
    }

    public static Unary unaryExpr(Token operator, Expr right) {
        return new Unary(operator, right);
    }

    public static class Unary extends Expr {

        private Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        private final Token operator;

        public Token operator() {
            return operator;
        }

        private final Expr right;

        public Expr right() {
            return right;
        }
    }

    abstract <R> R accept(Visitor<R> visitor);
}
