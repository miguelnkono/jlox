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

    public LoxFunction(Stmt.Function declaration) {
        // We get the entire function node and store it.
        this.declaration = declaration;
    }

    @Override
    public int arity() {
        // this returns the numbers of arguments of the function.
        return this.declaration.arguments.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        // first we create an inner scope for this function.
        Environment environment = new Environment(interpreter.global());

        /*
        * For each parameter we bind an argument value to that.
        * We loop through the list of parameters and create a new binding for them.
        * */
        for (int i = 0; i < this.declaration.arguments.size(); i++) {
            // we get the i-th parameter, the i-th argument and then bind them together.
            environment.define(this.declaration.arguments.get(i).lexeme(), arguments.get(i));
        }

        // now we execute the body of the function.
        interpreter.executeBlock(this.declaration.body, environment);
        return null;
    }

    @Override
    public String toString() {
        // returns the name of the function.
        return "<fn %s>".formatted(this.declaration.name);
    }
}
