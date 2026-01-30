package ui;

import database.DBConnection;
import models.User;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

/**
 * Representative Panel
 * Manage service representatives
 */
public class RepresentativePanel extends JPanel {

    private User currentUser;
    private JTable repTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton, viewAssignmentsButton;
    private JComboBox<String> filterCombo;

    public RepresentativePanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        loadRepresentatives("All");
    }

    private void initComponents() {
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Left side - Title and Filter
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("👷 Representative Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        leftPanel.add(titleLabel);

        JLabel filterLabel = new JLabel("Category:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 13));
        leftPanel.add(filterLabel);

        String[] filters = {"All", "Plumber", "Electrician", "Mechanic", "Gardener", "Cleaner", "Other"};
        filterCombo = new JComboBox<>(filters);
        filterCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        filterCombo.addActionListener(e -> loadRepresentatives((String)filterCombo.getSelectedItem()));
        leftPanel.add(filterCombo);

        headerPanel.add(leftPanel, BorderLayout.WEST);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        addButton = createStyledButton("➕ Add Rep", new Color(39, 174, 96));
        viewAssignmentsButton = createStyledButton("📋 Assignments", new Color(52, 152, 219));
        editButton = createStyledButton("✏️ Edit", new Color(243, 156, 18));
        deleteButton = createStyledButton("🗑️ Delete", new Color(231, 76, 60));
        refreshButton = createStyledButton("🔄 Refresh", new Color(149, 165, 166));

        // Only Admin can add/edit/delete
        if (!currentUser.getRole().equals("Admin")) {
            addButton.setEnabled(false);
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }

        addButton.addActionListener(e -> addRepresentative());
        viewAssignmentsButton.addActionListener(e -> viewAssignments());
        editButton.addActionListener(e -> editRepresentative());
        deleteButton.addActionListener(e -> deleteRepresentative());
        refreshButton.addActionListener(e -> loadRepresentatives((String)filterCombo.getSelectedItem()));

        buttonPanel.add(addButton);
        buttonPanel.add(viewAssignmentsButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"ID", "Name", "Category", "Contact", "Email", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        repTable = new JTable(tableModel);
        repTable.setFont(new Font("Arial", Font.PLAIN, 13));
        repTable.setRowHeight(30);
        repTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        repTable.setSelectionBackground(new Color(155, 89, 182, 100));
        repTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        repTable.getTableHeader().setBackground(new Color(155, 89, 182));
        repTable.getTableHeader().setForeground(Color.WHITE);

        // Custom renderer for status
        repTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected && column == 5) {
                    String status = (String) value;
                    switch(status) {
                        case "Available":
                            setForeground(new Color(39, 174, 96));
                            setFont(getFont().deriveFont(Font.BOLD));
                            break;
                        case "Busy":
                            setForeground(new Color(243, 156, 18));
                            setFont(getFont().deriveFont(Font.BOLD));
                            break;
                        case "Unavailable":
                            setForeground(new Color(231, 76, 60));
                            setFont(getFont().deriveFont(Font.BOLD));
                            break;
                        default:
                            setForeground(Color.BLACK);
                            setFont(getFont().deriveFont(Font.PLAIN));
                    }
                } else if (!isSelected) {
                    setForeground(Color.BLACK);
                    setFont(getFont().deriveFont(Font.PLAIN));
                }

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(repTable);
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
        button.setPreferredSize(new Dimension(130, 35));
        return button;
    }

    private void loadRepresentatives(String filter) {
        tableModel.setRowCount(0);

        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM Representatives";

            if (!filter.equals("All")) {
                query += " WHERE category = ?";
            }

            query += " ORDER BY name";

            PreparedStatement pstmt = conn.prepareStatement(query);
            if (!filter.equals("All")) {
                pstmt.setString(1, filter);
            }

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("rep_id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("contact"),
                        rs.getString("email"),
                        rs.getString("status")
                };
                tableModel.addRow(row);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading representatives: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addRepresentative() {
        RepresentativeDialog dialog = new RepresentativeDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Add New Representative",
                null
        );
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadRepresentatives((String)filterCombo.getSelectedItem());
        }
    }

    private void editRepresentative() {
        int selectedRow = repTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a representative to edit!",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int repId = (int) tableModel.getValueAt(selectedRow, 0);

        RepresentativeDialog dialog = new RepresentativeDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Edit Representative",
                repId
        );
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadRepresentatives((String)filterCombo.getSelectedItem());
        }
    }

    private void deleteRepresentative() {
        int selectedRow = repTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a representative to delete!",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int repId = (int) tableModel.getValueAt(selectedRow, 0);
        String repName = (String) tableModel.getValueAt(selectedRow, 1);

        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete:\n" + repName + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DBConnection.getConnection();
                String query = "DELETE FROM Representatives WHERE rep_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, repId);

                int result = pstmt.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Representative deleted successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadRepresentatives((String)filterCombo.getSelectedItem());
                }

                pstmt.close();

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error deleting representative: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewAssignments() {
        int selectedRow = repTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a representative to view assignments!",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int repId = (int) tableModel.getValueAt(selectedRow, 0);
        String repName = (String) tableModel.getValueAt(selectedRow, 1);

        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT i.issue_id, i.category, i.location, i.status, a.assigned_date, a.status as assign_status " +
                    "FROM Assignments a " +
                    "JOIN Issues i ON a.issue_id = i.issue_id " +
                    "WHERE a.rep_id = ? " +
                    "ORDER BY a.assigned_date DESC";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, repId);
            ResultSet rs = pstmt.executeQuery();

            StringBuilder assignmentList = new StringBuilder();
            assignmentList.append(String.format(
                    "<html><body style='width: 500px; font-family: Arial;'>" +
                            "<h2 style='color: #9b59b6;'>Assignments for %s</h2>", repName
            ));

            int count = 0;
            while (rs.next()) {
                count++;
                assignmentList.append(String.format(
                        "<div style='background-color: #ecf0f1; padding: 10px; margin: 10px 0;'>" +
                                "<b>Issue #%d:</b> %s<br/>" +
                                "<b>Location:</b> %s<br/>" +
                                "<b>Status:</b> %s | <b>Assignment Status:</b> %s<br/>" +
                                "<b>Assigned:</b> %s" +
                                "</div>",
                        rs.getInt("issue_id"),
                        rs.getString("category"),
                        rs.getString("location"),
                        rs.getString("status"),
                        rs.getString("assign_status"),
                        rs.getTimestamp("assigned_date")
                ));
            }

            if (count == 0) {
                assignmentList.append("<p>No assignments found.</p>");
            }

            assignmentList.append("</body></html>");

            JOptionPane.showMessageDialog(this,
                    new JLabel(assignmentList.toString()),
                    "Assignments",
                    JOptionPane.INFORMATION_MESSAGE);

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading assignments: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}