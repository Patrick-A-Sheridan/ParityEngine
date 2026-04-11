package graphing_calculator_052.Solvers;

import graphing_calculator_052.Solvers.Types.*;
import graphing_calculator_052.Solvers.ElementaryMath.*;
import graphing_calculator_052.Solvers.FastSolver.FastNumberNode;

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
    
        return equationString.toString();
}
}
