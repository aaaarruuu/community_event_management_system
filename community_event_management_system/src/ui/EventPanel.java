package ui;

import database.DBConnection;
import models.User;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;

/**
 * Event Panel
 * Manage community events - Create, View, Update, Delete
 */
public class EventPanel extends JPanel {

    private User currentUser;
    private JTable eventTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton, viewButton;

    public EventPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        loadEvents();
    }

    private void initComponents() {
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("📅 Event Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        addButton = createStyledButton("➕ Add Event", new Color(39, 174, 96));
        viewButton = createStyledButton("👁️ View Details", new Color(52, 152, 219));
        editButton = createStyledButton("✏️ Edit Event", new Color(243, 156, 18));
        deleteButton = createStyledButton("🗑️ Delete Event", new Color(231, 76, 60));
        refreshButton = createStyledButton("🔄 Refresh", new Color(149, 165, 166));

        addButton.addActionListener(e -> addEvent());
        viewButton.addActionListener(e -> viewEventDetails());
        editButton.addActionListener(e -> editEvent());
        deleteButton.addActionListener(e -> deleteEvent());
        refreshButton.addActionListener(e -> loadEvents());

        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"ID", "Title", "Date", "Time", "Venue", "Organizer", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        eventTable = new JTable(tableModel);
        eventTable.setFont(new Font("Arial", Font.PLAIN, 13));
        eventTable.setRowHeight(30);
        eventTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventTable.setSelectionBackground(new Color(52, 152, 219, 100));
        eventTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        eventTable.getTableHeader().setBackground(new Color(52, 152, 219));
        eventTable.getTableHeader().setForeground(Color.WHITE);

        // Set column widths
        eventTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        eventTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        eventTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        eventTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        eventTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        eventTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        eventTable.getColumnModel().getColumn(6).setPreferredWidth(250);

        JScrollPane scrollPane = new JScrollPane(eventTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 35));
        return button;
    }

    private void loadEvents() {
        tableModel.setRowCount(0);

        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM Events ORDER BY event_date DESC, event_time DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("event_id"),
                        rs.getString("title"),
                        rs.getDate("event_date"),
                        rs.getTime("event_time"),
                        rs.getString("venue"),
                        rs.getString("organizer"),
                        rs.getString("description")
                };
                tableModel.addRow(row);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading events: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addEvent() {
        EventDialog dialog = new EventDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Add New Event",
                currentUser,
                null
        );
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadEvents();
        }
    }

    private void viewEventDetails() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an event to view!",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int eventId = (int) tableModel.getValueAt(selectedRow, 0);
        showEventDetails(eventId);
    }

    private void showEventDetails(int eventId) {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM Events WHERE event_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String details = String.format(
                        "<html><body style='width: 400px; font-family: Arial;'>" +
                                "<h2 style='color: #3498db;'>📅 %s</h2>" +
                                "<p><b>Date:</b> %s</p>" +
                                "<p><b>Time:</b> %s</p>" +
                                "<p><b>Venue:</b> %s</p>" +
                                "<p><b>Organizer:</b> %s</p>" +
                                "<p><b>Description:</b></p>" +
                                "<p style='padding: 10px; background-color: #ecf0f1;'>%s</p>" +
                                "</body></html>",
                        rs.getString("title"),
                        rs.getDate("event_date"),
                        rs.getTime("event_time"),
                        rs.getString("venue"),
                        rs.getString("organizer"),
                        rs.getString("description")
                );

                JOptionPane.showMessageDialog(this,
                        new JLabel(details),
                        "Event Details",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void editEvent() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an event to edit!",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int eventId = (int) tableModel.getValueAt(selectedRow, 0);

        EventDialog dialog = new EventDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Edit Event",
                currentUser,
                eventId
        );
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadEvents();
        }
    }

    private void deleteEvent() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an event to delete!",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int eventId = (int) tableModel.getValueAt(selectedRow, 0);
        String eventTitle = (String) tableModel.getValueAt(selectedRow, 1);

        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete event:\n" + eventTitle + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DBConnection.getConnection();
                String query = "DELETE FROM Events WHERE event_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, eventId);

                int result = pstmt.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Event deleted successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadEvents();
                }

                pstmt.close();

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error deleting event: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}