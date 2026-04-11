package graphing_calculator_051;
import graphing_calculator_051.Solvers.BasicMathUtil;
import graphing_calculator_051.Solvers.FastSolver.Manipulators.*;
import graphing_calculator_051.Solvers.FastSolver.Types.BaseNode;
import graphing_calculator_051.Tokenizer.Tokenizer;

public class Dispatcher {
    public void run(String input) {
        // tokenyness
        input = BasicMathUtil.sanitizationCheck(input);
Tokenizer t = new Tokenizer(input);
t.tokenize();
//prints junk&time to prestuffs
t.printTokens();
//creates AST
ASTParser parser = new ASTParser(t.getTokenList());
// simplifies
BaseNode ast = parser.parseAddition();
Simplifier simplifier = new Simplifier();
// Prints result
System.out.println(simplifier.simplify(ast));
    }
}  