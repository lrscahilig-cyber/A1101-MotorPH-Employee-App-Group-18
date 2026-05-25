import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MotorPHEmployeeApp extends JFrame {

    private JTable employeeTable;
    private JTextField txtEmployeeId;
    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtDepartment;
    private JTextField txtPosition;
    private JTextField txtSalary;

    public MotorPHEmployeeApp() {

        setTitle("MotorPH Employee Management System");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initHeader();
        initSidebar();
        initMainContent();
    }

    private void initHeader() {

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 42, 86));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("MotorPH Employee Management System");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        headerPanel.add(titleLabel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void initSidebar() {

        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new GridLayout(8, 1, 10, 10));
        sidebarPanel.setBackground(new Color(44, 62, 80));
        sidebarPanel.setPreferredSize(new Dimension(200, 0));
        sidebarPanel.setBorder(new EmptyBorder(20, 15, 20, 15));

        String[] menuItems = {
                "Dashboard",
                "Employees",
                "Attendance",
                "Payroll",
                "Departments",
                "Reports",
                "Settings",
                "Logout"
        };

        for (String item : menuItems) {

            JButton button = new JButton(item);
            button.setFocusPainted(false);
            button.setBackground(new Color(52, 73, 94));
            button.setForeground(Color.WHITE);

            sidebarPanel.add(button);
        }

        add(sidebarPanel, BorderLayout.WEST);
    }

    private void initMainContent() {

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel formPanel = createFormPanel();
        JScrollPane tablePanel = createTablePanel();

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {

        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtEmployeeId = new JTextField(15);
        txtFirstName = new JTextField(15);
        txtLastName = new JTextField(15);
        txtDepartment = new JTextField(15);
        txtPosition = new JTextField(15);
        txtSalary = new JTextField(15);

        addFormField(panel, gbc, 0, "Employee ID:", txtEmployeeId);
        addFormField(panel, gbc, 1, "First Name:", txtFirstName);
        addFormField(panel, gbc, 2, "Last Name:", txtLastName);
        addFormField(panel, gbc, 3, "Department:", txtDepartment);
        addFormField(panel, gbc, 4, "Position:", txtPosition);
        addFormField(panel, gbc, 5, "Salary:", txtSalary);

        JButton btnAdd = new JButton("Add Employee");

        btnAdd.addActionListener(e -> addEmployee());

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;

        panel.add(btnAdd, gbc);

        return panel;
    }

    private void addFormField(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String labelText,
            JTextField textField
    ) {

        gbc.gridx = 0;
        gbc.gridy = row;

        JLabel label = new JLabel(labelText);

        panel.add(label, gbc);

        gbc.gridx = 1;

        panel.add(textField, gbc);
    }

    private JScrollPane createTablePanel() {

        String[] columns = {
                "Employee ID",
                "First Name",
                "Last Name",
                "Department",
                "Position",
                "Salary"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0);

        employeeTable = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(employeeTable);

        return scrollPane;
    }

    private void addEmployee() {

        DefaultTableModel model =
                (DefaultTableModel) employeeTable.getModel();

        model.addRow(new Object[]{

                txtEmployeeId.getText(),
                txtFirstName.getText(),
                txtLastName.getText(),
                txtDepartment.getText(),
                txtPosition.getText(),
                txtSalary.getText()
        });

        clearFields();
    }

    private void clearFields() {

        txtEmployeeId.setText("");
        txtFirstName.setText("");
        txtLastName.setText("");
        txtDepartment.setText("");
        txtPosition.setText("");
        txtSalary.setText("");
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new MotorPHEmployeeApp().setVisible(true);
        });
    }
}
