package com.example.lox.jlox;

import java.util.List;

sealed interface Stmt
        permits
        Stmt.Block,
        Stmt.Class,
        Stmt.Expression,
        Stmt.Function,
        Stmt.If,
        Stmt.Print,
        Stmt.Return,
        Stmt.Var,
        Stmt.While {

    static Block ofBlock(List<Stmt> statements) {
        return new Block(statements);
    }

    static Class ofClass(Token name, Expr.Variable superClass, List<Stmt.Function> methods) {
        return new Class(name, superClass, methods);
    }

    static Expression ofExpression(Expr expression) {
        return new Expression(expression);
    }

    static Function ofFunction(Token name, List<Token> params, List<Stmt> body) {
        return new Function(name, params, body);
    }

    static If ofIf(Expr condition, Stmt thenBranch, Stmt elseBranch) {
        return new If(condition, thenBranch, elseBranch);
    }

    static Print ofPrint(Expr expression) {
        return new Print(expression);
    }

    static Return ofReturn(Token keyword, Expr value) {
        return new Return(keyword, value);
    }

    static Var ofVar(Token name, Expr initializer) {
        return new Var(name, initializer);
    }

    static While ofWhile(Expr condition, Stmt body) {
        return new While(condition, body);
    }

    <R> R accept(Visitor<R> visitor);

    sealed interface Visitor<R> permits Interpreter, Resolver {
        R visitBlockStmt(Block stmt);

        R visitClassStmt(Class stmt);

        R visitExpressionStmt(Expression stmt);

        R visitFunctionStmt(Function stmt);

        R visitIfStmt(If stmt);

        R visitPrintStmt(Print stmt);

        R visitReturnStmt(Return stmt);

        R visitVarStmt(Var stmt);

        R visitWhileStmt(While stmt);
    }

    record Block(List<Stmt> statements) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }
    }

    record Class(Token name, Expr.Variable superClass, List<Stmt.Function> methods) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitClassStmt(this);
        }
    }

    record Expression(Expr expression) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }
    }

    record Function(Token name, List<Token> params, List<Stmt> body) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStmt(this);
        }
    }

    record If(Expr condition, Stmt thenBranch, Stmt elseBranch) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }
    }

    record Print(Expr expression) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }
    }

    record Return(Token keyword, Expr value) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturnStmt(this);
        }
    }

    record Var(Token name, Expr initializer) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }
    }

    record While(Expr condition, Stmt body) implements Stmt {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }
    }
}
