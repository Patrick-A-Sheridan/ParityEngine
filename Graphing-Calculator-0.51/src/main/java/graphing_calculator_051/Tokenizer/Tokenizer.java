package graphing_calculator_051.Tokenizer;

import java.util.ArrayList;

public class Tokenizer {
    // constructors

    private String input;
    private char[] brokenInput;
    private ArrayList<Token> tokenList = new ArrayList<>();
    public Tokenizer(String in) {
        // assigns input and brokenInput respectively
        this.input = in;
        this.brokenInput = input.toCharArray();
    }
    public void tokenize() {
        int tokenCount = 0;
        int charIndex = 0;

        // optional reset so repeated tokenize() calls do not append forever
        tokenList = new ArrayList<>();
        try {
            while (charIndex < brokenInput.length) {
                // 1. Handle Numbers
                if (Character.isDigit(brokenInput[charIndex]) || brokenInput[charIndex] == '.') {
                    StringBuilder sb = new StringBuilder(String.valueOf(brokenInput[charIndex]));
                    while (brokenInput.length > charIndex + 1
                            && (Character.isDigit(brokenInput[charIndex + 1]) || brokenInput[charIndex + 1] == '.')) {
                        sb.append(brokenInput[charIndex + 1]);
                        charIndex++;
                    }

                    Token tempToken = new Token(sb.toString(), tokenCount);
                    tokenList.add(tempToken);
                    tokenCount++;
                    charIndex++;
                }
                // 2. Handle Functions/Variables
                else if (Character.isAlphabetic(brokenInput[charIndex])) {
                    StringBuilder sb = new StringBuilder(String.valueOf(brokenInput[charIndex]));
                    while (brokenInput.length > charIndex + 1 && Character.isAlphabetic(brokenInput[charIndex + 1])) {
                        sb.append(brokenInput[charIndex + 1]);
                        charIndex++;
                    }

                    Token tempToken = new Token(sb.toString(), tokenCount);
                    tokenList.add(tempToken);
                    tokenCount++;
                    charIndex++;
                }
                // 3. Handle Operands
                else if ("+-/*^".indexOf(brokenInput[charIndex]) != -1) {
                    String operandString = String.valueOf(brokenInput[charIndex]);
 boolean isMinus = false;
                    if (brokenInput[charIndex] == '-' && (tokenList.size() > 0)) {
                        if(tokenList.get(tokenCount-1).getTokenType().equals("Number") || tokenList.get(tokenCount-1).getTokenType().equals("rightParentheses") || tokenList.get(tokenCount-1).getTokenType().equals("independentVariable") || tokenList.get(tokenCount-1).getTokenType().equals("dependentVariable"))
                            isMinus = true;
                    }

                        Token tempToken = new Token(operandString, tokenCount, isMinus);
                        tokenList.add(tempToken);
                        tokenCount++;
                        charIndex++;
                }
                // 4. Handle Parentheses
                else if (brokenInput[charIndex] == '(' || brokenInput[charIndex] == ')') {
                    Token tempToken = new Token(String.valueOf(brokenInput[charIndex]), tokenCount);
                    tokenList.add(tempToken);
                    tokenCount++;
                    charIndex++;
                }
                // 5. Handle Whitespace
                else if (brokenInput[charIndex] == ' ') {
                    charIndex++;
                } else {
                    throw new NumberFormatException("Error: Invalid Syntax");
                }
            }

        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<Token> getTokenList() {
        return tokenList;
    }

    public int getTokenListLength() {
        return tokenList.size();
    }

    public void setTokenList(ArrayList<Token> inTokenList) {
        this.tokenList = inTokenList;
    }

    public void printTokens() {
        for (var i = 0; i < this.getTokenListLength(); i++) {
            if (this.getTokenList().get(i) == null) {
                continue;
            } else {
                System.out.println("Token#" + i + " is a " + this.getTokenList().get(i).getTokenType() + " and has a value of: " + this.getTokenList().get(i).getTokenString());
            }
        }
    }
    public void setTokenString(String in) {
        this.input = in;
        this.brokenInput = input.toCharArray();
    }
}