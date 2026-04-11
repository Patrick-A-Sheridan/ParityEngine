
public class Parser {

    public static String parse_addition(String input) {
        String num1_String = "";
        String num2_String = "";
        String tempString = "";
        String endString = input;
        double endNum = 0.0;
        // locates addition and numbers to side of it
        //locates left number
        do {
            for (int i = 0; i <= endString.length() - 1; i++) {

                if (endString.charAt(i) == '+') {
                int decrement = i-1;
                int increment = i + 1;
                num1_String = "";
                num2_String = "";
                tempString = "";
                endNum = 0.0;

                    while (decrement >= 0
                            && (Character.isDigit(endString.charAt(decrement)) || endString.charAt(decrement) == '.')) {

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
                            && (Character.isDigit(endString.charAt(increment)) || endString.charAt(increment) == '.')) {

                        num2_String = num2_String + endString.charAt(increment);
                        increment++;
                    } 

                    // adds the two numbers as doubles
                    endNum = Double.parseDouble(num1_String) + Double.parseDouble(num2_String);

                    // reinserts double into string
                    endString = endString.substring(0, decrement + 1)
                            + endNum +
                            endString.substring(increment, endString.length());

                }
  if (endString.charAt(i) == '-') {
                int decrement = i-1;
                int increment = i + 1;
                num1_String = "";
                num2_String = "";
                tempString = "";
                endNum = 0.0;

                    while (decrement >= 0
                            && (Character.isDigit(endString.charAt(decrement)) || endString.charAt(decrement) == '.')) {

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
                            && (Character.isDigit(endString.charAt(increment)) || endString.charAt(increment) == '.')) {

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
            }

        } while (endString.contains("+") || endString.contains("-"));
        // returns
        System.out.println();
        return endString;
    }


    public static String parse_multiplication(String input) {
        String num1_String = "";
        String num2_String = "";
        String tempString = "";
        String endString = input;
        double endNum = 0.0;
        // locates addition and numbers to side of it
        //locates left number
        do {
            for (int i = 0; i <= endString.length() - 1; i++) {
               
                if (endString.charAt(i) == '*') {
                int decrement = i-1;
                int increment = i + 1;
                num1_String = "";
                num2_String = "";
                tempString = "";
                endNum = 0.0;

                    while (decrement >= 0
                            && (Character.isDigit(endString.charAt(decrement)) || endString.charAt(decrement) == '.')) {

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
                            && (Character.isDigit(endString.charAt(increment)) || endString.charAt(increment) == '.')) {

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
  if (endString.charAt(i) == '/') {
                int decrement = i-1;
                int increment = i + 1;
                num1_String = "";
                num2_String = "";
                tempString = "";
                endNum = 0.0;

                    while (decrement >= 0
                            && (Character.isDigit(endString.charAt(decrement)) || endString.charAt(decrement) == '.')) {

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
                            && (Character.isDigit(endString.charAt(increment)) || endString.charAt(increment) == '.')) {

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
            }

        } while (endString.contains("*") || endString.contains("/"));
        // returns
        System.out.println();
        return endString;
    }
}