public class Functions {
    public static double[][] createTable(String input, double startValue, double endValue, double stepSize) {
        int tableLength = (int) ((endValue - startValue) / stepSize) + 1;
        double[][] Table = new double[tableLength][2];
        double counter = startValue;
        for (int i = 0; i < tableLength; i++) {
            Table[i][0] = counter;
            Double solution = Functions.evaluate(input, counter);
            Table[i][1] = solution;
            counter = counter + stepSize;

        }
        return Table;
    }
   public static double evaluate(String expression, double x) {
    double result = Double.NaN;
    try {
        String formattedX = String.valueOf(x);
        String instance = expression.replaceAll("x", formattedX);
        String solved = Complex_Parser.Master_Solve(instance);
        result = Double.parseDouble(solved);
    }
    catch (Exception e) {
        result = Double.NaN;
    }
    return result;
}
}