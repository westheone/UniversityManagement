import java.util.*;

public final class UniversitySystem {
    private final List<Course> catalog = List.of(
            new Course("CS101", "Introduction to Programming", Level.UG, 3, false),
            new Course("CS102", "Object-Oriented Programming", Level.UG, 3, false),
            new Course("CS201", "Data Structures", Level.UG, 3, false),
            new Course("CS202", "Discrete Mathematics", Level.UG, 3, false),
            new Course("CS203", "Computer Organization", Level.UG, 3, false),
            new Course("CS301", "Algorithms", Level.UG, 3, false),
            new Course("CS302", "Database Systems", Level.UG, 3, false),
            new Course("CS303", "Operating Systems", Level.UG, 3, false),
            new Course("CS304", "Computer Networks", Level.UG, 3, false),
            new Course("CS305", "Software Engineering", Level.UG, 3, false),
            new Course("CS501", "Advanced Algorithms", Level.MS, 3, false),
            new Course("CS502", "Machine Learning", Level.MS, 3, false),
            new Course("CS590", "MS Project", Level.MS, 3, true),
            new Course("CS701", "Advanced Distributed Systems", Level.PhD, 3, false),
            new Course("CS702", "Research Methods in Computer Science", Level.PhD, 3, false),
            new Course("CS799", "PhD Dissertation", Level.PhD, 6, true));

    private final Map<String, Student> students = new LinkedHashMap<>();

    private int nextId = 1001;

    public UniversitySystem() {
        Student ug = addStudent("Alex", "Morgan", "555-0101", "101 College Avenue", Level.UG);
        seed(ug, "CS101", Grade.A);
        seed(ug, "CS102", Grade.B);
        seed(ug, "CS201", Grade.A);
        seed(ug, "CS202", Grade.B);
        seed(ug, "CS203", Grade.C);
        seed(ug, "CS301", Grade.F);
        ug.register(course("CS302"));
        Student phd = addStudent("Jordan", "Lee", "555-0102", "202 University Lane", Level.PhD);
        seed(phd, "CS301", Grade.A);
        seed(phd, "CS302", Grade.B);
        seed(phd, "CS501", Grade.A);
        seed(phd, "CS502", Grade.B);
        seed(phd, "CS701", Grade.A);
        seed(phd, "CS590", Grade.FAIL);
        phd.register(course("CS799"));
    }

    private void seed(Student student, String code, Grade grade) {
        student.register(course(code)).assignGrade(grade);
    }

    public List<Course> catalog() {
        return catalog;
    }

    public Course course(String code) {
        return catalog.stream().filter(c -> c.code().equals(code)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown course."));
    }

    public Student addStudent(String first, String last, String phone, String address, Level level) {
        if (level == null)
            throw new IllegalArgumentException("Select a student level.");
        String id = Integer.toString(nextId);
        Student student = switch (level) {
            case UG -> new UndergraduateStudent(id, first, last, phone, address);
            case MS -> new MSStudent(id, first, last, phone, address);
            case PhD -> new PhDStudent(id, first, last, phone, address);
        };
        students.put(id, student);
        nextId++;
        return student;
    }

    public Student login(String last, String id) {
        Student student = students.get(id);
        if (last == null || student == null || !student.lastName().equalsIgnoreCase(last.strip()))
            throw new IllegalArgumentException("Last name or student ID is incorrect.");
        return student;
    }
}
