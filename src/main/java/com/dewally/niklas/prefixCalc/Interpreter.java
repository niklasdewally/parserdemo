package prefixCalc;

import ast.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static prefixCalc.Lexer.lex;

public class Interpreter {
    public static int evaluate(Node node){
        List<Node> children = node.getChildren();
        List<Integer> arguments = new ArrayList<>();
        String function = "";
        for (Node child: children) {
            switch (child.getValue()) {
                case "FUNCTION" -> function = child.getChildren().get(0).getValue();
                case "OPERANDS" -> arguments = collectArguments(child);
            }
        }
        // APPLY THE FUNCTIONS!!
        switch (function){
            case "+" -> {return arguments.stream().reduce(0,(a,b)-> a+b);}
            case "-" -> {
                int first = arguments.get(0);
                arguments.remove(0);
                return first - arguments.stream().reduce(0,(a,b)-> a+b);
            }
            case "*" -> {
               return arguments.stream().reduce(1,(a,b)-> a*b);
            }

            case default -> throw new IllegalArgumentException("Unknown function " + function);
        }
    }

    private static List<Integer> collectArguments(Node operands) {
        List<Integer> arguments = new ArrayList<>();
        for (Node child : operands.getChildren()) {

            switch (child.getValue()) {
                case "INTEGER" -> arguments.add(Integer.valueOf(child.getChildren().get(0).getValue()));
                case "EXPRESSION" -> arguments.add(evaluate(child));
                case default -> throw new IllegalArgumentException("Type of operand " + child.getValue() + " not known.");
            }
        }
        return arguments;
    }
    public static void main(String[] args) {
        Parser parser = new Parser();
        Scanner stdin = new Scanner(System.in);

        while (stdin.hasNextLine()){
            Node astRoot = parser.run(lex(stdin.nextLine()));
            System.out.println("> " + evaluate(astRoot));
        }
    }
}
