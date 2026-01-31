package ui;

import database.DBConnection;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Event Dialog
 * Form for adding/editing events
 */
public class EventDialog extends JDialog {

    private User currentUser;
    private Integer eventId;
    private boolean saved = false;

    private JTextField titleField, venueField, organizerField;
    private JTextArea descriptionArea;
    private JSpinner dateSpinner;
    private JSpinner timeSpinner;
    private JButton saveButton, cancelButton;

    public EventDialog(Frame parent, String title, User user, Integer eventId) {
        super(parent, title, true);
        this.currentUser = user;
        this.eventId = eventId;

        setSize(550, 600);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();

        if (eventId != null) {
            loadEventData();
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
        gbc.insets = new Insets(8, 8, 8, 8);

        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        formPanel.add(createLabel("📝 Event Title:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        titleField = new JTextField(30);
        titleField.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(titleField, gbc);

        // Date
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(createLabel("📅 Event Date:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setFont(new Font("Arial", Font.PLAIN, 13));
        dateSpinner.setPreferredSize(new Dimension(200, 30));
        formPanel.add(dateSpinner, gbc);

        // Time
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(createLabel("🕐 Event Time:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        SpinnerDateModel timeModel = new SpinnerDateModel();
        timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm:ss");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(timeSpinner, gbc);

        // Venue
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        formPanel.add(createLabel("📍 Venue:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        venueField = new JTextField(30);
        venueField.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(venueField, gbc);

        // Organizer
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        formPanel.add(createLabel("👤 Organizer:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        organizerField = new JTextField(30);
        organizerField.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(organizerField, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(createLabel("📄 Description:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        descriptionArea = new JTextArea(6, 30);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        formPanel.add(scrollPane, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        saveButton = new JButton(eventId == null ? "💾 Save Event" : "💾 Update Event");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBackground(new Color(39, 174, 96));
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setPreferredSize(new Dimension(150, 40));
        saveButton.addActionListener(e -> saveEvent());

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
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(new Color(52, 73, 94));
        return label;
    }

    private void loadEventData() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM Events WHERE event_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                titleField.setText(rs.getString("title"));

                // Set date
                java.sql.Date sqlDate = rs.getDate("event_date");
                if (sqlDate != null) {
                    dateSpinner.setValue(new Date(sqlDate.getTime()));
                }

                // Set time
                java.sql.Time sqlTime = rs.getTime("event_time");
                if (sqlTime != null) {
                    timeSpinner.setValue(new Date(sqlTime.getTime()));
                }

                venueField.setText(rs.getString("venue"));
                organizerField.setText(rs.getString("organizer"));
                descriptionArea.setText(rs.getString("description"));
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveEvent() {
        // Validate inputs
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter event title!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (dateSpinner.getValue() == null) {
            JOptionPane.showMessageDialog(this, "Please select event date!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (venueField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter venue!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            String query;

            if (eventId == null) {
                // Insert new event
                query = "INSERT INTO Events (title, event_date, event_time, description, venue, organizer, created_by) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
            } else {
                // Update existing event
                query = "UPDATE Events SET title=?, event_date=?, event_time=?, description=?, venue=?, organizer=? " +
                        "WHERE event_id=?";
            }

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, titleField.getText().trim());
            pstmt.setDate(2, new java.sql.Date(((Date)dateSpinner.getValue()).getTime()));
            pstmt.setTime(3, new java.sql.Time(((Date)timeSpinner.getValue()).getTime()));
            pstmt.setString(4, descriptionArea.getText().trim());
            pstmt.setString(5, venueField.getText().trim());
            pstmt.setString(6, organizerField.getText().trim());

            if (eventId == null) {
                pstmt.setInt(7, currentUser.getUserId());
            } else {
                pstmt.setInt(7, eventId);
            }

            int result = pstmt.executeUpdate();

            if (result > 0) {
                saved = true;
                JOptionPane.showMessageDialog(this,
                        eventId == null ? "Event added successfully!" : "Event updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }

            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error saving event: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
