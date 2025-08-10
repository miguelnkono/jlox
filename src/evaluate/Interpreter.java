package evaluate;

import ast.Expr;
import ast.Expr.Binary;
import ast.Expr.Grouping;
import ast.Expr.Literal;
import ast.Expr.Unary;
import ast.Stmt;
import environment.Environment;
import scanner.Token;
import lox.Main;

import java.util.List;
import java.util.Objects;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

	private Environment environment = new Environment();

	/*
	* Function to interpret the ast.
	* */
	public void interpret(List<Stmt> statements) {
		try {
			for (Stmt statement : statements) {
				execute(statement);
			}
		} catch (RuntimeError re) {
			Main.runtimeError(re);
		}
	}

	private void execute(Stmt statement) {
		statement.accept(this);
	}

	private String stringify(Object object) {
		if (object == null) return "nil";

		if (object instanceof Double) {
			String text = object.toString();
			if (text.endsWith(".0")) {
				text = text.substring(0, text.length() - 2);
			}
			return text;
		}

		return object.toString();
	}

	@Override
	public Object visitAssignExpr(Expr.Assign expr) {
		Object value = evaluate(expr.value);
		environment.assign(expr.name, value);
		return value;
	}

	@Override
	public Object visitBinaryExpr(Binary expr) {
		// [left-token operator right-token]
		Object leftValue = evaluate(expr.left);		// left token evaluated
		Object rightValue = evaluate(expr.right);	// right token evaluated

		switch (expr.operator.type()) {
			case MINUS:
				checkNumberOperands(expr.operator, leftValue, rightValue);
				return (double) leftValue - (double) rightValue;
			case SLASH:
				checkNumberOperands(expr.operator, leftValue, rightValue);
				return (double) leftValue / (double) rightValue;
			case STAR:
				checkNumberOperands(expr.operator, leftValue, rightValue);
				return (double) leftValue * (double) rightValue;
			case PLUS:
				// adding two numbers together.
				if (leftValue instanceof Double && rightValue instanceof Double)
					return (double) leftValue + (double) rightValue;
				// concatenating two strings together.
				if (leftValue instanceof String && rightValue instanceof String)
					return leftValue + (String) rightValue;
				// if neither of the two operands are numbers or strings we raise a runtime error.
				throw new RuntimeError(expr.operator, "Operands must be two numbers or strings.");
            case GREATER:
				checkNumberOperands(expr.operator, leftValue, rightValue);
				return (double) leftValue > (double) rightValue;
			case GREATER_EQUAL:
				checkNumberOperands(expr.operator, leftValue, rightValue);
				return (double) leftValue >= (double) rightValue;
			case LESS:
				checkNumberOperands(expr.operator, leftValue, rightValue);
				return (double) leftValue < (double) rightValue;
			case LESS_EQUAL:
				checkNumberOperands(expr.operator, leftValue, rightValue);
				return (double) leftValue <= (double) rightValue;
			// a != a
			case BANG_EQUAL:
				return !isEqual(leftValue, rightValue);
			case EQUAL_EQUAL:
				return isEqual(leftValue, rightValue);
		}

		// unreachable code...
		return null;
	}

	private void checkNumberOperands(Token operator, Object leftValue, Object rightValue) {
		if (leftValue instanceof Double && rightValue instanceof Double) return;
		throw new RuntimeError(operator, "Operands must be numbers.");
	}

	@Override
	public Object visitGroupingExpr(Grouping expr) {
		return evaluate(expr.expression);
	}

	@Override
	public Object visitLiteralExpr(Literal expr) {
		return expr.value;
	}

	@Override
	public Object visitUnaryExpr(Unary expr) {
		// we evaluate the right operant first.
		var right = evaluate(expr.right);

        return switch (expr.operator.type()) {
            case MINUS -> {
                checkNumberOperand(expr.operator, right);
                yield -(double) right;
            }
            case BANG -> !isTruthy(right);
            default ->
                // this code is unreachable.
                    null;
        };

    }

	@Override
	public Object visitVariableExpr(Expr.Variable expr) {
		return environment.get(expr.name);
	}

	// function to return a runtime error
	private void checkNumberOperand(Token operator, Object operand) {
		if (operand instanceof Double) return;
		throw new RuntimeError(operator, "Operand must be number.");
	}

	// take an expression and recursively evaluate that expression.
	private Object evaluate(Expr expr) {
		return expr.accept(this);
	}

	// function to determine what values are truthy or falsely
	private Boolean isTruthy(Object object) {
		// if the value is null -> return false
		if (object == null) return false;
		// if the value is an instance of the Boolean class -> return the boolean value.
		if (object instanceof Boolean) return (boolean) object;
		// otherwise we return true meaning we encountered a truthy value.
		return true;
	}

	// function to check the equality of two values.
	private Boolean isEqual(Object left, Object right) {
//		if (left == right) return true;
//		if (left == null | right == null) return false;
//		return left.equals(right);
		// shorter way.
		return Objects.equals(left, right);
	}

	@Override
	public Void visitBlockStmt(Stmt.Block stmt) {
		executeBlock(stmt.statements, new Environment(environment));
		return null;
	}

	private void executeBlock(List<Stmt> statements, Environment environment) {
		Environment previous = this.environment;	// the outer scope.
		try {
			this.environment = environment;	// we shadow the outer variables.

			for (Stmt stmt : statements) {
				execute(stmt);
			}
		} finally {
			// we returned to the outer scope.
			this.environment = previous;
		}
	}

	@Override
	public Void visitExpressionStmt(Stmt.Expression stmt) {
		evaluate(stmt.expression);
		return null;
	}

	@Override
	public Void visitPrintStmt(Stmt.Print stmt) {
		Object evaluated = evaluate(stmt.expression);
		System.out.println(stringify(evaluated));
		return null;
	}

	@Override
	public Void visitVarStmt(Stmt.Var stmt) {
		Object value = null;
		if (stmt.initializer != null) {
			value = evaluate(stmt.initializer);
		}

		environment.define(stmt.name.lexeme, value);
		return null;
	}
}
