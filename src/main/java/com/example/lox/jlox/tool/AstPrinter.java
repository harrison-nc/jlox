package com.example.lox.jlox.tool;

import com.example.lox.jlox.Expr;

import static com.example.lox.jlox.scanner.TokenType.OR;
import static com.example.lox.jlox.tool.Util.stringify;

public class AstPrinter implements Expr.Visitor<String> {

    private AstPrinter() {
    }

    public static String printExpr(Expr expr) {
        return new AstPrinter().print(expr);
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
    public String visitLogicalExpr(Expr.Logical expr) {
        if (expr.operator().type() == OR) {
            return parenthesize("or", expr.left(), expr.right());
        }
        return parenthesize("and", expr.left(), expr.right());
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }
}
