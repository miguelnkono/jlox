package evaluate;

import scanner.Token;

// this is our class to return errors to the user, it extends the RuntimeException class of Java.
// It includes the token from which the runtime error occurred.
public class RuntimeError extends RuntimeException{
    private final Token token;

    public RuntimeError(Token token, String msg) {
        super(msg);
        this.token = token;
    }

    public Token token() { return this.token; }
}
