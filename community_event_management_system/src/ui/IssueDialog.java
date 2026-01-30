package ui;

import database.DBConnection;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

/**
 * Issue Dialog
 * Form for reporting community issues
 */
public class IssueDialog extends JDialog {

    private User currentUser;
    private Integer issueId;
    private boolean saved = false;

    private JComboBox<String> categoryCombo, priorityCombo;
    private JTextField locationField;
    private JTextArea descriptionArea;
    private JButton saveButton, cancelButton;

    public IssueDialog(Frame parent, String title, User user, Integer issueId) {
        super(parent, title, true);
        this.currentUser = user;
        this.issueId = issueId;

        setSize(550, 550);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Category
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        formPanel.add(createLabel("⚙️ Category:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        String[] categories = {"Water Leakage", "Electrical Problem", "Road Damage",
                "Garbage/Bins", "Structural Issue", "Other"};
        categoryCombo = new JComboBox<>(categories);
        categoryCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(categoryCombo, gbc);

        // Location
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(createLabel("📍 Location:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        locationField = new JTextField(30);
        locationField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(locationField, gbc);

        // Priority
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(createLabel("🔥 Priority:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        String[] priorities = {"Low", "Medium", "High", "Critical"};
        priorityCombo = new JComboBox<>(priorities);
        priorityCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        priorityCombo.setSelectedIndex(1); // Default: Medium
        formPanel.add(priorityCombo, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(createLabel("📄 Description:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        descriptionArea = new JTextArea(8, 30);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        formPanel.add(scrollPane, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        saveButton = new JButton("💾 Report Issue");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBackground(new Color(231, 76, 60));
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setPreferredSize(new Dimension(150, 40));
        saveButton.addActionListener(e -> saveIssue());

        cancelButton = new JButton("❌ Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(52, 73, 94));
        return label;
    }

    private void saveIssue() {
        // Validate inputs
        if (locationField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter issue location!",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (descriptionArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter issue description!",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            String query = "INSERT INTO Issues (category, description, location, reporter_id, status, priority) " +
                    "VALUES (?, ?, ?, ?, 'Pending', ?)";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, (String)categoryCombo.getSelectedItem());
            pstmt.setString(2, descriptionArea.getText().trim());
            pstmt.setString(3, locationField.getText().trim());
            pstmt.setInt(4, currentUser.getUserId());
            pstmt.setString(5, (String)priorityCombo.getSelectedItem());

            int result = pstmt.executeUpdate();

            if (result > 0) {
                saved = true;
                JOptionPane.showMessageDialog(this,
                        "Issue reported successfully!\nA representative will be assigned soon.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }

            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error reporting issue: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}