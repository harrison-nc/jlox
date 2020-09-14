package com.example.lox.jlox;

import com.example.lox.jlox.scanner.Token;

public abstract class Stmt {
    private Stmt() {
    }

    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {

        R visitExpressionStmt(Expression stmt);

        R visitPrintStmt(Print stmt);

        R visitVarStmt(Var stmt);
    }

    public static class Expression extends Stmt {

        private final Expr expression;

        private Expression(Expr expression) {
            this.expression = expression;
        }

        public static Expression of(Expr expression) {
            return new Expression(expression);
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }

        public Expr expression() {
            return expression;
        }
    }

    public static class Print extends Stmt {

        private final Expr expression;

        private Print(Expr expression) {
            this.expression = expression;
        }

        public static Print of(Expr expression) {
            return new Print(expression);
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }

        public Expr expression() {
            return expression;
        }
    }

    public static class Var extends Stmt {

        private final Token name;
        private final Expr initializer;

        private Var(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        public static Var of(Token name, Expr initializer) {
            return new Var(name, initializer);
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }

        public Token name() {
            return name;
        }

        public Expr initializer() {
            return initializer;
        }
    }
}
