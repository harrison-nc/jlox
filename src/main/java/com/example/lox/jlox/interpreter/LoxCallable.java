package com.example.lox.jlox.interpreter;

import java.util.List;

public interface LoxCallable {
    int arity();

    Object call(Interpreter<Void> interpreter, List<Object> arguments);
}
