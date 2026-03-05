import java.util.Scanner;

public class SAT {
    public static void main(String[] args) throws Exception {
        try (Scanner stdin = new Scanner(System.in)) {
            System.out.println("Welcome to SAT solver!\nEnter Expression in the following format:\n! = not\n& = and\n| = or\n> = implies\n= = double conditional\n/ = xor\nUse () to manipulate Expression order\nYour Expression: ");
            Expression expression = new Expression(stdin.nextLine());
            Expression.draw(expression);
            Expression.SAT(expression);
        }
    }

}
