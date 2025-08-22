package evaluate;

import java.util.List;

// this interface will represent an object in jlox that can be call like a function, typically: functions, classes, etc.
// every object that will want to be called like a function will need to implement this interface.
public interface LoxCallable {
    // arity is the number of arguments a function or an operation expect.
    // so this function checks to see that the numbers of arguments passed to the function correspond to the parameters
    // expected by the function.
    int arity();

    /*
     * We pass in the interpreter in case the class implementing call() needs it. We also give it the list of evaluated argument values.
     * The implementer’s job is then to return the value that the call expression produces.
     *
     * We take the interpreter as the first argument so that we can access the global scope of the interpreter in case we want to use
     * something that is in that scope such built-in function in define variables.
     * */
    Object call(Interpreter interpreter, List<Object> arguments);
}
