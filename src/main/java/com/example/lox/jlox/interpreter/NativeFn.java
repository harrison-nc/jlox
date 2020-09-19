package com.example.lox.jlox.interpreter;

import com.example.lox.jlox.Lox;
import com.example.lox.jlox.tool.AstPrinter;
import com.example.lox.jlox.tool.Util;

import java.util.List;
import java.util.function.BiFunction;

import static com.example.lox.jlox.tool.Util.println;
import static com.example.lox.jlox.tool.Util.stringify;

final class NativeFn {
    static final LoxCallable sexpr = fn(1, NativeFn::sexprFn);
    static final LoxCallable clock = fn(0, NativeFn::clockFn);
    static final LoxCallable print = fn(1, NativeFn::printFn);
    static final LoxCallable eval = fn(1, NativeFn::evalFn);

    private static Object evalFn(Interpreter<?> interpreter, List<Object> arguments) {
        if (null == arguments) {
            Util.err("[error] eval: no argument provided");
        } else if (arguments.size() > 1) {
            Util.err("[error] eval: too many argument provided");
        } else {
            Object value = arguments.get(0);
            if (value instanceof String) {
                try {
                    String source = (String) value;
                    var tokenList = Lox.scan(source);
                    var stmtList = Lox.parse(tokenList);
                    return new BaseInterpreter().execute(stmtList);
                } catch (RuntimeError err) {
                    Util.err("[error] eval: an error occurred while evaluating expression.\nmessage: [%s]"
                            .formatted(err.getMessage()));
                }
            }
        }
        return null;
    }

    private static  Object clockFn(Interpreter<?> interpreter, List<Object> arguments) {
        return (double) System.currentTimeMillis() / 1000.0;
    }

    private static  Object printFn(Interpreter<?> interpreter, List<Object> arguments) {
        if (null != arguments && arguments.size() > 0) {
            Object value = arguments.get(0);
            if (null != value) {
                println(stringify(value));
            }
        }
        return null;
    }

    private static  Object sexprFn(Interpreter<?> interpreter, List<Object> arguments) {
        if (null != arguments && arguments.size() > 0) {
            Object value = arguments.get(0);
            if (value instanceof String) {
                try {
                    String source = (String) value;
                    var tokenList = Lox.scan(source);
                    var stmtList = Lox.parse(tokenList);
                    var printer = AstPrinter.stmtPrinter(stmtList);
                    var sexpr = printer.print();
                    println(sexpr);
                } catch (Throwable t) {
                    return null;
                }
            }
        }
        return null;
    }

    private static LoxCallable fn(final int arity, final BiFunction<Interpreter<?>, List<Object>, Object> call) {
        return new LoxCallable() {
            @Override
            public int arity() {
                return arity;
            }

            @Override
            public Object call(Interpreter<?> interpreter, List<Object> arguments) {
                return call.apply(interpreter, arguments);
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        };
    }
}
