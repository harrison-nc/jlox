package com.example.lox.jlox.interpreter;

import com.example.lox.jlox.Expr;
import com.example.lox.jlox.Lox;
import com.example.lox.jlox.scanner.Token;
import com.example.lox.jlox.scanner.TokenType;
import com.example.lox.jlox.tool.Util;

public final class Interpreter implements Expr.Visitor<Object> {
    public void interpret(Expr expr) {
        try {
            Object value = evaluate(expr);
            Util.println(stringify(value));
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    private String stringify(Object object) {
        if (object == null) {
            return "nil";
        } else if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        } else {
            return object.toString();
        }
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value();
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression());
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right());

        switch (expr.operator().type()) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator(), right);
                return -(double) right;
            default:
                // Unreachable.
                return null;
        }
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left());
        Object right = evaluate(expr.right());

        TokenType type = expr.operator().type();
        switch (type) {
            case MINUS:
                checkNumberOperands(expr.operator(), left, right);
                return (double) left - (double) right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                } else {
                    // Treat the operands as String value.
                    return left + "" + right;
                }
            case SLASH:
                checkNumberOperands(expr.operator(), left, right);
                return (double) left / (double) right;
            case STAR:
                checkNumberOperands(expr.operator(), left, right);
                return (double) left * (double) right;
            case GREATER:
                checkNumberOperands(expr.operator(), left, right);
                return (double) left > (double) right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator(), left, right);
                return (double) left >= (double) right;
            case LESS:
                checkNumberOperands(expr.operator(), left, right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator(), left, right);
                return (double) left <= (double) right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
        }
        // Unreachable.
        return null;
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "operands must be numbers.");
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "operand must be a number.");
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return false;
        else if (a == null) return false;
        else return a.equals(b);
    }

    private boolean isTruthy(Object object) {
        if (object == null) {
            return false;
        } else if (object instanceof Boolean) {
            return (boolean) object;
        } else {
            return true;
        }
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }
}
