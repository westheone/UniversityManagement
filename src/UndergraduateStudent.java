public final class UndergraduateStudent extends Student {
    public UndergraduateStudent(String id, String first, String last, String phone, String address) {
        super(id, first, last, phone, address);
    }
    public Level level() { return Level.UG; }
    public boolean canRegisterFor(Course course) { return course.level() == Level.UG; }
}
