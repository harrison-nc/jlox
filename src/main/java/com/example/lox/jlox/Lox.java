package com.example.lox.jlox;

import com.example.lox.jlox.intern.LoxError;
import com.example.lox.jlox.parser.Parser;
import com.example.lox.jlox.scanner.Scanner;
import com.example.lox.jlox.scanner.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.example.lox.jlox.AstPrinter.printExpr;
import static com.example.lox.jlox.intern.ExitCode.EX_DATAERR;
import static com.example.lox.jlox.intern.ExitCode.EX_USAGE;

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
            System.out.println();
        }
    }

    private static void run(String source) {
        Scanner scanner = Scanner.apply(source);
        List<Token> tokens = scanner.scanTokens();

        if(LoxError.hadError()) {
            LoxError.report();
            return;
        }

        Parser parser = Parser.parseTokens(tokens);
        List<Expr> exprList = parser.parseAll();

        if(LoxError.hadError()) {
            LoxError.report();
            return;
        }

        exprList.forEach(e -> System.out.println(printExpr(e)));
    }

    private Lox() {
    }
}
