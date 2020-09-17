package com.example.lox.jlox.interpreter;

import java.util.List;

import static com.example.lox.jlox.tool.Util.println;
import static com.example.lox.jlox.tool.Util.stringify;

final class NativeFn {
    final static LoxCallable clock = new LoxCallable() {

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

    final static LoxCallable print = new LoxCallable() {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            if (null != arguments && arguments.size() > 0) {
                Object value = arguments.get(0);
                println(stringify(value));
            } else {
                println("");
            }
            return null;
        }

        @Override
        public String toString() {
            return "<native fn>";
        }
    };
}
