package com.dewally.niklas.parserdemo.ast;

public class ASTFactory {

    public ASTFactory() {
    }

    /**
     * Create a new AST.
     *
     * @return the root node of the AST.
     */
    public INode createAST() {
        return createAST(false);
    }

    /**
     * Create a new AST.
     *
     * @param doTracing if true, a graphviz diagram will be generated for the AST for every operation done on it.
     * @return the root node of the AST.
     */
    public INode createAST(boolean doTracing) {
        if (doTracing) {
            return TracingNode.createASTRoot("");
        }
        return new Node(null, "");
    }


}
