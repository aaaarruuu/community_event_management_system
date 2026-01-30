package ui;

import database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Registration Screen
 * Allows new users to register for the system
 */
public class RegistrationScreen extends JFrame {

    private JTextField usernameField, contactField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> roleCombo;
    private JButton registerButton, cancelButton, backToLoginButton;

    public RegistrationScreen() {
        setTitle("Community Event Management System - New User Registration");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(46, 204, 113);
                Color color2 = new Color(52, 152, 219);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(null);

        // Header Label
        JLabel headerLabel = new JLabel("🏘️ JOIN OUR COMMUNITY", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 32));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setBounds(150, 30, 600, 50);
        mainPanel.add(headerLabel);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Create your account to get started", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        subtitleLabel.setBounds(150, 85, 600, 30);
        mainPanel.add(subtitleLabel);

        // Registration Panel
        JPanel registrationPanel = new JPanel();
        registrationPanel.setLayout(null);
        registrationPanel.setBackground(Color.WHITE);
        registrationPanel.setBounds(200, 140, 500, 480);
        registrationPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(46, 204, 113), 3),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Registration Title
        JLabel regTitle = new JLabel("📝 User Registration", SwingConstants.CENTER);
        regTitle.setFont(new Font("Arial", Font.BOLD, 24));
        regTitle.setForeground(new Color(46, 204, 113));
        regTitle.setBounds(50, 15, 400, 35);
        registrationPanel.add(regTitle);

        int yPos = 70;
        int spacing = 70;

        // Username
        JLabel userLabel = new JLabel("👤 Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setForeground(new Color(52, 73, 94));
        userLabel.setBounds(30, yPos, 120, 25);
        registrationPanel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBounds(30, yPos + 30, 440, 35);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        registrationPanel.add(usernameField);

        yPos += spacing;

        // Password
        JLabel passLabel = new JLabel("🔒 Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passLabel.setForeground(new Color(52, 73, 94));
        passLabel.setBounds(30, yPos, 120, 25);
        registrationPanel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBounds(30, yPos + 30, 440, 35);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        registrationPanel.add(passwordField);

        yPos += spacing;

        // Confirm Password
        JLabel confirmLabel = new JLabel("🔒 Confirm Password:");
        confirmLabel.setFont(new Font("Arial", Font.BOLD, 14));
        confirmLabel.setForeground(new Color(52, 73, 94));
        confirmLabel.setBounds(30, yPos, 180, 25);
        registrationPanel.add(confirmLabel);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmPasswordField.setBounds(30, yPos + 30, 440, 35);
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        registrationPanel.add(confirmPasswordField);

        yPos += spacing;

        // Contact
        JLabel contactLabel = new JLabel("📞 Contact Number:");
        contactLabel.setFont(new Font("Arial", Font.BOLD, 14));
        contactLabel.setForeground(new Color(52, 73, 94));
        contactLabel.setBounds(30, yPos, 150, 25);
        registrationPanel.add(contactLabel);

        contactField = new JTextField();
        contactField.setFont(new Font("Arial", Font.PLAIN, 14));
        contactField.setBounds(30, yPos + 30, 210, 35);
        contactField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        registrationPanel.add(contactField);

        // Role
        JLabel roleLabel = new JLabel("👥 Role:");
        roleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        roleLabel.setForeground(new Color(52, 73, 94));
        roleLabel.setBounds(260, yPos, 80, 25);
        registrationPanel.add(roleLabel);

        String[] roles = {"Member", "Admin"};
        roleCombo = new JComboBox<>(roles);
        roleCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        roleCombo.setBounds(260, yPos + 30, 210, 35);
        roleCombo.setBackground(Color.WHITE);
        registrationPanel.add(roleCombo);

        yPos += spacing;

        // Email
        JLabel emailLabel = new JLabel("📧 Email Address:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailLabel.setForeground(new Color(52, 73, 94));
        emailLabel.setBounds(30, yPos, 150, 25);
        registrationPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBounds(30, yPos + 30, 440, 35);
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        registrationPanel.add(emailField);

        yPos += spacing + 10;

        // Register Button
        registerButton = new JButton("✅ REGISTER");
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBackground(new Color(46, 204, 113));
        registerButton.setBounds(30, yPos, 210, 45);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> registerUser());
        registrationPanel.add(registerButton);

        // Cancel Button
        cancelButton = new JButton("❌ CLEAR");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 16));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setBounds(260, yPos, 210, 45);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> clearFields());
        registrationPanel.add(cancelButton);

        mainPanel.add(registrationPanel);

        // Back to Login Button
        backToLoginButton = new JButton("⬅️ Back to Login");
        backToLoginButton.setFont(new Font("Arial", Font.BOLD, 14));
        backToLoginButton.setForeground(Color.WHITE);
        backToLoginButton.setBackground(new Color(52, 152, 219));
        backToLoginButton.setBounds(350, 630, 200, 40);
        backToLoginButton.setFocusPainted(false);
        backToLoginButton.setBorderPainted(false);
        backToLoginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToLoginButton.addActionListener(e -> backToLogin());
        mainPanel.add(backToLoginButton);

        // Footer
        JLabel footerLabel = new JLabel("© 2026 Community Event Management System", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerLabel.setForeground(Color.WHITE);
        footerLabel.setBounds(250, 665, 400, 20);
        mainPanel.add(footerLabel);

        // Add hover effects
        addHoverEffect(registerButton, new Color(46, 204, 113), new Color(39, 174, 96));
        addHoverEffect(cancelButton, new Color(149, 165, 166), new Color(127, 140, 141));
        addHoverEffect(backToLoginButton, new Color(52, 152, 219), new Color(41, 128, 185));

        // Add Enter key listener for password confirmation
        confirmPasswordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    registerUser();
                }
            }
        });

        add(mainPanel);
    }

    private void addHoverEffect(JButton button, Color normal, Color hover) {
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hover);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(normal);
            }
        });
    }

    private void registerUser() {
        // Get field values
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
        String contact = contactField.getText().trim();
        String email = emailField.getText().trim();
        String role = (String) roleCombo.getSelectedItem();

        // Validation
        if (username.isEmpty()) {
            showError("Please enter a username!");
            usernameField.requestFocus();
            return;
        }

        if (username.length() < 4) {
            showError("Username must be at least 4 characters long!");
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showError("Please enter a password!");
            passwordField.requestFocus();
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters long!");
            passwordField.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match!");
            confirmPasswordField.requestFocus();
            return;
        }

        if (contact.isEmpty()) {
            showError("Please enter a contact number!");
            contactField.requestFocus();
            return;
        }

        if (!contact.matches("\\d{10}")) {
            showError("Contact number must be 10 digits!");
            contactField.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            showError("Please enter an email address!");
            emailField.requestFocus();
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Please enter a valid email address!");
            emailField.requestFocus();
            return;
        }

        // Check if username already exists
        try {
            Connection conn = DBConnection.getConnection();
            String checkQuery = "SELECT COUNT(*) FROM Users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                showError("Username already exists! Please choose a different username.");
                usernameField.requestFocus();
                rs.close();
                checkStmt.close();
                return;
            }

            rs.close();
            checkStmt.close();

            // Insert new user
            String insertQuery = "INSERT INTO Users (username, password, role, contact, email) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, username);
            insertStmt.setString(2, password); // Note: In production, use password hashing
            insertStmt.setString(3, role);
            insertStmt.setString(4, contact);
            insertStmt.setString(5, email);

            int result = insertStmt.executeUpdate();
            insertStmt.close();

            if (result > 0) {
                JOptionPane.showMessageDialog(this,
                        "✅ Registration Successful!\n\n" +
                                "Username: " + username + "\n" +
                                "Role: " + role + "\n\n" +
                                "You can now login with your credentials.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                // Redirect to login screen
                backToLogin();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error: " + e.getMessage());
        }
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        contactField.setText("");
        emailField.setText("");
        roleCombo.setSelectedIndex(0);
        usernameField.requestFocus();
    }

    private void backToLogin() {
        dispose();
        new LoginScreen();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new RegistrationScreen());
    }
}