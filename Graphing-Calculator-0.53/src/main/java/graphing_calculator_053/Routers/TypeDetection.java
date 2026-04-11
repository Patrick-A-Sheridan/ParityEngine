package graphing_calculator_053.Routers;
import javax.management.relation.InvalidRelationIdException;

import graphing_calculator_053.Identification;

public class TypeDetection {

    String input = "";
    String StartID;
    StringBuilder endID = new StringBuilder();

    TypeDetection(String input, Identification ID) {
    Identification CheckerID = new Identification(ID.ID());
        this.input = input;
        this.StartID = CheckerID.ID();
    }
// helper method for checking when the input came from graphing
    private String graphCheck() {
        if (input.startsWith("y=")) {
            endID.append("StandardCartesian");
            return endID.toString();
        } else if (input.contains("=") && input.contains("x")) {
            endID.append("ImplicitCartesian");
            return endID.toString();
        } else if (input.startsWith("r=")) {
            endID.append("StandardPolar");
            return endID.toString();
        } else if (input.contains("r")) {
            endID.append("NonStandardPolar");
        } else if (input.contains("theta")) {
            endID.append("ImplicitPolar");
            return endID.toString();
        } else if (input.contains("t")) {
            endID.append("Parametric");
            return endID.toString();
        }
        else if (!input.contains("=") && input.contains("x")){
        endID.append("Expression");
    return endID.toString();}

            endID.append("NumericalExpression");
            return endID.toString();
        }
    
// detect the type of function inputted and return according to type
    public String detect(String input, Identification ID) {
        try{
        endID.delete(0, endID.length());
        switch (this.StartID) {
            case "CF" -> {
                endID.append("CF");
            }
            case "CP" -> {
                endID.append("CP");

            }
            case "GF" -> {
                endID.append("GF");
                return graphCheck();
            }
            case "GP" -> {
                endID.append("GP");
                return graphCheck();
            }
            default -> {
                throw new InvalidRelationIdException("Command in detector has no Return Type");
            }

        }
    }
    catch(InvalidRelationIdException e){System.out.println(e);}
    
        return endID.toString();
    }
}