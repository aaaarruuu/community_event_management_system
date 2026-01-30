package ui;

import database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

/**
 * Representative Dialog
 * Form for adding/editing representatives
 */
public class RepresentativeDialog extends JDialog {

    private Integer repId;
    private boolean saved = false;

    private JTextField nameField, contactField, emailField;
    private JComboBox<String> categoryCombo, statusCombo;
    private JButton saveButton, cancelButton;

    public RepresentativeDialog(Frame parent, String title, Integer repId) {
        super(parent, title, true);
        this.repId = repId;

        setSize(500, 450);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();

        if (repId != null) {
            loadRepData();
        }
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

        // Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        formPanel.add(createLabel("👤 Name:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        nameField = new JTextField(25);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(nameField, gbc);

        // Category
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(createLabel("🔧 Category:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        String[] categories = {"Plumber", "Electrician", "Mechanic", "Gardener", "Cleaner", "Other"};
        categoryCombo = new JComboBox<>(categories);
        categoryCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(categoryCombo, gbc);

        // Contact
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(createLabel("📞 Contact:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        contactField = new JTextField(25);
        contactField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(contactField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        formPanel.add(createLabel("📧 Email:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        emailField = new JTextField(25);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(emailField, gbc);

        // Status
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        formPanel.add(createLabel("📊 Status:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        String[] statuses = {"Available", "Busy", "Unavailable"};
        statusCombo = new JComboBox<>(statuses);
        statusCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(statusCombo, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        saveButton = new JButton(repId == null ? "💾 Save" : "💾 Update");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBackground(new Color(155, 89, 182));
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setPreferredSize(new Dimension(120, 40));
        saveButton.addActionListener(e -> saveRepresentative());

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

    private void loadRepData() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM Representatives WHERE rep_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, repId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                categoryCombo.setSelectedItem(rs.getString("category"));
                contactField.setText(rs.getString("contact"));
                emailField.setText(rs.getString("email"));
                statusCombo.setSelectedItem(rs.getString("status"));
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveRepresentative() {
        // Validate inputs
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter name!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (contactField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter contact!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            String query;

            if (repId == null) {
                query = "INSERT INTO Representatives (name, category, contact, email, status) VALUES (?, ?, ?, ?, ?)";
            } else {
                query = "UPDATE Representatives SET name=?, category=?, contact=?, email=?, status=? WHERE rep_id=?";
            }

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, nameField.getText().trim());
            pstmt.setString(2, (String)categoryCombo.getSelectedItem());
            pstmt.setString(3, contactField.getText().trim());
            pstmt.setString(4, emailField.getText().trim());
            pstmt.setString(5, (String)statusCombo.getSelectedItem());

            if (repId != null) {
                pstmt.setInt(6, repId);
            }

            int result = pstmt.executeUpdate();

            if (result > 0) {
                saved = true;
                JOptionPane.showMessageDialog(this,
                        repId == null ? "Representative added successfully!" : "Representative updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }

            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error saving representative: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}