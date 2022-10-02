package com.dewally.niklas.parserdemo.ast;

import java.util.List;

/**
 * {@code INode} represents a node in an AST.
 * An {@code INode} has exactly one parent, but can have any number of children.
 */
public interface INode {
    /**
     * Create a new child node of this node, with a given value.
     *
     * @param value - the value to store inside the child node.
     * @return the created child node.
     */
    INode createChild(String value);

    /**
     * Remove the specified node as a child node.
     *
     * @param node - the node to remove from this nodes' tree.
     */
    void removeChild(INode node);

    /**
     * @return the parent of this node.
     */
    INode getParent();

    /**
     * @return a {@link List} of all children of this node.
     */
    List<INode> getChildren();

    /**
     * Get the value of this node.
     *
     * @return the String value of this node.
     */
    String getValue();
}
