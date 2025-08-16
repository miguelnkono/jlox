package parser;

import ast.Expr;
import ast.Stmt;
import scanner.Token;
import scanner.TokenType;
import lox.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import static scanner.TokenType.*;

public class Parser {
    private final List<Token> tokens;   // this is the list of tokens to parse. Also known as the 'letters' in the alphabet.
    private int current = 0;    // the current position in the list of tokens. Also known as the 'current letter' in the alphabet.
    private static class ParseError extends RuntimeException {}

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Stmt> parse() {
        List<Stmt> stmtList = new ArrayList<>();
        while (!isAtEnd()) {
            stmtList.add(declaration());
        }
        
        return stmtList;
    }

    private Stmt declaration() {
        try {
            if (match(VAR)) return varDeclaration();

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expect variable name");

        Expr initial = null;
        if (match(EQUAL)) {
            initial = expression();
        }

        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initial);
    }

    // parse the program from the very beginning of the script.
    private Stmt statement() {
        if (match(PRINT)){
            return printStatement();
        }
        if (match(LEFT_BRACE)) {
            return new Stmt.Block(block());
        }

        if (match(BREAK)) return breakStatement();

        // matching an if statement.
        if (match(IF)) return ifStatement();

        // matching a while loop.
        if (match(WHILE)) return whileStatement();

        // matching a for loop.
        if (match(FOR)) return forStatement();

        return expressionStatement();
    }

    private Stmt breakStatement() {
        consume(SEMICOLON, "Expect ';' after the break keyword.");
        return null;
    }

    private Stmt forStatement() {
        consume(LEFT_PAREN, "Expect '(' after the 'for'.");

        // parsing the initializer.
        Stmt initializer;
        if (match(SEMICOLON)) {
            // if we encounter a semicolon in the initializer we set it to null.
            initializer = null;
        } else if (match(VAR)) {
            // if we encounter a variable declaration, we create it and it is scope to the for loop.
            initializer = varDeclaration();
        } else {
            // if we encounter an expression in the initializer, we parse it.
            initializer = expressionStatement();
        }

        // parsing the conditional part of the for loop.
        Expr condition = null;
        if (!check(SEMICOLON)) {
            // if the condition is not semicolon then we can parse it. But if it, then we simply ignores it.
            condition = expression();
        }
        consume(SEMICOLON, "Expect ';' after the loop condition");

        // parsing the increment part of the for loop.
        Expr increment = null;
        if (!check(SEMICOLON)) {
            increment = expression();
        }
        consume(RIGHT_PAREN, "Expect ')' after the for clause.");

        // parsing the body of the for loop.
        Stmt body = statement();

        if (increment != null) {
            body = new Stmt.Block(
                    Arrays.asList(body, new Stmt.Expression(increment))
            );
        }

        if (condition == null) condition = new Expr.Literal(true);
        body = new Stmt.While(condition, body);

        if (initializer != null) {
            body = new Stmt.Block(
                    Arrays.asList(initializer, body)
            );
        }

        return body;
    }

    private Stmt whileStatement() {
        consume(LEFT_PAREN, "Expect a left parentheses after the while keyword.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expected a right parentheses after the condition.");
        Stmt body = statement();
        return new Stmt.While(condition, body);
    }

    private Stmt ifStatement() {
        // if (condition) { stmt } else { stmt }
        consume(LEFT_PAREN, "Expect '(' after if.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after the condition expression.");

        Stmt thenStmt = statement();
        Stmt elseStmt = null;
        if (match(ELSE)) {
            elseStmt = statement();
        }
        return new Stmt.If(condition, thenStmt, elseStmt);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();

        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Expect '}' after the block.");
        return statements;
    }

    // parse an expressionStatement node.
    private Stmt expressionStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Except ';' after the value.");
        return new Stmt.Expression(value);
    }

    // parse print statements.
    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Except ';' after the value");
        return new Stmt.Print(value);
    }

    // parse the expression rule.
    private Expr expression() {
        return assignment();
    }

    private Expr assignment() {
        // first we parse the left side of the assignment expression.
        // Expr expr = equality();
        Expr expr = or();

        // we look to see if there is an equal sign.
        if (match(EQUAL)) {
            Token equals = previous();  // we get the equal sign.
            Expr value = assignment();  // we parse the right hand side of the assignment.

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name, value);
            }

            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    // parse the or logical operator.
    private Expr or() {
        Expr expr = and();

        while (match(OR)) {
            Token previous = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, previous, right);
        }
        return expr;
    }

    // parse the and logical operator.
    private Expr and() {
        Expr expr = equality();

        while (match(AND)) {
            Token previous = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, previous, right);
        }
        return expr;
    }

    // parse the equality rule.
    private Expr equality() {
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {
        Expr expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    private Expr primary() {
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NIL)) return new Expr.Literal(null);

        if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal());
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        if (match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        throw error(peek(), "Expect expression.");
    }

    private Token consume(TokenType tokenType, String s) {
        if (check(tokenType)) {
            return advance();
        }

        throw error(peek(), s);
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type() == SEMICOLON) return;

            switch (peek().type()) {
                case CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN -> {
                    return;
                }
            }
            advance();
        }
    }

    private ParseError error(Token token, String msg) {
        Main.error(token, msg);
        return new ParseError();
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type() == type;
    }


    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean isAtEnd() {
        return peek().type() == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

}
