package com.dewally.niklas.parserdemo.prefixCalc;

import java.io.IOException;
import java.util.Scanner;

import static com.dewally.niklas.parserdemo.prefixCalc.Lexer.lex;

public class Visualiser {
    public static void main(String[] args) throws IOException {
        Scanner stdin = new Scanner(System.in);
        Parser parser = new Parser(true);
        try {
            parser.run(lex(stdin.nextLine()));
        } catch (IllegalArgumentException e) {
            System.out.println("Illegal input character " + e.getMessage());
        }

    }
}
