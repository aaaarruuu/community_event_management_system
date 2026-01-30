package ui;

import database.DBConnection;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

/**
 * Dashboard Panel
 * Shows statistics and quick overview
 */
public class DashboardPanel extends JPanel {

    private User currentUser;
    private JLabel pendingIssuesLabel, inProgressLabel, completedLabel;
    private JLabel upcomingEventsLabel, pastEventsLabel, availableRepsLabel;

    public DashboardPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));

        JLabel titleLabel = new JLabel("📊 Dashboard Overview");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(52, 73, 94));
        titlePanel.add(titleLabel);

        add(titlePanel, BorderLayout.NORTH);

        // Stats Panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(2, 3, 20, 20));
        statsPanel.setBackground(new Color(236, 240, 241));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Create stat cards
        statsPanel.add(createStatCard("⚠️ Pending Issues", "0", new Color(231, 76, 60), "pendingIssues"));
        statsPanel.add(createStatCard("🔄 In-Progress Issues", "0", new Color(243, 156, 18), "inProgress"));
        statsPanel.add(createStatCard("✅ Completed Issues", "0", new Color(39, 174, 96), "completed"));
        statsPanel.add(createStatCard("📅 Upcoming Events", "0", new Color(52, 152, 219), "upcomingEvents"));
        statsPanel.add(createStatCard("📜 Past Events", "0", new Color(149, 165, 166), "pastEvents"));
        statsPanel.add(createStatCard("👷 Available Reps", "0", new Color(155, 89, 182), "availableReps"));

        add(statsPanel, BorderLayout.CENTER);

        // Welcome Message Panel
        JPanel welcomePanel = new JPanel();
        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, new Color(52, 152, 219)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel welcomeLabel = new JLabel("<html><center>Welcome, <b>" + currentUser.getUsername() +
                "</b>!<br/>Use the tabs above to navigate through Events, Issues, and Representatives.</center></html>");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        welcomeLabel.setForeground(new Color(52, 73, 94));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomePanel.add(welcomeLabel);

        add(welcomePanel, BorderLayout.SOUTH);
    }

    private JPanel createStatCard(String title, String value, Color color, String type) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 3),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Icon/Title Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(color);
        topPanel.add(titleLabel);

        // Value Label
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 48));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Store reference based on type
        switch(type) {
            case "pendingIssues": pendingIssuesLabel = valueLabel; break;
            case "inProgress": inProgressLabel = valueLabel; break;
            case "completed": completedLabel = valueLabel; break;
            case "upcomingEvents": upcomingEventsLabel = valueLabel; break;
            case "pastEvents": pastEventsLabel = valueLabel; break;
            case "availableReps": availableRepsLabel = valueLabel; break;
        }

        card.add(topPanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    public void refreshData() {
        try {
            Connection conn = DBConnection.getConnection();

            // Get dashboard stats from view
            String query = "SELECT * FROM dashboard_stats";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                pendingIssuesLabel.setText(String.valueOf(rs.getInt("pending_issues")));
                inProgressLabel.setText(String.valueOf(rs.getInt("inprogress_issues")));
                completedLabel.setText(String.valueOf(rs.getInt("completed_issues")));
                upcomingEventsLabel.setText(String.valueOf(rs.getInt("upcoming_events")));
                pastEventsLabel.setText(String.valueOf(rs.getInt("past_events")));
                availableRepsLabel.setText(String.valueOf(rs.getInt("available_reps")));
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading dashboard data: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}