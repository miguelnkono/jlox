# JLox Interpreter (Java)

A self-taught implementation of the JLox scanner in Java, inspired by the book [Crafting Interpreters](https://craftinginterpreters.com/). This project is a learning exercise to understand how programming languages and interpreters work from the ground up.

## Features

- **Lexer/Scanner**: Converts source code into tokens.
- **Parser**: Builds an abstract syntax tree (AST) from tokens.
- **Interpreter**: Executes the AST.
- **Error Handling**: Reports syntax and runtime errors.
- **REPL**: Interactive prompt for evaluating code snippets.
- **Script Execution**: Run `.lox` files directly.

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven or Gradle (for building)

### Build & Run

**Using Maven:**
```sh
mvn clean package
java -jar target/jlox.jar [script.lox]This is the JLox scanner implementation in Java language.
```

## Example

### Printing and Variables
```lox
print "Hello, World!";
var a = 10;
print a + 5;

if (true) {
  print "This will be printed.";
} else {
  print "This will not be printed.";
}

for (var i = 0; i < 5; i = i + 1) {
  print i;
}

fun greet(name) {
  print "Hello, " + name + "!";
}

greet("Lox");

class Person {
  init(name) {
    this.name = name;
  }

  sayHello() {
    print "Hi, I am " + this.name + ".";
  }
}

var john = Person("John");
john.sayHello();

try {
  var result = 10 / 0;
} catch (error) {
  print "Caught an error: " + error;
}
```

