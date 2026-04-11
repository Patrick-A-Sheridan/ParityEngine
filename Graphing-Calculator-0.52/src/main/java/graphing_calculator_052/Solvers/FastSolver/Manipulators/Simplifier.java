package graphing_calculator_052.Solvers.FastSolver.Manipulators;

import java.security.InvalidAlgorithmParameterException;

import graphing_calculator_052.Solvers.ElementaryMath.*;
import graphing_calculator_052.Solvers.Types.*;
import graphing_calculator_052.Solvers.FastSolver.*;

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
        throw new IllegalArgumentException("Unknown node type");
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
            } else if (a.left() instanceof VariableNode v) {

                return new TermNode(new MultiplicationNode(simplifyIdentities(simplify(a.right())), v));
            } else if (a.left() instanceof VariableNode v_1 && a.right() instanceof VariableNode v_2) {
                return new MultiplicationNode(v_1, v_2);
            } else
                return new MultiplicationNode(simplify(a.left()), simplify(a.right()));

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
                return new AdditionNode(simplify(a.left()), simplify(a.right()));
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
                return new SubtractionNode(simplify(a.left()), simplify(a.right()));
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
                return new DivisionNode(simplify(a.left()), simplify(a.right()));
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
                return new ExponentNode(simplify(a.left()), simplify(a.right()));
        }
        return node;
    }

    public BaseNode simplifyIdentities(BaseNode node) {
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
                return new AdditionNode(simplify(((new MultiplicationNode(a.left(), gamma.left())))),
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

        return node;

    }


        public Double FastEvalplugVar(BaseNode node, VariableNode v, double plug) {
        if (node instanceof VariableNode Var && Var.type().equals(v.type())) {
            return plug;
        }
        if (node instanceof TermNode t) {
            if (t.node().right() instanceof VariableNode tType && tType.type().equals(v.type())) {
                return (evaluate(t.node().left()) * (plug));
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
