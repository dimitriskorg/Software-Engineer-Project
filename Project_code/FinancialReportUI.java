import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Date;

public class FinancialReportUI extends JFrame {
    private Connection conn;
    private JSpinner startDateSpinner, endDateSpinner;
    private JButton fetchButton, exportButton;
    private JTextArea summaryArea;

    public FinancialReportUI(Connection conn) {
        super("Οικονομικά Στατιστικά");
        this.conn = conn;
        setSize(600, 400);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(new JLabel("Από:"));
        startDateSpinner = new JSpinner(new SpinnerDateModel());
        controlPanel.add(startDateSpinner);

        controlPanel.add(new JLabel("Έως:"));
        endDateSpinner = new JSpinner(new SpinnerDateModel());
        controlPanel.add(endDateSpinner);

        fetchButton = new JButton("Λήψη Στατιστικών");
        controlPanel.add(fetchButton);

        exportButton = new JButton("Εξαγωγή Οπτικοποίησης");
        controlPanel.add(exportButton);

        add(controlPanel, BorderLayout.NORTH);

        summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        add(new JScrollPane(summaryArea), BorderLayout.CENTER);

        fetchButton.addActionListener(e -> onRequestData());
        exportButton.addActionListener(e -> onExportReport());
    }

    private void onRequestData() {
        java.util.Date start = (java.util.Date) startDateSpinner.getValue();
        java.util.Date end = (java.util.Date) endDateSpinner.getValue();
        FinancialReport report = new FinancialReport(conn);

        SummaryData summary = report.fetchSummary(start, end);
        if (summary == null) {
            JOptionPane.showMessageDialog(this, "Δεν υπάρχουν δεδομένα για την επιλεγμένη χρονική περίοδο.");
            summaryArea.setText("");
            return;
        }
        displaySummary(summary);
    }

    private void onExportReport() {
        JOptionPane.showMessageDialog(this, "Η εξαγωγή αρχείου δεν έχει υλοποιηθεί ακόμη.");
    }

    private void displaySummary(SummaryData summary) {
        StringBuilder sb = new StringBuilder();
        sb.append("Σύνοψη Εσόδων & Εξόδων\n");
        sb.append("Έσοδα: ").append(summary.totalIncome).append("\n");
        sb.append("Έξοδα: ").append(summary.totalExpenses).append("\n");
        sb.append("Καθαρό Κέρδος: ").append(summary.totalIncome - summary.totalExpenses).append("\n");
        summaryArea.setText(sb.toString());
    }
}

class SummaryData {
    public double totalIncome;
    public double totalExpenses;
    public SummaryData(double income, double expenses) {
        this.totalIncome = income;
        this.totalExpenses = expenses;
    }
}

class FinancialReport {
    private Connection conn;
    public FinancialReport(Connection conn) { this.conn = conn; }

    public SummaryData fetchSummary(Date start, Date end) {
        double income = 0, expenses = 0;
        try {
            String sqlIncome = "SELECT SUM(amount) FROM Income WHERE Date BETWEEN ? AND ?";
            try (PreparedStatement st = conn.prepareStatement(sqlIncome)) {
                st.setDate(1, new java.sql.Date(start.getTime()));
                st.setDate(2, new java.sql.Date(end.getTime()));
                ResultSet rs = st.executeQuery();
                if (rs.next()) income = rs.getDouble(1);
            }
            String sqlExpenses = "SELECT SUM(amount) FROM Expenses WHERE Date BETWEEN ? AND ?";
            try (PreparedStatement st = conn.prepareStatement(sqlExpenses)) {
                st.setDate(1, new java.sql.Date(start.getTime()));
                st.setDate(2, new java.sql.Date(end.getTime()));
                ResultSet rs = st.executeQuery();
                if (rs.next()) expenses = rs.getDouble(1);
            }
            if (income == 0 && expenses == 0) return null;
            return new SummaryData(income, expenses);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
