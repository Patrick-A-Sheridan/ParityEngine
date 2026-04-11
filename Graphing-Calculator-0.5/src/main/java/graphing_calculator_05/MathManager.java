package graphing_calculator_05;

import graphing_calculator_05.Solvers.BasicMath;
import graphing_calculator_05.Solvers.FastSolver.BasicMath.DoubleArithmetic;
import graphing_calculator_05.Tokenizer.Tokenizer;

public class MathManager {

    public static String preCheck(String input) {
    char[] splitString = input.toCharArray();
    StringBuilder endString = new StringBuilder();

    for (int i = 0; i < splitString.length; i++) {
        if (splitString[i] == ' ') {
            continue;
        }

        endString.append(splitString[i]);

        if (i + 1 < splitString.length) {
            char current = splitString[i];
            char next = splitString[i + 1];

            if (
                    (Character.isDigit(current) && Character.isLetter(next)) ||
                    (Character.isDigit(current) && next == '(') ||
                    (BasicMath.isVariable(current) && BasicMath.isVariable(next)) ||
                    (BasicMath.isVariable(current) && next == '(') ||
                    (current == ')' && Character.isDigit(next)) ||
                    (current == ')' && Character.isLetter(next)) ||
                    (current == ')' && next == '(')
            ) {
                endString.append('*');
            }
        }
    }

    return endString.toString();
}

public static void run(String input) {
    String TokenIn = MathManager.preCheck(input);
    Tokenizer tokenizer = new Tokenizer(TokenIn);
    tokenizer.tokenize();
    // Parses parentheses
    DoubleArithmetic.parseParentheses(tokenizer);
    // 1. Process Exponents
    if (input.contains("^")) {
        tokenizer.setTokenMap(DoubleArithmetic.parseExponents(tokenizer));
    }

    // 2. Process Mult/Div
    if (input.contains("*") || input.contains("/")) {
        tokenizer.setTokenMap(DoubleArithmetic.parseMultiplicationAndDivision(tokenizer));
    }

    // 3. Process Add/Sub
    if (input.contains("+") || input.contains("-")) {
        tokenizer.setTokenMap(DoubleArithmetic.parseAdditionAndSubtraction(tokenizer));
    }

    // Print Final Result
    System.out.println("Final Equation: " + BasicMath.getTokenMapString(tokenizer));
}
}