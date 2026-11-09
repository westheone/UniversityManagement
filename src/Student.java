import java.util.*;

public abstract class Student {
    private final String id, firstName, lastName, phone, address;
    private final List<Enrollment> enrollments = new ArrayList<>();

    protected Student(String id, String firstName, String lastName, String phone, String address) {
        this.id = required(id); this.firstName = required(firstName);
        this.lastName = required(lastName); this.phone = required(phone); this.address = required(address);
    }
    static String required(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("All fields are required.");
        return value.strip();
    }
    public String id() { return id; }
    public String firstName() { return firstName; }
    public String lastName() { return lastName; }
    public String phone() { return phone; }
    public String address() { return address; }
    public abstract Level level();
    public abstract boolean canRegisterFor(Course course);
    public List<Enrollment> enrollments() { return List.copyOf(enrollments); }

    public String registrationStatus(Course course) {
        if (!canRegisterFor(course)) return "Not eligible";
        Enrollment latest = null;
        for (Enrollment e : enrollments) {
            if (!e.course().code().equals(course.code())) continue;
            if (e.grade() == null) return "Currently registered";
            latest = e;
        }
        if (latest == null) return "Available";
        return latest.grade().failed() ? "Retake available" : "Completed";
    }
    public Enrollment register(Course course) {
        String status = registrationStatus(course);
        if (!status.equals("Available") && !status.equals("Retake available"))
            throw new IllegalArgumentException("Cannot register: " + status + ".");
        Enrollment attempt = new Enrollment(course);
        enrollments.add(attempt);
        return attempt;
    }
    public void drop(Enrollment attempt) {
        if (!enrollments.contains(attempt)) throw new IllegalArgumentException("Registration not found.");
        if (attempt.grade() != null) throw new IllegalArgumentException("Only ungraded courses can be dropped.");
        enrollments.remove(attempt);
    }
    public boolean replaced(Enrollment attempt) {
        if (attempt.grade() == null) return false;
        int index = enrollments.indexOf(attempt);
        for (int i = index + 1; i < enrollments.size(); i++) {
            Enrollment next = enrollments.get(i);
            if (next.course().code().equals(attempt.course().code()) && next.grade() != null) return true;
        }
        return false;
    }
    public OptionalDouble gpa() {
        int points = 0, credits = 0;
        for (Enrollment e : enrollments) {
            if (e.grade() == null || e.course().passFail() || replaced(e)) continue;
            points += e.grade().points() * e.course().credits();
            credits += e.course().credits();
        }
        return credits == 0 ? OptionalDouble.empty() : OptionalDouble.of((double) points / credits);
    }
}
