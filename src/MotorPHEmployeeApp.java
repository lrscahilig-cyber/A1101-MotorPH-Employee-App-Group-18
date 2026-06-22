import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;

public class MotorPHEmployeeApp extends JFrame {

    // ============================
    // DATA STRUCTURES
    // ============================
    static Map<String, String[]> employees = new LinkedHashMap<>(); // LinkedHashMap preserves insertion order for CSV saving
    static java.util.List<String[]> attendance = new ArrayList<>();
    static String[] empHeaders;
    static String[] attHeaders;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private static final String EMPLOYEE_FILE = "Employee Details.csv";
    private static final String ATTENDANCE_FILE = "Attendance Record.csv";

    // ============================
    // MAIN ENTRY POINT
    // ============================
    public static void main(String[] args) {
        try {
            loadEmployees(EMPLOYEE_FILE);
            loadAttendance(ATTENDANCE_FILE);

            SwingUtilities.invokeLater(() -> new MotorPHEmployeeApp());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error Loading CSV Files:\n" + e.getMessage(), "Initialization Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // ============================
    // LOGIN FRAME INTERFACE
    // ============================
    public MotorPHEmployeeApp() {
        setTitle("MotorPH Employee System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(new Color(245, 247, 250));

        JLabel title = new JLabel("MotorPH Portal");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        panel.add(title);
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(loginButton);

        add(panel);
        loginButton.addActionListener(e -> login());
        setVisible(true);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.equalsIgnoreCase("employee") && password.equals("12345")) {
            dispose();
            new EmployeeDashboard();
        } else if ((username.equalsIgnoreCase("payroll_staff") || username.equalsIgnoreCase("payroll staff")) && password.equals("12345")) {
            dispose();
            new PayrollDashboard();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============================
    // FEATURE 2: EMPLOYEE DASHBOARD (Read-Only Portal)
    // ============================
    static class EmployeeDashboard extends JFrame {
        private JTextField empField;
        private JTextArea area;

        EmployeeDashboard() {
            setTitle("Employee Self-Service Dashboard");
            setSize(500, 400);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            JPanel topPanel = new JPanel();
            topPanel.setBackground(new Color(230, 235, 245));
            topPanel.add(new JLabel("Enter Employee Number:"));
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
            logoutButton.addActionListener(e -> { dispose(); new MotorPHEmployeeApp(); });
            setVisible(true);
        }

        private void showEmployee() {
            String empNum = empField.getText().trim();
            if (!employees.containsKey(empNum)) {
                JOptionPane.showMessageDialog(this, "Employee Not Found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] emp = employees.get(empNum);
            StringBuilder sb = new StringBuilder("=== MOTORPH EMPLOYEE PROFILE ===\n\n");
            for (int i = 0; i < empHeaders.length; i++) {
                sb.append(String.format("%-18s: %s\n", empHeaders[i], emp[i]));
            }
            area.setText(sb.toString());
        }
    }

    // ============================
    // FEATURES 2, 3 & 4: PAYROLL STAFF DASHBOARD (CRUD + Salary Engine)
    // ============================
    static class PayrollDashboard extends JFrame {
        private JTable table;
        private DefaultTableModel tableModel;
        
        // Input Form Fields
        private JTextField txtId, txtLastName, txtFirstName, txtHourlyRate;

        PayrollDashboard() {
            setTitle("MotorPH Management & Payroll Studio");
            setSize(1000, 650);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            // Build structural Table Headers matching prompt requests
            String[] displayHeaders = {"Employee #", "First Name", "Last Name", "Hourly Rate", "Hours Worked", "Gross Pay", "Deductions", "Net Pay"};
            tableModel = new DefaultTableModel(displayHeaders, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; } // Managed UI Forms control edits
            };
            table = new JTable(tableModel);
            add(new JScrollPane(table), BorderLayout.CENTER);

            // Side/Top Input Form Component Panel
            JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
            formPanel.setBorder(BorderFactory.createTitledBorder("Employee Record Editor"));
            
            formPanel.add(new JLabel("Employee ID:")); txtId = new JTextField(); formPanel.add(txtId);
            formPanel.add(new JLabel("First Name:")); txtFirstName = new JTextField(); formPanel.add(txtFirstName);
            formPanel.add(new JLabel("Last Name:")); txtLastName = new JTextField(); formPanel.add(txtLastName);
            formPanel.add(new JLabel("Hourly Rate:")); txtHourlyRate = new JTextField(); formPanel.add(txtHourlyRate);

            // Management Action Controls Panel
            JPanel controlPanel = new JPanel(new FlowLayout());
            JButton btnAdd = new JButton("Add Record");
            JButton btnUpdate = new JButton("Update Selected");
            JButton btnDelete = new JButton("Delete Selected");
            JButton btnSave = new JButton("Commit changes to CSV");
            JButton btnLogout = new JButton("Logout");

            controlPanel.add(btnAdd);
            controlPanel.add(btnUpdate);
            controlPanel.add(btnDelete);
            controlPanel.add(btnSave);
            controlPanel.add(btnLogout);

            JPanel interfaceWrapper = new JPanel(new BorderLayout());
            interfaceWrapper.add(formPanel, BorderLayout.NORTH);
            interfaceWrapper.add(controlPanel, BorderLayout.SOUTH);
            add(interfaceWrapper, BorderLayout.NORTH);

            // Populate Table Component from Data structures
            refreshTableData();

            // Table Selection Listener (Fills Form with Selected Rows Data automatically)
            table.getSelectionModel().addListSelectionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    txtFirstName.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    txtLastName.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    txtHourlyRate.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    txtId.setEditable(false); // ID should not be changed directly on an update
                }
            });

            // Action Binding Configurations
            btnAdd.addActionListener(e -> addRecord());
            btnUpdate.addActionListener(e -> updateRecord());
            btnDelete.addActionListener(e -> deleteRecord());
            btnSave.addActionListener(e -> saveRecordsToCSV());
            btnLogout.addActionListener(e -> { dispose(); new MotorPHEmployeeApp(); });

            setVisible(true);
        }

        private void refreshTableData() {
            tableModel.setRowCount(0); // clear existing frame
            for (Map.Entry<String, String[]> entry : employees.entrySet()) {
                String empId = entry.getKey();
                String[] baseData = entry.getValue();

                String fName = baseData[getIndex(empHeaders, "First Name")];
                String lName = baseData[getIndex(empHeaders, "Last Name")];
                String rawRate = baseData[getIndex(empHeaders, "Hourly Rate")].replace("\"", "").trim();
                
                double hourlyRate = 0;
                try { hourlyRate = Double.parseDouble(rawRate); } catch(Exception ex) {}

                // Feature 3: Dynamic Computations based on mapped constraints
                double calculatedHours = calculateHoursWorked(empId);
                double grossPay = calculatedHours * hourlyRate;
                
                // Formulas matching parameters
                double sss = grossPay * 0.05;
                double philhealth = grossPay * 0.03;
                double pagibig = grossPay > 0 ? 50.0 : 0.0;
                double tax = grossPay * 0.10;
                double totalDeductions = sss + philhealth + pagibig + tax;
                double netPay = grossPay - totalDeductions;

                tableModel.addRow(new Object[]{
                    empId, fName, lName, 
                    String.format("%.2f", hourlyRate), 
                    String.format("%.2f", calculatedHours), 
                    String.format("%.2f", grossPay), 
                    String.format("%.2f", totalDeductions), 
                    String.format("%.2f", netPay)
                });
            }
        }

        private double calculateHoursWorked(String empNum) {
            int empIndex = getIndexFlexible(attHeaders, "Employee #");
            int loginIndex = getIndexFlexible(attHeaders, "Log In", "Time In");
            int logoutIndex = getIndexFlexible(attHeaders, "Log Out", "Time Out");

            if (empIndex == -1 || loginIndex == -1 || logoutIndex == -1) return 0.0;

            double totalHours = 0;
            for (String[] row : attendance) {
                if (row.length > Math.max(empIndex, Math.max(loginIndex, logoutIndex)) && row[empIndex].trim().equals(empNum)) {
                    try {
                        double login = parseTime(row[loginIndex].trim());
                        double logout = parseTime(row[logoutIndex].trim());
                        double workedHours = logout - login;
                        if (workedHours > 5) workedHours -= 1; // 1-hour unpaid lunch break allocation logic
                        if (workedHours > 0) totalHours += workedHours;
                    } catch (Exception ignored) {}
                }
            }
            return totalHours;
        }

        // Feature 2: Validation and Record Creation Injection
        private void addRecord() {
            String id = txtId.getText().trim();
            String fName = txtFirstName.getText().trim();
            String lName = txtLastName.getText().trim();
            String rate = txtHourlyRate.getText().trim();

            if (id.isEmpty() || fName.isEmpty() || lName.isEmpty() || rate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Validation Error: All tracking input fields are required!", "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (employees.containsKey(id)) {
                JOptionPane.showMessageDialog(this, "Conflict Error: Employee ID already registered.", "Duplication Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Double.parseDouble(rate);
                // Setup dynamic array padding safely mapping back out to Base Master headers structures
                String[] targetDataRow = new String[empHeaders.length];
                Arrays.fill(targetDataRow, "");
                targetDataRow[getIndex(empHeaders, "Employee #")] = id;
                targetDataRow[getIndex(empHeaders, "First Name")] = fName;
                targetDataRow[getIndex(empHeaders, "Last Name")] = lName;
                targetDataRow[getIndex(empHeaders, "Hourly Rate")] = rate;

                employees.put(id, targetDataRow);
                refreshTableData();
                clearFields();
                JOptionPane.showMessageDialog(this, "Employee entry initialized successfully!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please insert valid numeric decimal formats for calculation rates.", "Invalid Rate Format", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Feature 4: Record updates Processing Engine mapping back structural values
        private void updateRecord() {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select an entry record row context from the dataset table grid below to proceed.", "Selection Needed", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String id = tableModel.getValueAt(selectedRow, 0).toString();
            String fName = txtFirstName.getText().trim();
            String lName = txtLastName.getText().trim();
            String rate = txtHourlyRate.getText().trim();

            if (fName.isEmpty() || lName.isEmpty() || rate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Updatable profile strings cannot remain empty.", "Validation Alert", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Double.parseDouble(rate);
                String[] structuralRecordRow = employees.get(id);
                structuralRecordRow[getIndex(empHeaders, "First Name")] = fName;
                structuralRecordRow[getIndex(empHeaders, "Last Name")] = lName;
                structuralRecordRow[getIndex(empHeaders, "Hourly Rate")] = rate;

                refreshTableData();
                clearFields();
                JOptionPane.showMessageDialog(this, "System updated targeted profile allocations smoothly.");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Rate inputs must contain functional numbers.", "Input Conversion Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Feature 4: Safe Removal Actions Mapping
        private void deleteRecord() {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Target contextual entry row row maps required to terminate profiles.", "Selection Missing", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String id = tableModel.getValueAt(selectedRow, 0).toString();
            int confirmationResult = JOptionPane.showConfirmDialog(this, "Are you confident regarding removing Record targeting: ID: " + id + "?", "Destructive Deletion Confirmation Tracker Check", JOptionPane.YES_NO_OPTION);
            
            if (confirmationResult == JOptionPane.YES_OPTION) {
                employees.remove(id);
                refreshTableData();
                clearFields();
                JOptionPane.showMessageDialog(this, "Employee profiling traces drop completely done.");
            }
        }

        private void clearFields() {
            txtId.setText("");
            txtFirstName.setText("");
            txtLastName.setText("");
            txtHourlyRate.setText("");
            txtId.setEditable(true);
            table.clearSelection();
        }

        // Save Mechanism: Commits current data state back cleanly structure preserved back safely to operational source storage
        private void saveRecordsToCSV() {
            try (PrintWriter pw = new PrintWriter(new FileWriter(EMPLOYEE_FILE))) {
                // Re-write standard array structural Headers tracking back configurations first
                pw.println(String.join(",", empHeaders));
                // Iterating sequential dataset allocations safely
                for (String[] targetRecordRow : employees.values()) {
                    pw.println(String.join(",", targetRecordRow));
                }
                JOptionPane.showMessageDialog(this, "Changes committed successfully to disk tracking storage system data state values synchronized!", "Disk File State Check Sync", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed tracking disk operational state modifications update cycles correctly: \n" + ex.getMessage(), "IO Fatal Engine Crash Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ============================
    // DATA UTILITIES & CSV PARSING
    // ============================
    static void loadEmployees(String file) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String firstLine = br.readLine();
            if (firstLine == null) throw new IOException("Dataset execution trace failed file configuration reading error.");
            empHeaders = splitCSV(firstLine);

            int idIndex = getIndex(empHeaders, "Employee #");
            String rowLine;
            while ((rowLine = br.readLine()) != null) {
                if (rowLine.trim().isEmpty()) continue;
                String[] parsedRows = splitCSV(rowLine);
                if (parsedRows.length > idIndex) {
                    employees.put(parsedRows[idIndex].trim(), parsedRows);
                }
            }
        }
    }

    static void loadAttendance(String file) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String firstLine = br.readLine();
            if (firstLine == null) return;
            attHeaders = splitCSV(firstLine);

            String rowLine;
            while ((rowLine = br.readLine()) != null) {
                if (rowLine.trim().isEmpty()) continue;
                attendance.add(splitCSV(rowLine));
            }
        }
    }

    static String[] splitCSV(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    }

    static int getIndex(String[] headers, String name) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equalsIgnoreCase(name)) return i;
        }
        return -1;
    }

    static int getIndexFlexible(String[] headers, String... names) {
        for (int i = 0; i < headers.length; i++) {
            String current = headers[i].toLowerCase().replace(" ", "");
            for (String n : names) {
                if (current.equals(n.toLowerCase().replace(" ", ""))) return i;
            }
        }
        return -1;
    }

    static double parseTime(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) + Integer.parseInt(parts[1]) / 60.0;
    }
}