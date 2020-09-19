package com.example.lox.jlox.interpreter;

import java.util.List;

public interface LoxCallable {
    int arity();

    default boolean variadic() {
        return false;
    }

    default boolean notVariadic() {
        return !variadic();
    }

    Object call(Interpreter<?> interpreter, List<Object> arguments);
}
