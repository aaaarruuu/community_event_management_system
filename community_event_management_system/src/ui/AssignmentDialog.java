package ui;

import database.DBConnection;
import models.Representative;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Assignment Dialog
 * Assign representatives to issues based on category
 */
public class AssignmentDialog extends JDialog {

    private int issueId;
    private String issueCategory;
    private boolean assigned = false;

    private JComboBox<Representative> repCombo;
    private JTextArea notesArea;
    private JButton assignButton, cancelButton;
    private ArrayList<Representative> representatives;

    // Category to Representative type mapping
    private static final Map<String, String> CATEGORY_MAP = new HashMap<String, String>() {{
        put("Water Leakage", "Plumber");
        put("Electrical Problem", "Electrician");
        put("Road Damage", "Mechanic");
        put("Structural Issue", "Mechanic");
        put("Garbage/Bins", "Cleaner");
        put("Other", "Other");
    }};

    public AssignmentDialog(Frame parent, int issueId, String issueCategory) {
        super(parent, "Assign Representative", true);
        this.issueId = issueId;
        this.issueCategory = issueCategory;

        setSize(500, 400);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
        loadRepresentatives();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(236, 240, 241));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("👷 Assign Representative to Issue #" + issueId);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(155, 89, 182));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(titleLabel);

        infoPanel.add(Box.createVerticalStrut(10));

        String repType = CATEGORY_MAP.getOrDefault(issueCategory, "Other");
        JLabel categoryLabel = new JLabel("Issue Category: " + issueCategory + " → Needs: " + repType);
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        categoryLabel.setForeground(new Color(52, 73, 94));
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(categoryLabel);

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Representative Selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        JLabel repLabel = new JLabel("Select Representative:");
        repLabel.setFont(new Font("Arial", Font.BOLD, 14));
        repLabel.setForeground(new Color(52, 73, 94));
        formPanel.add(repLabel, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        repCombo = new JComboBox<>();
        repCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        repCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Representative) {
                    Representative rep = (Representative) value;
                    setText(rep.getName() + " - " + rep.getCategory() + " (" + rep.getStatus() + ")");
                    if (rep.getStatus().equals("Available")) {
                        setForeground(new Color(39, 174, 96));
                    } else {
                        setForeground(new Color(192, 57, 43));
                    }
                }
                return this;
            }
        });
        formPanel.add(repCombo, gbc);

        // Notes
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel notesLabel = new JLabel("Notes (Optional):");
        notesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        notesLabel.setForeground(new Color(52, 73, 94));
        formPanel.add(notesLabel, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        notesArea = new JTextArea(5, 30);
        notesArea.setFont(new Font("Arial", Font.PLAIN, 13));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane scrollPane = new JScrollPane(notesArea);
        formPanel.add(scrollPane, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        assignButton = new JButton("✅ Assign");
        assignButton.setFont(new Font("Arial", Font.BOLD, 14));
        assignButton.setForeground(Color.WHITE);
        assignButton.setBackground(new Color(155, 89, 182));
        assignButton.setFocusPainted(false);
        assignButton.setBorderPainted(false);
        assignButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        assignButton.setPreferredSize(new Dimension(120, 40));
        assignButton.addActionListener(e -> assignRepresentative());

        cancelButton = new JButton("❌ Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(assignButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadRepresentatives() {
        representatives = new ArrayList<>();
        String repType = CATEGORY_MAP.getOrDefault(issueCategory, "Other");

        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM Representatives WHERE category = ? ORDER BY status, name";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, repType);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Representative rep = new Representative();
                rep.setRepId(rs.getInt("rep_id"));
                rep.setName(rs.getString("name"));
                rep.setCategory(rs.getString("category"));
                rep.setContact(rs.getString("contact"));
                rep.setEmail(rs.getString("email"));
                rep.setStatus(rs.getString("status"));

                representatives.add(rep);
                repCombo.addItem(rep);
            }

            if (representatives.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No representatives found for category: " + repType,
                        "No Representatives",
                        JOptionPane.WARNING_MESSAGE);
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

    private void assignRepresentative() {
        if (repCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a representative!",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Representative selectedRep = (Representative) repCombo.getSelectedItem();

        try {
            Connection conn = DBConnection.getConnection();

            // Check if already assigned
            String checkQuery = "SELECT * FROM Assignments WHERE issue_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, issueId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Update existing assignment
                String updateQuery = "UPDATE Assignments SET rep_id = ?, notes = ?, status = 'Assigned' WHERE issue_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, selectedRep.getRepId());
                updateStmt.setString(2, notesArea.getText().trim());
                updateStmt.setInt(3, issueId);
                updateStmt.executeUpdate();
                updateStmt.close();
            } else {
                // Create new assignment
                String insertQuery = "INSERT INTO Assignments (issue_id, rep_id, status, notes) VALUES (?, ?, 'Assigned', ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setInt(1, issueId);
                insertStmt.setInt(2, selectedRep.getRepId());
                insertStmt.setString(3, notesArea.getText().trim());
                insertStmt.executeUpdate();
                insertStmt.close();
            }

            // Update issue status
            String updateIssueQuery = "UPDATE Issues SET status = 'In-Progress' WHERE issue_id = ?";
            PreparedStatement issueStmt = conn.prepareStatement(updateIssueQuery);
            issueStmt.setInt(1, issueId);
            issueStmt.executeUpdate();
            issueStmt.close();

            rs.close();
            checkStmt.close();

            assigned = true;
            JOptionPane.showMessageDialog(this,
                    "Representative assigned successfully!\n" +
                            selectedRep.getName() + " will handle this issue.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error assigning representative: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isAssigned() {
        return assigned;
    }
}