public final class Enrollment {
    private final Course course;
    private Grade grade;
    public Enrollment(Course course) {
        this.course = java.util.Objects.requireNonNull(course);
    }
    public Course course() { return course; }
    public Grade grade() { return grade; }
    public void assignGrade(Grade grade) {
        java.util.Objects.requireNonNull(grade);
        if (this.grade != null) throw new IllegalArgumentException("This attempt is already graded.");
        if (course.passFail() != (grade == Grade.PASS || grade == Grade.FAIL))
            throw new IllegalArgumentException("Grade does not match the course grading type.");
        this.grade = grade;
    }
}
