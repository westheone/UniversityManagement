public final class UniversitySystemTest {
    private static int checks;

    private static void check(boolean condition) {
        checks++;
        if (!condition)
            throw new AssertionError("Check " + checks + " failed.");
    }

    private static void rejects(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            checks++;
            return;
        }
        throw new AssertionError("Expected rejection.");
    }

    private static void near(Student student, double expected) {
        check(Math.abs(student.gpa().orElseThrow() - expected) < 0.00001);
    }

    public static void main(String[] args) {
        UniversitySystem system = new UniversitySystem();
        Student ug = system.login(" mOrGaN ", "1001"), phd = system.login("LEE", "1002");
        check(system.catalog().size() == 16);
        near(ug, 16.0 / 6);
        near(phd, 3.6);
        rejects(() -> system.login("Morgan", "01001"));
        rejects(() -> system.login("Lee", "1001"));
        rejects(() -> system.addStudent(" ", "Test", "123", "Street", Level.MS));
        rejects(() -> system.addStudent("New", "Test", "123", "Street", null));
        Student ms = system.addStudent(" New ", "Student", "123", "Street", Level.MS);
        check(ms.id().equals("1003") && ms.firstName().equals("New"));
        check(system.login("student", "1003") == ms && ms.gpa().isEmpty());
        check(system.catalog().stream().filter(ug::canRegisterFor).count() == 10);
        check(system.catalog().stream().filter(ms::canRegisterFor).count() == 13);
        check(system.catalog().stream().filter(phd::canRegisterFor).count() == 16);
        rejects(() -> ug.register(system.course("CS501")));
        rejects(() -> ms.register(system.course("CS799")));
        rejects(() -> ug.register(system.course("CS101")));
        rejects(() -> ug.register(system.course("CS302")));
        rejects(() -> ug.drop(ug.enrollments().get(0)));
        Enrollment retake = ug.register(system.course("CS301"));
        near(ug, 16.0 / 6);
        rejects(() -> ug.register(system.course("CS301")));
        ug.drop(retake);
        near(ug, 16.0 / 6);
        Enrollment replacement = ug.register(system.course("CS301"));
        replacement.assignGrade(Grade.A);
        near(ug, 20.0 / 6);
        check(ug.replaced(ug.enrollments().get(5)));
        rejects(() -> replacement.assignGrade(Grade.B));
        rejects(() -> ug.register(system.course("CS301")));
        Enrollment project = phd.register(system.course("CS590"));
        rejects(() -> project.assignGrade(Grade.A));
        project.assignGrade(Grade.FAIL);
        near(phd, 3.6);
        Enrollment second = phd.register(system.course("CS590"));
        second.assignGrade(Grade.PASS);
        near(phd, 3.6);
        rejects(() -> phd.register(system.course("CS590")));
        Enrollment failed = ms.register(system.course("CS101"));
        failed.assignGrade(Grade.F);
        Enrollment failedAgain = ms.register(system.course("CS101"));
        failedAgain.assignGrade(Grade.F);
        near(ms, 0);
        check(ms.replaced(failed));
        Enrollment finalAttempt = ms.register(system.course("CS101"));
        finalAttempt.assignGrade(Grade.B);
        near(ms, 3);
        Student weighted = system.addStudent("Weighted", "Test", "123", "Street", Level.PhD);
        weighted.register(new Course("TEST1", "Four credits", Level.UG, 4, false)).assignGrade(Grade.A);
        weighted.register(new Course("TEST2", "Two credits", Level.UG, 2, false)).assignGrade(Grade.C);
        near(weighted, 20.0 / 6);
        rejects(() -> weighted.register(new Course("TEST1", "Same code", Level.UG, 4, false)));
        check(new UniversitySystem().login("Morgan", "1001").enrollments().size() == 7);
        System.out.println("Passed " + checks + " model checks.");
    }
}
