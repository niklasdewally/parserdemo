package com.dewally.niklas.parserdemo.ast;

import com.dewally.niklas.parserdemo.graphviz.GraphvizNodeConverter;
import guru.nidi.graphviz.attribute.For;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.model.Factory.node;

public class TracingNode implements INode {
    // RISKY!!!!
    // This is incremented everytime a graph is drawn - not parallel safe!
    private static int diagramIndex = 0;
    /* The root of the tree.
     * The graphviz visualisation code draws the tree relative to this node.
     */
    private final TracingNode astRoot;
    private final TracingNode parent;
    private final List<INode> children;
    private final String value;

    protected TracingNode(TracingNode astRoot, TracingNode parent, String value) {
        this.astRoot = astRoot;
        this.parent = parent;
        this.value = value;
        this.children = new ArrayList<>();
    }


    // Used by the above static factory to create the root node of the ast.
    private TracingNode(String value) {
        this.astRoot = this;
        this.parent = null;
        this.value = value;
        this.children = new ArrayList<>();
    }

    protected static TracingNode createASTRoot(String value) {
        resetDiagramCount();
        return new TracingNode(value);
    }

    protected static void resetDiagramCount() {
        diagramIndex = 0;
    }

    @Override
    public INode createChild(String value) {
        TracingNode child = new TracingNode(astRoot, this, value);
        children.add(child);
        generateDiagram(child);
        return child;
    }

    @Override
    public void removeChild(INode node) {
        children.remove(node);
        generateDiagram(this);
    }

    @Override
    public INode getParent() {
        return parent;
    }

    @Override
    public List<INode> getChildren() {
        return children;
    }

    @Override
    public String getValue() {
        return value;
    }

    // TODO: Current-node will be highlighted
    private void generateDiagram(TracingNode highlightedNode) {

        try {

            Graph graph = GraphvizNodeConverter.nodeToGraphviz(astRoot);
            String highlightedNodeID = String.valueOf(highlightedNode.hashCode());

            // add highlighted node
            graph = graph.with(node(highlightedNodeID).with(attr("color","red")));

            Graphviz.fromGraph(graph).scale(2).render(Format.PNG).toFile(new File("graph-" + String.format("%03d",diagramIndex)));
            diagramIndex++;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
