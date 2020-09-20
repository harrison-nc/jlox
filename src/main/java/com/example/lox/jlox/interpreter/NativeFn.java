package com.example.lox.jlox.interpreter;

import com.example.lox.jlox.Lox;
import com.example.lox.jlox.tool.AstPrinter;
import com.example.lox.jlox.tool.Util;

import java.util.List;

import static com.example.lox.jlox.tool.Util.*;

final class NativeFn {
    static final LoxCallable expr = fn(1, NativeFn::expr);
    static final LoxCallable clock = fn(0, NativeFn::clockFn);
    static final LoxCallable print = fn(1, NativeFn::printFn);
    static final LoxCallable println = fn(1, NativeFn::printlnFn);
    static final LoxCallable eval = fn(255, NativeFn::evalFn, true);

    private static Object evalFn(Interpreter<?> interpreter, List<Object> arguments, int arity) {
        if (null != arguments && arguments.size() < arity) {
            BaseInterpreter baseInterpreter = new BaseInterpreter();
            Object result = null;
            for (Object value : arguments) {
                try {
                    String source = "" + value;
                    var tokenList = Lox.scan(source);
                    var stmtList = Lox.parse(tokenList);
                    result = baseInterpreter.interpret(stmtList);
                } catch (RuntimeError err) {
                    error("[evaluation error] %s".formatted(err.getMessage()));
                }
            }
            return result;
        }
        return null;
    }

    private static Object clockFn(Interpreter<?> interpreter, List<Object> arguments, int arity) {
        return (double) System.currentTimeMillis() / 1000.0;
    }

    private static Object printFn(Interpreter<?> interpreter, List<Object> arguments, int arity) {
        if (null != arguments && arguments.size() == 1) {
            Object value = arguments.get(0);
            if (null != value) {
                print(stringify(value));
            }
        } else {
            error("[argument mismatch] print requires 1 argument");
        }
        return null;
    }

    private static Object printlnFn(Interpreter<?> interpreter, List<Object> arguments, int arity) {
        if (null != arguments && arguments.size() == 1) {
            Object value = arguments.get(0);
            if (null != value) {
                println(stringify(value));
            }
        } else {
            error("[argument mismatch]: println requires 1 argument");
        }
        return null;
    }

    private static Object expr(Interpreter<?> interpreter, List<Object> arguments, int arity) {
        if (null != arguments && arguments.size() == arity) {
            for (Object value : arguments) {
                try {
                    String source = "" + value;
                    var tokenList = Lox.scan(source);
                    var stmtList = Lox.parse(tokenList);
                    var printer = AstPrinter.of(stmtList);
                    var expr = printer.print();
                    println(expr);
                } catch (Throwable t) {
                    return null;
                }

            }
        } else {
            error("[argument mismatch]: sexpr requires %d argument(s)".formatted(arity));
        }
        return null;
    }

    private static <T> LoxCallable fn(final int arity, final NativeCall<T> call) {
        return fn(arity, call, false);
    }

    private static <T> LoxCallable fn(final int arity, final NativeCall<T> call, boolean variadic) {
        return new LoxCallable() {
            @Override
            public int arity() {
                return arity;
            }

            @Override
            public boolean variadic() {
                return variadic;
            }

            @Override
            public Object call(Interpreter<?> interpreter, List<Object> arguments) {
                return call.apply(interpreter, arguments, arity);
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        };
    }

    private static void error(String message) {
        Util.err(message);
    }

    @FunctionalInterface
    interface NativeCall<R> {
        R apply(Interpreter<?> i, List<Object> args, int arity);
    }
}
