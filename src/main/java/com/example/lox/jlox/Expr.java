package com.example.lox.jlox;

import java.util.List;

sealed interface Expr
        permits
        Expr.Assign,
        Expr.Binary,
        Expr.Call,
        Expr.Get,
        Expr.Grouping,
        Expr.Literal,
        Expr.Logical,
        Expr.Set,
        Expr.Super,
        Expr.This,
        Expr.Unary,
        Expr.Variable {

    static Assign ofAssign(Token name, Expr value) {
        return new Assign(name, value);
    }

    static Binary ofBinary(Expr left, Token operator, Expr right) {
        return new Binary(left, operator, right);
    }

    static Call ofCall(Expr callee, Token paren, List<Expr> arguments) {
        return new Call(callee, paren, arguments);
    }

    static Get ofGet(Expr object, Token name) {
        return new Get(object, name);
    }

    static Grouping ofGrouping(Expr expression) {
        return new Grouping(expression);
    }

    static Literal ofLiteral(Object value) {
        return new Literal(value);
    }

    static Logical ofLogical(Expr left, Token operator, Expr right) {
        return new Logical(left, operator, right);
    }

    static Set ofSet(Expr object, Token name, Expr value) {
        return new Set(object, name, value);
    }

    static Super ofSuper(Token keyword, Token method) {
        return new Super(keyword, method);
    }

    static This ofThis(Token keyword) {
        return new This(keyword);
    }

    static Unary ofUnary(Token operator, Expr right) {
        return new Unary(operator, right);
    }

    static Variable ofVariable(Token name) {
        return new Variable(name);
    }

    <R> R accept(Visitor<R> visitor);

    sealed interface Visitor<R> permits Interpreter, Resolver {
        R visitAssignExpr(Assign expr);

        R visitBinaryExpr(Binary expr);

        R visitCallExpr(Call expr);

        R visitGetExpr(Get expr);

        R visitGroupingExpr(Grouping expr);

        R visitLiteralExpr(Literal expr);

        R visitLogicalExpr(Logical expr);

        R visitSetExpr(Set expr);

        R visitSuperExpr(Super expr);

        R visitThisExpr(This expr);

        R visitUnaryExpr(Unary expr);

        R visitVariableExpr(Variable expr);
    }

    record Assign(Token name, Expr value) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }
    }

    record Binary(Expr left, Token operator, Expr right) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }

    record Call(Expr callee, Token paren, List<Expr> arguments) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }
    }

    record Get(Expr object, Token name) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGetExpr(this);
        }
    }

    record Grouping(Expr expression) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }
    }

    record Literal(Object value) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }

    record Logical(Expr left, Token operator, Expr right) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }
    }

    record Set(Expr object, Token name, Expr value) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitSetExpr(this);
        }
    }

    record Super(Token keyword, Token method) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitSuperExpr(this);
        }
    }

    record This(Token keyword) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitThisExpr(this);
        }
    }

    record Unary(Token operator, Expr right) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }

    record Variable(Token name) implements Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }
    }
}
