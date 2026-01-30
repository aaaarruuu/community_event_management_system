package ui;

import models.User;
import javax.swing.*;
import java.awt.*;

/**
 * Main Dashboard
 * Central hub with tabbed panels for all features
 */
public class MainDashboard extends JFrame {

    private User currentUser;
    private JTabbedPane tabbedPane;
    private DashboardPanel dashboardPanel;
    private EventPanel eventPanel;
    private IssuePanel issuePanel;
    private RepresentativePanel representativePanel;

    public MainDashboard(User user) {
        this.currentUser = user;

        setTitle("Community Event Management System - Dashboard");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        // Main container
        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        container.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);

        // Initialize panels
        dashboardPanel = new DashboardPanel(currentUser);
        eventPanel = new EventPanel(currentUser);
        issuePanel = new IssuePanel(currentUser);
        representativePanel = new RepresentativePanel(currentUser);

        // Add tabs with icons
        tabbedPane.addTab("  📊 Dashboard  ", dashboardPanel);
        tabbedPane.addTab("  📅 Events  ", eventPanel);
        tabbedPane.addTab("  ⚠️ Issues  ", issuePanel);
        tabbedPane.addTab("  👷 Representatives  ", representativePanel);

        // Set tab colors
        tabbedPane.setBackgroundAt(0, new Color(52, 152, 219, 50));
        tabbedPane.setBackgroundAt(1, new Color(46, 204, 113, 50));
        tabbedPane.setBackgroundAt(2, new Color(231, 76, 60, 50));
        tabbedPane.setBackgroundAt(3, new Color(155, 89, 182, 50));

        container.add(tabbedPane, BorderLayout.CENTER);

        // Footer Panel
        JPanel footerPanel = createFooterPanel();
        container.add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(41, 128, 185));
        panel.setPreferredSize(new Dimension(0, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Left side - Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("🏘️ Community Event Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        leftPanel.add(titleLabel);

        // Right side - User info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        JLabel userLabel = new JLabel("👤 " + currentUser.getUsername() + " (" + currentUser.getRole() + ")  ");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setForeground(Color.WHITE);
        rightPanel.add(userLabel);

        JButton logoutButton = new JButton("🚪 Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(new Color(192, 57, 43));
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> logout());
        rightPanel.add(logoutButton);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(44, 62, 80));
        panel.setPreferredSize(new Dimension(0, 35));

        JLabel footerLabel = new JLabel("© 2026 Community Event Management System | Version 1.0");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        footerLabel.setForeground(Color.WHITE);
        panel.add(footerLabel);

        return panel;
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginScreen();
        }
    }

    public void refreshDashboard() {
        dashboardPanel.refreshData();
    }
}