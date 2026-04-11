package graphing_calculator_05.Solvers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import graphing_calculator_05.Tokenizer.Tokenizer;
import graphing_calculator_05.Tokenizer.Token;

public abstract class BasicMath {
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
        Map<Integer, Token> tokenMap = inTokens.getTokenMap();
        tokenMap = inTokens.getTokenMap();
        StringBuilder output = new StringBuilder();
        for (int n = 0; n < tokenMap.size(); n++) {
            if (tokenMap.get(n) != null) {
                output.append(tokenMap.get(n).getTokenString());
            }
        }
        return output.toString();
    }

public static HashMap<Integer, Token> genericDoubleParser(Tokenizer parent, String inOp) {
        HashMap<Integer, Token> parentMap = parent.getTokenMap();
        double result = 0.0;
        ArrayList<Integer> ops = new ArrayList<>();
        
        if (inOp.equals("+-")) {
            ops = new ArrayList<>(parent.getAdditionLocations());
        } else if (inOp.equals("*/")) {
            ops = new ArrayList<>(parent.getMultiplicationLocations());
        } else if (inOp.equals("^")) {
            ops = new ArrayList<>(parent.getExponentLocations()); 
        }

        int shift = 0;
        try {
            for (int p = 0; p < ops.size(); p++) {
                int opsIndex = ops.get(p) - shift;
                int leftDistance = 1;
                int rightDistance = 1;
                Token left = null;
                Token right = null;

                while (left == null && (opsIndex - leftDistance) >= 0) {
                    left = parentMap.get(opsIndex - leftDistance);
                    if (left == null) leftDistance++;
                }

                while (right == null && (opsIndex + rightDistance) < parent.getTokenMapLength()) {
                    right = parentMap.get(opsIndex + rightDistance);
                    if (right == null) rightDistance++;
                }

                Token op = parentMap.get(opsIndex);

                // Ensure all parts exist and match the current operation mode
                if (right != null && left != null && op != null) {
                    boolean match = false;

                    if (inOp.equals("*/")) {
                        if (op.getTokenType().equals("multiply")) {
                            result = Double.parseDouble(left.getTokenString()) * Double.parseDouble(right.getTokenString());
                            match = true;
                        } else if (op.getTokenType().equals("divide")) {
                            result = Double.parseDouble(left.getTokenString()) / Double.parseDouble(right.getTokenString());
                            match = true;
                        }
                    } else if (inOp.equals("+-")) {
                        if (op.getTokenType().equals("plus")) {
                            result = Double.parseDouble(left.getTokenString()) + Double.parseDouble(right.getTokenString());
                            match = true;
                        } else if (op.getTokenType().equals("subtract")) {
                            result = Double.parseDouble(left.getTokenString()) - Double.parseDouble(right.getTokenString());
                            match = true;
                        }
                    } else if (inOp.equals("^") && op.getTokenType().equals("exponentiate")) {
                        result = Math.pow(Double.parseDouble(left.getTokenString()), Double.parseDouble(right.getTokenString()));
                        match = true;
                    }

                    if (match) {
                        Token temp = new Token(String.valueOf(result), opsIndex, parent);
                        HashMap<Integer, Token> newMap = new HashMap<>();
                        int write = 0;

                        for (int read = 0; read < parent.getTokenMapLength(); read++) {
                            if (read == opsIndex - leftDistance) {
                                newMap.put(write++, temp);
                                read = opsIndex + rightDistance;
                            } else {
                                Token t = parentMap.get(read);
                                if (t != null) {
                                    newMap.put(write++, t);
                                }
                            }
                        }
                        parent.setTokenMap(newMap);
                        parentMap = newMap;
                        shift += leftDistance + rightDistance;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Parser Error: " + e.getMessage());
        }

        parent.setAdditionLocations(new ArrayList<>());
        parent.setTokenString(BasicMath.getTokenMapString(parent));
        parent.setTokenString(BasicMath.getTokenMapString(parent));
        parent.tokenize();
        return parentMap;
    }
}
