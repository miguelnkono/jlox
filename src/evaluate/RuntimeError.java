package evaluate;

import scanner.Token;

public class RuntimeError extends RuntimeException{
    private final Token token;

    RuntimeError(Token token, String msg) {
        super(msg);
        this.token = token;
    }

    public Token token() { return this.token; }
}
