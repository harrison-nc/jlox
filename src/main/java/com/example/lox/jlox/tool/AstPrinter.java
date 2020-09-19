package com.example.lox.jlox.tool;

import com.example.lox.jlox.Expr;
import com.example.lox.jlox.Lox;
import com.example.lox.jlox.Stmt;
import com.example.lox.jlox.scanner.Token;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static com.example.lox.jlox.scanner.TokenType.OR;
import static com.example.lox.jlox.tool.Util.println;
import static com.example.lox.jlox.tool.Util.stringify;

public abstract class AstPrinter {
    protected AstPrinter() {
    }

    public static AstPrinter of(List<Stmt> stmt) {
        return new StmtPrinter(stmt);
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            println("Usage: printer [script]");
            System.exit(64);
        }

        String file = args[0];
        String source = Files.readString(Path.of(file), Charset.defaultCharset());

        List<Token> tokens = Lox.scan(source);
        List<Stmt> stmts = Lox.parse(tokens);

        AstPrinter printer = AstPrinter.of(stmts);
        String ast = printer.print();
        println(ast);

        Lox.execute(stmts);
    }

    public abstract String print();
}

class ExprPrinter extends AstPrinter implements Expr.Visitor<String> {
    private final StmtPrinter astPrinter;
    private Expr expression;

    ExprPrinter(StmtPrinter astPrinter) {
        this.astPrinter = astPrinter;
    }

    public void expression(Expr expr) {
        expression = expr;
    }

    @Override
    public String print() {
        if (expression != null) {
            return expression.accept(this);
        } else {
            return "";
        }
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator().lexeme(), expr.left(), expr.right());
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression());
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        return stringify(expr.value());
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator().lexeme(), expr.right());
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return expr.name().lexeme();
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        return expr.name().lexeme() + " " + expr.value().accept(this);
    }

    @Override
    public String visitLogicalExpr(Expr.Logical expr) {
        if (expr.operator().type() == OR) {
            return parenthesize("or", expr.left(), expr.right());
        }
        return parenthesize("and", expr.left(), expr.right());
    }

    @Override
    public String visitCallExpr(Expr.Call expr) {
        String callee = expr.callee().accept(this);
        List<Expr> arguments = expr.arguments();
        Expr[] exprs = arguments.toArray(new Expr[0]);
        return parenthesize(callee, exprs);
    }

    @Override
    public String visitFunExpr(Expr.Fun expr) {
        String fn = expr.keyword().lexeme();

        String params = expr.params()
                .stream()
                .map(Token::lexeme)
                .reduce((a, b) -> a + " " + b)
                .orElse("");

        String body = astPrinter.print(expr.body().toArray(new Stmt[0]));
        return "(%s [%s] %s)".formatted(fn, params, body);
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }
}

class StmtPrinter extends AstPrinter implements Stmt.Visitor<String> {
    private final ExprPrinter astPrinter;
    private final List<Stmt> statements;

    StmtPrinter(List<Stmt> stmts) {
        astPrinter = new ExprPrinter(this);
        this.statements = stmts;
    }

    @Override
    public String print() {
        StringBuilder builder = new StringBuilder();
        for (Stmt stmt : statements) {
            if (builder.toString().equals("")) {
                builder.append(stmt.accept(this));
            } else {
                builder.append("\n").append(stmt.accept(this));
            }
        }
        return builder.toString();
    }

    @Override
    public String visitBreakStmt(Stmt.Break stmt) {
        return "(break)";
    }

    @Override
    public String visitBlockStmt(Stmt.Block stmt) {
        Stmt[] body = stmt.statements().toArray(new Stmt[0]);
        return "(do %s)".formatted(print(body));
    }

    @Override
    public String visitExpressionStmt(Stmt.Expression stmt) {
        return stmt.expression().accept(astPrinter);
    }

    @Override
    public String visitFunctionStmt(Stmt.Function stmt) {
        String name = stmt.name().lexeme();

        String args = stmt.params()
                .stream()
                .map(Token::lexeme)
                .reduce((a, b) -> a + " " + b)
                .orElse("");

        String body = print(stmt.body().toArray(new Stmt[0]));

        return "(def %s (fn [%s] %s))".formatted(name, args, body);
    }

    @Override
    public String visitIfStmt(Stmt.If stmt) {
        String condition = stmt.condition().accept(astPrinter);
        String thenBranch = print(stmt.thenBranch());
        String elseBranch = null;
        if (stmt.elseBranch() != null) {
            elseBranch = print(stmt.elseBranch());
        }
        if (elseBranch == null) {
            return "(if %s %s)".formatted(condition, thenBranch);
        } else {
            return "(if %s %s %s)".formatted(condition, thenBranch, elseBranch);
        }
    }

    @Override
    public String visitReturnStmt(Stmt.Return stmt) {
        String value = "";

        if (stmt.value() != null) {
            value = stmt.value().accept(astPrinter);
        }

        return "(%s %s)".formatted(stmt.keyword().lexeme(), value);
    }

    @Override
    public String visitVarStmt(Stmt.Var stmt) {
        return "(def %s %s)".formatted(stmt.name().lexeme(),
                stmt.initializer().accept(astPrinter));
    }

    @Override
    public String visitWhileStmt(Stmt.While stmt) {
        String condition = stmt.condition().accept(astPrinter);
        String body = print(stmt.body());
        return "(while %s %s)".formatted(condition, body);
    }

    String print(Stmt... stmts) {
        return Stream.of(stmts)
                .map(stmt -> stmt.accept(this))
                .reduce((a, b) -> a + " " + b)
                .orElse("");
    }
}
