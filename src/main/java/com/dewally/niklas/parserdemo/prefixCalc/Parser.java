package com.dewally.niklas.parserdemo.prefixCalc;

import com.dewally.niklas.parserdemo.ast.Node;
import com.dewally.niklas.parserdemo.ast.Token;
import com.dewally.niklas.parserdemo.graphviz.GraphvizNodeConverter;
import com.dewally.niklas.parserdemo.prefixCalc.tokens.*;
import guru.nidi.graphviz.attribute.For;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.dewally.niklas.parserdemo.prefixCalc.Lexer.lex;


/*
 * expression -> OPENBRACKET function args CLOSEDBRACKET
 *
 * function -> ADD
 * 	| SUBTRACT
 * 	| MULTIPLY
 *
 * args -> args INT
 * 	| args expression
 * 	| NIL
 *
 */

public class Parser {
    private int currentTokenIndex;
    private List<Token> tokens;
    private Node root;
    private List<Node> tracedTrees;
    private boolean trace = false;
    private int traceFileNumber = 0;

    public Parser() {
    }

    public Node run(List<Token> tokens){
        this.tokens = tokens;
        currentTokenIndex = 0;
        tracedTrees = new ArrayList<>();

        root = new Node(null,"EXPRESSION");

        try {
            if (!expression(root)) {
                //System.out.println("Invalid at token number " + (currentTokenIndex + 1) + " " + tokens.get(currentTokenIndex).getClass().getSimpleName());
                throw new IllegalArgumentException();
            }

            // If there are trailing characters at the end , error
            if (currentTokenIndex != tokens.size() -1) {
                //System.out.println("Invalid at token number " + (currentTokenIndex + 1) + " " + tokens.get(currentTokenIndex).getClass().getSimpleName());
                throw new IllegalArgumentException();
            }

            //System.out.println("Valid!");
        }
        catch (IndexOutOfBoundsException e){
            throw new IllegalArgumentException();
            //System.out.println("Expected more input!");
        }

        return root;
    }

    public Node runWithTrace(List<Token> tokens) {
       trace = true;
       traceFileNumber = 0;
       return run(tokens);
    }


    private boolean expression(Node currentNode) {
        // Go through rule, checking token by token

        if (!tokens.get(currentTokenIndex).getClass().equals(OpenBracketToken.class)) {
            return false;
        }
        currentNode.createChild("(");
        onGraphUpdate();

        // Now check the next token
        currentTokenIndex++;

        // Try to recursively resolve function to see if this is what we are expecting.
        Node functionNode = currentNode.createChild("FUNCTION");
        onGraphUpdate();

        if (!terminalFunction(functionNode)) {
            currentNode.removeChild(functionNode);
            onGraphUpdate();
            return false;
        }
        currentTokenIndex++;


        // Try to recursively resolve args to see if this is what we are expecting.
        int beforeIndex = currentTokenIndex;
        Node argsNode = currentNode.createChild("OPERANDS");
        onGraphUpdate();
        if(args(argsNode)) {
            currentTokenIndex++;
        }
        else{
            // Backtrack
            currentTokenIndex = beforeIndex;
            currentNode.removeChild(argsNode);
            onGraphUpdate();
            return false;
        }
        // However, this can also be null
        currentNode.createChild(")");
        if (!tokens.get(currentTokenIndex).getClass().equals(ClosedBracketToken.class)) {
            return false;
        }

        onGraphUpdate();
    return true;
    }

    private boolean terminalFunction(Node currentNode) {
        // Terminals!
        Token currentToken = tokens.get(currentTokenIndex);
        switch (currentToken) {
            case AddToken a -> {
                currentNode.createChild("+");
                onGraphUpdate();
                return true;
            }
            case SubtractToken b -> {
                currentNode.createChild("-");
                onGraphUpdate();
                return true;
            }
            case MultiplyToken c -> {
                currentNode.createChild("*");
                onGraphUpdate();
                return true;
            }
            case default -> {
                return false;
            }
        }
    }


/*
    args -> INT args'
	| expression args'

args' -> INT args'
	| expression args'
	| NIL

I do it this way so I avoid having args -> args INT, which is left recursion which makes this run infinitely!

     */
    private boolean args(Node currentNode) {
        int beforeIndex = currentTokenIndex;
        Node exprNode = currentNode.createChild("EXPRESSION");
        onGraphUpdate();
        if (expression(exprNode)) {
            currentTokenIndex++;
            return argsP(currentNode);
        }
        else {
            // Backtrack
            currentTokenIndex = beforeIndex;
            currentNode.removeChild(exprNode);
            onGraphUpdate();
        }
        Node intNode = currentNode.createChild("INTEGER");
        onGraphUpdate();
        if (terminalInteger(intNode)) {
            currentTokenIndex++;
            return argsP(currentNode);
        }
        else {
            currentNode.removeChild(intNode);
            onGraphUpdate();
        }
        return false;
    }

    private boolean argsP(Node currentNode) {
        Node intNode = currentNode.createChild("INTEGER");
        onGraphUpdate();
        if (terminalInteger(intNode)) {
            currentTokenIndex++;
            return argsP(currentNode);
        }
        else  {
            currentNode.removeChild(intNode);
            onGraphUpdate();
        }

        int beforeIndex = currentTokenIndex;
        Node exprNode = currentNode.createChild("EXPRESSION");
        onGraphUpdate();
        if (expression(exprNode)) {
            currentTokenIndex++;
            return argsP(currentNode);
        }
        else {
            currentTokenIndex = beforeIndex;
            currentNode.removeChild(exprNode);
            onGraphUpdate();
        }
        // NULL condition
        // THIS IS A HACK! - upper layers expect argsP to use up a token, but null isnt a token!
        currentTokenIndex--;
        return true;
    }
    private boolean terminalInteger(Node currentNode){
        if (tokens.get(currentTokenIndex).getClass().equals(IntToken.class)) {
           currentNode.createChild(tokens.get(currentTokenIndex).getValue()) ;
           onGraphUpdate();
            return true;
        }
        return false;
    }

    private void onGraphUpdate(){
        // if we are tracing, draw the graph of the current state;
        if (trace) {
            try {
                Graphviz.fromGraph(GraphvizNodeConverter.nodeToGraphViz(root)).render(Format.SVG).toFile(new File("graph-" + traceFileNumber + ".svg"));
                traceFileNumber++;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void main(String[] args) {
        Scanner stdin = new Scanner(System.in);
        Parser parser = new Parser();
        while (stdin.hasNextLine()) {
            try {
                Node astRoot = parser.run(lex(stdin.nextLine()));
                System.out.println();
            }
            catch (IllegalArgumentException e){
                System.out.println("Illegal input character " + e.getMessage());
            }
        }
    }
}
