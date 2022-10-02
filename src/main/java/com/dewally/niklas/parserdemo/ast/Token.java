package com.dewally.niklas.parserdemo.ast;

public interface Token {
    default String getValue() {
        return "";
    }
}
