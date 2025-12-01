package core.entity;

public enum FunctionType {
    ANALYTIC("analytic"),
    TABULATED("tabulated");

    private final String value;

    FunctionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
