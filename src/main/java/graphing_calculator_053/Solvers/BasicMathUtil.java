package graphing_calculator_053.Solvers;
import java.util.ArrayList;

import graphing_calculator_053.Tokenizer.Token;
import graphing_calculator_053.Tokenizer.Tokenizer;

public abstract class BasicMathUtil {
    // isNumber is O N L Y for tokenization
    public static boolean isNumber(String input) {
        if (input.contains("0") || input.contains("1") || input.contains("2") || input.contains("3")
                || input.contains("4")
                || input.contains("5") || input.contains("6") || input.contains("7") || input.contains("8")
                || input.contains("9")) {
            return true;
        } else
            return false;
    }

    public static boolean isVariable(Character input) {
        if ((input == 'x') || (input == 'y') || (input == 'z')) {
            return true;
        } else
            return false;
    }

    public static String getTokenMapString(Tokenizer inTokens) {
        ArrayList<Token> tokenMap = inTokens.getTokenList();
        tokenMap = inTokens.getTokenList();
        StringBuilder output = new StringBuilder();
        for (int n = 0; n < tokenMap.size(); n++) {
            if (tokenMap.get(n) != null) {
                output.append(tokenMap.get(n).getTokenString());
            }
        }
        return output.toString();
    }

public static String sanitizationCheck(String input) {
    StringBuilder end = new StringBuilder();
    for (int i = 0; i < input.length(); i++) {
        char current = input.charAt(i);
        end.append(current);

        // Peek ahead to see if need to inject astrisk
        if (i < input.length() - 1) {
            char next = input.charAt(i + 1);

            // Case 1: Digit/Var before Paren -> #( or x(
            boolean openingParen = (next == '(') && (Character.isDigit(current) || isVariable(current));
            // Case 2: Paren before Digit/Var/Paren -> )# or )x or )(
            boolean closingParen = (current == ')') && (Character.isDigit(next) || isVariable(next) || next == '(');
            // Case 3: Digit before Var -> #x
            boolean digitVar = Character.isDigit(current) && isVariable(next);

            if (openingParen || closingParen || digitVar) {
                end.append("*");
            }
        }
    }
    return end.toString();
}

    }
