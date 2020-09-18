package com.example.lox.jlox;

import com.example.lox.jlox.scanner.Token;

import java.util.List;

public abstract class Stmt {

    private Stmt() {
    }

    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {
        R visitBlockStmt(Block stmt);

        R visitExpressionStmt(Expression stmt);

        R visitFunctionStmt(Function stmt);

        R visitIfStmt(If stmt);

        R visitReturnStmt(Return stmt);

        R visitVarStmt(Var stmt);

        R visitWhileStmt(While stmt);

        R visitBreakStmt(Break stmt);
    }


    public static final class Block extends Stmt {
        private final List<Stmt> statements;

        private Block(List<Stmt> statements) {
            this.statements = List.copyOf(statements);
        }

        public static Block of(List<Stmt> statements) {
            return new Block(statements);
        }


        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }

        public List<Stmt> statements() {
            return statements;
        }
    }

    public static final class Expression extends Stmt {
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

    public static final class Function extends Stmt {

        private final Token name;
        private final List<Token> params;
        private final List<Stmt> body;

        private Function(Token name, List<Token> params, List<Stmt> body) {
            this.name = name;
            this.params = List.copyOf(params);
            this.body = List.copyOf(body);
        }

        public static Function of(Token name, List<Token> params, List<Stmt> body) {
            return new Function(name, params, body);
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStmt(this);
        }

        public Token name() {
            return name;
        }

        public List<Token> params() {
            return params;
        }

        public List<Stmt> body() {
            return body;
        }
    }

    public static final class If extends Stmt {

        private final Expr condition;
        private final Stmt thenBranch;
        private final Stmt elseBranch;

        private If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        public static If of(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            return new If(condition, thenBranch, elseBranch);
        }


        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }

        public Expr condition() {
            return condition;
        }

        public Stmt thenBranch() {
            return thenBranch;
        }

        public Stmt elseBranch() {
            return elseBranch;
        }
    }

    public static final class Return extends Stmt {

        private final Token keyword;
        private final Expr value;

        private Return(Token keyword, Expr value) {
            this.keyword = keyword;
            this.value = value;
        }

        public static Return of(Token keyword, Expr value) {
            return new Return(keyword, value);
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturnStmt(this);
        }

        public Token keyword() {
            return keyword;
        }

        public Expr value() {
            return value;
        }
    }

    public static final class Var extends Stmt {

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

    public static final class While extends Stmt {
        private final Expr condition;
        private final Stmt body;

        private While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }

        public static While of(Expr condition, Stmt body) {
            return new While(condition, body);
        }


        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }

        public Expr condition() {
            return condition;
        }

        public Stmt body() {
            return body;
        }
    }

    public static class Break extends Stmt {

        private final Token token;

        private Break(Token token) {
            this.token = token;
        }

        public static Break of(Token token) {
            return new Break(token);
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBreakStmt(this);
        }

        public Token token() {
            return token;
        }
    }
}

