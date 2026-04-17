package graphing_calculator_053.Solvers.FastSolver.Manipulators;

import java.security.InvalidAlgorithmParameterException;

import graphing_calculator_053.Solvers.AdvancedMath.Trig.ReciprocalTrigNode;
import graphing_calculator_053.Solvers.AdvancedMath.Trig.TrigNode;
import graphing_calculator_053.Solvers.ElementaryMath.*;
import graphing_calculator_053.Solvers.FastSolver.*;
import graphing_calculator_053.Solvers.Types.*;

public class Simplifier {
    public double evaluate(BaseNode node) {
        if (node instanceof FastNumberNode n) {
            return n.value(); // leaf node: return its numeric value
        }
        if (node instanceof MultiplicationNode a) {
            if (a.right() instanceof NumberNode && a.left() instanceof NumberNode) {
                return evaluate(a.left()) * evaluate(a.right());
            }
        } else if (node instanceof DivisionNode a) {
            if (a.right() instanceof NumberNode && a.left() instanceof NumberNode) {
                return evaluate(a.left()) / evaluate(a.right());
            }
        } else if (node instanceof AdditionNode a) {
            if (a.right() instanceof NumberNode && a.left() instanceof NumberNode) {
                return evaluate(a.left()) + evaluate(a.right());
            }
        } else if (node instanceof SubtractionNode a) {
            if (a.right() instanceof NumberNode && a.left() instanceof NumberNode) {
                return evaluate(a.left()) - evaluate(a.right());
            }
        } else if (node instanceof ExponentNode a) {
            if (a.right() instanceof NumberNode && a.left() instanceof NumberNode) {
                return Math.pow(evaluate(a.left()), evaluate(a.right()));
            }
        }
         else if (node instanceof TermNode a) {
             return (evaluate(a.node().left()) * evaluate(a.node().right()));
            }
             else if (node instanceof TrigNode a) {
                 if (a.type().equals("sin")) {
                return Math.sin(evaluate(a.Function()));
                 }
              else if (a.type().equals("cos")) {
                return Math.cos(evaluate(a.Function()));
             } else if (a.type().equals("tan")) {
                return Math.tan(evaluate(a.Function()));
            }
        }
        else if (node instanceof ReciprocalTrigNode a) {
            if (a.type().equals("sec")) {
                return 1/Math.cos(evaluate(a.Function()));
            } else if (a.type().equals("csc")) {
                return 1/Math.sin(evaluate(a.Function()));
            } else if (a.type().equals("cot")) {
                return 1/Math.tan(evaluate(a.Function()));
            }
        }
        throw new IllegalArgumentException("Unknown node type" + node);
    }

    public BaseNode simplify(BaseNode node) {
        if (node instanceof EquivalenceNode e) {
            return new EquivalenceNode(simplify(e.left()), simplify(e.right()));
        }

       else if (node instanceof MultiplicationNode a) {
            if (a.right() instanceof NumberNode && a.left() instanceof NumberNode) {
                return new FastNumberNode(evaluate(a.left()) * evaluate(a.right()));
            } else if (a.right() instanceof VariableNode v) {
                return new TermNode(new MultiplicationNode(simplifyIdentities(simplify(a.left())), v));
            } else if (a.left() instanceof VariableNode v && a.right() instanceof NumberNode n) {
                return new TermNode(new MultiplicationNode(simplifyIdentities(simplify(n)), v));
            } else if (a.left() instanceof VariableNode v_1 && a.right() instanceof VariableNode v_2) {
                return new MultiplicationNode(v_1, v_2);
            } else
                return new MultiplicationNode(simplify(simplifyIdentities(a.left())), simplify(simplifyIdentities(a.right())));

        
        }

        else if (node instanceof AdditionNode a) {
            if (a.right() instanceof NumberNode && a.left() instanceof NumberNode) {
                return new FastNumberNode(evaluate(a.left()) + evaluate(a.right()));
            } else if (a.right() instanceof VariableNode v) {
                return new AdditionNode(simplifyIdentities(simplify(a.left())), v);
            } else if (a.left() instanceof VariableNode v) {
                return new AdditionNode(v, simplifyIdentities(simplify(a.right())));
            } else if (a.left() instanceof VariableNode v_1 && a.right() instanceof VariableNode v_2) {
                return new AdditionNode(v_1, v_2);
            } else
                return new AdditionNode(simplify(simplifyIdentities(a.left())), simplify(simplifyIdentities(a.right())));
        }

        else if (node instanceof SubtractionNode a) {
            if (a.right() instanceof NumberNode && a.left() instanceof NumberNode) {
                return new FastNumberNode(evaluate(a.left()) - evaluate(a.right()));
            } else if (a.right() instanceof VariableNode v) {
                return new SubtractionNode(simplifyIdentities(simplify(a.left())), v);
            } else if (a.left() instanceof VariableNode v) {
                return new SubtractionNode(v, simplifyIdentities(simplify(a.right())));
            } else if (a.left() instanceof VariableNode v_1 && a.right() instanceof VariableNode v_2) {
                return new SubtractionNode(v_1, v_2);
            } else
                return new SubtractionNode(simplify(simplifyIdentities(a.left())), simplify(simplifyIdentities(a.right())));
        }

        else if (node instanceof DivisionNode a) {
            if (a.right() instanceof NumberNode && a.left() instanceof NumberNode) {
                return new FastNumberNode(evaluate(a.left()) / evaluate(a.right()));
            } else if (a.right() instanceof VariableNode v) {
                return new DivisionNode(simplifyIdentities(simplify(a.left())), v);
            } else if (a.left() instanceof VariableNode v) {
                return new DivisionNode(v, simplifyIdentities(simplify(a.right())));
            } else if (a.left() instanceof VariableNode v_1 && a.right() instanceof VariableNode v_2) {
                return new DivisionNode(v_1, v_2);
            } else
                return new DivisionNode(simplify(simplifyIdentities(a.left())), simplify(simplifyIdentities(a.right())));
        }

        else if (node instanceof ExponentNode a) {
            if (a.right() instanceof NumberNode && a.left() instanceof NumberNode) {
                return new FastNumberNode(Math.pow(evaluate(a.left()), evaluate(a.right())));
            } else if (a.right() instanceof VariableNode v) {
                return new ExponentNode(simplifyIdentities(simplify(a.left())), v);
            } else if (a.left() instanceof VariableNode v) {
                return new ExponentNode(v, simplifyIdentities(simplify(a.right())));
            } else if (a.left() instanceof VariableNode v_1 && a.right() instanceof VariableNode v_2) {
                return new ExponentNode(v_1, v_2);
            } else
                return new ExponentNode(simplify(simplifyIdentities(a.left())),
                        simplify(simplifyIdentities(a.right())));
        }
        

else if (node instanceof TrigNode a) {
    BaseNode inner = simplify(simplifyIdentities(a.Function()));
    if (inner instanceof FastNumberNode n) {
        switch (a.type()) {
            case "sin" -> { return new FastNumberNode(Math.sin(n.value())); }
            case "cos" -> { return new FastNumberNode(Math.cos(n.value())); }
            case "tan" -> { return new FastNumberNode(Math.tan(n.value())); }
        }
    }
    return new TrigNode(a.type(), inner);
}
else if (node instanceof ReciprocalTrigNode a) {
    BaseNode inner = simplify(simplifyIdentities(a.Function()));
    if (inner instanceof FastNumberNode n) {
        switch (a.type()) {
            case "csc" -> { return new FastNumberNode(1 / Math.sin(n.value())); }
            case "sec" -> { return new FastNumberNode(1 / Math.cos(n.value())); }
            case "cot" -> { return new FastNumberNode(1 / Math.tan(n.value())); }
        }
    }
    return new ReciprocalTrigNode(a.type(), inner);
}


        return node;
    }

    public BaseNode simplifyIdentities(BaseNode node) {
        if (node instanceof ExponentNode exponentNode) {
            if (exponentNode.right() instanceof NumberNode n) {
                if (evaluate(n) == 1)
                    return simplify(exponentNode.left());
                if (evaluate(n) == 0)
                    return new FastNumberNode(1);
            }
        }
        if (node instanceof DivisionNode d) {
            if (d.left().equals(d.right())) {
                return new FastNumberNode(1);
            }
            if (d.left() instanceof MultiplicationNode M1) {
                if (M1.left().equals(d.right())) {return M1.right();}
                if(M1.right().equals(d.right())){ return M1.left();}
            }
             if (d.right() instanceof MultiplicationNode M2) {
                  if (M2.left().equals(d.left())) {return new DivisionNode(new FastNumberNode(1), M2.right());}
                  if (M2.right().equals(d.left())) {return new DivisionNode(new FastNumberNode(1), M2.left());}
            }

        }
        if (node instanceof MultiplicationNode a) {
            if (a.right() instanceof NumberNode n) {
                if (evaluate(n) == 1)
                    return simplify(a.left());
                if (evaluate(n) == 0)
                    return new FastNumberNode(0);
            }
            if (a.left() instanceof NumberNode n) {
                if (evaluate(n) == 1)
                    return simplify(a.right());
                if (evaluate(n) == 0)
                    return new FastNumberNode(0);
            }
            if (a.right() instanceof AdditionNode gamma) {
                return new AdditionNode((simplify(new MultiplicationNode(a.left(), gamma.left()))),
                        simplify(new MultiplicationNode(a.left(), gamma.right())));
            }
            if (a.left() instanceof AdditionNode gamma) {
                return new AdditionNode((simplify(new MultiplicationNode(a.right(), gamma.left()))),
                        simplify(new MultiplicationNode(a.right(), gamma.right())));
            }
            if (a.right() instanceof SubtractionNode gamma) {
                return new SubtractionNode((simplify(new MultiplicationNode(a.left(), gamma.left()))),
                        simplify(new MultiplicationNode(a.left(), gamma.right())));
            }
            if (a.left() instanceof SubtractionNode gamma) {
                return new SubtractionNode((simplify(new MultiplicationNode(a.right(), gamma.left()))),
                        simplify(new MultiplicationNode(a.right(), gamma.right())));
            }

        } else if (node instanceof AdditionNode a) {
            if (a.right() instanceof NumberNode n && evaluate(n) == 0)
                return simplify(a.left());
            if (a.left() instanceof NumberNode n && evaluate(n) == 0)
                return simplify(a.right());
            if (a.left() instanceof VariableNode v_1 && a.right() instanceof VariableNode v_2) {
                if (v_1.type().equals(v_2.type())) {
                    return new TermNode(new MultiplicationNode(new FastNumberNode(2), v_1));
                }
            }
            if (a.left() instanceof TermNode v_1 && a.right() instanceof TermNode v_2) {
                if (v_1.node().right().equals(v_2.node().right()) && v_1.node().left() instanceof NumberNode
                        && v_2.node().left() instanceof NumberNode) {
                    return new TermNode(new MultiplicationNode(
                            new FastNumberNode(evaluate(v_1.node().left()) + evaluate(v_2.node().left())),
                            v_1.node().right()));
                }
            }
            if (a.left() instanceof TermNode v_1 && a.right() instanceof VariableNode v_2) {
                if (v_1.node().right().equals(v_2) && v_1.node().left() instanceof NumberNode) {
                    return new TermNode(new MultiplicationNode(
                            new FastNumberNode(evaluate(v_1.node().left()) + 1), v_2));
                }
            }

        }
        return node;
    }

    public BaseNode FastplugVar(BaseNode node, VariableNode v, double plug) {
        if (node instanceof VariableNode Var && Var.type().equals(v.type())) {
            return new FastNumberNode(plug);
        }
        if (node instanceof TermNode t) {
            if (t.node().right() instanceof VariableNode tType && tType.type().equals(v.type())) {
                return new MultiplicationNode(t.node().left(), new FastNumberNode(plug));
            }
        }
        if (node instanceof AdditionNode a) {
            return new AdditionNode(FastplugVar(a.left(), v, plug), FastplugVar(a.right(), v, plug));
        }
        if (node instanceof SubtractionNode a) {
            return new SubtractionNode(FastplugVar(a.left(), v, plug), FastplugVar(a.right(), v, plug));
        }
        if (node instanceof MultiplicationNode a) {
            return new MultiplicationNode(FastplugVar(a.left(), v, plug), FastplugVar(a.right(), v, plug));
        }
        if (node instanceof DivisionNode a) {
            return new DivisionNode(FastplugVar(a.left(), v, plug), FastplugVar(a.right(), v, plug));
        }
        if (node instanceof ExponentNode a) {
            return new ExponentNode(FastplugVar(a.left(), v, plug), FastplugVar(a.right(), v, plug));
        }
                if (node instanceof TermNode a) {
            return new MultiplicationNode(FastplugVar(a.node().left(), v, plug), FastplugVar(a.node().right(), v, plug));
        }

        return node;

    }


        public Double FastEvalplugVar(BaseNode node, VariableNode v, double plug) {
        if (node instanceof VariableNode Var && Var.type().equals(v.type())) {
            return plug;
        }
        if (node instanceof TermNode t) {
            if (t.node().right() instanceof VariableNode tType && tType.type().equals(v.type())) {
                return (FastEvalplugVar(t.node().left(), v, plug) * (plug));
            }
        }
        if (node instanceof AdditionNode a) {
            return FastEvalplugVar(a.left(), v, plug) + FastEvalplugVar(a.right(), v, plug);
        }
        if (node instanceof SubtractionNode a) {
    return FastEvalplugVar(a.left(), v, plug) - FastEvalplugVar(a.right(), v, plug);
        }
        if (node instanceof MultiplicationNode a) {
 return FastEvalplugVar(a.left(), v, plug) * FastEvalplugVar(a.right(), v, plug);
        }
        if (node instanceof DivisionNode a) {
 return FastEvalplugVar(a.left(), v, plug) / FastEvalplugVar(a.right(), v, plug);
        }
        if (node instanceof ExponentNode a) {
           return Math.pow(FastEvalplugVar(a.left(), v, plug), FastEvalplugVar(a.right(), v, plug));
        }
if (node instanceof TrigNode a) {
    double inner = FastEvalplugVar(a.Function(), v, plug);
    switch (a.type()) {
        case "sin" -> { return Math.sin(inner); }
        case "cos" -> { return Math.cos(inner); }
        case "tan" -> { return Math.tan(inner); }
    }
}
if (node instanceof ReciprocalTrigNode a) {
    double inner = FastEvalplugVar(a.Function(), v, plug);
    switch (a.type()) {
        case "csc" -> { return 1 / Math.sin(inner); }
        case "sec" -> { return 1 / Math.cos(inner); }
        case "cot" -> { return 1 / Math.tan(inner); }
    }
}
        return evaluate(node);

    }


    public BaseNode stripEquationY(BaseNode input) {
        try {
            if (input instanceof EquivalenceNode e) {
                return e.right();
            } else {
                throw new InvalidAlgorithmParameterException(
                        "How About you input an equation next time, Simplifier stripY");
            }
        } catch (InvalidAlgorithmParameterException e) {
            System.out.println(e);
        }
        return null;
    }
}
