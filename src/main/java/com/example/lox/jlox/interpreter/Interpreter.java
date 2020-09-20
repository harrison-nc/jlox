package com.example.lox.jlox.interpreter;

import com.example.lox.jlox.Expr;
import com.example.lox.jlox.Lox;
import com.example.lox.jlox.Stmt;
import com.example.lox.jlox.scanner.Token;
import com.example.lox.jlox.scanner.TokenType;

import java.util.ArrayList;
import java.util.List;

import static com.example.lox.jlox.scanner.TokenType.OR;
import static com.example.lox.jlox.tool.Util.println;
import static com.example.lox.jlox.tool.Util.stringify;

public final class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    final Environment globals = new Environment();
    private Environment environment = globals;

    public Interpreter() {
        LoxCallable clock = new LoxCallable() {

            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return (double) System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        };
        globals.define("clock", clock);
    }

    public void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value();
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left());

        if (expr.operator().type() == OR) {
            if (isTruthy(left)) return left;
        } else {
            if (!isTruthy(left)) return left;
        }

        return evaluate(expr.right());
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
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee());

        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments()) {
            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof LoxCallable)) {
            throw new RuntimeError(expr.paren(), "Can only call functions and classes.");
        }

        LoxCallable function = (LoxCallable) callee;

        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expr.paren(), "Expected %d arguments but got %d."
                    .formatted(function.arity(),
                            arguments.size()));
        }

        return function.call(this, arguments);
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
                } else if (left instanceof String || right instanceof String) {
                    // Treat the operands as String value.
                    left = left instanceof String ? left : stringify(left);
                    right = right instanceof String ? right : stringify(right);
                    return left + "" + right;
                } else {
                    throw new RuntimeError(expr.operator(), "Expects two operands of type number or a string and another value.");
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

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return environment.get(expr.name());
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value());
        environment.assign(expr.name(), value);
        return value;
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

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition()))) {
            execute(stmt.thenBranch());
        } else if (null != stmt.elseBranch()) {
            execute(stmt.elseBranch());
        }
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression());
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        LoxFunction function = new LoxFunction(stmt, environment);
        environment.define(stmt.name().lexeme(), function);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression());
        println(stringify(value));
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value() != null) {
            value = evaluate(stmt.value());
        }

        throw new Return(value);
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;

        if (stmt.initializer() != null) {
            value = evaluate(stmt.initializer());
        }

        environment.define(stmt.name().lexeme(), value);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.condition()))) {
            execute(stmt.body());
        }
        return null;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements(), new Environment(environment));
        return null;
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;

        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }
}
