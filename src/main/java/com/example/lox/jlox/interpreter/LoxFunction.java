package com.example.lox.jlox.interpreter;

import com.example.lox.jlox.Stmt.Function;
import com.example.lox.jlox.scanner.Token;

import java.util.List;

public class LoxFunction implements LoxCallable {
    private final Function declaration;
    private final Environment closure;

    LoxFunction(Function declaration, Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    @Override
    public int arity() {
        return declaration.params().size();
    }

    @Override
    public Object call(Interpreter<?> interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
        List<Token> params = declaration.params();
        for (int i = 0; i < params.size(); i++) {
            environment.define(
                    params.get(i).lexeme(),
                    arguments.get(i));
        }

        try {
            interpreter.execute(declaration.body(), environment);
        } catch (Return returnValue) {
            return returnValue.value;
        }
        return null;
    }

    @Override
    public String toString() {
        return "<fn %s>".formatted(declaration.name().lexeme());
    }
}
