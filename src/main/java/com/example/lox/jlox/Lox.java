package com.example.lox.jlox;

import com.example.lox.jlox.interpreter.BaseInterpreter;
import com.example.lox.jlox.interpreter.Interpreter;
import com.example.lox.jlox.interpreter.MainInterpreter;
import com.example.lox.jlox.interpreter.RuntimeError;
import com.example.lox.jlox.parser.Parser;
import com.example.lox.jlox.scanner.Scanner;
import com.example.lox.jlox.scanner.Token;
import com.example.lox.jlox.scanner.TokenType;
import com.example.lox.jlox.tool.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.example.lox.jlox.Ex.EX_DATAERR;
import static com.example.lox.jlox.Ex.EX_SOFTWARE;
import static com.example.lox.jlox.tool.Util.*;

/**
 * Lox Interpreter.
 */
public class Lox {
    private static Interpreter<?> interpreter;
    private static boolean hadRuntimeError = false;
    private static boolean hadError = false;

    private Lox() {
    }

    public static void run(String[] args) throws IOException {
        if (args.length > 1) {
            println("Usage: jlox [script]");
            System.exit(Ex.EX_USAGE.code());
        } else if (args.length == 1) {
            interpreter = new MainInterpreter();
            runFile(args[0]);
        } else {
            interpreter = new BaseInterpreter();
            runPrompt();
        }
    }

    private static void runFile(String pathString) throws IOException {
        byte[] bytes = Files.readAllBytes(Path.of(pathString));

        var tokenList = scan(new String(bytes, Charset.defaultCharset()));
        var stmtList = parse(tokenList);
        execute(stmtList);

        checkForError();
        // Exit normally if there were no errors.
    }

    private static void checkForError() {
        // Exit if there were errors while scanning or parsing.
        if (hadError) {
            System.exit(EX_DATAERR.code());
        }

        // Exit if there was any error while interpreting an expression.
        if (hadRuntimeError) {
            System.exit(EX_SOFTWARE.code());
        }
    }

    private static void runPrompt() throws IOException {
        var input = new InputStreamReader(System.in);
        var reader = new BufferedReader(input);

        for (; ; ) {
            print("> ");
            String line = reader.readLine();

            if (line == null) {
                break;
            }

            var tokenList = scan(line);
            var stmtList = parse(tokenList);
            var result = execute(stmtList);

            println(stringify(result));

            hadError = false;
        }
    }

    public static List<Token> scan(String source) {
        Scanner scanner = Scanner.of(source);
        return scanner.scan();
    }

    public static List<Stmt> parse(List<Token> tokens) {
        if (hadError || null == tokens) {
            return new ArrayList<>(1);
        }

        Parser parser = Parser.of(tokens);
        return parser.parse();
    }

    public static Object execute(List<Stmt> statements) {
        if (hadError || null == statements) {
            return null;
        }

        return interpreter.execute(statements);
    }

    public static void error(int line, String message) {
        error(line, "", message);
    }

    public static void error(Token token, String message) {
        if (token.type() == TokenType.EOF) {
            report(token.line(), " at end", message);
        } else {
            report(token.line(), " at '" + token.lexeme() + "'", message);
        }
    }

    public static void error(int line, String where, String message) {
        report(line, where, message);
    }

    public static void runtimeError(RuntimeError error) {
        Util.err("[line " + error.token().line() + "] " + error.getMessage());
        hadRuntimeError = true;
    }

    private static void report(int line, String where, String message) {
        Util.err("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}
