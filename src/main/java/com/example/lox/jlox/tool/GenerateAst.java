package com.example.lox.jlox.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.example.lox.jlox.Ex.EX_USAGE;
import static com.example.lox.jlox.tool.Util.println;

public class GenerateAst {
    private GenerateAst() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            println("Usage: generate_ast <output directory>");
            System.exit(EX_USAGE.code());
        } else {
            String outputDir = args[0];
            defineAst(outputDir, "Expr", List.of(
                    "Assign   : Token name, Expr value",
                    "Binary   : Expr left, Token operator, Expr right",
                    "Call     : Expr callee, Token paren, List<Expr> arguments",
                    "Grouping : Expr expression",
                    "Literal  : Object value",
                    "Logical  : Expr left, Token operator, Expr right",
                    "Unary    : Token operator, Expr right",
                    "Variable : Token name",
                    "Fun : Token keyword, List<Token> params, List<Stmt> body"
            ));
            defineAst(outputDir, "Stmt", List.of(
                    "Block     : List<Stmt> statements",
                    "Expression: Expr expression",
                    "Function  : Token name, List<Token> params, List<Stmt> body",
                    "If        : Expr condition, Stmt thenBranch, Stmt elseBranch",
                    "Return    : Token keyword, Expr value",
                    "Var       : Token name, Expr initializer",
                    "While     : Expr condition, Stmt body",
                    "Break     : Token token"
            ));
        }
    }

    private static void defineAst(String outputDir, String baseName, List<String> source) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8);

        StringBuilder classBuilder = new StringBuilder();
        StringBuilder visitMethods = new StringBuilder();

        for (String typeList : source) {
            String[] typeDecl = typeList.split(":");
            String className = typeDecl[0].trim();
            String fieldList = typeDecl[1].trim();

            String type = defineType(baseName, className, fieldList);
            classBuilder.append(type);
            String visit = "R visit%s%s(%s %s);"
                    .formatted(
                            className,
                            baseName,
                            className,
                            baseName.toLowerCase());
            append(visitMethods, visit, "\n");
        }

        String visitors = defineVisitor(baseName, visitMethods.toString());
        String classes = classBuilder.toString();

        writer.println("""
                package com.example.lox.jlox;
                                
                import java.util.List;
                import com.example.lox.jlox.scanner.Token;
                                
                public abstract class %s {
                                
                private %s () {
                }
                                
                public abstract <R> R accept(Visitor<R> visitor);
                                
                %s
                                
                %s
                }               
                """.formatted(baseName, baseName, visitors, classes)
        );

        writer.close();
    }

    private static String defineVisitor(String baseName, String methods) {
        String visitor = """
                public interface Visitor<R> {
                %s
                }
                """;

        return visitor.formatted(methods);
    }

    private static String defineType(String baseName, String className, String fieldList) {
        String def = """
                public static final class %s extends %s {
                %s
                                
                private %s (%s) {
                %s
                }
                                
                %s
                                
                @Override
                public <R> R accept(Visitor<R> visitor) {
                return visitor.visit%s%s(this);
                }
                                
                %s
                }
                """;

        StringBuilder defFields = new StringBuilder();
        StringBuilder defAccessors = new StringBuilder();
        StringBuilder defParameters = new StringBuilder();
        StringBuilder defAssigns = new StringBuilder();

        String[] fields = fieldList.split(",");
        for (String field : fields) {
            String[] decl = field.trim().split(" ");
            String type = decl[0].trim();
            String name = decl[1].trim();

            String defField = defineField(type, name);
            append(defFields, defField, "\n");

            String accessor = defineAccessor(type, name);
            append(defAccessors, accessor, " ");

            String assign = defineAssign(name);
            append(defAssigns, assign, "\n");

            append(defParameters, name, ", ");
        }

        String defFactory = defineFactory(className, fieldList, defParameters.toString());

        return def.formatted(
                className,
                baseName,
                defFields.toString(),
                className,
                fieldList,
                defAssigns,
                defFactory,
                className,
                baseName,
                defAccessors.toString());
    }

    private static String defineAssign(String fieldNames) {
        return "this.%s = %s;".formatted(fieldNames, fieldNames);
    }

    private static String defineFactory(String className, String fieldList, String arguments) {
        String def = """
                public static %s of (%s) {
                return new %s(%s);
                }
                """;

        return def.formatted(
                className,
                fieldList,
                className,
                arguments);
    }

    private static String defineField(String type, String name) {
        return "private final %s %s;".formatted(type, name);
    }

    private static String defineAccessor(String type, String name) {
        return "public %s %s() {return %s;}".formatted(type, name, name);
    }

    private static void append(StringBuilder defFields, String defField, String separator) {
        if (defFields.toString().equals("")) {
            defFields.append(defField);
        } else {
            defFields.append(separator).append(defField);
        }
    }
}
