import java.math.BigDecimal;
import java.math.MathContext;

public class Complex_Parser {
    public static String Master_Solve(String input) {
        double val = 0.0;
        String num1 = Basic_Parser.remove_spaces(input);
        num1 = Basic_Parser.remove_doubleNegatives(num1);
        num1 = Complex_Parser.Parse_Parenthesis_Operations(num1);
        num1 = Basic_Parser.remove_doubleNegatives(num1);
        num1 = Complex_Parser.Parse_Parenthesis(num1);
        num1 = Basic_Parser.remove_doubleNegatives(num1);
        num1 = Complex_Parser.Simplify_Equation(num1);
        num1 = Basic_Parser.remove_doubleNegatives(num1);
        num1 = Complex_Parser.toPlain(num1);

        // Attempt to round numeric-only result
    try {
        val = Double.parseDouble(num1);
    } catch (NumberFormatException e) {
    }
    return num1;
}
public static String toPlain(String input) {
    try {
        // parse string as double
        double val = Double.parseDouble(input);

        // convert to BigDecimal with max precision to avoid rounding/scientific notation
        BigDecimal bd = new BigDecimal(val, MathContext.DECIMAL64)
                            .stripTrailingZeros();

        String out = bd.toPlainString();

        // prevent "-0"
        return out.equals("-0") ? "0" : out;
    } catch (NumberFormatException e) {
        // not a pure number, leave as-is
        return input;
    }
}


public static String Simplify_Equation(String input) {
        String num1 = Basic_Parser.remove_spaces(input);
        num1 = Basic_Parser.remove_doubleNegatives(num1);
        num1 = Basic_Parser.parse_Exponents(num1);
                    num1 = Basic_Parser.remove_doubleNegatives(num1);
                    num1 = Basic_Parser.parse_multiplication(num1);
                    num1 = Basic_Parser.remove_doubleNegatives(num1);
                    num1 = Basic_Parser.parse_addition(num1);
                    num1 = Basic_Parser.remove_doubleNegatives(num1);
        return num1;
    }
    public static String Parse_Parenthesis_Operations(String input) {
        String Output = input;
        // finds where the equation has "#(" or ")("
        for (int i = 0; i < Output.length()-1; i++) {
            if (Character.isDigit(Output.charAt(i)) && (Output.charAt(i + 1) == ('(')) || ((Output.charAt(i) == (')')) && (Output.charAt(i + 1) == ('(')))
            
            ) {
                Output = Output.substring(0, i + 1) + "*" + Output.substring(i + 1, Output.length());
                i = 0;
            }
        }
        return Output;
    }

    public static String Parse_Parenthesis(String input) {
        String Output = input;
        String innerTerms = "Error";
        // Finds first parenthasis with number inside
        int First_Parenthasis = 0;
        int Endindex = 0;
        // checks if next parenthasis is '('
        while (Output.contains("(")) {
            for (int i = Output.indexOf("("); i < Output.length(); i++) {
                if (Output.charAt(Endindex) == '(') {
                    First_Parenthasis = Endindex + 1;
                    Endindex++;
                } else if (Output.charAt(Endindex) == ')') {
                    // finds substring of inner terms
                    innerTerms = Output.substring(First_Parenthasis, Endindex);
                    // simplifies inner terms
                    innerTerms = Simplify_Equation(innerTerms);
                    // reinserts inner terms with removing parenthasis

                    Output = Output.substring(0, First_Parenthasis - 1) + innerTerms
                            + Output.substring(Endindex + 1, Output.length());
                    innerTerms = "Error";
                    First_Parenthasis = 0;
                    Endindex = 0;
                } else {
                    Endindex++;
                }
            }
        }
        return Output;
    }
}
