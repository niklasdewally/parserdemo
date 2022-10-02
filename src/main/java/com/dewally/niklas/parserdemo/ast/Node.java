package com.dewally.niklas.parserdemo.ast;

import java.util.ArrayList;
import java.util.List;

public class Node implements INode {
    private final Node parent;
    private final List<INode> children;
    private final String value;


    public Node(Node parent, String value) {
        this.parent = parent;
        this.children = new ArrayList<>();
        this.value = value;
    }

    @Override
    public INode createChild(String name) {
        Node node = new Node(this, name);
        children.add(node);
        return node;
    }

    @Override
    public void removeChild(INode node) {
        children.remove(node);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public INode getParent() {
        return parent;
    }

    public List<INode> getChildren() {
        return children;
    }
}
