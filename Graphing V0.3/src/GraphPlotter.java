import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.util.Scanner;

public class GraphPlotter {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter an equation or a function in terms of x:\n"
                + " Use ~ for subtraction\n"
                + " Use + for addition\n"
                + " Use / for division\n"
                + " Use * for multiplication\n"
                + " Use ^ for exponents\n");
        String expression = sc.nextLine();
        if (expression.contains("x")) {
            System.out.println("Enter minimum x value:");
            double minX = Double.parseDouble(sc.nextLine());

            System.out.println("Enter maximum x value:");
            double maxX = Double.parseDouble(sc.nextLine());

            System.out.println("Enter step size (e.g. 0.01):");
            double stepSize = Double.parseDouble(sc.nextLine());

            // Simple window size.
            final int WIDTH = 800;
            final int HEIGHT = 1200;

            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("Function Grapher");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(WIDTH, HEIGHT);

                GraphPanel panel = new GraphPanel(expression, minX, maxX, stepSize);
                frame.add(panel);

                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        }
        else {
            System.out.println(Complex_Parser.Master_Solve(expression));};
    }

}