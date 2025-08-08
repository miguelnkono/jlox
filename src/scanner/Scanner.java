package scanner;

import lox.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static scanner.TokenType.*;

public class Scanner {
    private final String source; // the source code of the user.
    private final List<Token> tokens = new ArrayList<>();   // the list of all the tokens scanned.
    private int current = 0;    // the current position in the source code;
    private int start = 0;  // the start position of each token.
    private int line = 1;   // the line in the source file.

    private static final Map<String, TokenType> keywords;    // all the keywords inside our language.

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }

    public Scanner(String source) {
        this.source = source;
    }

    /*
    * this function will loop through the source code, at each loop it scans a new token.
    * when the scanning is finish we add a last eof token to simplify a bit the work of the
    * parser.
    * */
    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token("", EOF, null, line));
        return tokens;
    }

    /*
    * this function is responsible for scanning the source code and producing a new token.
    * */
    private void scanToken() {
        char c = advance();

        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;

            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL  : GREATER);
                break;
            case '/':
                if (match('/')) {
                    // this is for comments in JLox.
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else if (match('*')) {
                    comment();
                } else {
                    addToken(SLASH);
                }
                break;

            // if we encountered some kind of whitespace, we simply ignore it.
            case '\t':
            case ' ':
            case '\r':
                break;
            // if we encountered a new line we simply increment the line variable and we break.
            case '\n':
                line++;
                break;

            // we scan strings
            case '"':
                string();
                break;

            default:
                if (isDigit(c)) {
                    // if the character we encountered is a digit literal.
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Main.error(line, "Unexpected character");
                }
                break;
        }
    }

    private void comment() {
        int nesting = 1;
        while (nesting > 0 && !isAtEnd()) {
            char c = peek();
            char next = peekNext();
            if (c == '/' && next == '*') {
                // we create a new nested level.
                advance();
                advance();
                nesting++;
            } else if (c == '*' && next == '/') {
                // close a nested level.
                advance();
                advance();
                nesting--;
            } else {
                // here we add support for multiline comments.
                if (c == '\n') line++;
                advance();
            }
        }
        // if the nesting number is greater than 0 then it means we missed a nested level.
        if (nesting > 0) {
            Main.error(line, "Unterminated comment.");
        }
    }

    private void identifier() {
        while (isAlphaNumber(peek())) advance();

        // here we look to see if the chunk of code is register as a keyword.
        String text = source.substring(start, current);
        TokenType tokenType = keywords.get(text);

        if (tokenType == null) tokenType = IDENTIFIER;
        addToken(tokenType);
    }

    // function to checks if a character is an alphabetical symbol.
    private boolean isAlpha(char c) {
        return c >= 'a' && c <= 'z' ||
                c >= 'A' && c <= 'Z' ||
                c == '_';
    }

    // function to check if a character is an alphanumeric symbol.
    private boolean isAlphaNumber(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private void number() {
        while (isDigit(peek())) advance();

        if (peek() == '.' && isDigit(peekNext())) {

            do advance();
            while (isDigit(peek()));
        }

        double number = Double.parseDouble(source.substring(start, current));
        addToken(NUMBER, number);
    }

    // this function reads the next character in the source file.
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    // this function checks to see if we encountered a number.
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /*
    * this function scan a string token.
    * */
    private void string() {
        // we loop till we encountered the '"' symbol, or we are at the end of file.
        while (peek() != '"' && !isAtEnd()) {
            // we increment the line number if necessary.
            if (peek() == '\n') line++;

            advance();
        }

        if (isAtEnd()) {
            Main.error(line, "Unterminated string.");
            return;
        }

        // consume the last '"' symbol.
        advance();

        String text = source.substring(start + 1, current - 1);
        addToken(STRING, text);
    }

    // this function reads the current character inside the source code.
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    /*
    * this function checks to see if the current character matches the character passed as argument.
    * */
    private boolean match(char c) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != c) return false;

        current++;
        return true;
    }

    // create and add a new token in the list of tokens.
    private void addToken(TokenType tokenType) {
        addToken(tokenType, null);
    }

    private void addToken(TokenType tokenType, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(text, tokenType, literal, line));
    }

    // we read the next character in the source code.
    private char advance() {
        return source.charAt(current++);
    }

    /*
     * this function will check to see if we are at the end of the source file.
     * */
    private boolean isAtEnd() {
        return current >= source.length();
    }
}
