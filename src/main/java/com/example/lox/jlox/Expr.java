package com.example.lox.jlox;

import com.example.lox.jlox.scanner.Token;

public abstract class Expr {

    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {

        R visitBinaryExpr(Binary expr);

        R visitGroupingExpr(Grouping expr);

        R visitLiteralExpr(Literal expr);

        R visitUnaryExpr(Unary expr);
    }

    public static class Binary extends Expr {

        private final Expr left;
        private final Token operator;
        private final Expr right;

        private Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        public static Binary of(Expr left, Token operator, Expr right) {
            return new Binary(left, operator, right);
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        public Expr left() {
            return left;
        }

        public Token operator() {
            return operator;
        }

        public Expr right() {
            return right;
        }
    }

    public static class Grouping extends Expr {

        private final Expr expression;

        private Grouping(Expr expression) {
            this.expression = expression;
        }

        public static Grouping of(Expr expression) {
            return new Grouping(expression);
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        public Expr expression() {
            return expression;
        }
    }

    public static class Literal extends Expr {

        private final Object value;

        private Literal(Object value) {
            this.value = value;
        }

        public static Literal of(Object value) {
            return new Literal(value);
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        public Object value() {
            return value;
        }
    }

    public static class Unary extends Expr {

        private final Token operator;
        private final Expr right;

        private Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        public static Unary of(Token operator, Expr right) {
            return new Unary(operator, right);
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        public Token operator() {
            return operator;
        }

        public Expr right() {
            return right;
        }
    }
}
