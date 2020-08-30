package com.example.lox.jlox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.example.lox.jlox.ExitCode.EX_DATAERR;
import static com.example.lox.jlox.ExitCode.EX_USAGE;

/**
 * Lox Interpreter
 */
public class Lox {
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(EX_USAGE.code());
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String pathString) throws IOException {
        // Todo: Could have used Files.readAllLines(..)
        // Todo: Could have used Files.lines() and then use the stream api
        byte[] bytes = Files.readAllBytes(Path.of(pathString));
        run(new String(bytes, Charset.defaultCharset()));

        if (LoxError.hadError()) {
            LoxError.report();
            System.exit(EX_DATAERR.code());
        }
    }

    private static void runPrompt() throws IOException {
        var input = new InputStreamReader(System.in);
        var reader = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();

            if (line == null) {
                break;
            }

            run(line);
        }
    }

    private static void run(String source) {
        Scanner scanner = Scanner.apply(source);
        List<Token> tokens = scanner.scanTokens();

        for (var token : tokens) {
            System.out.println(token);
        }
    }
}
