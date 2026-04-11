import java.math.BigDecimal;

public class Basic_Parser {
    public static String remove_spaces(String input) {
        String endString = input.replaceAll(" ", "");
        return endString;
    }
        public static String remove_doubleNegatives(String input) {
            String endString = input.replaceAll("--", "");
            endString = endString.replaceAll("~-", "+");
            endString = endString.replaceAll("E", "*10^");
        return endString;
    }
    public static String parse_addition(String input) {
        String num1_String = "";
        String num2_String = "";
        String tempString = "";
        String endString = input;
        double endNum = 0.0;
        int i = 0;
        while (endString.contains("+") || endString.contains("~")) {
            if (i >= endString.length() - 1) {
                i = 0;
            }
            if (endString.charAt(i) == '+') {
                int decrement = i - 1;
                int increment = i + 1;
                num1_String = "";
                num2_String = "";
                tempString = "";
                endNum = 0.0;
                while (decrement >= 0
                        && (Character.isDigit(endString.charAt(decrement)) || endString.charAt(decrement) == '.'
                                || endString.charAt(decrement) == '-')) {

                    num1_String = num1_String + endString.charAt(decrement);

                    decrement--;
                }
                //reverses num1_String
                for (int j = 0; j < num1_String.length(); j++) {
                    tempString = num1_String.substring(j, j + 1).concat(tempString);
                }
                num1_String = tempString;
                //locates right number
                while ((increment <= endString.length() - 1)
                        && (Character.isDigit(endString.charAt(increment)) || endString.charAt(increment) == '.'
                                || endString.charAt(increment) == '-')) {

                    num2_String = num2_String + endString.charAt(increment);
                    increment++;
                }
                // adds the two numbers as doubles
                if (num1_String.equals("") && num2_String.equals("")) {
                    endNum = 0;
            }
                
                if (num1_String.equals("") && !num2_String.equals("")) {
                    endNum = Double.parseDouble(num2_String);
            }
                if (!num1_String.equals("") && num2_String.equals("")) {
                    endNum = Double.parseDouble(num1_String);
                }
                if (!num1_String.equals("") && !num2_String.equals("")) {
                    endNum = Double.parseDouble(num1_String) + Double.parseDouble(num2_String);
                }

                // reinserts double into string
                endString = endString.substring(0, decrement + 1)
                        + endNum +
                        endString.substring(increment, endString.length());

            }
            if (i >= endString.length() - 1) {
                i = 0;
            }
            if (endString.substring(i, i + 1).equals("~")) {
                int decrement = i - 1;
                int increment = i + 1;
                num1_String = "";
                num2_String = "";
                tempString = "";
                endNum = 0.0;
                while (decrement >= 0
                        && (Character.isDigit(endString.charAt(decrement)) || endString.charAt(decrement) == '.'
                                || endString.charAt(decrement) == '-')) {

                    num1_String = num1_String + endString.charAt(decrement);
                    decrement--;
                }
                //reverses num1_String
                for (int j = 0; j < num1_String.length(); j++) {
                    tempString = num1_String.substring(j, j + 1).concat(tempString);
                }
                num1_String = tempString;
                //locates right number
                while ((increment <= endString.length() - 1)
                        && (Character.isDigit(endString.charAt(increment)) || endString.charAt(increment) == '.'
                                || endString.charAt(increment) == '-')) {

                    num2_String = num2_String + endString.charAt(increment);
                    increment++;
                }
                // subtracts the two numbers as doubles
                endNum = Double.parseDouble(num1_String) - Double.parseDouble(num2_String);
                // reinserts double into string
                endString = endString.substring(0, decrement + 1)
                        + endNum +
                        endString.substring(increment, endString.length());
            }
            i++;
        }
        // returns
        return endString;
    }
    public static String parse_multiplication(String input) {
        String num1_String = "";
        String num2_String = "";
        String tempString = "";
        String endString = input;
        double endNum = 0.0;
        int i = 0;
        while (endString.contains("*") || endString.contains("/")) {
            if (i >= endString.length() - 1) {
                i = 0;
            }
            if (endString.charAt(i) == '*') {
                int decrement = i - 1;
                int increment = i + 1;
                num1_String = "";
                num2_String = "";
                tempString = "";
                endNum = 0.0;
                while (decrement >= 0
                        && (Character.isDigit(endString.charAt(decrement)) || endString.charAt(decrement) == '.'
                                || endString.charAt(decrement) == '-')) {

                    num1_String = num1_String + endString.charAt(decrement);

                    decrement--;
                }
                //reverses num1_String
                for (int j = 0; j < num1_String.length(); j++) {
                    tempString = num1_String.substring(j, j + 1).concat(tempString);
                }
                num1_String = tempString;
                //locates right number
                while ((increment <= endString.length() - 1)
                        && (Character.isDigit(endString.charAt(increment)) || endString.charAt(increment) == '.'
                                || endString.charAt(increment) == '-')) {

                    num2_String = num2_String + endString.charAt(increment);
                    increment++;
                }
                // multiplies the two numbers as doubles
                endNum = Double.parseDouble(num1_String) * Double.parseDouble(num2_String);

                // reinserts double into string
                endString = endString.substring(0, decrement + 1)
                        + endNum +
                        endString.substring(increment, endString.length());

            }
            if (i >= endString.length() - 1) {
                i = 0;
            }
            if (endString.charAt(i) == '/') {
                int decrement = i - 1;
                int increment = i + 1;
                num1_String = "";
                num2_String = "";
                tempString = "";
                endNum = 0.0;
                while (decrement >= 0
                        && (Character.isDigit(endString.charAt(decrement)) || endString.charAt(decrement) == '.'
                                || endString.charAt(decrement) == '-')) {

                    num1_String = num1_String + endString.charAt(decrement);
                    decrement--;
                }
                //reverses num1_String
                for (int j = 0; j < num1_String.length(); j++) {
                    tempString = num1_String.substring(j, j + 1).concat(tempString);
                }
                num1_String = tempString;
                //locates right number
                while ((increment <= endString.length() - 1)
                        && (Character.isDigit(endString.charAt(increment)) || endString.charAt(increment) == '.'
                                || endString.charAt(increment) == '-')) {

                    num2_String = num2_String + endString.charAt(increment);
                    increment++;
                }
                // divides the two numbers as doubles
                endNum = Double.parseDouble(num1_String) / Double.parseDouble(num2_String);
                // reinserts double into string
                endString = endString.substring(0, decrement + 1)
                        + endNum +
                        endString.substring(increment, endString.length());
            }
            i++;
        }
        // returns
        return endString;
    }
    public static String parse_Exponents(String input) {
        String num1_String = "";
        String num2_String = "";
        String tempString = "";
        String endString = input;
        double endNum = 0.0;
        int i = endString.length()-1;
        while (endString.contains("^")) {
            if (i < 0) {
                i = endString.length() - 1;
            }
            if (i >= endString.length()) {
                
      i = endString.length() - 1;
            }
            if (endString.charAt(i) == '^') {
                int decrement = i - 1;
                int increment = i + 1;
                num1_String = "";
                num2_String = "";
                tempString = "";
                endNum = 0.0;
                while (decrement >= 0
                        && (Character.isDigit(endString.charAt(decrement)) || endString.charAt(decrement) == '.' || endString.charAt(decrement) == '-'
                                )) {

                    num1_String = num1_String + endString.charAt(decrement);
                    
                    decrement--;
                }
                //reverses num1_String
                for (int j = 0; j < num1_String.length(); j++) {
                    tempString = num1_String.substring(j, j + 1).concat(tempString);
                }
                num1_String = tempString;
                //locates right number
                while ((increment <= endString.length() - 1)
                        && (Character.isDigit(endString.charAt(increment)) || endString.charAt(increment) == '.'
                                || endString.charAt(increment) == '-')) {
                    num2_String = num2_String + endString.charAt(increment);
                    increment++;
                }
                // multiplies the two numbers as doubles
                endNum = Math.pow(Double.parseDouble(num1_String), Double.parseDouble(num2_String));
                // reinserts double into string
String result = new BigDecimal(Double.toString(endNum))
                    .stripTrailingZeros()
                    .toPlainString();

endString = endString.substring(0, decrement + 1)
        + result
        + endString.substring(increment);
            }
                        i--;
        }
// returns
            return endString;
    }
}