package com.example.lox.jlox.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.example.lox.jlox.Ex.EX_USAGE;

public class GenerateAst {
    private GenerateAst() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            Util.println("Usage: generate_ast <output directory>");
            System.exit(EX_USAGE.code());
        } else {
            String outputDir = args[0];
            defineAst(outputDir, List.of(
                    "Binary : Expr left, Token operator, Expr right",
                    "Grouping : Expr expression",
                    "Literal : Object value",
                    "Unary : Token operator, Expr right"
            ));
        }
    }

    private static void defineAst(String outputDir, List<String> types)
            throws IOException {
        String baseName = "Expr";
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8);

        writer.println("package com.example.lox.jlox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("public abstract class " + baseName + " {");

        defineVisitor(writer, baseName, types);

        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fieldList = type.split(":")[1].trim();
            defineType(writer, baseName, className, fieldList);
        }

        // The base accept() method.
        writer.println();
        writer.println("    abstract <R> R accept(Visitor<R> visitor);");
        writer.println("}");
        writer.close();
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println();
        writer.println("    public interface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println();
            writer.println("        R visit" + typeName + baseName + "(" +
                    typeName + " " + baseName.toLowerCase() + ");");
        }

        writer.println("    }");
    }

    private static void defineFactory(PrintWriter writer, String className, String fieldList) {
        writer.println();
        writer.println("        public static " + className + " of (" + fieldList + ") {");

        StringBuilder args = new StringBuilder();
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            String name = field.split(" ")[1];
            if (args.toString().equals("")) {
                args.append(name);
            } else {
                args.append(", ").append(name);
            }
        }

        writer.println("            return new " + className + "(" + args.toString() + ");");
        writer.println("        }");
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        writer.println();
        writer.println("    public static class " + className + " extends " +
                baseName + " {");


        // Constructor.
        writer.println();
        writer.println("        private " + className + "(" + fieldList + ") {");

        // Store parameters in fields.
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println("            this." + name + " = " + name + ";");
        }

        writer.println("        }");

        // Factory method.
        defineFactory(writer, className, fieldList);

        // Visitor pattern.
        writer.println();
        writer.println("        @Override");
        writer.println("        public <R> R accept(Visitor<R> visitor) {");
        writer.println("            return visitor.visit" +
                className + baseName + "(this);");
        writer.println("        }");

        // Fields.
        for (String field : fields) {
            writer.println();
            writer.println("        private final " + field + ";");

            // Assessors.
            writer.println();
            String type = field.split(" ")[0].trim();
            String name = field.split(" ")[1].trim();
            writer.println("        public " + type + " " + name + "() {");
            writer.println("            return " + name + ";");
            writer.println("        }");
        }

        writer.println("    }");
    }
}
