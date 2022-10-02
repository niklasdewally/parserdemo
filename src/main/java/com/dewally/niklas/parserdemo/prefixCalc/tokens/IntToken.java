package com.dewally.niklas.parserdemo.prefixCalc.tokens;

public class IntToken implements Token{
    private String value;

    @Override
    public String getValue() {
        return this.value;
    }

    public IntToken(String val) {
        this.value = val;
    }
}
