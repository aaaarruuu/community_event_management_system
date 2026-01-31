package ui;

import database.DBConnection;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Login Screen
 * Attractive UI with colors and authentication
 */
public class LoginScreen extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox rememberCheckBox;
    private JButton loginButton, resetButton;
    private User currentUser;

    public LoginScreen() {
        setTitle("Community Event Management System - Login");
        setSize(900, 600);
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
                Color color1 = new Color(28, 223, 112);
                Color color2 = new Color(5, 174, 234);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(null);

        // Header Label with Icon
        JLabel headerLabel = new JLabel("🏘️ COMMUNITY EVENT SYSTEM", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 32));
        headerLabel.setForeground(Color.yellow);
        headerLabel.setBounds(150, 40, 600, 50);
        mainPanel.add(headerLabel);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Managing Communities, Connecting People", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        subtitleLabel.setForeground(new Color(204, 63, 63, 255));
        subtitleLabel.setBounds(150, 95, 600, 30);
        mainPanel.add(subtitleLabel);

        // Login Panel
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginPanel.setBackground(Color.white);
        loginPanel.setBounds(275, 160, 350, 365);
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219), 3),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Login Title
        JLabel loginTitle = new JLabel("🔐 User Login", SwingConstants.CENTER);
        loginTitle.setFont(new Font("Arial", Font.BOLD, 24));
        loginTitle.setForeground(new Color(41, 128, 185));
        loginTitle.setBounds(50, 20, 250, 35);
        loginPanel.add(loginTitle);

        // Username Label and Field
        JLabel userLabel = new JLabel("👤 Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setForeground(new Color(52, 73, 94));
        userLabel.setBounds(30, 80, 120, 25);
        loginPanel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBounds(30, 110, 290, 35);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        loginPanel.add(usernameField);

        // Password Label and Field
        JLabel passLabel = new JLabel("🔒 Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passLabel.setForeground(new Color(52, 73, 94));
        passLabel.setBounds(30, 155, 120, 25);
        loginPanel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBounds(30, 185, 290, 35);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        loginPanel.add(passwordField);

        // Remember Password Checkbox
        rememberCheckBox = new JCheckBox("Remember Me");
        rememberCheckBox.setFont(new Font("Arial", Font.PLAIN, 12));
        rememberCheckBox.setBackground(Color.WHITE);
        rememberCheckBox.setBounds(30, 230, 150, 25);
        loginPanel.add(rememberCheckBox);

        // Login Button
        loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(39, 174, 96));
        loginButton.setBounds(30, 270, 135, 40);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> performLogin());
        loginPanel.add(loginButton);

        // Reset Button
        resetButton = new JButton("RESET");
        resetButton.setFont(new Font("Arial", Font.BOLD, 16));
        resetButton.setForeground(Color.WHITE);
        resetButton.setBackground(new Color(231, 76, 60));
        resetButton.setBounds(185, 270, 135, 40);
        resetButton.setFocusPainted(false);
        resetButton.setBorderPainted(false);
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetButton.addActionListener(e -> resetFields());
        loginPanel.add(resetButton);

        // Register Link/Button
        JLabel registerLabel = new JLabel("Don't have an account?", SwingConstants.CENTER);
        registerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        registerLabel.setForeground(new Color(127, 140, 141));
        registerLabel.setBounds(80, 315, 140, 20);
        loginPanel.add(registerLabel);

        JButton registerButton = new JButton("Register Now");
        registerButton.setFont(new Font("Arial", Font.BOLD, 12));
        registerButton.setForeground(new Color(41, 128, 185));
        registerButton.setBackground(Color.WHITE);
        registerButton.setBounds(215, 313, 110, 25);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> openRegistration());
        loginPanel.add(registerButton);

        mainPanel.add(loginPanel);

        // Footer
        JLabel footerLabel = new JLabel("© 2026 Community Event Management System", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerLabel.setForeground(Color.WHITE);
        footerLabel.setBounds(250, 520, 400, 30);
        mainPanel.add(footerLabel);

        // Add hover effects
        addHoverEffect(loginButton, new Color(39, 174, 96), new Color(46, 204, 113));
        addHoverEffect(resetButton, new Color(231, 76, 60), new Color(192, 57, 43));

        // Add Enter key listener
        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
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

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password!",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Authenticate user
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                currentUser = new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("contact"),
                        rs.getString("email")
                );

                JOptionPane.showMessageDialog(this,
                        "Login Successful! Welcome " + currentUser.getUsername(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                // Open Main Dashboard
                new MainDashboard(currentUser);
                dispose();

            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password!",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }

            rs.close();
            pstmt.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFields() {
        usernameField.setText("");
        passwordField.setText("");
        rememberCheckBox.setSelected(false);
        usernameField.requestFocus();
    }

    private void openRegistration() {
        dispose();
        new RegistrationScreen();
    }

    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new LoginScreen());
    }
}
