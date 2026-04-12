  MathEngine v0.53 (title pending) is a full-stack symbolic math and graphing system built from scratch as an experiment in how far I can push mathematical computation without relying on external libraries or built-in evaluators. The goal is to handle everything end-to-end: parsing, simplifying, evaluating, and visualizing expressions using only custom systems, minimizing overhead and maximizing efficiency. There have been several major iterations so far. I started with a basic string manipulator, then, with quite a bit of research (thank you, Wikipedia) and headache, I got a functional pipeline. Although it is fully functional within its current scope, this project is heavily a work in progress, and I've spent a decent amount of my free time working on it, and I intend to continue to do so, I want to see how far I can push this. 

At the core of the engine is an Abstract Syntax Tree (AST) pipeline. Input expressions first go through a custom tokenizer that turns raw strings into structured tokens while handling tricky cases like unary minus, implicit multiplication, and multi-character variables or function names. From there, a recursive descent parser builds a hierarchical AST that correctly represents operator precedence and nesting.

Once the AST is built, it enters a multi-pass simplification system. This system repeatedly applies transformations like constant folding, identity removal, term grouping, and limited distributive expansion. It runs until the tree stabilizes, meaning no further simplifications can be made under the current rule set. Alongside symbolic reduction, the engine can also partially evaluate constant subtrees and compress terms (for example, turning x + x into 2x, or simplifying expressions like x/(x*5) into 0.2).

For numerical evaluation, the engine walks the AST and substitutes variables dynamically. This is used both for console calculations and graph generation. In graphing mode, expressions are evaluated across a defined domain using a configurable step size, producing a series of (x, y) points that are sent through a Spring Boot REST API to the frontend.

The frontend graphing system is built in vanilla JavaScript using the HTML5 Canvas API. It includes a coordinate transformation layer that maps mathematical space to screen space, along with zooming and panning centered on the cursor. The renderer draws adaptive grid lines with dynamic tick spacing and axis labels. It also includes discontinuity detection, where invalid values (like NaN, infinities, or sharp numerical jumps) break the line to avoid incorrect connections—especially important for functions like 1/x.

On the frontend side, the graph is further resampled and normalized to maintain consistent spacing based on the user-defined step size. This helps keep visual output stable even when backend sampling density changes. A gap-detection system also ensures discontinuous segments don’t get visually stitched together.

The backend is organized around a dispatcher that routes requests into either symbolic console mode or graphing mode. A type detection layer classifies input as a plain expression, an equation, or a graphable function (Cartesian for now, with implicit, parametric, and polar support planned). That classification determines how the AST is processed and what kind of output is returned.

From a user perspective, the system has two main entry points. The console takes raw expressions and returns simplified symbolic results, while the graphing interface takes functions of x plus a domain definition (min, max, step) and renders them in real time. Both share the same underlying AST pipeline, so symbolic and visual outputs always stay consistent.

Overall, MathEngine is less of a traditional calculator and more of an experimental computation framework. It’s an attempt to build a complete symbolic math system from first principles, tightly integrating parsing, simplification, evaluation, and rendering into a single coherent pipeline.

Roadmap / Future Development

Planned improvements, roughly in priority order:

- Core trigonometric functions (sin, cos, tan, sec, csc, cot)
- Mathematical constants (π, e, φ)
- Numerical differentiation and integration
- Multi-function graphing support
- Full symbolic calculus system
- Implicit, polar, and parametric graphing modes
- High-precision BigDecimal-based evaluation integrated into the AST
- Higher-fidelity rendering modes for graphing
- Full matrix and linear algebra support in the console
- A computational methods library for structured problem-solving (from algebra through multivariable calculus and linear algebra)
