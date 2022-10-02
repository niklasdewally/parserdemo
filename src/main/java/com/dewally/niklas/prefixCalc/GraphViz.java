package prefixCalc;

import ast.Node;

import javax.swing.plaf.synth.SynthTableHeaderUI;
import java.util.Scanner;

import static prefixCalc.Lexer.lex;

public class GraphViz {
    public static void main(String[] args) {
        Scanner stdin = new Scanner(System.in);
        Parser parser = new Parser();
            try {
                Node astRoot = parser.run(lex(stdin.nextLine()));

                Node currNode = astRoot;
                System.out.println("digraph {");
                print(currNode);
                System.out.println("}");
            }
            catch (IllegalArgumentException e){
                System.out.println("Illegal input character " + e.getMessage());
        }

    }

    private static void print(Node currNode){
        System.out.println(currNode.hashCode() + " [label=\"" + currNode.getValue() + "\"]");
        currNode.getChildren().forEach((Node n) -> {
                System.out.println(currNode.hashCode() + " -> " + n.hashCode());
                if (n.getChildren().size() > 0) {
                    print(n);
                }
                else {
                    System.out.println(n.hashCode() + " [label=\"" + n.getValue() + "\"]");
                }
        });
    }
}
