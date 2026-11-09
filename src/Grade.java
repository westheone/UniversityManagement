public enum Grade {
    A(4), B(3), C(2), D(1), F(0), PASS(-1), FAIL(-1);
    private final int points;
    Grade(int points) { this.points = points; }
    public int points() { return points; }
    public boolean failed() { return this == F || this == FAIL; }
    @Override public String toString() {
        return this == PASS ? "Pass" : this == FAIL ? "Fail" : name();
    }
}
