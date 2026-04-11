package graphing_calculator_05.Tokenizer;

import java.util.Queue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


public class Tokenizer {
    // constructors

    private String input;
    private char[] brokenInput;
    private HashMap<Integer, Token> tokenMap = new HashMap<>();
    private int i = 0;
    private ArrayList<Integer> additionLocations = new ArrayList<>();
    private ArrayList<Integer> multiplicationLocations = new ArrayList<>();
    private ArrayList<Integer> exponentLocations = new ArrayList<>();
    private Queue<Integer> leftParenthesesLocations = new LinkedList<>();
    private ArrayList<Integer> rightParenthesesLocations= new ArrayList<>();
    public Tokenizer() {
    }

    public Tokenizer(String in) {
        //assigns input and brokenInput respectively
        this.input = in;
        this.brokenInput = input.toCharArray();
    }

public void tokenize() {
    int tokenCount = 0; // NEW: Separate counter for the Map keys
    int charIndex = 0;  // Using charIndex for clarity instead of the class-level 'i'
    
    try {
        while (charIndex < brokenInput.length) {
            // 1. Handle Numbers
            if (Character.isDigit(brokenInput[charIndex])) {
                StringBuilder sb = new StringBuilder(String.valueOf(brokenInput[charIndex]));
                while (brokenInput.length > charIndex + 1
                        && (Character.isDigit(brokenInput[charIndex + 1]) || brokenInput[charIndex + 1] == '.')) {
                    sb.append(brokenInput[charIndex + 1]);
                    charIndex++;
                }
                Token tempToken = new Token(sb.toString(), tokenCount, this);
                tokenMap.put(tokenCount++, tempToken); // Key 0, 1, 2...
                charIndex++;
            }
            // 2. Handle Functions/Variables
            else if (Character.isAlphabetic(brokenInput[charIndex])) {
                StringBuilder sb = new StringBuilder(String.valueOf(brokenInput[charIndex]));
                while (brokenInput.length > charIndex + 1 && (Character.isAlphabetic(brokenInput[charIndex + 1]))) {
                    sb.append(brokenInput[charIndex + 1]);
                    charIndex++;
                }
                Token tempToken = new Token(sb.toString(), tokenCount, this);
                tokenMap.put(tokenCount++, tempToken);
                charIndex++;
            }
            // 3. Handle Operands
            else if ("+-/*^".indexOf(brokenInput[charIndex]) != -1) {
                String operandString = String.valueOf(brokenInput[charIndex]);
                
                // Syntax check
                if (brokenInput.length > charIndex + 1 && "+/*^".indexOf(brokenInput[charIndex + 1]) != -1) {
                    throw new NumberFormatException("Error: Invalid Syntax");
                }

                Token tempToken = new Token(operandString, tokenCount, this);
                tokenMap.put(tokenCount, tempToken);

                // Record locations using the token index
                if (brokenInput[charIndex] == '+') additionLocations.add(tokenCount);
                if (brokenInput[charIndex] == '*') multiplicationLocations.add(tokenCount);
                if (brokenInput[charIndex] == '-') additionLocations.add(tokenCount);
                if (brokenInput[charIndex] == '/') multiplicationLocations.add(tokenCount);
                if (brokenInput[charIndex] == '^') exponentLocations.add(tokenCount);
                if (brokenInput[charIndex] == '(') leftParenthesesLocations.add(tokenCount);
                if (brokenInput[charIndex] == ')') rightParenthesesLocations.add(tokenCount);
                tokenCount++;
                charIndex++;
            } 
            // 4. Handle Parentheses
            else if (brokenInput[charIndex] == '(' || brokenInput[charIndex] == ')') {
                Token tempToken = new Token(String.valueOf(brokenInput[charIndex]), tokenCount, this);
                tokenMap.put(tokenCount++, tempToken);
                charIndex++;
            }
            // 5. Handle Whitespace
            else if (brokenInput[charIndex] == ' ') {
                charIndex++;
            } else {
                throw new NumberFormatException("Error: Invalid Syntax");
            }
        }
        // Sync the class 'i' with the total number of tokens found
        this.i = tokenCount; 
        
    } catch (NumberFormatException e) {
        System.out.println(e.getMessage());
    }
}

    public HashMap<Integer, Token> getTokenMap() {
        return tokenMap;
    }

    public int getTokenMapLength() {
        return i;
    }

    public void setTokenMap(HashMap<Integer, Token> inTokenMap) {
        this.tokenMap = inTokenMap;
    }

    public ArrayList<Integer> getAdditionLocations() {
        return additionLocations;
    }

    public ArrayList<Integer> getMultiplicationLocations() {
        return multiplicationLocations;
    }

    public ArrayList<Integer> getExponentLocations() {
        return exponentLocations;
    }

    public int getLeftParentheses() {
        if (leftParenthesesLocations.peek() != null) {
            return leftParenthesesLocations.poll();
        } else {
            return -1;
        }
    }

    public ArrayList<Integer> getRightParentheses() {
        return rightParenthesesLocations;
    }
    public void setAdditionLocations(ArrayList<Integer> input) {
        additionLocations = input;
    }

    public void setMultiplicationLocations(ArrayList<Integer> input) {
        multiplicationLocations = input;
    }
   public void setExponentLocations(ArrayList<Integer> input) {
        exponentLocations = input;
    }

    public void deletetoken(int in) {
        tokenMap.remove(in);
    }

    public void addToken(int num, Token in) {
        tokenMap.put(num, in);
    }
    public void printTokens() {
        for (var i = 0; i < this.getTokenMapLength(); i++) {
            if (this.getTokenMap().get(i) == null) {
                continue;
            } else {
                System.out.println("Token#" + i + " Has a value of: " + this.getTokenMap().get(i).getTokenString());
            }
        }
    }
        public void setTokenString(String in) {
        this.input = in;
        this.brokenInput = input.toCharArray();
    }
}