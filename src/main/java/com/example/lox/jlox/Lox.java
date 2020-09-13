package com.example.lox.jlox;

import com.example.lox.jlox.interpreter.Interpreter;
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
import java.util.List;

import static com.example.lox.jlox.Ex.EX_DATAERR;
import static com.example.lox.jlox.Ex.EX_SOFTWARE;
import static com.example.lox.jlox.tool.Util.println;

/**
 * Lox Interpreter.
 */
public class Lox {
    private static final Interpreter interpreter = new Interpreter();
    private static boolean hadRuntimeError = false;
    private static boolean hadError = false;

    private Lox() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            println("Usage: jlox [script]");
            System.exit(Ex.EX_USAGE.code());
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String pathString) throws IOException {
        byte[] bytes = Files.readAllBytes(Path.of(pathString));
        var tokenList = scan(new String(bytes, Charset.defaultCharset()));

        if (!hadError) {
            var exprList = parse(tokenList);
            if (!hadError) {
                exprList.forEach(Lox::interpret);
            }
        }

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
            System.out.print("> ");
            String line = reader.readLine();

            if (line == null) {
                break;
            }

            var tokenList = scan(line);

            // If there was not error while scanning then parse the tokens.
            if (!hadError) {
                // Parses only one expression at a time so there is only one item in the list.
                var exprList = parse(tokenList);
                var expr = exprList.get(0);

                // Run interpreter if there was no error while parsing the source code.
                if (expr != null) {
                    interpret(expr);
                }
            }

            hadError = false;
        }
    }

    private static List<Token> scan(String source) {
        Scanner scanner = Scanner.of(source);
        return scanner.scan();
    }

    private static List<Expr> parse(List<Token> tokens) {
        Parser parser = Parser.of(tokens);
        return parser.parseAll();
    }

    private static void interpret(Expr expr) {
        Util.print(expr, " => ");
        interpreter.interpret(expr);
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
        Util.err(error.getMessage() + "\n[line " + error.token().line() + "]");
        hadRuntimeError = true;
    }

    private static void report(int line, String where, String message) {
        Util.err("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}
