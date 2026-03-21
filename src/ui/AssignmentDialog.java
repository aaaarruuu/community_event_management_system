package ui;

import database.DBConnection;
import models.Representative;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class AssignmentDialog extends JDialog {

    private final int    issueId;
    private final String issueCategory;
    private boolean      assigned = false;

    private JComboBox<Representative> repCombo;
    private JTextArea                 notesArea;

    public AssignmentDialog(Frame parent, int issueId, String issueCategory) {
        super(parent, "Assign Representative", true);
        this.issueId       = issueId;
        this.issueCategory = issueCategory;
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
        loadRepresentatives();
    }

    private void initComponents() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(Color.WHITE);
        main.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        // ── Info bar ──────────────────────────────────────────────────────────
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(new Color(236, 240, 241));
        info.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("👷 Assign Representative to Issue #" + issueId);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(new Color(155, 89, 182));
        info.add(title);
        info.add(Box.createVerticalStrut(8));

        JLabel catLbl = new JLabel("Issue Category: " + issueCategory);
        catLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        catLbl.setForeground(new Color(52, 73, 94));
        info.add(catLbl);

        main.add(info, BorderLayout.NORTH);

        // ── Form ──────────────────────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        JLabel repLbl = new JLabel("Select Representative:");
        repLbl.setFont(new Font("Arial", Font.BOLD, 13));
        form.add(repLbl, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        repCombo = new JComboBox<>();
        repCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        repCombo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof Representative) {
                    Representative r = (Representative) value;
                    setText(r.getName() + " – " + r.getCategory() + " (" + r.getStatus() + ")");
                    setForeground("ACTIVE".equalsIgnoreCase(r.getStatus()) ?
                            new Color(39, 174, 96) : new Color(192, 57, 43));
                }
                return this;
            }
        });
        form.add(repCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel notesLbl = new JLabel("Notes (Optional):");
        notesLbl.setFont(new Font("Arial", Font.BOLD, 13));
        form.add(notesLbl, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        notesArea = new JTextArea(5, 28);
        notesArea.setFont(new Font("Arial", Font.PLAIN, 13));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        form.add(new JScrollPane(notesArea), gbc);

        main.add(form, BorderLayout.CENTER);

        // ── Buttons ───────────────────────────────────────────────────────────
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btns.setBackground(Color.WHITE);

        JButton assignBtn = colorBtn("✅ Assign", new Color(155, 89, 182));
        assignBtn.addActionListener(e -> doAssign());

        JButton cancelBtn = colorBtn("❌ Cancel", new Color(149, 165, 166));
        cancelBtn.addActionListener(e -> dispose());

        btns.add(assignBtn);
        btns.add(cancelBtn);
        main.add(btns, BorderLayout.SOUTH);

        setContentPane(main);
    }

    private void loadRepresentatives() {
        // Load all representatives that match this issue's category (best-effort).
        // Fall back to all active reps if the category mapping yields nothing.
        String sql = "SELECT rep_id, rep_name, phone, email, category, status FROM representatives "
                + "WHERE status IN ('ACTIVE','Active') ORDER BY rep_name";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Representative r = new Representative();
                r.setRepId(rs.getInt("rep_id"));
                r.setName(rs.getString("rep_name"));
                r.setPhone(rs.getString("phone"));
                r.setEmail(rs.getString("email"));
                r.setCategory(rs.getString("category"));
                r.setStatus(rs.getString("status"));
                repCombo.addItem(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading representatives: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        if (repCombo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No active representatives found. Please add one first.",
                    "No Representatives", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void doAssign() {
        if (repCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a representative!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Representative rep = (Representative) repCombo.getSelectedItem();
        String notes = notesArea.getText().trim();

        try (Connection conn = DBConnection.getConnection()) {
            // Update the issue directly
            String upd = "UPDATE issues SET assigned_to=?, assigned_date=NOW(), status='IN_PROGRESS' WHERE issue_id=?";
            try (PreparedStatement ps = conn.prepareStatement(upd)) {
                ps.setInt(1, rep.getRepId()); ps.setInt(2, issueId);
                ps.executeUpdate();
            }

            assigned = true;
            JOptionPane.showMessageDialog(this,
                    "Representative assigned successfully!\n" + rep.getName() + " will handle this issue.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error assigning representative: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isAssigned() { return assigned; }

    private JButton colorBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(120, 38));
        return b;
    }
}