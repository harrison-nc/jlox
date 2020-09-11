package com.example.lox.jlox;

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
import static com.example.lox.jlox.Ex.EX_USAGE;
import static com.example.lox.jlox.tool.Util.printf;
import static com.example.lox.jlox.tool.Util.println;

/**
 * Lox Interpreter.
 */
public class Lox {
    private static boolean hadError = false;

    private Lox() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            println("Usage: jlox [script]");
            System.exit(EX_USAGE.code());
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String pathString) throws IOException {
        byte[] bytes = Files.readAllBytes(Path.of(pathString));
        var tokenList = scan(new String(bytes, Charset.defaultCharset()));

        if (Lox.hadError) {
            System.exit(EX_DATAERR.code());
        }

        parse(tokenList);
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

            if (!hadError) {
                parse(tokenList);
            }

            hadError = false;
        }
    }

    private static List<Token> scan(String source) {
        Scanner scanner = Scanner.of(source);
        List<Token> tokens = scanner.scan();

        tokens.forEach(Lox::printToken);
        println("Token #" + tokens.size());

        return tokens;
    }

    private static List<Expr> parse(List<Token> tokens) {
        Parser parser = Parser.of(tokens);

        // Expr expr = parser.parse();
        // printExpr(expr);
        // return List.of(expr);

        List<Expr> exprs = parser.parseAll();
        exprs.forEach(Lox::printExpr);
        return exprs;
    }

    static void printExpr(Expr e) {
        println(AstPrinter.printExpr(e));
    }

    static void printToken(Token t) {
        printf("%3d: %-13s %s%n", t.line(), t.type(), t.literal());
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

    private static void report(int line, String where, String message) {
        Util.err("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}
