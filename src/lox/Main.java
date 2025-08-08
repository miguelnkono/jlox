package lox;

import ast.Expr;
import interpreter.Scanner;
import interpreter.Token;
import interpreter.TokenType;
import parser.Parser;
import tools.AstPrinter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    private static boolean hadError;

    public static void main(String[] args) throws IOException {
        // here we access the length of the command line arguments, and we branch on the
        // number of them.
        if (args.length > 1) {
            // if the number of command line argument are greater than one, it means that
            // the usage misused the usage of the jlox script. So notify him and we close.
            System.err.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            // if we end up here it means the user wants to pass a jlox script file to the
            // interpreter. So we run the interpreter with the file content as source code.
            runFile(args[0]);
        } else {
            // here we run the interpreter as REPL.
            runPrompt();
        }
    }

    /*
    * this run function reads inputs from the source string and execute the interpreter.
    * */
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();

        // stop if there is an error in the parsing.
        if (hadError) return;

        System.out.println(new AstPrinter().print(expression));
    }

    /*
    * we give a path to a file, so the interpreter reads the file content and execute it.
    * */
    private static void runFile(String path) throws IOException {
        // read the content of the file as an array of bytes.
        byte[] bytes = Files.readAllBytes(Path.of(path));
        run(new String(bytes, Charset.defaultCharset()));

        // if an error occurs.
        if (hadError) System.exit(65);
    }

    /*
     * if we fire up jlox without given it any command line argument, it drops us inside
     * the REPL.
     * */
    private static void runPrompt() throws IOException {
        // we open a stream and wrapped it inside a buffer to read from the command line.
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            // we read the inputs, and we pass it to the interpreter.
            String line = reader.readLine();
            if (line.startsWith(".exit")) {
                break;
            }
            // here we execute the interpreter with the line string as input.
            run(line);
            hadError = false;
        }
    }

    /*
    * this function report information to the user.
    * */
    private static void report(int line, String where, String message) {
        System.err.printf("[Line " + line + "] Error " + where + ": " + message);
        hadError = true;
    }

    /*
    * this function serve as error reporter to the user.
    * */
    public static void error(int line, String msg) {
        report(line, null, msg);
    }

    public static void error(Token token, String message) {
        if (token.type() == TokenType.EOF) {
            report(token.line(), " at end", message);
        } else {
            report(token.line(), " at '" + token.lexeme + "'", message);
        }
    }
}
