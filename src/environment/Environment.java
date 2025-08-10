package environment;

import evaluate.RuntimeError;
import scanner.Token;

import java.util.Map;
import java.util.HashMap;

public class Environment {
    private final Environment enclosing;  // most outer environment.
    private final Map<String, Object> values = new HashMap<>();

    // this constructor will create an outer scope. It's used for global scope.
    public Environment() {
        this.enclosing = null;
    }

    // this constructor will create an inner scope.
    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    /*
    * When a new variable is created, we add a new bindings by adding the name and the value of that variable to
    * the environment(values map).
    *
    * The variable definition can also allow redefinition of a variable which means that if we define a variable that
    * has already been added to the environment, that variable will be overwritten.
    * */
    public void define(String name, Object value) {
        values.put(name, value);
    }

    /*
    * Getting the variable of a variable in the environment.
    * If the variable doesn't exit we throw an error.
    * */
    public Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        // look at the outer scope if the variable is not found inside the inner scope.
        if (enclosing != null) return enclosing.get(name);

        throw new RuntimeError(name, "undefined variable '" + name.lexeme() + "'.");
    }

    public void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme(), value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "undefined variable '" + name.lexeme() + "'.");
    }
}
