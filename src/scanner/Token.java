package scanner;

public class Token {
    public final String lexeme;
    private final TokenType tokenType;
    private final Object literal;
    private final int tokenLine;

    public Token(String lexeme, TokenType tokenType, Object literal, int tokenLine) {
        this.lexeme = lexeme;
        this.tokenType = tokenType;
        this.literal = literal;
        this.tokenLine = tokenLine;
    }

    public TokenType type() {
        return tokenType;
    }

    public Object literal() {
        return literal;
    }

    public int line() {
        return tokenLine;
    }

    @Override
    public String toString() {
        return "%s %s %s".formatted(tokenType, lexeme, tokenLine);
    }
}
