package com.dewally.niklas.parserdemo.graphviz;

import com.dewally.niklas.parserdemo.ast.Node;

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.model.Factory.*;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.MutableGraph;

import java.io.File;
import java.io.IOException;


public class GraphvizNodeConverter {

    public static Graph nodeToGraphViz(Node node){
        MutableGraph graph = mutGraph().setDirected(true);
        graph = graph.add(mutNode(String.valueOf(node.hashCode())).add(attr("label",node.getValue())));
        for (Node childNode: node.getChildren()) {
            walkTree(childNode,graph);
        }
        return graph.toImmutable();
    }

    private static void walkTree(Node node,MutableGraph graph){
        String parentName = String.valueOf(node.getParent().hashCode());
        String nodeName = String.valueOf(node.hashCode());

        graph.add(mutNode(parentName)
                  .addLink(mutNode(nodeName).add(attr("label",node.getValue())))
        );

        for (Node childNode: node.getChildren()) {
            walkTree(childNode,graph);
        }
    }


    public static void main(String[] args) throws IOException {
        Node node = new Node(null,"Hello");
        node.createChild("Hello").createChild("Hello2");
        node.createChild("foo").createChild("bar");
        Graph g = nodeToGraphViz(node);
        Graphviz.fromGraph(g).render(Format.SVG_STANDALONE).toFile(new File("h.svg"));
    }
}
