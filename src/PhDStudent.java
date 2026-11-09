public final class PhDStudent extends Student {
    public PhDStudent(String id, String first, String last, String phone, String address) {
        super(id, first, last, phone, address);
    }
    public Level level() { return Level.PhD; }
    public boolean canRegisterFor(Course course) { return true; }
}
