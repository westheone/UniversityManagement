import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public final class UniversityApp {
    private final UniversitySystem system = new UniversitySystem();
    private final JFrame frame = new JFrame("University Management System");
    private Student student;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UniversityApp().start());
    }
    private void start() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(900, 650));
        frame.setSize(1100, 760);
        welcome(); frame.setLocationRelativeTo(null); frame.setVisible(true);
    }
    private void display(JComponent content) {
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        frame.setContentPane(content); frame.revalidate(); frame.repaint();
    }
    private JButton button(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(event -> {
            try { action.run(); }
            catch (IllegalArgumentException error) {
                JOptionPane.showMessageDialog(frame, error.getMessage(), "Please check", JOptionPane.ERROR_MESSAGE);
            }
        });
        return button;
    }
    private void welcome() {
        JPanel page = new JPanel(new BorderLayout(12, 12));
        page.add(new JLabel("University Management System", SwingConstants.CENTER), BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane();
        JTextField last = new JTextField(24), id = new JTextField(24);
        JPanel login = new JPanel(new GridBagLayout());
        addField(login, 0, "Last name", last); addField(login, 1, "Student ID", id);
        addRow(login, 2, button("Log In", () -> {
            student = system.login(last.getText(), id.getText()); dashboard(0);
        }));
        addRow(login, 3, new JLabel("Demo: Morgan / 1001 (UG) or Lee / 1002 (PhD)"));
        tabs.addTab("Log In", login);

        JPanel signup = new JPanel(new GridBagLayout());
        JTextField first = new JTextField(24), surname = new JTextField(24);
        JTextField phone = new JTextField(24), address = new JTextField(24);
        JComboBox<Level> level = new JComboBox<>(Level.values()); level.setSelectedIndex(-1);
        addField(signup, 0, "First name", first); addField(signup, 1, "Last name", surname);
        addField(signup, 2, "Phone number", phone); addField(signup, 3, "Address", address);
        addField(signup, 4, "Student level", level);
        addRow(signup, 5, button("Create Student", () -> {
            student = system.addStudent(first.getText(), surname.getText(), phone.getText(),
                address.getText(), (Level) level.getSelectedItem());
            JOptionPane.showMessageDialog(frame, "Welcome, " + student.firstName() + " " + student.lastName()
                + "!\nYour student ID is " + student.id() + ". Use it to log in during this session.");
            dashboard(0);
        }));
        tabs.addTab("Create New Student", signup);
        page.add(tabs, BorderLayout.CENTER);
        page.add(new JLabel("Session only: records reset when the program closes."), BorderLayout.SOUTH);
        display(page);
    }
    private void addField(JPanel panel, int row, String text, JComponent input) {
        JLabel label = new JLabel(text + ":"); label.setLabelFor(input);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8); c.anchor = GridBagConstraints.WEST;
        c.gridx = 0; c.gridy = row; panel.add(label, c);
        c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL; panel.add(input, c);
    }
    private void addRow(JPanel panel, int row, JComponent component) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = row; c.gridwidth = 2; c.insets = new Insets(12, 8, 8, 8);
        panel.add(component, c);
    }
    private JTable table(String[] columns, Object[][] rows) {
        JTable table = new JTable(new DefaultTableModel(rows, columns) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28); table.setFillsViewportHeight(true);
        if (columns.length > 1) table.getColumnModel().getColumn(1).setPreferredWidth(310);
        table.getAccessibleContext().setAccessibleName(columns[0] + " records");
        return table;
    }
    private void dashboard(int selectedTab) {
        JPanel page = new JPanel(new BorderLayout(12, 12));
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 4));
        header.add(new JLabel(student.firstName() + " " + student.lastName()));
        header.add(new JLabel("Student ID: " + student.id()));
        header.add(button("Log Out", () -> { student = null; welcome(); }));
        page.add(header, BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Course Catalog", catalog()); tabs.addTab("My Record", record());
        tabs.setSelectedIndex(selectedTab); page.add(tabs, BorderLayout.CENTER); display(page);
    }
    private JPanel catalog() {
        List<Course> courses = system.catalog().stream().filter(student::canRegisterFor).toList();
        Object[][] rows = new Object[courses.size()][];
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            rows[i] = new Object[]{c.code(), c.title(), c.level(), c.credits(),
                c.passFail() ? "Pass/Fail" : "A-F", student.registrationStatus(c)};
        }
        JTable table = table(new String[]{"Code", "Title", "Level", "Credits", "Grading", "Status"}, rows);
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(new JLabel("Select an eligible course, project, or dissertation."), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton register = button("Register / Retake", () -> {
            int row = table.getSelectedRow();
            if (row < 0) throw new IllegalArgumentException("Select a catalog entry first.");
            student.register(courses.get(row));
            dashboard(0);
            StringBuilder summary = new StringBuilder(student.firstName() + " " + student.lastName()
                + " | Student ID: " + student.id() + "\n\nCurrently registered:\n");
            for (Enrollment e : student.enrollments()) if (e.grade() == null)
                summary.append(e.course().code()).append(" - ").append(e.course().title()).append('\n');
            JOptionPane.showMessageDialog(frame, summary.toString(), "Registration successful", JOptionPane.INFORMATION_MESSAGE);
        });
        register.setEnabled(false);
        table.getSelectionModel().addListSelectionListener(event -> {
            int row = table.getSelectedRow();
            String status = row < 0 ? "" : student.registrationStatus(courses.get(row));
            register.setEnabled(status.equals("Available") || status.equals("Retake available"));
        });
        panel.add(register, BorderLayout.SOUTH); return panel;
    }
    private JPanel record() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel profile = new JPanel(new GridLayout(0, 1, 4, 4));
        profile.add(new JLabel("Level: " + student.level() + " | Phone: " + student.phone()));
        profile.add(new JLabel("Address: " + student.address()));
        panel.add(profile, BorderLayout.NORTH);
        List<Enrollment> current = student.enrollments().stream().filter(e -> e.grade() == null).toList();
        List<Enrollment> completed = student.enrollments().stream().filter(e -> e.grade() != null).toList();
        Object[][] activeRows = new Object[current.size()][];
        for (int i = 0; i < current.size(); i++) {
            Course c = current.get(i).course();
            activeRows[i] = new Object[]{c.code(), c.title(), c.credits(), "Not graded"};
        }
        JTable active = table(new String[]{"Code", "Title", "Credits", "Grade"}, activeRows);
        JPanel activePanel = new JPanel(new BorderLayout(6, 6));
        activePanel.setBorder(BorderFactory.createTitledBorder("Current Courses"));
        activePanel.add(new JScrollPane(active), BorderLayout.CENTER);
        JButton drop = button("Drop Selected Course", () -> {
            int row = active.getSelectedRow();
            if (row < 0) throw new IllegalArgumentException("Select a current course first.");
            student.drop(current.get(row)); dashboard(1);
        });
        drop.setEnabled(false);
        active.getSelectionModel().addListSelectionListener(e -> drop.setEnabled(active.getSelectedRow() >= 0));
        activePanel.add(drop, BorderLayout.SOUTH);
        Object[][] completedRows = new Object[completed.size()][];
        for (int i = 0; i < completed.size(); i++) {
            Enrollment e = completed.get(i); Course c = e.course();
            completedRows[i] = new Object[]{c.code(), c.title(), c.credits(), e.grade(),
                student.replaced(e) ? "Replaced" : c.passFail() ? "Excluded from GPA" : "Counts toward GPA"};
        }
        String gpa = student.gpa().isPresent()
            ? String.format(java.util.Locale.US, "%.2f", student.gpa().getAsDouble()) : "N/A";
        JPanel completedPanel = new JPanel(new BorderLayout());
        completedPanel.setBorder(BorderFactory.createTitledBorder("Completed Courses - GPA: " + gpa));
        completedPanel.add(new JScrollPane(table(new String[]{"Code", "Title", "Credits", "Grade", "GPA status"}, completedRows)));
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, activePanel, completedPanel);
        split.setResizeWeight(0.45); split.setDividerLocation(230);
        panel.add(split, BorderLayout.CENTER); return panel;
    }
}
