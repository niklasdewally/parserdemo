package com.dewally.niklas.parserdemo.graphviz;

import com.dewally.niklas.parserdemo.ast.INode;
import com.dewally.niklas.parserdemo.ast.Node;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.MutableGraph;

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;

public class GraphvizNodeConverter {

    /**
     * {@code nodeToGraphviz} turns a tree of {@link INode} objects into a graphviz {@link Graph} object, linking parent
     * to child.
     *
     * @param astRoot the root node of the tree to be drawn.
     * @return the tree as a {@link Graph}
     */
    public static Graph nodeToGraphviz(INode astRoot) {
        MutableGraph graph = mutGraph().setDirected(true);

        // Identify each node by its unique hashcode
        String nodeID = String.valueOf(astRoot.hashCode());

        // Label each node with its contents
        String nodeLabel = astRoot.getValue();

        graph = graph.add(mutNode(nodeID).add(attr("label", nodeLabel)));

        for (INode childNode : astRoot.getChildren()) {
            walkTree(childNode, graph);
        }
        return graph.toImmutable();
    }

    /* Recursively add links from parent-> child */
    private static void walkTree(INode node, MutableGraph graph) {

        String parentID = String.valueOf(node.getParent().hashCode());
        String nodeID = String.valueOf(node.hashCode());
        String nodeLabel = node.getValue();

        // Parent has already been drawn, so will already have a label.
        // We do not need to give it again.
        graph.add(mutNode(parentID)
                .addLink(mutNode(nodeID).add(attr("label", nodeLabel)))
        );

        for (INode childNode : node.getChildren()) {
            walkTree(childNode, graph);
        }
    }
}
