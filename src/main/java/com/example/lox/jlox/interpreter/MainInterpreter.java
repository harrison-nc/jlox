package com.example.lox.jlox.interpreter;

import com.example.lox.jlox.Expr;
import com.example.lox.jlox.Stmt;
import com.example.lox.jlox.Stmt.Function;

import java.util.List;

public final class MainInterpreter implements Interpreter<Void> {
    private final BaseInterpreter baseInterpreter = new BaseInterpreter();

    public MainInterpreter() {
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return baseInterpreter.visitLiteralExpr(expr);
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        return baseInterpreter.visitLogicalExpr(expr);
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return baseInterpreter.visitGroupingExpr(expr);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        return baseInterpreter.visitUnaryExpr(expr);
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        return baseInterpreter.visitCallExpr(expr);
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        return baseInterpreter.visitBinaryExpr(expr);
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return baseInterpreter.visitVariableExpr(expr);
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        return baseInterpreter.visitAssignExpr(expr);
    }

    @Override
    public Object visitFunExpr(Expr.Fun expr) {
        return baseInterpreter.visitFunExpr(expr);
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        baseInterpreter.visitIfStmt(stmt);
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        baseInterpreter.visitExpressionStmt(stmt);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Function stmt) {
        baseInterpreter.visitFunctionStmt(stmt);
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        baseInterpreter.visitReturnStmt(stmt);
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        baseInterpreter.visitVarStmt(stmt);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        baseInterpreter.visitWhileStmt(stmt);
        return null;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        baseInterpreter.visitBlockStmt(stmt);
        return null;
    }

    @Override
    public Void visitBreakStmt(Stmt.Break stmt) {
        baseInterpreter.visitBreakStmt(stmt);
        return null;
    }

    @Override
    public Void execute(List<Stmt> statements) {
        baseInterpreter.execute(statements);
        return null;
    }

    @Override
    public Void execute(List<Stmt> statements, Environment environment) {
        baseInterpreter.execute(statements, environment);
        return null;
    }
}
