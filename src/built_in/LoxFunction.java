package built_in;

import ast.Stmt;
import environment.Environment;
import evaluate.Interpreter;
import evaluate.LoxCallable;

import java.util.List;

// this is how a function will be represented in the JLox programming language.
// since it is a function it implements the LoxCallable interface.
public class LoxFunction implements LoxCallable {
    private final Stmt.Function declaration;
    private final Environment closure;  // this is for closure functions.

    public LoxFunction(Stmt.Function declaration, Environment closure) {
        // We get the entire function node and store it.
        this.declaration = declaration;
        this.closure = closure;
    }

    @Override
    public int arity() {
        // this returns the numbers of arguments of the function.
        return this.declaration.arguments.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        // first we create an inner scope for this function.
        // Environment environment = new Environment(interpreter.global());
        Environment environment = new Environment(closure);

        /*
        * For each parameter we bind an argument value to that.
        * We loop through the list of parameters and create a new binding for them.
        * */
        for (int i = 0; i < this.declaration.arguments.size(); i++) {
            // we get the i-th parameter, the i-th argument and then bind them together.
            environment.define(this.declaration.arguments.get(i).lexeme(), arguments.get(i));
        }

        // now we execute the body of the function.
        try {
            interpreter.executeBlock(this.declaration.body, environment);
        } catch (Return returnValue) {
            return returnValue.value;
        }
        return null;
    }

    @Override
    public String toString() {
        // returns the name of the function.
        return "<fn %s>".formatted(this.declaration.name);
    }
}
