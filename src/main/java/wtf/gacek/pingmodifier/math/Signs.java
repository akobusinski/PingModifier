package wtf.gacek.pingmodifier.math;

public enum Signs {

    MORE_OR_EQUAL("<="),
    LESS_OR_EQUAL(">="),
    MORE("<"),
    LESS(">"),
    EQUAL("==");

    private final String sign;
    Signs(String sign) {
        this.sign = sign;
    }
    public String getSign() {
        return this.sign;
    }
}
