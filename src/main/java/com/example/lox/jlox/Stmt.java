package com.example.lox.jlox;

public abstract class Stmt {
    private Stmt() {
    }

    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {

        R visitExpressionStmt(Expression stmt);

        R visitPrintStmt(Print stmt);
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
}
