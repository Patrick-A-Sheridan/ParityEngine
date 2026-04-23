package graphing_calculator_053.Solvers;

import graphing_calculator_053.Solvers.ElementaryMath.*;
import graphing_calculator_053.Solvers.FastSolver.FastNumberNode;
import graphing_calculator_053.Solvers.Types.*;
import graphing_calculator_053.Solvers.AdvancedMath.Trig.*;
public class EquationToString{
public EquationToString(){
}

public String equationToString(BaseNode input) {
StringBuilder equationString = new StringBuilder();
    if (input instanceof FastNumberNode a) {
    java.text.DecimalFormat df = new java.text.DecimalFormat("0.##########");
    equationString.append(df.format(a.value()));
    }
        if (input instanceof VariableNode a) {
        equationString.append(String.valueOf(a.type()));
    }
    if (input instanceof EquivalenceNode a) {
        equationString.append(equationToString(a.left()) + "=" + equationToString(a.right()));
    }
    if (input instanceof AdditionNode a) {
        equationString.append(equationToString(a.left()) + "+" + equationToString(a.right()));
    }
        if (input instanceof SubtractionNode a) {
        equationString.append(equationToString(a.left()) + "-" + equationToString(a.right()));
    }
    if (input instanceof MultiplicationNode a) {
        equationString.append(equationToString(a.left()) + "*" + equationToString(a.right()));
    }
    if (input instanceof DivisionNode a) {
        equationString.append("(" + equationToString(a.left()) + ") / (" + equationToString(a.right()) + ")");
    }
    if (input instanceof ExponentNode a) {
        equationString.append("(" + equationToString(a.left()) + ")^(" + equationToString(a.right()) + ")");
    }
    if (input instanceof TermNode a) {
        equationString.append("(" + equationToString(a.node()) + ")");
    }
    if (input instanceof TrigNode a) {
        equationString.append(a.type() + "(" + equationToString(a.Function()) + ")");
    }
   if (input instanceof ReciprocalTrigNode a) {
        equationString.append(a.type() + "(" + equationToString(a.Function()) + ")");
    }    
        return equationString.toString();
}
}

