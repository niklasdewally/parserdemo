package com.dewally.niklas.parserdemo.prefixCalc.tokens;

public interface Token {
    default String getValue() {
        return "";
    }
}
