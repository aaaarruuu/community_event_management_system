package ui;

import database.DBConnection;
import models.User;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Issue Panel
 * Manage community issues/problems
 */
public class IssuePanel extends JPanel {
    
    private User currentUser;
    private JTable issueTable;
    private DefaultTableModel tableModel;
    private JButton addButton, viewButton, assignButton, updateStatusButton, deleteButton, refreshButton;
    private JComboBox<String> filterCombo;
    
    public IssuePanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
        loadIssues("All");
    }
    
    private void initComponents() {
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Left side - Title and Filter
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("⚠️ Issue Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        leftPanel.add(titleLabel);
        
        JLabel filterLabel = new JLabel("Filter:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 13));
        leftPanel.add(filterLabel);
        
        String[] filters = {"All", "Pending", "In-Progress", "Completed", "Cancelled"};
        filterCombo = new JComboBox<>(filters);
        filterCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        filterCombo.addActionListener(e -> loadIssues((String)filterCombo.getSelectedItem()));
        leftPanel.add(filterCombo);
        
        headerPanel.add(leftPanel, BorderLayout.WEST);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        addButton = createStyledButton("➕ Report Issue", new Color(39, 174, 96));
        viewButton = createStyledButton("👁️ View Details", new Color(52, 152, 219));
        assignButton = createStyledButton("👷 Assign Rep", new Color(155, 89, 182));
        updateStatusButton = createStyledButton("🔄 Update Status", new Color(243, 156, 18));
        deleteButton = createStyledButton("🗑️ Delete", new Color(231, 76, 60));
        refreshButton = createStyledButton("🔄 Refresh", new Color(149, 165, 166));
        
        addButton.addActionListener(e -> reportIssue());
        viewButton.addActionListener(e -> viewIssueDetails());
        assignButton.addActionListener(e -> assignRepresentative());
        updateStatusButton.addActionListener(e -> updateStatus());
        deleteButton.addActionListener(e -> deleteIssue());
        refreshButton.addActionListener(e -> loadIssues((String)filterCombo.getSelectedItem()));
        
        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(assignButton);
        buttonPanel.add(updateStatusButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columns = {"ID", "Category", "Location", "Status", "Priority", "Date Reported", "Reporter", "Access"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        issueTable = new JTable(tableModel);
        issueTable.setFont(new Font("Arial", Font.PLAIN, 13));
        issueTable.setRowHeight(30);
        issueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        issueTable.setSelectionBackground(new Color(231, 76, 60, 100));
        issueTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        issueTable.getTableHeader().setBackground(new Color(231, 76, 60));
        issueTable.getTableHeader().setForeground(Color.WHITE);
        
        // Custom cell renderer for status colors and access
        issueTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String access = (String) table.getValueAt(row, 7); // Access column
                    String status = (String) table.getValueAt(row, 3);
                    
                    // Color by access first, then by status
                    if (access != null && access.equals("🔓 Can Edit")) {
                        c.setBackground(new Color(232, 245, 233)); // Light green
                    } else if (access != null && access.equals("🔒 View Only")) {
                        c.setBackground(new Color(255, 243, 224)); // Light orange
                    } else {
                        // Fallback to status-based coloring
                        switch(status) {
                            case "Pending":
                                c.setBackground(new Color(255, 243, 224));
                                break;
                            case "In-Progress":
                                c.setBackground(new Color(232, 245, 233));
                                break;
                            case "Completed":
                                c.setBackground(new Color(225, 245, 254));
                                break;
                            default:
                                c.setBackground(Color.WHITE);
                        }
                    }
                }
                
                // Special styling for Access column
                if (column == 7) {
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
        
        JScrollPane scrollPane = new JScrollPane(issueTable);
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
        
        JLabel canEditLabel = new JLabel("🔓 Can Edit (Your issues or Admin)");
        canEditLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        canEditLabel.setForeground(new Color(39, 174, 96));
        canEditLabel.setOpaque(true);
        canEditLabel.setBackground(new Color(232, 245, 233));
        canEditLabel.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        legendPanel.add(canEditLabel);
        
        JLabel viewOnlyLabel = new JLabel("🔒 View Only (Others' issues)");
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
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(130, 35));
        return button;
    }
    
    private void loadIssues(String filter) {
        tableModel.setRowCount(0);
        
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT i.*, u.username FROM Issues i " +
                          "LEFT JOIN Users u ON i.reporter_id = u.user_id";
            
            if (!filter.equals("All")) {
                query += " WHERE i.status = ?";
            }
            
            query += " ORDER BY i.date_reported DESC";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            if (!filter.equals("All")) {
                pstmt.setString(1, filter);
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int issueId = rs.getInt("issue_id");
                int reporterId = rs.getInt("reporter_id");
                String reporterName = rs.getString("username");
                if (reporterName == null) reporterName = "Unknown";
                
                // Determine access level
                String access;
                if (currentUser.getRole().equals("Admin") || reporterId == currentUser.getUserId()) {
                    access = "🔓 Can Edit";
                } else {
                    access = "🔒 View Only";
                }
                
                Object[] row = {
                    issueId,
                    rs.getString("category"),
                    rs.getString("location"),
                    rs.getString("status"),
                    rs.getString("priority"),
                    rs.getTimestamp("date_reported"),
                    reporterName,
                    access
                };
                tableModel.addRow(row);
            }
            
            rs.close();
            pstmt.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading issues: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void reportIssue() {
        IssueDialog dialog = new IssueDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Report New Issue",
            currentUser,
            null
        );
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            loadIssues((String)filterCombo.getSelectedItem());
        }
    }
    
    private void viewIssueDetails() {
        int selectedRow = issueTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an issue to view!",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int issueId = (int) tableModel.getValueAt(selectedRow, 0);
        showIssueDetails(issueId);
    }
    
    private void showIssueDetails(int issueId) {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT i.*, u.username, u.contact, " +
                          "a.assign_id, r.name as rep_name, r.contact as rep_contact " +
                          "FROM Issues i " +
                          "LEFT JOIN Users u ON i.reporter_id = u.user_id " +
                          "LEFT JOIN Assignments a ON i.issue_id = a.issue_id " +
                          "LEFT JOIN Representatives r ON a.rep_id = r.rep_id " +
                          "WHERE i.issue_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, issueId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String repInfo = rs.getString("rep_name") != null ? 
                    rs.getString("rep_name") + " (" + rs.getString("rep_contact") + ")" : 
                    "Not Assigned";
                
                String details = String.format(
                    "<html><body style='width: 450px; font-family: Arial;'>" +
                    "<h2 style='color: #e74c3c;'>⚠️ Issue #%d</h2>" +
                    "<p><b>Category:</b> %s</p>" +
                    "<p><b>Location:</b> %s</p>" +
                    "<p><b>Status:</b> <span style='color: %s;'>%s</span></p>" +
                    "<p><b>Priority:</b> %s</p>" +
                    "<p><b>Reported By:</b> %s (%s)</p>" +
                    "<p><b>Date:</b> %s</p>" +
                    "<p><b>Assigned To:</b> %s</p>" +
                    "<p><b>Description:</b></p>" +
                    "<p style='padding: 10px; background-color: #ecf0f1;'>%s</p>" +
                    "</body></html>",
                    issueId,
                    rs.getString("category"),
                    rs.getString("location"),
                    getStatusColor(rs.getString("status")),
                    rs.getString("status"),
                    rs.getString("priority"),
                    rs.getString("username"),
                    rs.getString("contact"),
                    rs.getTimestamp("date_reported"),
                    repInfo,
                    rs.getString("description")
                );
                
                JOptionPane.showMessageDialog(this,
                    new JLabel(details),
                    "Issue Details",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
            rs.close();
            pstmt.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private String getStatusColor(String status) {
        switch(status) {
            case "Pending": return "#f39c12";
            case "In-Progress": return "#3498db";
            case "Completed": return "#27ae60";
            default: return "#95a5a6";
        }
    }
    
    private void assignRepresentative() {
        int selectedRow = issueTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an issue to assign!",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int issueId = (int) tableModel.getValueAt(selectedRow, 0);
        String category = (String) tableModel.getValueAt(selectedRow, 1);
        
        AssignmentDialog dialog = new AssignmentDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            issueId,
            category
        );
        dialog.setVisible(true);
        
        if (dialog.isAssigned()) {
            loadIssues((String)filterCombo.getSelectedItem());
        }
    }
    
    private void updateStatus() {
        int selectedRow = issueTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an issue to update!",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int issueId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 3);
        
        String[] statuses = {"Pending", "In-Progress", "Completed", "Cancelled"};
        String newStatus = (String) JOptionPane.showInputDialog(
            this,
            "Select new status:",
            "Update Issue Status",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statuses,
            currentStatus
        );
        
        if (newStatus != null && !newStatus.equals(currentStatus)) {
            try {
                Connection conn = DBConnection.getConnection();
                String query = "UPDATE Issues SET status = ? WHERE issue_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, newStatus);
                pstmt.setInt(2, issueId);
                
                int result = pstmt.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Status updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    loadIssues((String)filterCombo.getSelectedItem());
                }
                
                pstmt.close();
                
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error updating status: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteIssue() {
        int selectedRow = issueTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an issue to delete!",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int issueId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Security Check: Only Admin or Issue Reporter can delete
        if (!canModifyIssue(issueId)) {
            JOptionPane.showMessageDialog(this,
                "🔒 Access Denied!\n\nYou can only delete issues that you reported.\n" +
                "Admins can delete all issues.",
                "Permission Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this issue?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DBConnection.getConnection();
                String query = "DELETE FROM Issues WHERE issue_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, issueId);
                
                int result = pstmt.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Issue deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    loadIssues((String)filterCombo.getSelectedItem());
                }
                
                pstmt.close();
                
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error deleting issue: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Security method: Check if current user can modify the issue
     * @param issueId The ID of the issue to check
     * @return true if user is Admin or the issue reporter
     */
    private boolean canModifyIssue(int issueId) {
        // Admins can modify all issues
        if (currentUser.getRole().equals("Admin")) {
            return true;
        }
        
        // Check if current user is the reporter
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT reporter_id FROM Issues WHERE issue_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, issueId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int reporterId = rs.getInt("reporter_id");
                rs.close();
                pstmt.close();
                return reporterId == currentUser.getUserId();
            }
            
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
}
