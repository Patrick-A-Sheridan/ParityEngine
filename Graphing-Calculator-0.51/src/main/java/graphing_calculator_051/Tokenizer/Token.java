package graphing_calculator_051.Tokenizer;
import graphing_calculator_051.Solvers.BasicMathUtil;

public class Token {
    private String tokenString;
    private String type;
    int objectkey;
    boolean isMinus = false;

    public Token(String input, int key, boolean inMinus) {
        this.isMinus = inMinus;
        this.tokenString = input;
        this.objectkey = key;
        this.identifyToken();
    }
    public Token(String input, int key) {
        this.tokenString = input;
        this.objectkey = key;
        this.identifyToken();
    }
    public void identifyToken(){
        try {
            switch (tokenString) {
                case "sin", "tan", "cos" -> this.type = "trigFunction";
                case "cot", "csc", "sec" -> this.type = "recipricoltrigFunction";
                case "log" -> this.type = "logarithm";
                case "(" -> this.type = "leftParentheses";
                case ")" -> this.type = "rightParentheses";
                case "x" -> this.type = "independentVariable";
                case "y" -> this.type = "dependentVariable";
                case "+" -> this.type = "plus";
                case "*" -> this.type = "multiply";
                case "/" -> this.type = "divide";
                case "^" -> this.type = "exponentiate";

                case "-" -> {
                    if (isMinus) {
                        this.type = "subtract";
                    } else {
                        this.type =  "negativeSign";
                        };
                    }

                default -> {
                    if (BasicMathUtil.isNumber(tokenString)) {
                        this.type = "Number";
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }
    public String getTokenString() {
        return tokenString;
    }

    public String getTokenType() {
        return type;
    }
}