import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class MotorPHEmployeeApp extends JFrame {

    // ============================
    // DATA
    // ============================

    static Map<String, String[]> employees = new HashMap<>();
    static java.util.List<String[]> attendance = new ArrayList<>();

    static String[] empHeaders;
    static String[] attHeaders;

    JTextField usernameField;
    JPasswordField passwordField;

    // ============================
    // MAIN
    // ============================

    public static void main(String[] args) {

        try {
            loadEmployees("Employee Details.csv");
            loadAttendance("Attendance Record.csv");

            SwingUtilities.invokeLater(() -> {
                new MotorPHEmployeeApp();
            });

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error Loading CSV Files\n" + e.getMessage()
            );
        }
    }

    // ============================
    // LOGIN FRAME
    // ============================

    public MotorPHEmployeeApp() {

        setTitle("MotorPHEmployeeApp - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(new Color(245, 247, 250));

        JLabel title = new JLabel("MotorPH Employee System");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");

        panel.add(title);
        panel.add(new JLabel("Username"));
        panel.add(usernameField);
        panel.add(new JLabel("Password"));
        panel.add(passwordField);
        panel.add(loginButton);

        add(panel);

        loginButton.addActionListener(e -> login());

        setVisible(true);
    }

    // ============================
    // LOGIN FUNCTION
    // ============================

    void login() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.equalsIgnoreCase("employee")
                && password.equals("12345")) {

            dispose();
            new EmployeeDashboard();

        } else if ((username.equalsIgnoreCase("payroll_staff")
                || username.equalsIgnoreCase("payroll staff"))
                && password.equals("12345")) {

            dispose();
            new PayrollDashboard();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid Username or Password"
            );
        }
    }

    // ============================
    // EMPLOYEE DASHBOARD
    // ============================

    static class EmployeeDashboard extends JFrame {

        JTextField empField;
        JTextArea area;

        EmployeeDashboard() {

            setTitle("Employee Dashboard");
            setSize(500, 400);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            JPanel topPanel = new JPanel();
            topPanel.setBackground(new Color(230, 235, 245));

            topPanel.add(new JLabel("Employee Number:"));

            empField = new JTextField(10);
            topPanel.add(empField);

            JButton searchButton = new JButton("Search");
            topPanel.add(searchButton);

            add(topPanel, BorderLayout.NORTH);

            area = new JTextArea();
            area.setEditable(false);
            area.setFont(new Font("Monospaced", Font.PLAIN, 14));

            add(new JScrollPane(area), BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel();
            JButton logoutButton = new JButton("Logout");
            bottomPanel.add(logoutButton);

            add(bottomPanel, BorderLayout.SOUTH);

            searchButton.addActionListener(e -> showEmployee());

            logoutButton.addActionListener(e -> {
                dispose();
                new MotorPHEmployeeApp();
            });

            setVisible(true);
        }

        void showEmployee() {

            String empNum = empField.getText().trim();

            if (!employees.containsKey(empNum)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Employee Not Found"
                );
                return;
            }

            String[] emp = employees.get(empNum);

            String firstName =
                    emp[getIndex(empHeaders, "First Name")];

            String lastName =
                    emp[getIndex(empHeaders, "Last Name")];

            String birthday =
                    emp[getIndex(empHeaders, "Birthday")];

            area.setText(
                    "Employee #: " + empNum + "\n\n" +
                    "Name: " + firstName + " " + lastName + "\n\n" +
                    "Birthday: " + birthday
            );
        }
    }

    // ============================
    // PAYROLL DASHBOARD
    // ============================

    static class PayrollDashboard extends JFrame {

        JTextField empField;
        JTextArea area;

        PayrollDashboard() {

            setTitle("Payroll Dashboard");
            setSize(700, 500);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            JPanel topPanel = new JPanel();
            topPanel.setBackground(new Color(230, 235, 245));

            topPanel.add(new JLabel("Employee Number:"));

            empField = new JTextField(10);
            topPanel.add(empField);

            JButton generateButton =
                    new JButton("Generate Payroll");

            topPanel.add(generateButton);

            add(topPanel, BorderLayout.NORTH);

            area = new JTextArea();
            area.setEditable(false);
            area.setFont(new Font("Monospaced", Font.PLAIN, 14));

            add(new JScrollPane(area), BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel();

            JButton logoutButton =
                    new JButton("Logout");

            bottomPanel.add(logoutButton);

            add(bottomPanel, BorderLayout.SOUTH);

            generateButton.addActionListener(
                    e -> generatePayroll()
            );

            logoutButton.addActionListener(e -> {
                dispose();
                new MotorPHEmployeeApp();
            });

            setVisible(true);
        }

        void generatePayroll() {

            String empNum =
                    empField.getText().trim();

            if (!employees.containsKey(empNum)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Employee Not Found"
                );

                return;
            }

            String[] emp = employees.get(empNum);

            String name =
                    emp[getIndex(empHeaders, "First Name")]
                    + " " +
                    emp[getIndex(empHeaders, "Last Name")];

            double hourlyRate =
                    Double.parseDouble(
                            emp[getIndex(empHeaders,
                                    "Hourly Rate")]
                                    .replace(",", "")
                                    .replace("\"", "")
                                    .trim()
                    );

            int empIndex =
                    getIndexFlexible(
                            attHeaders,
                            "Employee #"
                    );

            int loginIndex =
                    getIndexFlexible(
                            attHeaders,
                            "Log In",
                            "Time In"
                    );

            int logoutIndex =
                    getIndexFlexible(
                            attHeaders,
                            "Log Out",
                            "Time Out"
                    );

            double totalHours = 0;

            for (String[] row : attendance) {

                if (row[empIndex].equals(empNum)) {

                    double login =
                            parseTime(row[loginIndex]);

                    double logout =
                            parseTime(row[logoutIndex]);

                    double workedHours =
                            logout - login;

                    if (workedHours > 5) {
                        workedHours -= 1;
                    }

                    totalHours += workedHours;
                }
            }

            double gross =
                    totalHours * hourlyRate;

            double sss = gross * 0.05;
            double philhealth = gross * 0.03;
            double pagibig = 50;
            double tax = gross * 0.10;

            double deductions =
                    sss + philhealth + pagibig + tax;

            double net =
                    gross - deductions;

            area.setText(
                    "Employee #: " + empNum + "\n\n" +
                    "Name: " + name + "\n\n" +
                    String.format(
                            "Hours Worked: %.2f\n",
                            totalHours
                    ) +
                    String.format(
                            "Gross Salary: %.2f\n",
                            gross
                    ) +
                    String.format(
                            "SSS: %.2f\n",
                            sss
                    ) +
                    String.format(
                            "PhilHealth: %.2f\n",
                            philhealth
                    ) +
                    String.format(
                            "Pag-IBIG: %.2f\n",
                            pagibig
                    ) +
                    String.format(
                            "Tax: %.2f\n",
                            tax
                    ) +
                    String.format(
                            "Net Salary: %.2f\n",
                            net
                    )
            );
        }
    }

    // ============================
    // CSV FUNCTIONS
    // ============================

    static void loadEmployees(String file)
            throws Exception {

        BufferedReader br =
                new BufferedReader(
                        new FileReader(file)
                );

        empHeaders =
                splitCSV(br.readLine());

        String line;

        while ((line = br.readLine()) != null) {

            String[] row =
                    splitCSV(line);

            employees.put(
                    row[getIndex(
                            empHeaders,
                            "Employee #"
                    )],
                    row
            );
        }

        br.close();
    }

    static void loadAttendance(String file)
            throws Exception {

        BufferedReader br =
                new BufferedReader(
                        new FileReader(file)
                );

        attHeaders =
                splitCSV(br.readLine());

        String line;

        while ((line = br.readLine()) != null) {

            attendance.add(
                    splitCSV(line)
            );
        }

        br.close();
    }

    // ============================
    // UTILITIES
    // ============================

    static String[] splitCSV(String line) {

        return line.split(
                ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"
        );
    }

    static int getIndex(
            String[] headers,
            String name
    ) {

        for (int i = 0; i < headers.length; i++) {

            if (headers[i]
                    .trim()
                    .equalsIgnoreCase(name)) {

                return i;
            }
        }

        return -1;
    }

    static int getIndexFlexible(
            String[] headers,
            String... names
    ) {

        for (int i = 0; i < headers.length; i++) {

            String current =
                    headers[i]
                            .toLowerCase()
                            .replace(" ", "");

            for (String n : names) {

                if (current.equals(
                        n.toLowerCase()
                                .replace(" ", "")
                )) {

                    return i;
                }
            }
        }

        return -1;
    }

    static double parseTime(String time) {

        String[] p = time.split(":");

        return Integer.parseInt(p[0]) +
                Integer.parseInt(p[1]) / 60.0;
    }
}