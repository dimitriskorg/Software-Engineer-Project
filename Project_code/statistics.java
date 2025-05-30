import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class statistics {
    private JFrame mainFrame;
    private JPanel headerPanel;
    private JPanel containerPanel;
    
    public statistics() {
        // Create the main window
        mainFrame = new JFrame("Financial Statistics");
        mainFrame.setSize(600, 400);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        
        // Create the header
        createHeader();
        
        // Create the container with buttons
        createContainer();
        
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
    
    private void createHeader() {
        headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(new Color(122, 156, 95)); // #7a9c5f
        
        JLabel titleLabel = new JLabel("Financial Statistics");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(178, 34, 34)); // #b22222
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        JPanel logoutPanel = new JPanel();
        logoutPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(new Color(122, 156, 95)); // Same color as header
        logoutPanel.add(logoutBtn);
        headerPanel.add(logoutPanel, BorderLayout.EAST);
        
        mainFrame.add(headerPanel, BorderLayout.NORTH);
    }
    
    private void createContainer() {
        containerPanel = new JPanel();
        containerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        containerPanel.setBackground(new Color(244, 244, 244)); // #f4f4f4
        
        // Income & Expenses Summary Button
        JButton summaryBtn = createButton("Income & Expenses Summary");
        summaryBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSummaryDialog();
            }
        });
        containerPanel.add(summaryBtn);
        
        // Payments & Debts Analysis Button
        JButton paymentBtn = createButton("Payments & Debts Analysis");
        paymentBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPaymentDialog();
            }
        });
        containerPanel.add(paymentBtn);
        
        // Charts & Statistics Button
        JButton chartsBtn = createButton("Charts & Statistics");
        chartsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showChartsDialog();
            }
        });
        containerPanel.add(chartsBtn);
        
        mainFrame.add(containerPanel, BorderLayout.CENTER);
    }
    
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 50));
        button.setBackground(new Color(194, 165, 108)); // #c2a56c
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        
        // Change color when mouse hovers over the button
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(168, 138, 85)); // #a88a55
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(194, 165, 108)); // #c2a56c
            }
        });
        
        return button;
    }
    
    private void showSummaryDialog() {
        JDialog dialog = createDialog("Income & Expenses Summary");
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        
        contentPanel.add(createLabel("Total Income: 5000"));
        contentPanel.add(createLabel("Total Expenses: 3000"));
        contentPanel.add(createLabel("Profit/Loss: 2000"));
        
        JButton closeBtn = createCloseButton(dialog);
        contentPanel.add(closeBtn);
        
        dialog.add(contentPanel);
        dialog.setVisible(true);
    }
    
    private void showPaymentDialog() {
        JDialog dialog = createDialog("Payments & Debts Analysis");
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        
        contentPanel.add(createLabel("Paid Orders:"));
        
        JPanel list1 = new JPanel();
        list1.setLayout(new BoxLayout(list1, BoxLayout.Y_AXIS));
        list1.setBackground(Color.WHITE);
        list1.add(createLabel("Order 1: 200"));
        list1.add(createLabel("Order 2: 150"));
        contentPanel.add(list1);
        
        contentPanel.add(createLabel("Pending Payments:"));
        
        JPanel list2 = new JPanel();
        list2.setLayout(new BoxLayout(list2, BoxLayout.Y_AXIS));
        list2.setBackground(Color.WHITE);
        list2.add(createLabel("Order 3: 300"));
        contentPanel.add(list2);
        
        JButton closeBtn = createCloseButton(dialog);
        contentPanel.add(closeBtn);
        
        dialog.add(contentPanel);
        dialog.setVisible(true);
    }
    
    private void showChartsDialog() {
        JDialog dialog = createDialog("Charts & Statistics");
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        
        contentPanel.add(createLabel("Income-Expenses Chart"));
        contentPanel.add(createLabel("Expenses Distribution by Category"));
        contentPanel.add(createLabel("Payment Collection Progress"));
        
        JButton closeBtn = createCloseButton(dialog);
        contentPanel.add(closeBtn);
        
        dialog.add(contentPanel);
        dialog.setVisible(true);
    }
    
    private JDialog createDialog(String title) {
        JDialog dialog = new JDialog(mainFrame, title, true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(mainFrame);
        return dialog;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private JButton createCloseButton(JDialog dialog) {
        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(244, 67, 54)); // #f44336
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.setMaximumSize(new Dimension(150, 40));
        
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(closeBtn);
        
        return closeBtn;
    }
    
    public static void main(String[] args) {
        // Set look and feel for better appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Run the application
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new statistics();
            }
        });
    }
}