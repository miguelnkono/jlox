package interpreter;

public class Token {
    private final String lexeme;
    private final TokenType tokenType;
    private final Object literal;
    private final int tokenLine;

    public Token(String lexeme, TokenType tokenType, Object literal, int tokenLine) {
        this.lexeme = lexeme;
        this.tokenType = tokenType;
        this.literal = literal;
        this.tokenLine = tokenLine;
    }

    @Override
    public String toString() {
        return "%s %s %s".formatted(tokenType, lexeme, tokenLine);
    }
}
