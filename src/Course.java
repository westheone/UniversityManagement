public record Course(String code, String title, Level level, int credits, boolean passFail) {
    public Course {
        if (code == null || code.isBlank() || title == null || title.isBlank()
                || level == null || credits <= 0) throw new IllegalArgumentException("Invalid course.");
    }
}
