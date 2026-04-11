package graphing_calculator_051.Solvers.FastSolver.Manipulators;

import graphing_calculator_051.Solvers.FastSolver.BasicMath.AdditionNode;
import graphing_calculator_051.Solvers.FastSolver.BasicMath.DivisionNode;
import graphing_calculator_051.Solvers.FastSolver.BasicMath.ExponentNode;
import graphing_calculator_051.Solvers.FastSolver.BasicMath.MultiplicationNode;
import graphing_calculator_051.Solvers.FastSolver.BasicMath.SubtractionNode;
import graphing_calculator_051.Solvers.FastSolver.Types.*;
import graphing_calculator_051.Tokenizer.Token;
import java.util.ArrayList;

public class ASTParser {
    private final ArrayList<Token> tokens;
    public ASTParser(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    int index = 0;
    

    
    public BaseNode parseExponents() {
        if (index >= tokens.size()) {
            throw new IllegalStateException("Unexpected end of input");
        }
        BaseNode node = parseTerm();
        while (index < tokens.size() && (tokens.get(index).getTokenType().equals("exponentiate"))) {
            index++; // skip '+'
            BaseNode right = parseExponents();
            node = new ExponentNode(node, right);
        }
        return node;
    }


    public BaseNode parseMultiplication() {
        if (index >= tokens.size()) {
    throw new IllegalStateException("Unexpected end of input");
}
        BaseNode node = parseExponents();
        while (index < tokens.size() && (tokens.get(index).getTokenType().equals("multiply") || tokens.get(index).getTokenType().equals("divide"))) {
            int opIndex = index;
            index++; // skip '+'
            BaseNode right = parseExponents();
            switch (tokens.get(opIndex).getTokenType()) {
                case "multiply" ->  {node = new MultiplicationNode(node, right);}
            case "divide" -> {node = new DivisionNode(node, right);}
        }
    }
        return node;
    }

    public BaseNode parseAddition() {
        if (index >= tokens.size()) {
    throw new IllegalStateException("Unexpected end of input");
}
        BaseNode node = parseMultiplication();
        while (index < tokens.size() && (tokens.get(index).getTokenType().equals("plus")
                || tokens.get(index).getTokenType().equals("subtract"))) {
            int opIndex = index;
            index++; // skip '+'
            BaseNode right = parseMultiplication();
            switch (tokens.get(opIndex).getTokenType()) {
                case "plus" ->  {node = new AdditionNode(node, right);}
            case "subtract" -> {node = new SubtractionNode(node, right);}
            }
        }
        return node;
    }

    
private BaseNode parseTerm() {
    Token token = tokens.get(index);

    switch (token.getTokenType()) {
        case "Number" -> {
            index++;
            return new NumberNode(Double.parseDouble(token.getTokenString()));
        }
        case "leftParentheses" -> {
            index++; // skip '('
            BaseNode inside = parseAddition();
            if (index >= tokens.size() || !tokens.get(index).getTokenType().equals("rightParentheses")) {
                throw new IllegalStateException("Missing closing parenthesis");
            }
            index++; // skip ')'
            return inside;
        }
        default -> throw new IllegalStateException("Unexpected token: " + token.getTokenString());
    }
}

}
