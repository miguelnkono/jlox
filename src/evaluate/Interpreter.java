package evaluate;

import ast.Expr;
import ast.Expr.Binary;
import ast.Expr.Grouping;
import ast.Expr.Literal;
import ast.Expr.Unary;
import scanner.Token;
import lox.Main;

public class Interpreter implements Expr.Visitor<Object> {

	/*
	* Function to interpret the ast.
	* */
	public void interpret(Expr expr) {
		try {
			// evaluate the expression.
			Object value = evaluate(expr);
			// printing the result to the user screen.
			System.out.println(stringify(value));
		} catch (RuntimeError re) {
			Main.runtimeError(re);
		}
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

	@SuppressWarnings("incomplete-switch")
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
					return (String) leftValue + (String) rightValue;
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

	@SuppressWarnings("incomplete-switch")
	@Override
	public Object visitUnaryExpr(Unary expr) {
		// we evaluate the right operant first.
		var right = evaluate(expr.right);

		switch (expr.operator.type()) {
			case MINUS:
				checkNumberOperand(expr.operator, right);
				return -(double) right;
			case BANG:
				return !isTruthy(right);
		}

		// this code is unreachable.
		return null;
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
		if (left == null && right == null) return true;
		if (left == null) return false;
		return true;
	}

}
