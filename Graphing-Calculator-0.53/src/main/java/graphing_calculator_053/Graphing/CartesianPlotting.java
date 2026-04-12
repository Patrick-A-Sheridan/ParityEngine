package graphing_calculator_053.Graphing;
import java.util.ArrayList;
import java.util.List;

import graphing_calculator_053.Solvers.FastSolver.Manipulators.Simplifier;
import graphing_calculator_053.Solvers.Types.*;
public class CartesianPlotting {

public List<List<Double>> fastCreatePoints(Double step, Double min, Double max, BaseNode ast, VariableNode xVar) {
    Simplifier simplifier = new Simplifier();
    List<List<Double>> points = new ArrayList<>();

    // Safety to prevent infinite loops
    if (step <= 0.00001) step = 0.01;
    // Threshold for "practical infinity"
    final double MAX_Y = 1e10; 
    for (double x = min; x <= max; x += step) {

        double y;
        try {
            y = simplifier.FastEvalplugVar(ast, xVar, x);
        } catch (Exception e) {
            // If evaluation crashes, treat as discontinuity
            y = Double.NaN;
            continue;
        }
        if (
            Double.isNaN(y) ||
            Double.isInfinite(y) ||
            Math.abs(y) > MAX_Y 
        ) {
            // Skip this point completely → creates gap
List<Double> point = new ArrayList<>();
point.add(x);
point.add(Double.NaN); // marker for discontinuity
points.add(point);
        }
        List<Double> point = new ArrayList<>();
        point.add(x);
        point.add(y);
        points.add(point);
    }

    return points;
}
}
