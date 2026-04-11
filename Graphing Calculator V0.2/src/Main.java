public class Main {
    public static void main(String[] args) {

        System.out.println("\n  Type:\n");
        String InputOne = InputClass.getInput();
        String num1 = Parser.Simplify_Equation(InputOne);
        System.out.println(num1);
         System.out.println("Done!");
    }
}