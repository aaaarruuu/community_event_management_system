package ui;

import database.DBConnection;
import models.User;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;

public class AnnouncementPanel extends JPanel {

    private final User currentUser;

    private JTable            table;
    private DefaultTableModel tableModel;
    private JTextField        searchField;
    private JComboBox<String> filterCombo;

    // ── Palette (Orange theme) ────────────────────────────────────────────────
    private static final Color H1      = new Color(249, 115,  22);
    private static final Color H2      = new Color(251, 146,  60);
    private static final Color SUCCESS = new Color(  5, 150, 105);
    private static final Color WARNING = new Color(217, 119,   6);
    private static final Color DANGER  = new Color(220,  38,  38);
    private static final Color BG      = new Color( 15,  23,  42);
    private static final Color BG2     = new Color( 30,  41,  59);
    private static final Color BG3     = new Color( 51,  65,  85);
    private static final Color TH_BG   = new Color(120,  50,   5);
    private static final Color TEXT    = new Color(226, 232, 240);
    private static final Color TEXT_M  = new Color(148, 163, 184);
    private static final Color SEL_BG  = new Color(120,  50,   5);

    // ── Constructor ───────────────────────────────────────────────────────────
    public AnnouncementPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);
        initComponents();
        loadAnnouncements();
    }

    // ── Layout ────────────────────────────────────────────────────────────────
    private void initComponents() {
        add(buildTitleBar(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setBackground(BG);
        body.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        body.add(buildToolbar(), BorderLayout.NORTH);
        body.add(buildTable(),   BorderLayout.CENTER);
        body.add(buildButtons(), BorderLayout.SOUTH);
        add(body, BorderLayout.CENTER);
    }

    // ── Title Bar ─────────────────────────────────────────────────────────────
    private JPanel buildTitleBar() {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0,0,new Color(120,53,15),getWidth(),0,new Color(154,52,18));
                g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.15f));
                g2.setColor(H2); g2.fill(new Ellipse2D.Float(getWidth()-150,-50,200,200));
                g2.dispose(); super.paintComponent(g);
            }
        };
        p.setOpaque(true); p.setPreferredSize(new Dimension(0,72));
        p.setBorder(BorderFactory.createEmptyBorder(0,24,0,24));

        JPanel left = new JPanel(new GridBagLayout()); left.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx=0; gc.gridy=0; gc.anchor=GridBagConstraints.WEST; gc.insets=new Insets(0,0,2,0);

        JLabel title = new JLabel("ANNOUNCEMENTS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22)); title.setForeground(Color.WHITE);
        gc.gridy=1;
        JLabel sub = new JLabel("Community notices, alerts and important updates");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12)); sub.setForeground(new Color(255,200,150));

        gc.gridy=0; left.add(title,gc); gc.gridy=1; left.add(sub,gc);
        p.add(left, BorderLayout.WEST);

        if (currentUser.isAdmin()) {
            JButton addBtn = solidBtn("+ New Announcement", SUCCESS);
            addBtn.addActionListener(e -> showAddDialog());
            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0)); right.setOpaque(false);
            right.add(addBtn); p.add(right, BorderLayout.EAST);
        }
        return p;
    }

    // ── Toolbar ───────────────────────────────────────────────────────────────
    private JPanel buildToolbar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setBackground(BG2); p.setBorder(BorderFactory.createEmptyBorder(10,12,10,12));

        searchField = new JTextField(18);
        searchField.setBackground(new Color(51,65,85)); searchField.setForeground(TEXT);
        searchField.setCaretColor(TEXT); searchField.setFont(new Font("Segoe UI",Font.PLAIN,13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(H1,1),
                BorderFactory.createEmptyBorder(4,8,4,8)));
        searchField.setToolTipText("Search by title or message");
        p.add(searchField);

        JButton sb = solidBtn("Search", H1); sb.setPreferredSize(new Dimension(88,32));
        sb.addActionListener(e -> searchAnnouncements()); p.add(sb);

        filterCombo = new JComboBox<>(new String[]{
                "All", "CRITICAL Priority", "HIGH Priority", "MEDIUM Priority", "LOW Priority",
                "Pinned Only", "GENERAL", "EVENT", "MAINTENANCE", "SAFETY", "RULE"});
        filterCombo.setBackground(BG3); filterCombo.setForeground(TEXT);
        filterCombo.setFont(new Font("Segoe UI",Font.PLAIN,13));
        filterCombo.setPreferredSize(new Dimension(170,32));
        filterCombo.addActionListener(e -> loadAnnouncements()); p.add(filterCombo);

        JButton rb = solidBtn("Refresh", TEXT_M); rb.setPreferredSize(new Dimension(80,32));
        rb.addActionListener(e -> loadAnnouncements()); p.add(rb);
        return p;
    }

    // ── Table ─────────────────────────────────────────────────────────────────
    private JScrollPane buildTable() {
        String[] cols = {"ID","Title","Category","Priority","Posted By","Posted Date","Pinned","Expires"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable();
        return new JScrollPane(table);
    }

    private void styleTable() {
        table.setBackground(BG2); table.setForeground(TEXT);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32); table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0,0));
        table.setSelectionBackground(SEL_BG); table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setBackground(TH_BG);
        table.getTableHeader().setForeground(H2);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setPreferredSize(new Dimension(0,36));

        // Column widths
        int[] w = {40,300,100,90,130,160,60,120};
        for (int i=0;i<w.length;i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        }

        // Custom row renderer – colour by priority
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t,val,sel,focus,row,col);
                setBackground(sel ? SEL_BG : (row%2==0 ? BG2 : BG));
                setForeground(TEXT); setBorder(BorderFactory.createEmptyBorder(0,8,0,8));

                if (!sel) {
                    // Colour the Priority cell
                    String priority = "";
                    try { priority = (String) tableModel.getValueAt(row,3); } catch(Exception ignored){}
                    if (col == 3) {
                        switch(priority) {
                            case "CRITICAL": setForeground(new Color(239,68,68));  break;
                            case "HIGH":     setForeground(new Color(245,158,11)); break;
                            case "MEDIUM":   setForeground(new Color(99,102,241)); break;
                            case "LOW":      setForeground(new Color(148,163,184));break;
                        }
                    }
                    // Pinned column
                    if (col == 6 && "Yes".equals(val)) {
                        setForeground(new Color(251,191,36));
                    }
                }
                return this;
            }
        });
    }

    // ── Action Buttons ────────────────────────────────────────────────────────
    private JPanel buildButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setBackground(BG2); p.setBorder(BorderFactory.createEmptyBorder(10,12,10,12));

        JButton vb = solidBtn("View Details", new Color(14,165,233));
        vb.addActionListener(e -> viewDetails()); p.add(vb);

        if (currentUser.isAdmin()) {
            JButton eb = solidBtn("Edit", WARNING);
            eb.addActionListener(e -> editAnnouncement()); p.add(eb);

            JButton pb = solidBtn("Toggle Pin", new Color(251,191,36));
            pb.addActionListener(e -> togglePin()); p.add(pb);

            JButton db = solidBtn("Delete", DANGER);
            db.addActionListener(e -> deleteAnnouncement()); p.add(db);
        }
        return p;
    }

    // ── Data Loading ──────────────────────────────────────────────────────────
    public void loadAnnouncements() {
        tableModel.setRowCount(0);
        String filter = (String) filterCombo.getSelectedItem();

        StringBuilder sql = new StringBuilder(
                "SELECT a.announcement_id, a.title, a.category, a.priority, " +
                        "u.full_name AS poster, a.posted_date, a.is_pinned, a.expiry_date " +
                        "FROM announcements a " +
                        "JOIN users u ON a.posted_by = u.user_id WHERE 1=1 ");

        if ("Pinned Only".equals(filter)) {
            sql.append("AND a.is_pinned = TRUE ");
        } else if (filter != null && filter.contains("Priority")) {
            String pri = filter.replace(" Priority","");
            sql.append("AND a.priority = '").append(pri).append("' ");
        } else if (filter != null && !"All".equals(filter)) {
            sql.append("AND a.category = '").append(filter).append("' ");
        }
        sql.append("ORDER BY a.is_pinned DESC, a.posted_date DESC");

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql.toString())) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("announcement_id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getString("priority"),
                        rs.getString("poster"),
                        rs.getTimestamp("posted_date"),
                        rs.getBoolean("is_pinned") ? "Yes" : "No",
                        rs.getDate("expiry_date") != null ? rs.getDate("expiry_date").toString() : "No expiry"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading announcements:\n" + e.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchAnnouncements() {
        String term = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        String sql = "SELECT a.announcement_id, a.title, a.category, a.priority, " +
                "u.full_name AS poster, a.posted_date, a.is_pinned, a.expiry_date " +
                "FROM announcements a JOIN users u ON a.posted_by = u.user_id " +
                "WHERE LOWER(a.title) LIKE ? OR LOWER(a.message) LIKE ? " +
                "ORDER BY a.is_pinned DESC, a.posted_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + term + "%");
            ps.setString(2, "%" + term + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("announcement_id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getString("priority"),
                        rs.getString("poster"),
                        rs.getTimestamp("posted_date"),
                        rs.getBoolean("is_pinned") ? "Yes" : "No",
                        rs.getDate("expiry_date") != null ? rs.getDate("expiry_date").toString() : "No expiry"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Search error:\n" + e.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── View Details ──────────────────────────────────────────────────────────
    private void viewDetails() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Please select an announcement.","No Selection",JOptionPane.WARNING_MESSAGE); return; }
        int id = (int) tableModel.getValueAt(row, 0);

        String sql = "SELECT a.*, u.full_name AS poster FROM announcements a " +
                "JOIN users u ON a.posted_by = u.user_id WHERE a.announcement_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String info =
                        "Title:     " + rs.getString("title") + "\n" +
                                "Category:  " + rs.getString("category") + "\n" +
                                "Priority:  " + rs.getString("priority") + "\n" +
                                "Posted By: " + rs.getString("poster") + "\n" +
                                "Posted:    " + rs.getTimestamp("posted_date") + "\n" +
                                "Expires:   " + (rs.getDate("expiry_date") != null ? rs.getDate("expiry_date") : "No expiry") + "\n" +
                                "Pinned:    " + (rs.getBoolean("is_pinned") ? "Yes" : "No") + "\n\n" +
                                "Message:\n" + rs.getString("message");

                JTextArea ta = new JTextArea(info, 18, 50);
                ta.setEditable(false); ta.setLineWrap(true); ta.setWrapStyleWord(true);
                ta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                JOptionPane.showMessageDialog(this, new JScrollPane(ta),
                        "Announcement Details – #" + id, JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Add Dialog (ADMIN) ────────────────────────────────────────────────────
    private void showAddDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Announcement", true);
        dlg.setSize(520, 500); dlg.setLocationRelativeTo(this);

        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(new Color(30,41,59));
        main.setBorder(BorderFactory.createEmptyBorder(20,24,20,24));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL; gc.insets = new Insets(6,4,6,4); gc.weightx = 1;

        JTextField titleF = styledField();
        JTextArea  msgF   = new JTextArea(5, 30);
        msgF.setBackground(new Color(51,65,85)); msgF.setForeground(TEXT);
        msgF.setFont(new Font("Segoe UI",Font.PLAIN,13)); msgF.setLineWrap(true);

        JComboBox<String> catC = new JComboBox<>(new String[]{"GENERAL","EVENT","MAINTENANCE","SAFETY","RULE"});
        JComboBox<String> priC = new JComboBox<>(new String[]{"LOW","MEDIUM","HIGH","CRITICAL"});
        priC.setSelectedItem("MEDIUM");
        JComboBox<String> audC = new JComboBox<>(new String[]{"ALL","MEMBERS","ADMINS"});
        JTextField expiryF = styledField(); expiryF.setToolTipText("YYYY-MM-DD or leave blank");

        int r = 0;
        gc.gridy=r++; gc.gridx=0; gc.gridwidth=2; main.add(label("Title *"), gc);
        gc.gridy=r++;               main.add(titleF, gc);
        gc.gridy=r++; main.add(label("Message *"), gc);
        gc.gridy=r++; main.add(new JScrollPane(msgF), gc);

        gc.gridy=r; gc.gridx=0; gc.gridwidth=1; main.add(label("Category"), gc);
        gc.gridx=1; main.add(label("Priority"), gc); r++;
        gc.gridy=r; gc.gridx=0; main.add(catC, gc);
        gc.gridx=1; main.add(priC, gc); r++;

        gc.gridy=r; gc.gridx=0; main.add(label("Target Audience"), gc);
        gc.gridx=1; main.add(label("Expiry Date (opt)"), gc); r++;
        gc.gridy=r; gc.gridx=0; main.add(audC, gc);
        gc.gridx=1; main.add(expiryF, gc); r++;

        JButton saveBtn   = solidBtn("Post Announcement", SUCCESS);
        JButton cancelBtn = solidBtn("Cancel", new Color(100,116,139));
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0)); btnRow.setBackground(new Color(30,41,59));
        btnRow.add(cancelBtn); btnRow.add(saveBtn);
        gc.gridy=r; gc.gridx=0; gc.gridwidth=2; main.add(btnRow, gc);

        cancelBtn.addActionListener(e -> dlg.dispose());
        saveBtn.addActionListener(e -> {
            String t  = titleF.getText().trim();
            String m  = msgF.getText().trim();
            String cat= (String) catC.getSelectedItem();
            String pri= (String) priC.getSelectedItem();
            String aud= (String) audC.getSelectedItem();
            String exp= expiryF.getText().trim();
            if (t.isEmpty() || m.isEmpty()) {
                JOptionPane.showMessageDialog(dlg,"Title and message are required!","Validation",JOptionPane.WARNING_MESSAGE); return;
            }
            String sql;
            if (exp.isEmpty()) {
                sql = "INSERT INTO announcements(title,message,category,priority,posted_by,posted_date,target_audience) VALUES(?,?,?,?,?,NOW(),?)";
            } else {
                sql = "INSERT INTO announcements(title,message,category,priority,posted_by,posted_date,expiry_date,target_audience) VALUES(?,?,?,?,?,NOW(),?,?)";
            }
            try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1,t); ps.setString(2,m); ps.setString(3,cat);
                ps.setString(4,pri); ps.setInt(5,currentUser.getUserId());
                if (exp.isEmpty()) { ps.setString(6,aud); }
                else               { ps.setString(6,exp); ps.setString(7,aud); }
                ps.executeUpdate();
                JOptionPane.showMessageDialog(dlg,"Announcement posted successfully!","Success",JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose(); loadAnnouncements();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dlg,"DB Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.setContentPane(main); dlg.setVisible(true);
    }

    // ── Edit (ADMIN) ──────────────────────────────────────────────────────────
    private void editAnnouncement() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Select an announcement first.","No Selection",JOptionPane.WARNING_MESSAGE); return; }
        int id = (int) tableModel.getValueAt(row, 0);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM announcements WHERE announcement_id=?")) {
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return;

            JDialog dlg = new JDialog((Frame)SwingUtilities.getWindowAncestor(this),"Edit Announcement",true);
            dlg.setSize(520,460); dlg.setLocationRelativeTo(this);

            JPanel main = new JPanel(new GridBagLayout());
            main.setBackground(new Color(30,41,59));
            main.setBorder(BorderFactory.createEmptyBorder(20,24,20,24));
            GridBagConstraints gc = new GridBagConstraints();
            gc.fill=GridBagConstraints.HORIZONTAL; gc.insets=new Insets(6,4,6,4); gc.weightx=1;

            JTextField titleF = styledField(); titleF.setText(rs.getString("title"));
            JTextArea  msgF   = new JTextArea(5,30); msgF.setText(rs.getString("message"));
            msgF.setBackground(new Color(51,65,85)); msgF.setForeground(TEXT);
            msgF.setFont(new Font("Segoe UI",Font.PLAIN,13)); msgF.setLineWrap(true);

            JComboBox<String> catC = new JComboBox<>(new String[]{"GENERAL","EVENT","MAINTENANCE","SAFETY","RULE"});
            catC.setSelectedItem(rs.getString("category"));
            JComboBox<String> priC = new JComboBox<>(new String[]{"LOW","MEDIUM","HIGH","CRITICAL"});
            priC.setSelectedItem(rs.getString("priority"));

            int r=0;
            gc.gridy=r++; gc.gridx=0; gc.gridwidth=2; main.add(label("Title"), gc);
            gc.gridy=r++; main.add(titleF,gc);
            gc.gridy=r++; main.add(label("Message"),gc);
            gc.gridy=r++; main.add(new JScrollPane(msgF),gc);
            gc.gridy=r; gc.gridx=0; gc.gridwidth=1; main.add(label("Category"),gc);
            gc.gridx=1; main.add(label("Priority"),gc); r++;
            gc.gridy=r; gc.gridx=0; main.add(catC,gc);
            gc.gridx=1; main.add(priC,gc); r++;

            JButton saveBtn   = solidBtn("Update",SUCCESS);
            JButton cancelBtn = solidBtn("Cancel",new Color(100,116,139));
            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0)); btnRow.setBackground(new Color(30,41,59));
            btnRow.add(cancelBtn); btnRow.add(saveBtn);
            gc.gridy=r; gc.gridx=0; gc.gridwidth=2; main.add(btnRow,gc);

            cancelBtn.addActionListener(ev -> dlg.dispose());
            saveBtn.addActionListener(ev -> {
                try (PreparedStatement upd = conn.prepareStatement(
                        "UPDATE announcements SET title=?,message=?,category=?,priority=? WHERE announcement_id=?")) {
                    upd.setString(1,titleF.getText().trim());
                    upd.setString(2,msgF.getText().trim());
                    upd.setString(3,(String)catC.getSelectedItem());
                    upd.setString(4,(String)priC.getSelectedItem());
                    upd.setInt(5,id);
                    upd.executeUpdate();
                    JOptionPane.showMessageDialog(dlg,"Updated successfully!","Success",JOptionPane.INFORMATION_MESSAGE);
                    dlg.dispose(); loadAnnouncements();
                } catch(SQLException ex) {
                    JOptionPane.showMessageDialog(dlg,"DB Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                }
            });
            dlg.setContentPane(main); dlg.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage(),"DB Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Toggle Pin (ADMIN) ────────────────────────────────────────────────────
    private void togglePin() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Select an announcement.","No Selection",JOptionPane.WARNING_MESSAGE); return; }
        int id = (int) tableModel.getValueAt(row,0);
        String sql = "UPDATE announcements SET is_pinned = NOT is_pinned WHERE announcement_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1,id); ps.executeUpdate(); loadAnnouncements();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage(),"DB Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Delete (ADMIN) ────────────────────────────────────────────────────────
    private void deleteAnnouncement() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Select an announcement.","No Selection",JOptionPane.WARNING_MESSAGE); return; }
        int id = (int) tableModel.getValueAt(row,0);
        String title = (String) tableModel.getValueAt(row,1);
        int c = JOptionPane.showConfirmDialog(this,"Delete announcement: \""+title+"\"?","Confirm Delete",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
        if (c != JOptionPane.YES_OPTION) return;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM announcements WHERE announcement_id=?")) {
            ps.setInt(1,id); ps.executeUpdate(); loadAnnouncements();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage(),"DB Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private JButton solidBtn(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                g2.setColor(Color.WHITE); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics(); String t = getText();
                g2.drawString(t,(getWidth()-fm.stringWidth(t))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        b.setFont(new Font("Segoe UI",Font.BOLD,12));
        b.setPreferredSize(new Dimension(Math.max(100,text.length()*8+20),32));
        b.setFocusPainted(false); b.setBorderPainted(false); b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JTextField styledField() {
        JTextField f = new JTextField();
        f.setBackground(new Color(51,65,85)); f.setForeground(TEXT);
        f.setFont(new Font("Segoe UI",Font.PLAIN,13)); f.setCaretColor(TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71,85,105)),
                BorderFactory.createEmptyBorder(6,10,6,10)));
        return f;
    }

    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI",Font.BOLD,11));
        l.setForeground(new Color(148,163,184));
        return l;
    }
}