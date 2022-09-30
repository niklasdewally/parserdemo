package ast;

import java.util.ArrayList;
import java.util.List;

public class Node {
   Node parent;
   List<Node> children;

    public Node(Node parent) {
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public void addChild(Node node){
        if (!children.contains(node)){
           children.add(node) ;
        }
    }
}
