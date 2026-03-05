import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class Expression {

    String expression;
    boolean isAtom;
    Expression left;
    Expression right;
    char operator;
    int matrixSpace;
    Set<Character> atoms = new TreeSet<>();

    Expression(String e) {
        if (e != null) {
            e = e.replaceAll("\\s+", "");
            if (e.length() == 0) throw new IllegalArgumentException("Invalid expression!");
            isAtom = false;
            String first;
            while (true) {
                String token = token(e, 0);
                if (e.equals(token) && e.charAt(0) == '(') {
                    e = e.substring(1, e.length() - 1);
                } else {
                    expression = e;
                    first = token;                
                    break;
                }
            }
            if (e.equals(first)) {
                if (e.charAt(0) == '!') {
                    operator = '!';
                    right = new Expression(first.substring(1, first.length()));
                } else {
                    isAtom = true;
                }
            } else {
                left = new Expression(first);
                try {
                    operator = e.charAt(first.length());
                } catch (NullPointerException exception) {
                    throw new IllegalArgumentException("Invalid expression!");
                }
                if ((operator != '&' && operator != '|' && operator != '>' && operator != '=' && operator != '/')) {
                    throw new IllegalArgumentException("Invalid expression!");
                }
                try {
                    right = new Expression(e.substring(first.length() + 1, e.length()));
                } catch (NullPointerException exception) {
                    throw new IllegalArgumentException("Invalid expression!");
                }
            }
            if (isAtom) {
                atoms.add(first.charAt(0));
            } 
            if (left != null) {
                atoms.addAll(left.atoms);
            }
            if (right != null) {
                atoms.addAll(right.atoms);
            }
        }
    }

    @SuppressWarnings("unused")
    Expression(char e) {
        expression = "" + e;
        isAtom = true;  
        atoms.add(e);
    }

    public static String token(String e, int start) {
        boolean empty = true;
        boolean illegal = false;
        boolean jump;
        int pointer = start - 1;
        int counter = 0;
        do {
            pointer++;
            jump = false;
            if (e.charAt(pointer) != '(' && e.charAt(pointer) != ')') empty = false;
            if (e.charAt(pointer) == '!') {
                jump = true;
                continue;
            }
            if (e.charAt(pointer) == '(') counter++;
            if (e.charAt(pointer) == ')') counter--;
            if (counter < 0 || (counter == 0 && (e.charAt(pointer) == '&' || e.charAt(pointer) == '|' || e.charAt(pointer) == '>' || e.charAt(pointer) == '=' || e.charAt(pointer) == '/'))) {
                illegal = true;
                break;
            }
        } while (pointer < e.length() - 1 && (counter != 0 || jump));
        if (empty || illegal || (pointer == e.length() - 1 && counter != 0)) return null;
        return e.substring(start, pointer + 1);
    }

    public boolean evaluate(HashMap<Character, Boolean> interpretation) {
        if (isAtom) return interpretation.get(expression.charAt(0));
        if (operator == '!') return !(right.evaluate(interpretation));
        if (operator == '&') return (left.evaluate(interpretation) && right.evaluate(interpretation));
        if (operator == '|') return (left.evaluate(interpretation) || right.evaluate(interpretation));
        if (operator == '>') return (!left.evaluate(interpretation) || right.evaluate(interpretation));
        if (operator == '=') return ((left.evaluate(interpretation) && right.evaluate(interpretation)) || (!left.evaluate(interpretation) && !right.evaluate(interpretation)));
        if (operator == '/') return ((left.evaluate(interpretation) && !right.evaluate(interpretation)) || (!left.evaluate(interpretation) && right.evaluate(interpretation)));
        return false;
    } 

    public int matrixX() {
        if (isAtom) {
            return expression.length() + 1;
        } else {
            if (left != null) {
                return expression.length() + left.matrixX() + right.matrixX() + 1;
            } else {
                return expression.length() + right.matrixX() + 1;
            }
        }
    }

    public int matrixY() {
        if (isAtom) {
            return 1;
        } else if (operator == '!') {
            return expression.length() / 2 + right.matrixY() + 1;
        } else {    
            return expression.length() / 2 + Integer.max(left.matrixY(), right.matrixY()) + 1;
        }
    }

    static void draw(Expression expression) {
        int matrixX = expression.matrixX();
        int matrixY = expression.matrixY();
        int level = 0;
        char[][] matrix = new char[matrixY][matrixX];
        for (char[] row : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                row[j] = ' ';
            }
        }
        Deque<Expression> heightQueue = new ArrayDeque<>();
        Deque<Expression> queue = new ArrayDeque<>();
        expression.matrixSpace = matrix[0].length / 2 - expression.expression.length() / 2;
        heightQueue.add(expression);
        queue.add(expression);
        while (!queue.isEmpty()) {
            int sizeSnapshot = queue.size();
            int height = 0;
            for (int i = 0; i < sizeSnapshot; i++) {
                Expression current = heightQueue.poll();
                int newHeight = current.expression.length() / 2;
                if (newHeight > height) height = newHeight;
                if (current.left != null) heightQueue.add(current.left);
                if (current.right != null) heightQueue.add(current.right);
            }
            for (int i = 0; i < sizeSnapshot; i++) {
                Expression current = queue.poll();
                for (int j = current.matrixSpace; j < current.matrixSpace + current.expression.length(); j++) {
                    matrix[level][j] = current.expression.charAt(j - current.matrixSpace);
                }
                if (current.operator == '!') {
                    for (int j = 0; j < height; j++) matrix[level + 1 + j][current.matrixSpace + height] = '|';
                    current.right.matrixSpace = current.matrixSpace;
                    if (current.expression.length() % 2 == 0) current.right.matrixSpace++;
                    queue.add(current.right);
                } else if (!current.isAtom) {
                    int center = current.matrixSpace + current.expression.length() / 2;
                    for (int j = 1; j <= height; j++) {
                        matrix[level + j][center - j] = '/';
                        matrix[level + j][center + j] = '\\';
                    }
                    current.left.matrixSpace = current.matrixSpace - 1 - height + (current.expression.length() - current.left.expression.length()) / 2;
                    current.right.matrixSpace = current.matrixSpace + 1 + height + (current.expression.length() - current.right.expression.length()) / 2;
                    queue.add(current.left);
                    queue.add(current.right);
                }
            }
            level += height + 1;
        }
        for (char[] row : matrix) {
            for (char cell: row) {
                System.out.print(cell);
            }
            System.out.print("\n");
        }
    }

    static void SAT(Expression expression) {
        HashMap<Character, Boolean> interpretation = new HashMap<>();
        int successes = 0;
        for (int i = 0; i < Math.pow(2, expression.atoms.size()); i++) {
            String bin = Integer.toBinaryString(i);
            String binary = "0".repeat(expression.atoms.size() - bin.length()) + bin;
            int counter = 0;
            for (char symbol: expression.atoms) {
                if (binary.charAt(counter) - '0' == 0) {
                    interpretation.put(symbol, false);
                } else {
                    interpretation.put(symbol, true);
                }
                counter++;
            }
            if (expression.evaluate(interpretation)) successes++;
        }
        if (successes == 0) {
            System.out.println("Expression is a contradiction!");
        } else if (successes == Math.pow(2, expression.atoms.size())) {
            System.out.println("Expression is a tautology!");
        } else {
            System.out.println("Expression is satisfiable!");
        }
    }

    @Override
    public String toString() {
        return "Expression: " + expression + "\nLeft operation: " + left + "\nRight operation: " + right + "\nOperator: " + operator + "\nAtom? " + isAtom + "\n";
    }

}
