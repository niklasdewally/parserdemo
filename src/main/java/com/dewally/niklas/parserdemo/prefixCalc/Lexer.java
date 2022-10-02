package com.dewally.niklas.parserdemo.prefixCalc;

import com.dewally.niklas.parserdemo.ast.Token;
import com.dewally.niklas.parserdemo.prefixCalc.tokens.*;

import java.util.ArrayList;
import java.util.List;


enum LexerStates {
    NORM, NUM
}

public class Lexer {

    public static List<Token> lex(String input){

        LexerStates state = LexerStates.NORM;
        List<Token> tokens = new ArrayList<>();
        String num = "";

        for (char a: input.toCharArray()){
            switch (state) {
                case NORM -> {
                    if (Character.isDigit(a)) {
                       state = LexerStates.NUM;
                       num = Character.toString(a);
                    }
                    else {
                        norm(tokens, a);
                    }
                }

                case NUM -> {
                    if (Character.isDigit(a)) {
                       num = num + a;
                    }
                    else {
                       state = LexerStates.NORM;
                       tokens.add(new IntToken(num));
                        norm(tokens, a);
                    }
                }
            }
        }

        return tokens;
    }

    private static void norm(List<Token> tokens, char a) {
        switch (a) {
            case '(' -> tokens.add(new OpenBracketToken());
            case ' ' -> {}
            case ')' -> tokens.add(new ClosedBracketToken());
            case '+' -> tokens.add(new AddToken());
            case '-' -> tokens.add(new SubtractToken());
            case '*' -> tokens.add(new MultiplyToken());
            default -> {throw new IllegalArgumentException(Character.toString(a));}
        }
    }


    public static void main(String[] args) {
        System.out.println(lex("(+ 1 2 (- 3 4))"));
    }
}
