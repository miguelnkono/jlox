package evaluate;

import ast.Expr;
import ast.Expr.Binary;
import ast.Expr.Grouping;
import ast.Expr.Literal;
import ast.Expr.Unary;

public class Interpretor implements Expr.Visitor<Object> {

	@Override
	public Object visitBinaryExpr(Binary expr) {
		// TODO Auto-generated method stub
		return null;
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
		var right = evaluate(expr.right);
		
		switch(expr.operator.type()) {
			case MINUS:
				return -(double)right;
			case BANG:
				return !isTruthy(right);
		}
		
		return null;
	}
	
	// take an expression and recursively evaluate that expression.
	private Object evaluate(Expr expr) {
		return expr.accept(this);
	}

}
