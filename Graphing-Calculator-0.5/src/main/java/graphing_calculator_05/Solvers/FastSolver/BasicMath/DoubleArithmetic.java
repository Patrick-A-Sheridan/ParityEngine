package graphing_calculator_05.Solvers.FastSolver.BasicMath;

import graphing_calculator_05.Tokenizer.Tokenizer;
import graphing_calculator_05.Tokenizer.Token;
import java.util.HashMap;
import graphing_calculator_05.Solvers.BasicMath;
public class DoubleArithmetic extends BasicMath {

    public static void parseParentheses(Tokenizer parent) {
        HashMap<Integer, Token> parentMap = new HashMap<>();
        parentMap = parent.getTokenMap();
        //get rightmost left patentheses
        int RightmostLeftParentheses = parent.getLeftParentheses();
        System.out.println(RightmostLeftParentheses);
        int Shift = 1;
        int i = 0;
        int RightParenthesesLocal = -1;
        // go forward collecting tokens until hits right parentheses
        if (parent.getRightParentheses() != null && parentMap.size() != 0) {
            for (int RightParenthesesLocalfinder = RightmostLeftParentheses; RightParenthesesLocalfinder < parentMap
                    .size(); RightParenthesesLocalfinder += 1) {

                if (parentMap.get(RightParenthesesLocalfinder) != null) {
                    if (parentMap.get(RightParenthesesLocalfinder).getTokenType().equals("rightParentheses")) {
                        RightParenthesesLocal = RightParenthesesLocalfinder;
                        break;
                    }
                    i++;
                }
            }
            StringBuilder tempString = new StringBuilder();
            if (RightParenthesesLocal != -1) {
                for (int n = RightmostLeftParentheses; n < RightParenthesesLocal; n++) {
                    if (parentMap.get(n) != null) {
                        tempString.append(parentMap.get(n).getTokenString());
                    }
                }
                // performs standard ops on the group
                Tokenizer tempTokenizer = new Tokenizer(tempString.toString());
                parseExponents(tempTokenizer);
                parseMultiplicationAndDivision(tempTokenizer);
                parseAdditionAndSubtraction(tempTokenizer);
                tempTokenizer.tokenize();
                // replaces stuff in the old map
                for (int m = RightmostLeftParentheses; m < RightParenthesesLocal; m++) {
                    parent.deletetoken(m);
                }
                // adds the parsed tokens to the new map
                for (int l = RightmostLeftParentheses; l < RightParenthesesLocal; l++) {
                    Token tempToken = new Token(tempTokenizer.getTokenMap().get(l).getTokenString(), l, parent);
                    parent.addToken(l, tempToken);
                }
            }
            parent.tokenize();
        }
    }

    public static HashMap<Integer, Token> parseAdditionAndSubtraction(Tokenizer parent) {
        return BasicMath.genericDoubleParser(parent, "+-");
    }

    public static HashMap<Integer, Token> parseMultiplicationAndDivision(Tokenizer parent) {
        return BasicMath.genericDoubleParser(parent, "*/");
    }
    
    public static HashMap<Integer, Token> parseExponents(Tokenizer parent) {
        return BasicMath.genericDoubleParser(parent, "^");
    }
}