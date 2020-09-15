package com.example.lox.jlox.tool;

import com.example.lox.jlox.Expr;
import com.example.lox.jlox.Stmt;

import static com.example.lox.jlox.tool.Util.stringify;

public class AstPrinter implements Expr.Visitor<String>, Stmt.Visitor<String> {

    private AstPrinter() {
    }

    public static String printStmt(Stmt stmt) {
        return new AstPrinter().print(stmt);
    }

    public static String printExpr(Expr expr) {
        return new AstPrinter().print(expr);
    }

    private String print(Stmt stmt) {
        return stmt.accept(this);
    }

    private String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator().lexeme(), expr.left(), expr.right());
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression());
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        return stringify(expr.value());
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator().lexeme(), expr.right());
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return expr.name().lexeme();
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        return expr.name().lexeme() + " " + expr.value().accept(this);
    }

    @Override
    public String visitExpressionStmt(Stmt.Expression stmt) {
        return parenthesize("", "", stmt.expression());
    }

    @Override
    public String visitVarStmt(Stmt.Var stmt) {
        String name = "def " + stmt.name().lexeme();
        if (null == stmt.initializer()) {
            return parenthesize(name);
        }
        return parenthesize(name, stmt.initializer());
    }

    @Override
    public String visitPrintStmt(Stmt.Print stmt) {
        return parenthesize("print", stmt.expression());
    }

    @Override
    public String visitBlockStmt(Stmt.Block stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("(do");
        for (Stmt s : stmt.statements()) {
            builder.append("\n");
            builder.append(s.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    private String parenthesize(String name, Expr... exprs) {
        return parenthesize(name, " ", exprs);
    }

    private String parenthesize(String name, String separator, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(separator);
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }
}
