package graphing_calculator_051.Solvers.FastSolver.Manipulators;

import graphing_calculator_051.Solvers.FastSolver.BasicMath.AdditionNode;
import graphing_calculator_051.Solvers.FastSolver.BasicMath.DivisionNode;
import graphing_calculator_051.Solvers.FastSolver.BasicMath.ExponentNode;
import graphing_calculator_051.Solvers.FastSolver.BasicMath.MultiplicationNode;
import graphing_calculator_051.Solvers.FastSolver.BasicMath.SubtractionNode;
import graphing_calculator_051.Solvers.FastSolver.Types.*;

public class Simplifier {
    public double simplify(BaseNode node) {
        if (node instanceof NumberNode n) {
            return n.value(); // leaf node: return its numeric value
        }

        if (node instanceof MultiplicationNode a) {
            return simplify(a.left()) * simplify(a.right());
        } else if (node instanceof DivisionNode a) {
            return simplify(a.left()) / simplify(a.right());
        } else if (node instanceof AdditionNode a) {
            return simplify(a.left()) + simplify(a.right());
        } else if (node instanceof SubtractionNode a) {
            return simplify(a.left()) - simplify(a.right());
        } else if (node instanceof ExponentNode a) {
            return Math.pow(simplify(a.left()), simplify(a.right()));
        }
        throw new IllegalArgumentException("Unknown node type");
    }
}
