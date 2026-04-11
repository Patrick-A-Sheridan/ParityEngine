public class Main {
    public static void main(String[] args) {

        System.out.println("Hello, World!");
        String InputOne = InputClass.getInput();
        System.out.println(InputOne);
        String num1 = Parser.parse_multiplication(InputOne);
         num1 = Parser.parse_addition(num1);


        System.out.println(num1);
         System.out.println("Done!");
    }
}