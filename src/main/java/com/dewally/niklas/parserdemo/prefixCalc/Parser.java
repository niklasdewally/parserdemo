package com.dewally.niklas.parserdemo.prefixCalc;

import com.dewally.niklas.parserdemo.ast.ASTFactory;
import com.dewally.niklas.parserdemo.ast.INode;
import com.dewally.niklas.parserdemo.ast.Token;
import com.dewally.niklas.parserdemo.prefixCalc.tokens.*;

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
    private INode root;
    private boolean doTrace;
    private final int traceFileNumber = 0;
    private ASTFactory astFactory;

    public Parser() {
        new Parser(false);
    }

    public Parser(boolean doTrace) {
        this.doTrace = doTrace;
        astFactory = new ASTFactory();
    }

    public static void main(String[] args) {
        Scanner stdin = new Scanner(System.in);
        Parser parser = new Parser();
        while (stdin.hasNextLine()) {
            try {
                INode astRoot = parser.run(lex(stdin.nextLine()));
                System.out.println();
            } catch (IllegalArgumentException e) {
                System.out.println("Illegal input character " + e.getMessage());
            }
        }
    }

    public INode run(List<Token> tokens) {
        this.tokens = tokens;
        currentTokenIndex = 0;

        root = astFactory.createAST(doTrace);

        try {
            if (!expression(root)) {
                //System.out.println("Invalid at token number " + (currentTokenIndex + 1) + " " + tokens.get(currentTokenIndex).getClass().getSimpleName());
                throw new IllegalArgumentException();
            }

            // If there are trailing characters at the end , error
            if (currentTokenIndex != tokens.size() - 1) {
                //System.out.println("Invalid at token number " + (currentTokenIndex + 1) + " " + tokens.get(currentTokenIndex).getClass().getSimpleName());
                throw new IllegalArgumentException();
            }

            //System.out.println("Valid!");
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException();
            //System.out.println("Expected more input!");
        }

        return root;
    }

    private boolean expression(INode currentNode) {
        // Go through rule, checking token by token

        if (!tokens.get(currentTokenIndex).getClass().equals(OpenBracketToken.class)) {
            return false;
        }
        currentNode.createChild("(");

        // Now check the next token
        currentTokenIndex++;

        // Try to recursively resolve function to see if this is what we are expecting.
        INode functionNode = currentNode.createChild("FUNCTION");

        if (!terminalFunction(functionNode)) {
            currentNode.removeChild(functionNode);
            return false;
        }
        currentTokenIndex++;


        // Try to recursively resolve args to see if this is what we are expecting.
        int beforeIndex = currentTokenIndex;
        INode argsNode = currentNode.createChild("OPERANDS");
        if (args(argsNode)) {
            currentTokenIndex++;
        } else {
            // Backtrack
            currentTokenIndex = beforeIndex;
            currentNode.removeChild(argsNode);
            return false;
        }
        // However, this can also be null
        currentNode.createChild(")");
        return tokens.get(currentTokenIndex).getClass().equals(ClosedBracketToken.class);
    }

    private boolean terminalFunction(INode currentNode) {
        // Terminals!
        Token currentToken = tokens.get(currentTokenIndex);
        switch (currentToken) {
            case AddToken a -> {
                currentNode.createChild("+");
                return true;
            }
            case SubtractToken b -> {
                currentNode.createChild("-");
                return true;
            }
            case MultiplyToken c -> {
                currentNode.createChild("*");
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
    private boolean args(INode currentNode) {
        int beforeIndex = currentTokenIndex;
        INode exprNode = currentNode.createChild("EXPRESSION");

        if (expression(exprNode)) {
            currentTokenIndex++;
            return argsP(currentNode);
        } else {
            // Backtrack
            currentTokenIndex = beforeIndex;
            currentNode.removeChild(exprNode);
        }
        INode intNode = currentNode.createChild("INTEGER");
        if (terminalInteger(intNode)) {
            currentTokenIndex++;
            return argsP(currentNode);
        } else {
            currentNode.removeChild(intNode);
        }
        return false;
    }

    private boolean argsP(INode currentNode) {
        INode intNode = currentNode.createChild("INTEGER");
        if (terminalInteger(intNode)) {
            currentTokenIndex++;
            return argsP(currentNode);
        } else {
            currentNode.removeChild(intNode);
        }

        int beforeIndex = currentTokenIndex;
        INode exprNode = currentNode.createChild("EXPRESSION");
        if (expression(exprNode)) {
            currentTokenIndex++;
            return argsP(currentNode);
        } else {
            currentTokenIndex = beforeIndex;
            currentNode.removeChild(exprNode);
        }
        // NULL condition
        // THIS IS A HACK! - upper layers expect argsP to use up a token, but null isnt a token!
        currentTokenIndex--;
        return true;
    }

    private boolean terminalInteger(INode currentNode) {
        if (tokens.get(currentTokenIndex).getClass().equals(IntToken.class)) {
            currentNode.createChild(tokens.get(currentTokenIndex).getValue());
            return true;
        }
        return false;
    }
}
