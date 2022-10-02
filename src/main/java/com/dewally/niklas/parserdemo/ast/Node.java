package com.dewally.niklas.parserdemo.ast;

import java.util.ArrayList;
import java.util.List;

public class Node {
   private Node parent;
   private List<Node> children;
    private String value;


    public Node(Node parent,String value) {
        this.parent = parent;
        this.children = new ArrayList<>();
        this.value = value;
    }

    public Node createChild(String name){
        Node node = new Node(this,name);
        children.add(node);
        return node;
    }

    public void removeChild(Node node){
        children.remove(node);
    }

    public String getValue() {
        return value;
    }

    public void deleteAllChildren() {
        children.removeIf(x->true);
    }

    public Node getParent() {
        return parent;
    }

    public List<Node> getChildren() {
        return children;
    }
}
