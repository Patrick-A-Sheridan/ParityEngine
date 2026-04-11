package graphing_calculator_05.Tokenizer;
import graphing_calculator_05.Solvers.BasicMath;

public class Token {
    private String tokenString;
    private String type;
      private Tokenizer parent;
    int objectkey;

    public Token(String input, int key, Tokenizer creator) {
    this.parent = creator;
    this.tokenString = input;
    this.objectkey = key;
try{
    switch (tokenString) {
        case "sin", "tan", "cos" -> this.type = "trigFunction";
        case "cot", "csc", "sec" -> this.type = "reciprocalTrigFunction";
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
            if (key == 0) {
                this.type = "negativeSign";
            } else {
                String prevType = parent.getTokenMap().get(key - 1).getTokenType();
                this.type = switch (prevType) {
                    case "leftParentheses", "divide", "exponentiate" -> "negativeSign";
                    case "Number", "rightParentheses", "independentVariable", "dependentVariable" -> "subtract";
                    default -> this.type; // Keep current if no match
                };
            }
        }
        default -> {
            if (BasicMath.isNumber(tokenString)) {
                this.type = "Number";
            }
            
        }
    }
}
    catch (NumberFormatException e)
    {
         System.out.println(e.getMessage());
    }
}

public String getTokenString() {
    return tokenString;
}
    
   public String getTokenType(){
        return type;
    }


}