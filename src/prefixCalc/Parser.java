package prefixCalc;

import prefixCalc.tokens.*;

import java.util.List;
import java.util.Scanner;

import static prefixCalc.Lexer.lex;

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

    public Parser() {
    }

    public void run(List<Token> tokens){
        this.tokens = tokens;
        currentTokenIndex = 0;

        try {
            if (!expression()) {
                System.out.println("Invalid at token number " + (currentTokenIndex + 1) + " " + tokens.get(currentTokenIndex).getClass().getSimpleName());
                return;
            }

            // If there are trailing characters at the end , error
            if (currentTokenIndex != tokens.size() - 1) {
                System.out.println("Invalid at token number " + (currentTokenIndex + 1) + " " + tokens.get(currentTokenIndex).getClass().getSimpleName());
                return;
            }

            System.out.println("Valid!");
        }
        catch (IndexOutOfBoundsException e){
            System.out.println("Expected more input!");
        }
    }

    private boolean expression() {
        // Go through rule, checking token by token

        if (!tokens.get(currentTokenIndex).getClass().equals(OpenBracketToken.class)) {
            return false;
        }

        // Now check the next token
        currentTokenIndex++;

        // Try to recursively resolve function to see if this is what we are expecting.
        if (!terminalFunction()) {
            return false;
        }

        // Now check the next token
        currentTokenIndex++;


        // Try to recursively resolve args to see if this is what we are expecting.
        int beforeIndex = currentTokenIndex;
        if(args()) {
            currentTokenIndex++;
        }
        else{
            // Backtrack
            currentTokenIndex = beforeIndex;
        }
        // However, this can also be null
        if (!tokens.get(currentTokenIndex).getClass().equals(ClosedBracketToken.class))
            return false;

        return true;
    }

    private boolean terminalFunction() {
        // Terminals!
        Token currentToken = tokens.get(currentTokenIndex);
        switch (currentToken) {
            case AddToken a -> {
                return true;
            }
            case SubtractToken b -> {
                return true;
            }
            case MultiplyToken c -> {
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
    private boolean args() {
        int beforeIndex = currentTokenIndex;
        if (expression()) {
            currentTokenIndex++;
            return argsP();
        }
        else {
            // Backtrack
            currentTokenIndex = beforeIndex;
        }
        if (terminalInteger()) {
            currentTokenIndex++;
            return argsP();
        }
        return false;
    }

    private boolean argsP() {
        if (terminalInteger()) {
            currentTokenIndex++;
            return argsP();
        }
        int beforeIndex = currentTokenIndex;
        if (expression()) {
            currentTokenIndex++;
            argsP();
        }
        else {
            currentTokenIndex = beforeIndex;
        }
        // NULL condition
        // THIS IS A HACK! - upper layers expect argsP to use up a token, but null isnt a token!
        currentTokenIndex--;
        return true;
    }
    private boolean terminalInteger(){
        if (tokens.get(currentTokenIndex).getClass().equals(IntToken.class)) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        Scanner stdin = new Scanner(System.in);
        Parser parser = new Parser();
        while (stdin.hasNextLine()) {
            try {
                parser.run(lex(stdin.nextLine()));
            }
            catch (IllegalArgumentException e){
                System.out.println("Illegal input character " + e.getMessage());
            }
        }
    }
}
