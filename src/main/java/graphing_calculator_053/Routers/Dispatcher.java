package graphing_calculator_053.Routers;

import graphing_calculator_053.Identification;
import graphing_calculator_053.TimeManager;
import graphing_calculator_053.Graphing.CartesianPlotting;
import graphing_calculator_053.Solvers.BasicMathUtil;
import graphing_calculator_053.Solvers.EquationToString;
import graphing_calculator_053.Solvers.FastSolver.FastNumberNode;
import graphing_calculator_053.Solvers.FastSolver.Manipulators.*;
import graphing_calculator_053.Solvers.Types.*;
import graphing_calculator_053.Tokenizer.Tokenizer;
import java.util.ArrayList;
import java.util.List;

public class Dispatcher {
		long startTime = TimeManager.getPreciseTime();
    // --- CONSOLE ROUTER (Returns Symbolic String) ---
    public String run(String input, Identification ID) {
        try {
            // 1. Sanitize & Tokenize
            input = BasicMathUtil.sanitizationCheck(input);
            Tokenizer t = new Tokenizer(input);
            t.tokenize();
            
            // 2. Identify
            TypeDetection typeDetector = new TypeDetection(input, ID);
            String runID = typeDetector.detect(input, ID); // e.g. "CF"

            // 3. Execute
            if (runID.contains("CF")) {
                ASTParser parser = new ASTParser(t.getTokenList());
                BaseNode ast = parser.parseEquivalence();
                
                Simplifier simplifier = new Simplifier();
                BaseNode oldAst = new FastNumberNode(Double.NaN);

                // Simplify Loop
                while (!ast.equals(oldAst)) {
                    oldAst = ast;
                    ast = simplifier.simplify(ast);
		            ast = simplifier.simplifyIdentities(ast);
                }
                
                EquationToString eqtString = new EquationToString();
                		System.out.println("Time to compile: " + (TimeManager.getPreciseTime()- startTime) + "ms");
                return eqtString.equationToString(ast);
            }
            
            return "Error: Unknown Command or ID mismatch (" + runID + ")";

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // --- GRAPHING ROUTER (Returns Points [[x,y]...]) ---
    public List<List<Double>> run(String input, Identification ID, double min, double max, double step) {
        List<List<Double>> points = new ArrayList<>();

        try {
            // 1. Prep
            input = BasicMathUtil.sanitizationCheck(input);
            Tokenizer t = new Tokenizer(input);
            t.tokenize();

            // 2. Identify
            TypeDetection typeDetector = new TypeDetection(input, ID);
            String runID = typeDetector.detect(input, ID);

            // 3. Parsing Strategy
            ASTParser parser = new ASTParser(t.getTokenList());
            BaseNode ast = parser.parseEquivalence();
            Simplifier simplifier = new Simplifier();

            // Handle different graph types
            boolean validGraph = false;

            if (runID.endsWith("StandardCartesian")) { 
                // Case: "y = x^2" -> Strip "y=" to get just "x^2"
                ast = simplifier.stripEquationY(ast);
                validGraph = true;
            } else if (runID.endsWith("Expression")) {
                // Case: "x^2" -> Already an expression, do NOT strip
                validGraph = true;
            }

            // 4. Execution
            if (validGraph) {
                // Pre-simplify for performance
                BaseNode oldAst = new FastNumberNode(Double.NaN);
                while (!ast.equals(oldAst)) {
                    oldAst = ast;
                    ast = simplifier.simplify(ast);
                    ast = simplifier.simplifyIdentities(ast);
                }
                // Coordinate Loop
                CartesianPlotting cPlot = new CartesianPlotting();
                VariableNode xVar = new VariableNode("x");
                points = cPlot.fastCreatePoints(step, min, max, ast, xVar);
            } else {
                System.out.println("Dispatcher: Could not identify graph type for ID: " + runID);
            }

        } catch (Exception e) {
            System.out.println("Dispatcher Error: " + e.getMessage());
            e.printStackTrace();
        }
		System.out.println("Time to compile: " + (TimeManager.getPreciseTime()- startTime) + "ms");
        return points;
    }
}
