package com.example.lox.jlox;

import com.example.lox.jlox.scanner.Token;

public abstract class Expr {
    private Expr() {
    }

    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {

        R visitAssignExpr(Assign expr);

        R visitBinaryExpr(Binary expr);

        R visitGroupingExpr(Grouping expr);

        R visitLiteralExpr(Literal expr);

        R visitLogicalExpr(Logical expr);

        R visitUnaryExpr(Unary expr);

        R visitVariableExpr(Variable expr);
    }

    public static class Assign extends Expr {

        private final Token name;
        private final Expr value;

        private Assign(Token name, Expr value) {
            this.name = name;
            this.value = value;
        }

        public static Assign of(Token name, Expr value) {
            return new Assign(name, value);
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }

        public Token name() {
            return name;
        }

        public Expr value() {
            return value;
        }
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

    public static class Logical extends Expr {

        private final Expr left;
        private final Token operator;
        private final Expr right;

        private Logical(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        public static Logical of(Expr left, Token operator, Expr right) {
            return new Logical(left, operator, right);
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
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

    public static class Variable extends Expr {

        private final Token name;

        private Variable(Token name) {
            this.name = name;
        }

        public static Variable of(Token name) {
            return new Variable(name);
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }

        public Token name() {
            return name;
        }
    }
}
