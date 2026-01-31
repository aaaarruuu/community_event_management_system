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

        String[] columns = {"ID", "Title", "Date", "Time", "Venue", "Organizer", "Description", "Created By", "Access"};
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

        // Custom renderer for ownership visualization
        eventTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    String access = (String) table.getValueAt(row, 8); // Access column
                    if (access != null) {
                        if (access.equals("🔓 Can Edit")) {
                            c.setBackground(new Color(232, 245, 233)); // Light green
                        } else if (access.equals("🔒 View Only")) {
                            c.setBackground(new Color(255, 243, 224)); // Light orange
                        } else {
                            c.setBackground(Color.WHITE);
                        }
                    }
                }

                // Special styling for Access column
                if (column == 8) {
                    setFont(getFont().deriveFont(Font.BOLD));
                    String access = (String) value;
                    if (!isSelected) {
                        if (access != null && access.equals("🔓 Can Edit")) {
                            setForeground(new Color(39, 174, 96));
                        } else if (access != null && access.equals("🔒 View Only")) {
                            setForeground(new Color(243, 156, 18));
                        }
                    }
                }

                return c;
            }
        });

        // Set column widths
        eventTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        eventTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        eventTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        eventTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        eventTable.getColumnModel().getColumn(4).setPreferredWidth(130);
        eventTable.getColumnModel().getColumn(5).setPreferredWidth(110);
        eventTable.getColumnModel().getColumn(6).setPreferredWidth(200);
        eventTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        eventTable.getColumnModel().getColumn(8).setPreferredWidth(90);

        JScrollPane scrollPane = new JScrollPane(eventTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Add legend panel
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        legendPanel.setOpaque(false);
        legendPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JLabel legendLabel = new JLabel("Legend:");
        legendLabel.setFont(new Font("Arial", Font.BOLD, 12));
        legendLabel.setForeground(new Color(52, 73, 94));
        legendPanel.add(legendLabel);

        JLabel canEditLabel = new JLabel("🔓 Can Edit (Your events or Admin)");
        canEditLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        canEditLabel.setForeground(new Color(39, 174, 96));
        canEditLabel.setOpaque(true);
        canEditLabel.setBackground(new Color(232, 245, 233));
        canEditLabel.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        legendPanel.add(canEditLabel);

        JLabel viewOnlyLabel = new JLabel("🔒 View Only (Others' events)");
        viewOnlyLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        viewOnlyLabel.setForeground(new Color(243, 156, 18));
        viewOnlyLabel.setOpaque(true);
        viewOnlyLabel.setBackground(new Color(255, 243, 224));
        viewOnlyLabel.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        legendPanel.add(viewOnlyLabel);

        tablePanel.add(legendPanel, BorderLayout.SOUTH);

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
            String query = "SELECT e.*, u.username as creator_name FROM Events e " +
                    "LEFT JOIN Users u ON e.created_by = u.user_id " +
                    "ORDER BY e.event_date DESC, e.event_time DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int eventId = rs.getInt("event_id");
                int createdBy = rs.getInt("created_by");
                String creatorName = rs.getString("creator_name");
                if (creatorName == null) creatorName = "Unknown";

                // Determine access level
                String access;
                if (currentUser.getRole().equals("Admin") || createdBy == currentUser.getUserId()) {
                    access = "🔓 Can Edit";
                } else {
                    access = "🔒 View Only";
                }

                Object[] row = {
                        eventId,
                        rs.getString("title"),
                        rs.getDate("event_date"),
                        rs.getTime("event_time"),
                        rs.getString("venue"),
                        rs.getString("organizer"),
                        rs.getString("description"),
                        creatorName,
                        access
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

        // Security Check: Only Admin or Event Creator can edit
        if (!canModifyEvent(eventId)) {
            JOptionPane.showMessageDialog(this,
                    "🔒 Access Denied!\n\nYou can only edit events that you created.\n" +
                            "Admins can edit all events.",
                    "Permission Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

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

        // Security Check: Only Admin or Event Creator can delete
        if (!canModifyEvent(eventId)) {
            JOptionPane.showMessageDialog(this,
                    "🔒 Access Denied!\n\nYou can only delete events that you created.\n" +
                            "Admins can delete all events.",
                    "Permission Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

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

    /**
     * Security method: Check if current user can modify the event
     * @param eventId The ID of the event to check
     * @return true if user is Admin or the event creator
     */
    private boolean canModifyEvent(int eventId) {
        // Admins can modify all events
        if (currentUser.getRole().equals("Admin")) {
            return true;
        }

        // Check if current user is the creator
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT created_by FROM Events WHERE event_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int creatorId = rs.getInt("created_by");
                rs.close();
                pstmt.close();
                return creatorId == currentUser.getUserId();
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
