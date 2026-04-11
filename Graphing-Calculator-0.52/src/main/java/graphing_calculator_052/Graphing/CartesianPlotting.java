package graphing_calculator_052.Graphing;
import graphing_calculator_052.Solvers.FastSolver.Manipulators.Simplifier;
import graphing_calculator_052.Solvers.Types.*;
import java.util.ArrayList;
public class CartesianPlotting {

    public ArrayList<Double> fastCreatePoints(BaseNode input){
        ArrayList<Double> endArray = new ArrayList<>();
        VariableNode x = new VariableNode("x");
        Simplifier simplifier = new Simplifier();
        for (int i = -10; i <= 10; i++) {
            double y = simplifier.FastEvalplugVar(input, x, i);
            endArray.add(y);
        }
           //       System.out.println(endArray);
        return endArray;
    }
}
