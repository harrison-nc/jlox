package com.example.lox.jlox.interpreter;

import com.example.lox.jlox.Expr;
import com.example.lox.jlox.Lox;
import com.example.lox.jlox.Stmt;
import com.example.lox.jlox.scanner.Token;
import com.example.lox.jlox.scanner.TokenType;

import java.util.ArrayList;
import java.util.List;

import static com.example.lox.jlox.interpreter.NativeFn.*;
import static com.example.lox.jlox.scanner.TokenType.OR;
import static com.example.lox.jlox.tool.Util.stringify;

public class BaseInterpreter implements Interpreter<Object> {
    final Environment globals = new Environment();
    Environment environment = globals;

    public BaseInterpreter() {
        globals.define("clock", clock);
        globals.define("print", print);
        globals.define("sexpr", sexpr);
        globals.define("eval", eval);
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
        LoxCallable function = callable(expr, callee);

        if (function.arity() == 0) {
            return function.call(this, null);
        } else {
            List<Object> arguments = arguments(expr, function.arity());
            return function.call(this, arguments);
        }
    }

    private List<Object> arguments(Expr.Call expr, int arity) {
        List<Object> arguments = new ArrayList<>();

        for (Expr argument : expr.arguments()) {
            arguments.add(evaluate(argument));
        }

        if (arguments.size() != arity) {
            throw new RuntimeError(expr.paren(), "Expected %d arguments but got %d."
                    .formatted(arity,
                            arguments.size()));
        }
        return arguments;
    }

    private LoxCallable callable(Expr.Call expr, Object callee) {
        if (!(callee instanceof LoxCallable)) {
            throw new RuntimeError(expr.paren(), "Can only call functions and classes.");
        }

        return (LoxCallable) callee;
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
                    left = !(left instanceof String) ? stringify(left) : left;
                    right = !(right instanceof String) ? stringify(right) : right;
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

    @Override
    public Object visitFunExpr(Expr.Fun expr) {
        Stmt.Function function = Stmt.Function.of(expr.keyword(), expr.params(), expr.body());
        return new LoxFunction(function, environment);
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
    public Object visitIfStmt(Stmt.If stmt) {
        Object result = null;
        if (isTruthy(evaluate(stmt.condition()))) {
            result = execute(stmt.thenBranch());
        } else if (null != stmt.elseBranch()) {
            result = execute(stmt.elseBranch());
        }
        return result;
    }

    @Override
    public Object visitExpressionStmt(Stmt.Expression stmt) {
        return evaluate(stmt.expression());
    }

    @Override
    public Object visitFunctionStmt(Stmt.Function stmt) {
        LoxFunction function = new LoxFunction(stmt, environment);
        environment.define(stmt.name().lexeme(), function);
        return function;
    }

    @Override
    public Object visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value() != null) {
            value = evaluate(stmt.value());
        }

        throw new Return(value);
    }

    @Override
    public Object visitVarStmt(Stmt.Var stmt) {
        Object value = null;

        if (stmt.initializer() != null) {
            value = evaluate(stmt.initializer());
        }

        environment.define(stmt.name().lexeme(), value);
        return value;
    }

    @Override
    public Object visitWhileStmt(Stmt.While stmt) {
        Object result = null;
        while (isTruthy(evaluate(stmt.condition()))) {
            try {
                result = execute(stmt.body());
            } catch (Break breakLoop) {
                return result;
            }
        }
        return result;
    }

    @Override
    public Object visitBlockStmt(Stmt.Block stmt) {
        return execute(stmt.statements(), new Environment(environment));
    }

    @Override
    public Object visitBreakStmt(Stmt.Break stmt) {
        throw new Break(stmt.token());
    }

    private Object execute(Stmt stmt) {
        return stmt.accept(this);
    }

    @Override
    public Object execute(List<Stmt> statements) {
        Object result = null;
        try {
            for (Stmt statement : statements) {
                result = execute(statement);
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        } catch (Break error) {
            Lox.runtimeError(new RuntimeError(error.token(), "break is not allowed outside loop."));
        }
        return result;
    }

    @Override
    public Object execute(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        Object result;
        try {
            this.environment = environment;
            result = execute(statements);
        } finally {
            this.environment = previous;
        }
        return result;
    }
}
