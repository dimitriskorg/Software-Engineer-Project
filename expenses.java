import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.table.DefaultTableModel;

public class expenses extends JFrame {
    private JButton addExpenseButton;
    private JButton viewExpensesButton;
    private JButton logoutButton;
    private JPanel expensesListPanel;
    private JTable expensesTable;
    private DefaultTableModel tableModel;
    private JDialog addExpenseDialog;

    public expenses() {
        // Set up the main frame
        setTitle("Expenses");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Create header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(122, 156, 95));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel titleLabel = new JLabel("Expenses", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(178, 34, 34));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setPreferredSize(new Dimension(120, 40));
        logoutButton.addActionListener(e -> System.exit(0));

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setOpaque(false);
        logoutPanel.add(logoutButton);
        headerPanel.add(logoutPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Center panel to hold both button panel and expenses list
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));

        addExpenseButton = new JButton("Add Expense");
        styleButton(addExpenseButton, new Color(194, 165, 108));
        addExpenseButton.addActionListener(e -> showAddExpenseDialog());

        viewExpensesButton = new JButton("View Expenses");
        styleButton(viewExpensesButton, new Color(194, 165, 108));
        viewExpensesButton.addActionListener(e -> toggleExpensesList());

        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(addExpenseButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(viewExpensesButton);
        buttonPanel.add(Box.createVerticalStrut(20));

        // Expenses table panel
        expensesListPanel = new JPanel(new BorderLayout());
        expensesListPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        expensesListPanel.setVisible(false);

        String[] columnNames = {"Description", "Category", "Amount", "Date", "Payment Method"};
        tableModel = new DefaultTableModel(columnNames, 0);
        addSampleData();

        expensesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(expensesTable);
        expensesListPanel.add(scrollPane, BorderLayout.CENTER);

        // Combine both into centerPanel
        centerPanel.add(buttonPanel);
        centerPanel.add(expensesListPanel);

        add(centerPanel, BorderLayout.CENTER);

        // Create the add expense dialog
        createAddExpenseDialog();
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(250, 45));
        button.setMaximumSize(new Dimension(250, 45));
        button.setFocusPainted(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(168, 138, 85));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
    }

    private void createAddExpenseDialog() {
        addExpenseDialog = new JDialog(this, "Add Expense", true);
        addExpenseDialog.setSize(600, 500);
        addExpenseDialog.setLayout(new BorderLayout());
        addExpenseDialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Description
        JLabel descLabel = new JLabel("Expense Description");
        JTextField descField = new JTextField();

        // Category
        JLabel categoryLabel = new JLabel("Expense Category");
        JComboBox<String> categoryCombo = new JComboBox<>();
        categoryCombo.addItem("Salaries");
        categoryCombo.addItem("Supplies");
        categoryCombo.addItem("Operating Expenses");

        // Amount
        JLabel amountLabel = new JLabel("Amount");
        JTextField amountField = new JTextField();

        // Date
        JLabel dateLabel = new JLabel("Expense Date");
        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        // Payment Method
        JLabel paymentLabel = new JLabel("Payment Method");
        JComboBox<String> paymentCombo = new JComboBox<>();
        paymentCombo.addItem("Cash");
        paymentCombo.addItem("Bank Transfer");
        paymentCombo.addItem("Card");

        // Error message
        JLabel errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);

        formPanel.add(descLabel);
        formPanel.add(descField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryCombo);
        formPanel.add(amountLabel);
        formPanel.add(amountField);
        formPanel.add(dateLabel);
        formPanel.add(dateField);
        formPanel.add(paymentLabel);
        formPanel.add(paymentCombo);
        formPanel.add(errorLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveButton = new JButton("Save Expense");
        saveButton.setBackground(new Color(91, 192, 222));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> {
            try {
                String description = descField.getText();
                String category = (String) categoryCombo.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());
                String date = dateField.getText();
                String paymentMethod = (String) paymentCombo.getSelectedItem();

                if (description.isEmpty() || amount <= 0) {
                    errorLabel.setText("All fields are required and amount must be positive.");
                    return;
                }

                tableModel.addRow(new Object[]{description, category, amount, date, paymentMethod});
                addExpenseDialog.dispose();
            } catch (NumberFormatException ex) {
                errorLabel.setText("Amount must be a valid number.");
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(217, 83, 79));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(e -> addExpenseDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        addExpenseDialog.add(formPanel, BorderLayout.CENTER);
        addExpenseDialog.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void showAddExpenseDialog() {
        addExpenseDialog.setVisible(true);
    }

    private void toggleExpensesList() {
        expensesListPanel.setVisible(!expensesListPanel.isVisible());
        revalidate();
        repaint();
    }

    private void addSampleData() {
        tableModel.addRow(new Object[]{"Supplier Payment", "Supplies", 500.00, "2025-03-28", "Card"});
        tableModel.addRow(new Object[]{"Delivery Fuel", "Operating Expenses", 100.00, "2025-03-27", "Cash"});
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            expenses app = new expenses();
            app.setVisible(true);
        });
    }
}
