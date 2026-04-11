package graphing_calculator_051.Solvers;
import java.util.ArrayList;

import graphing_calculator_051.Tokenizer.Token;
import graphing_calculator_051.Tokenizer.Tokenizer;

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
            if (i < input.length() - 1 && ((input.charAt(i + 1) == '(') && Character.isDigit(input.charAt(i))
                    || (input.charAt(i) == ')') && Character.isDigit(input.charAt(i + 1))
                    || (input.charAt(i + 1) == 'x') && Character.isDigit(input.charAt(i))
                    || (Character.isAlphabetic(input.charAt(i + 1))) && Character.isDigit(input.charAt(i)))) {
                end.append(input.charAt(i));
                end.append("*");
                end.append(input.charAt(i + 1));
                i++;
            }
            else {
                end.append(input.charAt(i));}
        }
        return end.toString();
    }

    }
