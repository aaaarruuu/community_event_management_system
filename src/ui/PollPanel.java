package ui;

import database.DBConnection;
import models.User;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;

public class PollPanel extends JPanel {

    private final User currentUser;

    private JTable            table;
    private DefaultTableModel tableModel;
    private JTextField        searchField;
    private JComboBox<String> filterCombo;

    // ── Palette (Pink theme) ──────────────────────────────────────────────────
    private static final Color H1      = new Color(236,  72, 153);
    private static final Color H2      = new Color(244, 114, 182);
    private static final Color SUCCESS = new Color(  5, 150, 105);
    private static final Color WARNING = new Color(217, 119,   6);
    private static final Color DANGER  = new Color(220,  38,  38);
    private static final Color BG      = new Color( 15,  23,  42);
    private static final Color BG2     = new Color( 30,  41,  59);
    private static final Color BG3     = new Color( 51,  65,  85);
    private static final Color TH_BG   = new Color(100,  10,  60);
    private static final Color TEXT    = new Color(226, 232, 240);
    private static final Color TEXT_M  = new Color(148, 163, 184);
    private static final Color SEL_BG  = new Color(100,  10,  60);

    // ── Constructor ───────────────────────────────────────────────────────────
    public PollPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);
        initComponents();
        loadPolls();
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
                GradientPaint gp = new GradientPaint(0,0,new Color(100,10,60),getWidth(),0,new Color(131,24,67));
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

        JLabel title = new JLabel("POLLS & VOTING");
        title.setFont(new Font("Segoe UI",Font.BOLD,22)); title.setForeground(Color.WHITE);
        gc.gridy=1;
        JLabel sub = new JLabel("Community polls – vote and see results");
        sub.setFont(new Font("Segoe UI",Font.PLAIN,12)); sub.setForeground(new Color(255,180,220));

        gc.gridy=0; left.add(title,gc); gc.gridy=1; left.add(sub,gc);
        p.add(left, BorderLayout.WEST);

        if (currentUser.isAdmin()) {
            JButton addBtn = solidBtn("+ Create Poll", SUCCESS);
            addBtn.addActionListener(e -> showCreateDialog());
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
        searchField.setBackground(BG3); searchField.setForeground(TEXT);
        searchField.setCaretColor(TEXT); searchField.setFont(new Font("Segoe UI",Font.PLAIN,13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(H1,1),
                BorderFactory.createEmptyBorder(4,8,4,8)));
        p.add(searchField);

        JButton sb = solidBtn("Search", H1); sb.setPreferredSize(new Dimension(88,32));
        sb.addActionListener(e -> searchPolls()); p.add(sb);

        filterCombo = new JComboBox<>(new String[]{"All Polls","Active","Closed","Draft"});
        filterCombo.setBackground(BG3); filterCombo.setForeground(TEXT);
        filterCombo.setFont(new Font("Segoe UI",Font.PLAIN,13));
        filterCombo.setPreferredSize(new Dimension(140,32));
        filterCombo.addActionListener(e -> loadPolls()); p.add(filterCombo);

        JButton rb = solidBtn("Refresh", TEXT_M); rb.setPreferredSize(new Dimension(80,32));
        rb.addActionListener(e -> loadPolls()); p.add(rb);
        return p;
    }

    // ── Table ─────────────────────────────────────────────────────────────────
    private JScrollPane buildTable() {
        String[] cols = {"ID","Question","Category","Status","Total Votes","End Date","Created By"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable();
        return new JScrollPane(table);
    }

    private void styleTable() {
        table.setBackground(BG2); table.setForeground(TEXT);
        table.setFont(new Font("Segoe UI",Font.PLAIN,13));
        table.setRowHeight(32); table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0,0));
        table.setSelectionBackground(SEL_BG); table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setBackground(TH_BG);
        table.getTableHeader().setForeground(H2);
        table.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,12));
        table.getTableHeader().setPreferredSize(new Dimension(0,36));

        int[] w = {40,350,100,90,90,140,130};
        for (int i=0;i<w.length;i++) table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t,val,sel,focus,row,col);
                setBackground(sel ? SEL_BG : (row%2==0 ? BG2 : BG));
                setForeground(TEXT); setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
                if (!sel && col==3) {
                    String s = val != null ? val.toString() : "";
                    if ("ACTIVE".equals(s))  setForeground(new Color( 34,197, 94));
                    else if ("CLOSED".equals(s)) setForeground(new Color(148,163,184));
                    else if ("DRAFT".equals(s))  setForeground(new Color(245,158, 11));
                }
                return this;
            }
        });
    }

    // ── Action Buttons ────────────────────────────────────────────────────────
    private JPanel buildButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setBackground(BG2); p.setBorder(BorderFactory.createEmptyBorder(10,12,10,12));

        JButton voteBtn = solidBtn("Vote", H1);
        voteBtn.addActionListener(e -> showVoteDialog()); p.add(voteBtn);

        JButton resultsBtn = solidBtn("View Results", new Color(99,102,241));
        resultsBtn.addActionListener(e -> showResults()); p.add(resultsBtn);

        if (currentUser.isAdmin()) {
            JButton closeBtn = solidBtn("Close Poll", WARNING);
            closeBtn.addActionListener(e -> closePoll()); p.add(closeBtn);

            JButton deleteBtn = solidBtn("Delete", DANGER);
            deleteBtn.addActionListener(e -> deletePoll()); p.add(deleteBtn);
        }
        return p;
    }

    // ── Data Loading ──────────────────────────────────────────────────────────
    public void loadPolls() {
        tableModel.setRowCount(0);
        String filter = (String) filterCombo.getSelectedItem();

        StringBuilder sql = new StringBuilder(
                "SELECT p.poll_id, p.question, p.category, p.status, p.total_votes, " +
                        "p.end_date, u.full_name FROM polls p " +
                        "JOIN users u ON p.created_by = u.user_id WHERE 1=1 ");

        if ("Active".equals(filter))   sql.append("AND p.status='ACTIVE' AND p.end_date >= NOW() ");
        else if ("Closed".equals(filter))  sql.append("AND (p.status='CLOSED' OR p.end_date < NOW()) ");
        else if ("Draft".equals(filter))   sql.append("AND p.status='DRAFT' ");

        sql.append("ORDER BY p.poll_id DESC");

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql.toString())) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("poll_id"),
                        rs.getString("question"),
                        rs.getString("category"),
                        rs.getString("status"),
                        rs.getInt("total_votes"),
                        rs.getTimestamp("end_date"),
                        rs.getString("full_name")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage(),"DB Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchPolls() {
        String term = searchField.getText().trim();
        tableModel.setRowCount(0);
        String sql = "SELECT p.poll_id, p.question, p.category, p.status, p.total_votes, " +
                "p.end_date, u.full_name FROM polls p " +
                "JOIN users u ON p.created_by = u.user_id " +
                "WHERE LOWER(p.question) LIKE ? ORDER BY p.poll_id DESC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,"%" + term.toLowerCase() + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("poll_id"), rs.getString("question"), rs.getString("category"),
                        rs.getString("status"), rs.getInt("total_votes"),
                        rs.getTimestamp("end_date"), rs.getString("full_name")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,"Search error: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Vote Dialog ───────────────────────────────────────────────────────────
    private void showVoteDialog() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Please select a poll.","No Selection",JOptionPane.WARNING_MESSAGE); return; }
        int pollId  = (int) tableModel.getValueAt(row,0);
        String status = (String) tableModel.getValueAt(row,3);
        if (!"ACTIVE".equals(status)) {
            JOptionPane.showMessageDialog(this,"You can only vote on ACTIVE polls.","Not Active",JOptionPane.WARNING_MESSAGE); return;
        }

        // Check if already voted
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement chk = conn.prepareStatement(
                    "SELECT COUNT(*) FROM poll_votes WHERE poll_id=? AND user_id=?");
            chk.setInt(1,pollId); chk.setInt(2,currentUser.getUserId());
            ResultSet rc = chk.executeQuery();
            if (rc.next() && rc.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this,"You have already voted on this poll.","Already Voted",JOptionPane.INFORMATION_MESSAGE); return;
            }

            // Load options from JSON-like string stored in DB
            PreparedStatement pq = conn.prepareStatement("SELECT question, options FROM polls WHERE poll_id=?");
            pq.setInt(1,pollId);
            ResultSet pr = pq.executeQuery();
            if (!pr.next()) return;
            String question = pr.getString("question");
            String optJson  = pr.getString("options");

            // Parse simple JSON array ["opt1","opt2",...]
            String[] opts = parseJsonArray(optJson);
            if (opts.length == 0) { JOptionPane.showMessageDialog(this,"No options found.","Error",JOptionPane.ERROR_MESSAGE); return; }

            JDialog dlg = new JDialog((Frame)SwingUtilities.getWindowAncestor(this),"Vote – Poll #"+pollId,true);
            dlg.setSize(420,320); dlg.setLocationRelativeTo(this);

            JPanel main = new JPanel(new BorderLayout(10,12));
            main.setBackground(BG2); main.setBorder(BorderFactory.createEmptyBorder(20,24,20,24));

            JLabel qLbl = new JLabel("<html><b>" + question + "</b></html>");
            qLbl.setFont(new Font("Segoe UI",Font.BOLD,14)); qLbl.setForeground(H2);
            main.add(qLbl, BorderLayout.NORTH);

            ButtonGroup bg = new ButtonGroup();
            JPanel optPanel = new JPanel(); optPanel.setLayout(new BoxLayout(optPanel,BoxLayout.Y_AXIS));
            optPanel.setBackground(BG2);
            JRadioButton[] rbs = new JRadioButton[opts.length];
            for (int i=0;i<opts.length;i++) {
                rbs[i] = new JRadioButton(opts[i]);
                rbs[i].setBackground(BG2); rbs[i].setForeground(TEXT);
                rbs[i].setFont(new Font("Segoe UI",Font.PLAIN,13));
                bg.add(rbs[i]); optPanel.add(rbs[i]); optPanel.add(Box.createVerticalStrut(8));
            }
            main.add(optPanel, BorderLayout.CENTER);

            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0)); btnRow.setBackground(BG2);
            JButton cancelBtn = solidBtn("Cancel",TEXT_M); JButton voteBtn = solidBtn("Submit Vote",H1);
            btnRow.add(cancelBtn); btnRow.add(voteBtn); main.add(btnRow, BorderLayout.SOUTH);
            cancelBtn.addActionListener(ev -> dlg.dispose());
            voteBtn.addActionListener(ev -> {
                String selected = null;
                for (JRadioButton rb : rbs) if (rb.isSelected()) { selected = rb.getText(); break; }
                if (selected == null) { JOptionPane.showMessageDialog(dlg,"Please select an option.","No Option",JOptionPane.WARNING_MESSAGE); return; }
                try (PreparedStatement ins = conn.prepareStatement("INSERT INTO poll_votes(poll_id,user_id,selected_option) VALUES(?,?,?)");
                     PreparedStatement upd = conn.prepareStatement("UPDATE polls SET total_votes=total_votes+1 WHERE poll_id=?")) {
                    ins.setInt(1,pollId); ins.setInt(2,currentUser.getUserId()); ins.setString(3,selected);
                    ins.executeUpdate();
                    upd.setInt(1,pollId); upd.executeUpdate();
                    JOptionPane.showMessageDialog(dlg,"Vote submitted successfully!","Voted",JOptionPane.INFORMATION_MESSAGE);
                    dlg.dispose(); loadPolls();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dlg,"DB Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                }
            });
            dlg.setContentPane(main); dlg.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage(),"DB Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Results Dialog ────────────────────────────────────────────────────────
    private void showResults() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Please select a poll.","No Selection",JOptionPane.WARNING_MESSAGE); return; }
        int pollId     = (int) tableModel.getValueAt(row,0);
        String question = (String) tableModel.getValueAt(row,1);
        int totalVotes  = (int) tableModel.getValueAt(row,4);

        JDialog dlg = new JDialog((Frame)SwingUtilities.getWindowAncestor(this),"Results – "+question,true);
        dlg.setSize(480,420); dlg.setLocationRelativeTo(this);

        JPanel main = new JPanel(new BorderLayout(10,12));
        main.setBackground(BG2); main.setBorder(BorderFactory.createEmptyBorder(20,24,20,24));

        JLabel qLbl = new JLabel("<html><b>" + question + "</b></html>");
        qLbl.setFont(new Font("Segoe UI",Font.BOLD,14)); qLbl.setForeground(H2);
        JLabel tvLbl = new JLabel("Total votes: " + totalVotes);
        tvLbl.setFont(new Font("Segoe UI",Font.PLAIN,12)); tvLbl.setForeground(TEXT_M);
        JPanel topPanel = new JPanel(new BorderLayout(0,4)); topPanel.setBackground(BG2);
        topPanel.add(qLbl,BorderLayout.NORTH); topPanel.add(tvLbl,BorderLayout.SOUTH);
        main.add(topPanel, BorderLayout.NORTH);

        // Bar chart panel
        JPanel bars = new JPanel(); bars.setLayout(new BoxLayout(bars,BoxLayout.Y_AXIS));
        bars.setBackground(BG2);

        String sql = "SELECT selected_option, COUNT(*) AS cnt FROM poll_votes WHERE poll_id=? GROUP BY selected_option";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1,pollId);
            ResultSet rs = ps.executeQuery();
            Color[] barColors = {H1, new Color(99,102,241), SUCCESS, WARNING, new Color(14,165,233)};
            int ci = 0;
            while (rs.next()) {
                String opt = rs.getString("selected_option");
                int    cnt = rs.getInt("cnt");
                int    pct = totalVotes > 0 ? cnt * 100 / totalVotes : 0;
                Color  col = barColors[ci++ % barColors.length];

                JPanel row2 = new JPanel(new BorderLayout(10,0)); row2.setBackground(BG2);
                row2.setBorder(BorderFactory.createEmptyBorder(4,0,4,0));
                JLabel optLbl = new JLabel(opt); optLbl.setForeground(TEXT);
                optLbl.setFont(new Font("Segoe UI",Font.BOLD,12));
                optLbl.setPreferredSize(new Dimension(140,20));
                row2.add(optLbl, BorderLayout.WEST);

                // Bar
                JPanel barPanel = new JPanel(new BorderLayout()) {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D)g.create();
                        g2.setColor(BG3); g2.fillRoundRect(0,4,getWidth()-1,getHeight()-8,6,6);
                        int w = (int)((getWidth()-1) * pct / 100.0);
                        if (w > 0) { g2.setColor(col); g2.fillRoundRect(0,4,w,getHeight()-8,6,6); }
                        g2.dispose();
                    }
                };
                barPanel.setPreferredSize(new Dimension(200,28)); barPanel.setBackground(BG2);
                row2.add(barPanel, BorderLayout.CENTER);

                JLabel pctLbl = new JLabel(pct + "% (" + cnt + ")");
                pctLbl.setForeground(col); pctLbl.setFont(new Font("Segoe UI",Font.BOLD,12));
                pctLbl.setPreferredSize(new Dimension(80,20));
                row2.add(pctLbl, BorderLayout.EAST);
                bars.add(row2);
            }
            if (bars.getComponentCount()==0) {
                JLabel none = new JLabel("No votes yet."); none.setForeground(TEXT_M);
                none.setFont(new Font("Segoe UI",Font.PLAIN,13)); bars.add(none);
            }
        } catch (SQLException e) {
            JLabel err = new JLabel("Error: "+e.getMessage()); err.setForeground(Color.RED); bars.add(err);
        }

        main.add(new JScrollPane(bars), BorderLayout.CENTER);
        JButton closeBtn = solidBtn("Close",TEXT_M);
        closeBtn.addActionListener(e->dlg.dispose());
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT)); btnRow.setBackground(BG2);
        btnRow.add(closeBtn); main.add(btnRow,BorderLayout.SOUTH);
        dlg.setContentPane(main); dlg.setVisible(true);
    }

    // ── Create Poll Dialog (ADMIN) ────────────────────────────────────────────
    private void showCreateDialog() {
        JDialog dlg = new JDialog((Frame)SwingUtilities.getWindowAncestor(this),"Create Poll",true);
        dlg.setSize(520,480); dlg.setLocationRelativeTo(this);

        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(BG2); main.setBorder(BorderFactory.createEmptyBorder(20,24,20,24));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill=GridBagConstraints.HORIZONTAL; gc.insets=new Insets(6,4,6,4); gc.weightx=1;

        JTextField questionF  = styledField();
        JTextField opt1F=styledField(), opt2F=styledField(), opt3F=styledField(), opt4F=styledField();
        JComboBox<String> catC = new JComboBox<>(new String[]{"GENERAL","EVENT","AMENITY","RULE_CHANGE","BUDGET"});
        JTextField endDateF = styledField(); endDateF.setToolTipText("YYYY-MM-DD HH:MM:SS");

        int r=0;
        gc.gridy=r++; gc.gridx=0; gc.gridwidth=2; main.add(lbl("Poll Question *"),gc);
        gc.gridy=r++; main.add(questionF,gc);
        gc.gridy=r++; main.add(lbl("Option 1 *"),gc);
        gc.gridy=r++; main.add(opt1F,gc);
        gc.gridy=r; gc.gridx=0; gc.gridwidth=1; main.add(lbl("Option 2 *"),gc);
        gc.gridx=1; main.add(lbl("Option 3"),gc); r++;
        gc.gridy=r; gc.gridx=0; main.add(opt2F,gc); gc.gridx=1; main.add(opt3F,gc); r++;
        gc.gridy=r; gc.gridx=0; gc.gridwidth=2; main.add(lbl("Option 4 (optional)"),gc); r++;
        gc.gridy=r++; main.add(opt4F,gc);
        gc.gridy=r; gc.gridx=0; gc.gridwidth=1; main.add(lbl("Category"),gc);
        gc.gridx=1; main.add(lbl("End Date/Time *"),gc); r++;
        gc.gridy=r; gc.gridx=0; main.add(catC,gc); gc.gridx=1; main.add(endDateF,gc); r++;

        JButton saveBtn   = solidBtn("Create Poll",SUCCESS);
        JButton cancelBtn = solidBtn("Cancel",TEXT_M);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0)); btnRow.setBackground(BG2);
        btnRow.add(cancelBtn); btnRow.add(saveBtn);
        gc.gridy=r; gc.gridx=0; gc.gridwidth=2; main.add(btnRow,gc);

        cancelBtn.addActionListener(e->dlg.dispose());
        saveBtn.addActionListener(e->{
            String q   = questionF.getText().trim();
            String o1  = opt1F.getText().trim();
            String o2  = opt2F.getText().trim();
            String o3  = opt3F.getText().trim();
            String o4  = opt4F.getText().trim();
            String end = endDateF.getText().trim();
            if (q.isEmpty()||o1.isEmpty()||o2.isEmpty()||end.isEmpty()) {
                JOptionPane.showMessageDialog(dlg,"Question, at least 2 options, and End Date are required!","Validation",JOptionPane.WARNING_MESSAGE); return;
            }
            StringBuilder optsJson = new StringBuilder("[\"").append(o1).append("\",\"").append(o2).append("\"");
            if (!o3.isEmpty()) optsJson.append(",\"").append(o3).append("\"");
            if (!o4.isEmpty()) optsJson.append(",\"").append(o4).append("\"");
            optsJson.append("]");

            String sql = "INSERT INTO polls(question,options,category,created_by,start_date,end_date,status,total_votes,is_anonymous) " +
                    "VALUES(?,?,?,?,NOW(),?,'ACTIVE',0,TRUE)";
            try (Connection conn=DBConnection.getConnection(); PreparedStatement ps=conn.prepareStatement(sql)) {
                ps.setString(1,q); ps.setString(2,optsJson.toString());
                ps.setString(3,(String)catC.getSelectedItem());
                ps.setInt(4,currentUser.getUserId()); ps.setString(5,end);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(dlg,"Poll created successfully!","Success",JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose(); loadPolls();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dlg,"DB Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        });
        dlg.setContentPane(main); dlg.setVisible(true);
    }

    // ── Close Poll (ADMIN) ────────────────────────────────────────────────────
    private void closePoll() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Select a poll.","No Selection",JOptionPane.WARNING_MESSAGE); return; }
        int id = (int) tableModel.getValueAt(row,0);
        try (Connection conn=DBConnection.getConnection();
             PreparedStatement ps=conn.prepareStatement("UPDATE polls SET status='CLOSED' WHERE poll_id=?")) {
            ps.setInt(1,id); ps.executeUpdate(); loadPolls();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage(),"DB Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Delete Poll (ADMIN) ───────────────────────────────────────────────────
    private void deletePoll() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Select a poll.","No Selection",JOptionPane.WARNING_MESSAGE); return; }
        int id = (int) tableModel.getValueAt(row,0);
        int c = JOptionPane.showConfirmDialog(this,"Delete this poll and all its votes?","Confirm",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
        if (c != JOptionPane.YES_OPTION) return;
        try (Connection conn=DBConnection.getConnection()) {
            PreparedStatement d1 = conn.prepareStatement("DELETE FROM poll_votes WHERE poll_id=?"); d1.setInt(1,id); d1.executeUpdate();
            PreparedStatement d2 = conn.prepareStatement("DELETE FROM polls WHERE poll_id=?"); d2.setInt(1,id); d2.executeUpdate();
            loadPolls();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage(),"DB Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    /** Parses  ["opt1","opt2","opt3"]  into a String array */
    private String[] parseJsonArray(String json) {
        if (json == null || json.isBlank()) return new String[0];
        json = json.trim().replaceAll("^\\[|\\]$","");
        String[] parts = json.split(",");
        String[] result = new String[parts.length];
        for (int i=0;i<parts.length;i++) {
            result[i] = parts[i].trim().replaceAll("^\"|\"$","");
        }
        return result;
    }

    private JButton solidBtn(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()?bg.brighter():bg);
                g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                g2.setColor(Color.WHITE); g2.setFont(getFont());
                FontMetrics fm=g2.getFontMetrics(); String t=getText();
                g2.drawString(t,(getWidth()-fm.stringWidth(t))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        b.setFont(new Font("Segoe UI",Font.BOLD,12));
        b.setPreferredSize(new Dimension(Math.max(90,text.length()*8+16),32));
        b.setFocusPainted(false);b.setBorderPainted(false);b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JTextField styledField() {
        JTextField f = new JTextField();
        f.setBackground(BG3); f.setForeground(TEXT);
        f.setFont(new Font("Segoe UI",Font.PLAIN,13)); f.setCaretColor(TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71,85,105)),
                BorderFactory.createEmptyBorder(6,10,6,10)));
        return f;
    }

    private JLabel lbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI",Font.BOLD,11)); l.setForeground(TEXT_M);
        return l;
    }
}
