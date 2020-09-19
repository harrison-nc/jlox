package com.example.lox.jlox.interpreter;

import com.example.lox.jlox.Lox;
import com.example.lox.jlox.scanner.Token;
import com.example.lox.jlox.scanner.TokenType;
import com.example.lox.jlox.tool.AstPrinter;

import java.util.List;
import java.util.function.BiFunction;

import static com.example.lox.jlox.scanner.TokenType.EOF;
import static com.example.lox.jlox.tool.Util.println;
import static com.example.lox.jlox.tool.Util.stringify;

final class NativeFn {
    static final LoxCallable sexpr = fn(1, NativeFn::sexpr);
    static final LoxCallable clock = fn(0, NativeFn::clock);
    static final LoxCallable print = fn(1, NativeFn::printFn);
    static final LoxCallable eval = fn(1, NativeFn::eval);

    private static Object eval(Interpreter<Void> interpreter, List<Object> arguments) {
        Token token = Token.of(EOF, "end of file", null, 0);
        if (null == arguments) {
            Lox.runtimeError(new RuntimeError(token, "eval: requires an argument."));
        } else if (arguments.size() > 1) {
            Lox.runtimeError(new RuntimeError(token, "eval: requires only one argument."));
        } else {
            Object value = arguments.get(0);
            if (value instanceof String) {
                try {
                    String source = (String) value;
                    var tokenList = Lox.scan(source);
                    var stmtList = Lox.parse(tokenList);
                    Lox.interpret(stmtList);
                } catch (RuntimeError err) {
                    println("[error] eval: " + err.getMessage());
                    return null;
                }
            }
        }
        return null;
    }

    private static  Object clock(Interpreter<Void> interpreter, List<Object> arguments) {
        return (double) System.currentTimeMillis() / 1000.0;
    }

    private static  Object printFn(Interpreter<Void> interpreter, List<Object> arguments) {
        if (null != arguments && arguments.size() > 0) {
            Object value = arguments.get(0);
            println(stringify(value));
        } else {
            println("");
        }
        return null;
    }

    private static  Object sexpr(Interpreter<Void> interpreter, List<Object> arguments) {
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
        } else {
            println("");
        }
        return null;
    }

    private static LoxCallable fn(final int arity, final BiFunction<Interpreter<Void>, List<Object>, Object> call) {
        return new LoxCallable() {
            @Override
            public int arity() {
                return arity;
            }

            @Override
            public Object call(Interpreter<Void> interpreter, List<Object> arguments) {
                return call.apply(interpreter, arguments);
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        };
    }
}
