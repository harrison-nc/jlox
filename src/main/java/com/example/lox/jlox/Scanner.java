package com.example.lox.jlox;

import java.util.List;

public class Scanner {
    private final String source;

    public static Scanner apply(String source) {
        return new Scanner(source);
    }

    private Scanner(String source) {
        this.source = source;
    }

	public List<Token> scanTokens() {
		return null;
	}
}
