package core;

import java.util.ArrayList;
import java.util.Stack;

import exception.InvalidSyntaxException;
import expression.And;
import expression.Equals;
import expression.Expression;
import expression.GreaterThan;
import expression.LessThan;
import expression.NotEquals;
import expression.Or;

public class WhereParser {

    public static Expression parseWhere(ArrayList<String> tokens, int init) throws InvalidSyntaxException {
        Stack<Expression> exprStack = new Stack<>();
        Stack<String> opStack = new Stack<>();

        int i = init;
        while (i < tokens.size()) {
            String token = tokens.get(i);

            if (token.equals(";")) break;

            if (token.equals("(")) {
                opStack.push(token);
                i++;
            } else if (token.equals(")")) {
                i = handleClosingParenthesis(exprStack, opStack, i);
            } else if (isLogicalOperator(token)) {
                i = handleLogicalOperator(token, exprStack, opStack, i);
            } else {
                i = handleCondition(tokens, exprStack, i);
            }
        }

        clearRemainingOperators(exprStack, opStack);
        return validateAndGetResult(exprStack);
    }

    private static int handleClosingParenthesis(Stack<Expression> exprStack, Stack<String> opStack, int index) throws InvalidSyntaxException {
        while (!opStack.isEmpty() && !opStack.peek().equals("(")) {
            executeLogicalOperator(exprStack, opStack);
        }
        if (opStack.isEmpty()) throw new InvalidSyntaxException("Erro de sintaxe: Parênteses desalinhados.");
        opStack.pop(); 
        return index + 1;
    }

    private static int handleLogicalOperator(String token, Stack<Expression> exprStack, Stack<String> opStack, int index) throws InvalidSyntaxException {
        String upperOp = token.toUpperCase();
        while (!opStack.isEmpty() && getPrecedence(opStack.peek()) >= getPrecedence(upperOp)) {
            executeLogicalOperator(exprStack, opStack);
        }
        opStack.push(upperOp);
        return index + 1;
    }

    private static boolean isLogicalOperator(String token) {
        String upper = token.toUpperCase();
        return upper.equals("AND") || upper.equals("OR");
    }

    private static int handleCondition(ArrayList<String> tokens, Stack<Expression> exprStack, int index) throws InvalidSyntaxException {
        if (isComparisonExpression(tokens, index)) {
            if (index + 2 >= tokens.size()) {
                throw new InvalidSyntaxException("Expressão de comparação incompleta no WHERE.");
            }
            String column = tokens.get(index);
            String operator = tokens.get(index + 1);
            String value = tokens.get(index + 2).replace("'", "");

            exprStack.push(createComparisonExpression(column, operator, value));
            return index + 3; 
        } 
        
        // Se não for comparação, cai no booleano implícito
        String column = tokens.get(index);
        exprStack.push(new Equals(column, "true"));
        return index + 1;
    }

    private static void clearRemainingOperators(Stack<Expression> exprStack, Stack<String> opStack) throws InvalidSyntaxException {
        while (!opStack.isEmpty()) {
            String topOp = opStack.peek();
            if (topOp.equals("(") || topOp.equals(")")) {
                throw new InvalidSyntaxException("Erro de sintaxe: Parênteses desalinhados.");
            }
            executeLogicalOperator(exprStack, opStack);
        }
    }

    private static Expression validateAndGetResult(Stack<Expression> exprStack) throws InvalidSyntaxException {
        if (exprStack.size() != 1) {
            throw new InvalidSyntaxException("Erro de sintaxe: Expressão do WHERE mal formada.");
        }
        return exprStack.pop();
    }

    private static boolean isComparisonExpression(ArrayList<String> tokens, int index) {
        if (index + 1 >= tokens.size()) return false;
        String nextToken = tokens.get(index + 1);
        return nextToken.equals("=") || nextToken.equals("!=") || nextToken.equals(">") || nextToken.equals("<");
    }

    private static void executeLogicalOperator(Stack<Expression> exprStack, Stack<String> opStack) throws InvalidSyntaxException {
        if (exprStack.size() < 2) {
            throw new InvalidSyntaxException("Erro de sintaxe: Operador lógico sem argumentos suficientes.");
        }

        String operator = opStack.pop();
        Expression right = exprStack.pop(); // O topo da pilha é o lado direito
        Expression left = exprStack.pop();  // O anterior é o lado esquerdo

        if (operator.equals("AND")) {
            exprStack.push(new And(left, right));
        } else if (operator.equals("OR")) {
            exprStack.push(new Or(left, right));
        }
    }

    @SuppressWarnings("unchecked")
    private static Expression createComparisonExpression(String column, String op, String value) throws InvalidSyntaxException {
        switch (op) {
            case "=": return new Equals(column, value);
            case "!=": return new NotEquals(column, value);
            case ">": return new GreaterThan(column, (Comparable<Object>) convertStringToComparable(value));
            case "<": return new LessThan(column, (Comparable<Object>) convertStringToComparable(value));
            default: throw new InvalidSyntaxException("Operador de comparação inválido: " + op);
        }
    }

    private static Comparable<?> convertStringToComparable(String value) throws NumberFormatException {
        if (!value.contains(".")) {
            try {
                return Integer.valueOf(value);
            } catch (Exception e) {
                return Long.valueOf(value);
            }
        } else {
            try {
                return Float.valueOf(value);
            } catch (Exception e) {
                return Double.valueOf(value);
            }
        }
    }

    private static int getPrecedence(String operator) {
        switch (operator) {
            case "AND": return 2;
            case "OR":  return 1;
            default:    return 0; // Parênteses e outros delimitadores
        }
    }
}
