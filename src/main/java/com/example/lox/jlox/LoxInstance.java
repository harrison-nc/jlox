package com.example.lox.jlox;

class LoxInstance {
    final LoxClass klass;

    public LoxInstance(LoxClass klass) {
        this.klass = klass;
    }

    @Override
    public String toString() {
        return klass.name + " instance";
    }
}
