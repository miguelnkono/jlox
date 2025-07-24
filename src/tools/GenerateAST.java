package tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
        List<String> subclassesDescription = Arrays.asList(
                "Binary     : Expr left, Token operator, Expr right",
                "Grouping   : Expr expression",
                "Literal    : Object value",
                "Unary      : Token operator, Expr right"
        );
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
        writer.printf("abstract class %s {%n", baseName);

        // The AST classes.
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        writer.println("}");
        writer.close();
    }

    private static void defineType(
            PrintWriter writer, String baseName,
            String className, String fieldList) {
        writer.printf("  static class %s extends %s {%n", className, baseName);

        // Constructor.
        writer.printf("    %s(%s) {%n", className, fieldList);

        // Store parameters in fields.
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.printf("      this.%s = %s;%n", name, name);
        }

        writer.println("    }");

        // Fields.
        writer.println();
        for (String field : fields) {
            writer.printf("    final %s;%n", field);
        }

        writer.println("  }");
    }
}
