package com.example.lox.jlox.interpreter;

import com.example.lox.jlox.Expr;
import com.example.lox.jlox.Stmt;

import java.util.List;

public interface Interpreter<T> extends Expr.Visitor<Object>, Stmt.Visitor<T> {
    T interpret(List<Stmt> statements);

    T interpret(List<Stmt> statements, Environment environment);
}
