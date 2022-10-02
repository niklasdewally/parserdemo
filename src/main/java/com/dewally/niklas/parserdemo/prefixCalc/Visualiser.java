package com.dewally.niklas.parserdemo.prefixCalc;

import com.dewally.niklas.parserdemo.ast.Node;
import com.dewally.niklas.parserdemo.graphviz.GraphvizNodeConverter;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static com.dewally.niklas.parserdemo.prefixCalc.Lexer.lex;

public class Visualiser {
    public static void main(String[] args) throws IOException {
        Scanner stdin = new Scanner(System.in);
        Parser parser = new Parser();
            try {
                Node astRoot = parser.runWithTrace(lex(stdin.nextLine()));
                Graphviz.fromGraph(GraphvizNodeConverter.nodeToGraphViz(astRoot)).render(Format.SVG).toFile(new File("graph.svg"));
            }
            catch (IllegalArgumentException e){
                System.out.println("Illegal input character " + e.getMessage());
        }

    }
}
