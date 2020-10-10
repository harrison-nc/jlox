///usr/bin/env jbang "$0" "$@" ; exit $?

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class generateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        defineAst(outputDir, "Expr", List.of(
                "Assign   : Token name, Expr value",
                "Binary   : Expr left, Token operator, Expr right",
                "Call     : Expr callee, Token paren, List<Expr> arguments",
                "Get      : Expr object, Token name",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Logical  : Expr left, Token operator, Expr right",
                "Set      : Expr object, Token name, Expr value",
                "Super    : Token keyword, Token method",
                "This     : Token keyword",
                "Unary    : Token operator, Expr right",
                "Variable : Token name"
        ));

        defineAst(outputDir, "Stmt", List.of(
                "Block      : List<Stmt> statements",
                "Class      : Token name, Expr.Variable superClass, List<Stmt.Function> methods",
                "Expression : Expr expression",
                "Function   : Token name, List<Token> params, List<Stmt> body",
                "If         : Expr condition, Stmt thenBranch, Stmt elseBranch",
                "Print      : Expr expression",
                "Return     : Token keyword, Expr value",
                "Var        : Token name, Expr initializer",
                "While      : Expr condition, Stmt body"
        ));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8);

        var visitor = defineVisitor(baseName, types);
        var factories = new StringBuilder();
        var classes = new StringBuilder();

        // The AST classes.
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();

            var factory = defineFactory(className, fields);
            if (factories.isEmpty()) {
                factories.append(factory);
            } else {
                factories.append("\n").append(factory);
            }

            var clazz = defineType(baseName, className, fields);
            if (classes.isEmpty()) {
                classes.append(clazz);
            } else {
                classes.append("\n").append(clazz);
            }
        }

        var ast = """
                package com.example.lox.jlox;

                import java.util.List;
                import com.example.lox.jlox.Token;

                sealed interface %s {
                    <R> R accept(Visitor<R> visitor);

                %s
                %s
                %s
                }\
                """
                .formatted(baseName, factories.toString(), visitor, classes.toString());

        writer.println(ast);
        writer.close();
    }

    private static String defineVisitor(String baseName, List<String> types) {
        var visitMethods = visitMethods(baseName, types);
        return "    sealed interface Visitor<R> {\n%s    }".formatted(visitMethods);
    }

    private static String defineType(String baseName, String className, String fieldList) {
        return """
                    record %s (%s) implements %s {
                        @Override
                        public <R> R accept(Visitor<R> visitor) {
                            return visitor.visit%s%s(this);
                        }
                    }\
                """
                .formatted(className, fieldList, baseName, className, baseName);
    }

    private static String defineFactory(String className, String fieldList) {
        String parameters = params(fieldList);
        return "    static %s of%s (%s) {\n        return new %s(%s);\n    }\n".formatted(className, className, fieldList, className, parameters);
    }

    private static String params(String fieldList) {
        StringBuilder builder = new StringBuilder();

        String[] types = fieldList.split(",");

        for (var type : types) {
            String name = type.trim().split(" ")[1].trim();
            if (builder.isEmpty()) {
                builder.append(name);
            } else {
                builder.append(", ").append(name);
            }
        }

        return builder.toString();
    }

    private static String visitMethods(String baseName, List<String> types) {
        var visitMethods = new StringBuilder();

        for (var type : types) {
            String typeName = type.split(":")[0].trim();
            var method = "        R visit%s%s(%s %s);\n".formatted(typeName, baseName, typeName, baseName.toLowerCase());

            if (visitMethods.isEmpty()) {
                visitMethods.append(method);
            } else {
                visitMethods.append("\n").append(method);
            }
        }

        return visitMethods.toString();
    }
}
