package tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/*
* this class is responsible for generating an AST data structure for our JLox programming language.
* */
public class GenerateAST {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: GenerateAST <output_directory>");
            System.exit(64);
        }

        String outputDirectory = args[0];   // get the output directory.

        // description of subclasses.
        List<String> subclassesDescription = Arrays.asList(
                "Assign         : Token name, Expr value",
                "Binary         : Expr left, Token operator, Expr right",
                "Grouping       : Expr expression",
                "Literal        : Object value",
                "Unary          : Token operator, Expr right",
                "Variable       : Token name"
        );

        defineAst(outputDirectory, "Stmt", Arrays.asList(
                "Block          : List<Stmt> statements",
                "Expression     : Expr expression",
                "Print          : Expr expression",
                "Var            : Token name, Expr initializer"
        ));
        defineAst(outputDirectory, "Expr", subclassesDescription);
    }

    private static void defineAst(
            String outputDirectory,
            String baseName,
            List<String> types) throws IOException {

        String path = "%s/%s.java".formatted(outputDirectory, baseName);
        PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8);

        writer.println("package ast;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.printf("public abstract class %s {%n", baseName);

        defineVisitor(writer, baseName, types);

        // The AST classes.
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        // The base accept() method.
        writer.println();
        writer.println("    public  abstract <R> R accept(Visitor<R> visitor);");

        writer.println("}");
        writer.close();
    }

    private static void defineVisitor(
            PrintWriter writer, String baseName, List<String> types) {
        writer.println("    public interface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.printf("        R visit%s%s(%s %s);%n", typeName, baseName, typeName, baseName.toLowerCase());
        }

        writer.println("    }");
    }

    private static void defineType(
            PrintWriter writer, String baseName,
            String className, String fieldList) {
        writer.printf("    public  static class %s extends %s {%n", className, baseName);

        // Constructor.
        writer.printf("        public %s(%s) {%n", className, fieldList);

        // Store parameters in fields.
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.printf("            this.%s = %s;%n", name, name);
        }

        writer.println("        }");

        // Visitor pattern.
        writer.println();
        writer.println("        @Override");
        writer.println("        public <R> R accept(Visitor<R> visitor) {");
        writer.printf("             return visitor.visit%s%s(this);%n", className, baseName);
        writer.println("        }");

        // Fields.
        writer.println();
        for (String field : fields) {
            writer.printf("        public final %s;%n", field);
        }

        writer.println("    }");
    }
}
