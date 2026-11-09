public final class MSStudent extends Student {
    public MSStudent(String id, String first, String last, String phone, String address) {
        super(id, first, last, phone, address);
    }
    public Level level() { return Level.MS; }
    public boolean canRegisterFor(Course course) { return course.level() != Level.PhD; }
}
