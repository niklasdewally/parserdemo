package prefixCalc.tokens;

public interface Token {
    default String getValue() {
        return "";
    }
}
