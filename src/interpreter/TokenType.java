package interpreter;

public enum TokenType {
    // single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, STAR, SLASH,

    // one or two character tokens.
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL ,
    LESS, LESS_EQUAL ,

    // literals.
    IDENTIFIER, STRING, NUMBER,

    // keywords.
    AND, CLASS, ELSE, IF, FUN, FOR, NIL, OR, FALSE,
    PRINT, RETURN, SUPER, VAR, WHILE, THIS, TRUE,

    EOF
}
