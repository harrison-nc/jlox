package com.example.lox.jlox.interpreter;

import com.example.lox.jlox.Stmt.Function;
import com.example.lox.jlox.scanner.Token;

import java.util.List;

public class LoxFunction implements LoxCallable {
    private final Function declaration;

    LoxFunction(Function declaration) {
        this.declaration = declaration;
    }

    @Override
    public int arity() {
        return declaration.params().size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(interpreter.globals);
        List<Token> params = declaration.params();
        for (int i = 0; i < params.size(); i++) {
            environment.define(
                    params.get(i).lexeme(),
                    arguments.get(i));
        }

        interpreter.executeBlock(declaration.body(), environment);
        return null;
    }

    @Override
    public String toString() {
        return "<fn %s>".formatted(declaration.name().lexeme());
    }
}
