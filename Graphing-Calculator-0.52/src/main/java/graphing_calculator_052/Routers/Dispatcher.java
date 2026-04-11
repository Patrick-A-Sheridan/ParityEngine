package graphing_calculator_052.Routers;
import graphing_calculator_052.Solvers.BasicMathUtil;
import graphing_calculator_052.Solvers.EquationToString;
import graphing_calculator_052.Solvers.FastSolver.FastNumberNode;
import graphing_calculator_052.Solvers.FastSolver.Manipulators.*;
import graphing_calculator_052.Solvers.Types.*;
import graphing_calculator_052.Tokenizer.Tokenizer;
import graphing_calculator_052.Identification;
import graphing_calculator_052.Graphing.CartesianPlotting;

public class Dispatcher {
    public void run(String input, Identification ID) {
        // tokenyness
        input = BasicMathUtil.sanitizationCheck(input);
        Tokenizer t = new Tokenizer(input);
        t.tokenize();
        //prints junk&time to prestuffs
        t.printTokens();
        // gets the type of equation
        TypeDetection typeDetector = new TypeDetection(input, ID);
        Identification RunnerID = new Identification(typeDetector.detect(input, ID));
        // prints
        System.out.println(RunnerID.ID());
        //runs program based on RunnerID
        switch (RunnerID.ID()) {
            case "CF" -> {
                //creates AST
                ASTParser parser = new ASTParser(t.getTokenList());
                // simplifies
                BaseNode ast = parser.parseEquivalence();
                double nul = Double.NaN;
                BaseNode ast_2 = new FastNumberNode(nul);
                Simplifier simplifier = new Simplifier();
                                System.out.println("Precheck: " + ast);
                // Prints result
                while (!ast.equals(ast_2)) {
                    ast_2 = ast;
                    ast = simplifier.simplifyIdentities(ast);
                    ast = simplifier.simplify(ast);
                    System.out.println(ast);
                }

                System.out.println("Symbolic Version: " + ast);

                BaseNode thg = ast;
                BaseNode thg2 = new FastNumberNode(nul);
                ;
              //  thg = simplifier.FastplugVar(thg, new VariableNode("x"), 5);
                // Prints result
                while (!thg.equals(thg2)) {
                    thg2 = thg;
                    thg = simplifier.simplify(thg);
                    thg = simplifier.simplifyIdentities(thg);
                    System.out.println(thg);
                }
                EquationToString eqtString = new EquationToString();
                System.out.println(eqtString.equationToString(ast));

            }
            case "GFStandardCartesian" -> {
                 //creates AST
                ASTParser parser = new ASTParser(t.getTokenList());
                // simplifies
                BaseNode ast = parser.parseEquivalence();
                BaseNode ast_2 = new FastNumberNode(Double.NaN);
                Simplifier simplifier = new Simplifier();
                ast = simplifier.stripEquationY(ast);
                // Prints result
                while (!ast.equals(ast_2)) {
                    ast_2 = ast;
                    ast = simplifier.simplify(ast);
                    ast = simplifier.simplifyIdentities(ast);
                    System.out.println(ast);
                }
                System.out.println("Symbolic Version: " + ast);
                CartesianPlotting plotter = new CartesianPlotting();
               System.out.println(plotter.fastCreatePoints(ast));
            }
        }
    }
}