package graphing_calculator_053.Graphing;

import java.util.ArrayList;
import java.util.List;
import graphing_calculator_053.Solvers.FastSolver.Manipulators.Simplifier;
import graphing_calculator_053.Solvers.Types.*;

public class CartesianPlotting {
    public List<List<Double>> fastCreatePoints(Double step, Double min, Double max, BaseNode ast, VariableNode xVar) {
        Simplifier simplifier = new Simplifier();
        List<List<Double>> points = new ArrayList<>();

        // Safety to prevent infinite loops or server hang
        if (step <= 0.00001) step = 0.01;

        // Threshold for "practical infinity"
        final double MAX_Y = 1e7;

        for (double x = min; x <= max; x += step) {
            double y;
            
try {
    y = simplifier.FastEvalplugVar(ast, xVar, x);
} catch (Exception e) {
    continue; 
}

if (Double.isNaN(y) || Double.isInfinite(y) || Math.abs(y) > MAX_Y) {
    continue; 
}

            if (Double.isNaN(y) || Double.isInfinite(y) || Math.abs(y) > MAX_Y) {
                // Safely mark the structural asymptote gap
                List<Double> point = new ArrayList<>();
                point.add(x);
                point.add(Double.NaN);
                points.add(point);
                continue; // <--Prevents falling through and adding a duplicate
            }

            // Standard valid coordinate pair configuration
            List<Double> point = new ArrayList<>();
            point.add(x);
            point.add(y);
            points.add(point);
        }
        return points;
    }
}
    return points;
}
}
