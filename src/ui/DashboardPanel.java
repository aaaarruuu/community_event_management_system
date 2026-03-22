package ui;

import database.DBConnection;
import models.User;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;
import java.sql.*;

public class DashboardPanel extends JPanel {

    private final User currentUser;

    // Stat labels  (9 cards now)
    private JLabel totalEventsLbl, upcomingEventsLbl, totalIssuesLbl;
    private JLabel pendingIssuesLbl, totalRepsLbl, activeRepsLbl;
    private JLabel totalMembersLbl, announcementsLbl, activePollsLbl;

    private JTextArea activityArea;

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color C_OCEAN1   = new Color(6,   182, 212);
    private static final Color C_OCEAN2   = new Color(14,  165, 233);
    private static final Color C_VIOLET1  = new Color(139,  92, 246);
    private static final Color C_VIOLET2  = new Color(168,  85, 247);
    private static final Color C_ROSE1    = new Color(244,  63,  94);
    private static final Color C_ROSE2    = new Color(251, 113, 133);
    private static final Color C_AMBER1   = new Color(245, 158,  11);
    private static final Color C_AMBER2   = new Color(251, 191,  36);
    private static final Color C_EMERALD1 = new Color( 16, 185, 129);
    private static final Color C_EMERALD2 = new Color( 52, 211, 153);
    private static final Color C_INDIGO1  = new Color( 99, 102, 241);
    private static final Color C_INDIGO2  = new Color(129, 140, 248);
    private static final Color C_TEAL1    = new Color(  20, 184, 166);   // NEW – members
    private static final Color C_TEAL2    = new Color(  45, 212, 191);
    private static final Color C_ORANGE1  = new Color(249, 115,  22);   // NEW – announcements
    private static final Color C_ORANGE2  = new Color(251, 146,  60);
    private static final Color C_PINK1    = new Color(236,  72, 153);   // NEW – polls
    private static final Color C_PINK2    = new Color(244, 114, 182);
    private static final Color BG         = new Color( 15,  23,  42);
    private static final Color BG2        = new Color( 30,  41,  59);
    private static final Color TEXT_SEC   = new Color(124, 175, 243);

    // ── Constructor ───────────────────────────────────────────────────────────
    public DashboardPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);
        initComponents();
        loadData();
    }

    // ── Layout ────────────────────────────────────────────────────────────────
    private void initComponents() {
        add(buildHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(20, 20));
        body.setBackground(BG);
        body.setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));

        // Stats grid (3 rows × 3 cols = 9 cards)
        body.add(buildStatsGrid(), BorderLayout.CENTER);
        body.add(buildActivity(),  BorderLayout.SOUTH);
        add(body, BorderLayout.CENTER);
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(15,23,42), getWidth(), 0, new Color(30,58,138));
                g2.setPaint(gp); g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
                g2.setColor(C_OCEAN1);  g2.fill(new Ellipse2D.Float(getWidth()-160, -40, 200, 200));
                g2.setColor(C_VIOLET1); g2.fill(new Ellipse2D.Float(-60, -30, 180, 180));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(true);
        p.setPreferredSize(new Dimension(0, 108));
        p.setBorder(BorderFactory.createEmptyBorder(0, 28, 0, 28));

        JPanel text = new JPanel(new GridBagLayout());
        text.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 0; gc.anchor = GridBagConstraints.WEST; gc.insets = new Insets(0,0,2,0);

        JLabel welcome = new JLabel("Welcome back, " + currentUser.getFullName() + "!");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcome.setForeground(Color.WHITE);

        gc.gridy = 1;
        JLabel role = new JLabel("  " + (currentUser.getRole() != null ? currentUser.getRole() : "MEMBER")
                + "  ·  Community Management Portal");
        role.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        role.setForeground(C_EMERALD1);

        gc.gridy = 0; text.add(welcome, gc);
        gc.gridy = 1; text.add(role, gc);
        p.add(text, BorderLayout.CENTER);

        JPanel pill = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        pill.setOpaque(false);
        JLabel onlineLabel = new JLabel("● LIVE");
        onlineLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        onlineLabel.setForeground(C_EMERALD1);
        pill.add(onlineLabel);
        p.add(pill, BorderLayout.EAST);
        return p;
    }

    // ── Stats Grid (3 × 3 = 9 cards) ─────────────────────────────────────────
    private JPanel buildStatsGrid() {
        JPanel grid = new JPanel(new GridLayout(3, 3, 18, 18));
        grid.setOpaque(false);

        totalEventsLbl    = new JLabel("–");
        upcomingEventsLbl = new JLabel("–");
        totalIssuesLbl    = new JLabel("–");
        pendingIssuesLbl  = new JLabel("–");
        totalRepsLbl      = new JLabel("–");
        activeRepsLbl     = new JLabel("–");
        totalMembersLbl   = new JLabel("–");  // NEW
        announcementsLbl  = new JLabel("–");  // NEW
        activePollsLbl    = new JLabel("–");  // NEW

        // Row 1
        grid.add(statCard("EVENTS",    "Total Events",       C_OCEAN1,   C_OCEAN2,   totalEventsLbl));
        grid.add(statCard("UPCOMING",  "Upcoming Events",    C_EMERALD1, C_EMERALD2, upcomingEventsLbl));
        grid.add(statCard("MEMBERS",   "Total Members",      C_TEAL1,    C_TEAL2,    totalMembersLbl));
        // Row 2
        grid.add(statCard("ISSUES",    "Total Issues",       C_ROSE1,    C_ROSE2,    totalIssuesLbl));
        grid.add(statCard("PENDING",   "Pending Issues",     C_AMBER1,   C_AMBER2,   pendingIssuesLbl));
        grid.add(statCard("REPS",      "Representatives",    C_VIOLET1,  C_VIOLET2,  totalRepsLbl));
        // Row 3
        grid.add(statCard("ACTIVE",    "Active Reps",        C_INDIGO1,  C_INDIGO2,  activeRepsLbl));
        grid.add(statCard("NOTICES",   "Announcements",      C_ORANGE1,  C_ORANGE2,  announcementsLbl));
        grid.add(statCard("POLLS",     "Active Polls",       C_PINK1,    C_PINK2,    activePollsLbl));

        return grid;
    }

    private JPanel statCard(String badge, String title, Color c1, Color c2, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(14, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, BG2, getWidth(), getHeight(),
                        new Color(
                                c1.getRed()/6   + BG2.getRed()*5/6,
                                c1.getGreen()/6 + BG2.getGreen()*5/6,
                                c1.getBlue()/6  + BG2.getBlue()*5/6));
                g2.setPaint(gp); g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                GradientPaint bar = new GradientPaint(0,0,c1,getWidth(),0,c2);
                g2.setPaint(bar); g2.fillRoundRect(0, 0, getWidth()-1, 5, 4, 4);
                g2.setColor(new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 60));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-2, getHeight()-2, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        // Badge label (replaces emoji — avoids font issues on some JDKs)
        JLabel badgeLbl = new JLabel(badge) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0,0,c1,getWidth(),getHeight(),c2);
                g2.setPaint(gp); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(Color.WHITE); g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                FontMetrics fm = g2.getFontMetrics();
                String t = getText();
                g2.drawString(t,(getWidth()-fm.stringWidth(t))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        badgeLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badgeLbl.setOpaque(false);
        badgeLbl.setPreferredSize(new Dimension(58, 36));
        badgeLbl.setForeground(Color.WHITE);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
        valueLabel.setForeground(c2);
        valueLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel titleLbl = new JLabel(title.toUpperCase());
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        titleLbl.setForeground(TEXT_SEC);
        titleLbl.setAlignmentX(LEFT_ALIGNMENT);

        textPanel.add(valueLabel);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(titleLbl);

        card.add(badgeLbl,  BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    // ── Activity Feed ─────────────────────────────────────────────────────────
    private JPanel buildActivity() {
        JPanel p = new JPanel(new BorderLayout(0, 10)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG2); g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                GradientPaint bar = new GradientPaint(0,0,C_VIOLET1,200,0,C_INDIGO1);
                g2.setPaint(bar); g2.fillRoundRect(0, 0, getWidth()-1, 5, 4, 4);
                g2.setColor(new Color(139, 92, 246, 50));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-2, getHeight()-2, 16, 16);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(0, 200));
        p.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));

        // Header row: title + refresh button
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);

        JLabel titleLbl = new JLabel("RECENT ACTIVITY  &  ANNOUNCEMENTS");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLbl.setForeground(C_VIOLET2);

        JButton refreshBtn = new JButton("Refresh") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()
                        ? new Color(99,102,241,180) : new Color(99,102,241,100));
                g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                g2.setColor(Color.WHITE); g2.setFont(new Font("Segoe UI",Font.BOLD,11));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        refreshBtn.setPreferredSize(new Dimension(76,26));
        refreshBtn.setFocusPainted(false); refreshBtn.setBorderPainted(false);
        refreshBtn.setContentAreaFilled(false);
        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> loadData());

        headerRow.add(titleLbl,    BorderLayout.WEST);
        headerRow.add(refreshBtn,  BorderLayout.EAST);

        activityArea = new JTextArea();
        activityArea.setEditable(false);
        activityArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        activityArea.setLineWrap(true); activityArea.setWrapStyleWord(true);
        activityArea.setBackground(BG2);
        activityArea.setForeground(new Color(203, 213, 225));
        activityArea.setCaretColor(Color.WHITE);
        activityArea.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 0));

        JScrollPane scroll = new JScrollPane(activityArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(BG2);
        scroll.getViewport().setBackground(BG2);

        p.add(headerRow, BorderLayout.NORTH);
        p.add(scroll,    BorderLayout.CENTER);
        return p;
    }

    // ── Data Loading ──────────────────────────────────────────────────────────
    public void refresh() { loadData(); }

    private void loadData() {
        new SwingWorker<Void, Void>() {
            int[] counts = new int[9];   // now 9 stat cards
            String activity = "";

            @Override protected Void doInBackground() {
                try (Connection conn = DBConnection.getConnection()) {

                    // ── 9 stat queries ────────────────────────────────────────
                    counts[0] = count(conn, "SELECT COUNT(*) FROM events");
                    counts[1] = count(conn, "SELECT COUNT(*) FROM events WHERE event_date >= CURDATE()");
                    counts[2] = count(conn, "SELECT COUNT(*) FROM users WHERE is_active = TRUE");
                    counts[3] = count(conn, "SELECT COUNT(*) FROM issues");
                    counts[4] = count(conn, "SELECT COUNT(*) FROM issues WHERE status IN ('PENDING','Pending')");
                    counts[5] = count(conn, "SELECT COUNT(*) FROM representatives");
                    counts[6] = count(conn, "SELECT COUNT(*) FROM representatives WHERE status IN ('ACTIVE','Active')");
                    counts[7] = count(conn, "SELECT COUNT(*) FROM announcements WHERE (expiry_date IS NULL OR expiry_date >= NOW())");
                    counts[8] = count(conn, "SELECT COUNT(*) FROM polls WHERE status = 'ACTIVE' AND end_date >= NOW()");

                    // ── Activity feed ─────────────────────────────────────────
                    StringBuilder sb = new StringBuilder();

                    // 1. activity_logs (most recent 5)
                    sb.append("  [ ACTIVITY LOG ]\n");
                    try (Statement st = conn.createStatement();
                         ResultSet rs = st.executeQuery(
                                 "SELECT al.action_type, al.action_description, u.full_name, al.timestamp " +
                                         "FROM activity_logs al " +
                                         "LEFT JOIN users u ON al.user_id = u.user_id " +
                                         "ORDER BY al.timestamp DESC LIMIT 5")) {
                        int n = 0;
                        while (rs.next()) {
                            n++;
                            String who  = rs.getString("full_name");
                            if (who == null) who = "System";
                            String desc = rs.getString("action_description");
                            if (desc == null) desc = rs.getString("action_type");
                            if (desc != null && desc.length() > 60) desc = desc.substring(0,60) + "…";
                            sb.append("  ▸ ").append(who).append(" – ").append(desc)
                                    .append("  [").append(rs.getTimestamp("timestamp")).append("]\n");
                        }
                        if (n == 0) sb.append("  No activity logs yet.\n");
                    } catch (SQLException ignored) {
                        sb.append("  (activity_logs table not accessible)\n");
                    }

                    // 2. Recent events
                    sb.append("\n  [ RECENT EVENTS ]\n");
                    try (Statement st = conn.createStatement();
                         ResultSet rs = st.executeQuery(
                                 "SELECT event_name, event_date, status FROM events " +
                                         "ORDER BY created_date DESC LIMIT 4")) {
                        int n = 0;
                        while (rs.next()) {
                            n++;
                            sb.append("  ▸ ").append(rs.getString("event_name"))
                                    .append("  [").append(rs.getDate("event_date"))
                                    .append(" | ").append(rs.getString("status")).append("]\n");
                        }
                        if (n == 0) sb.append("  No events yet.\n");
                    } catch (SQLException ignored) {}

                    // 3. Recent issues
                    sb.append("\n  [ RECENT ISSUES ]\n");
                    try (Statement st = conn.createStatement();
                         ResultSet rs = st.executeQuery(
                                 "SELECT category, priority, status FROM issues " +
                                         "ORDER BY issue_id DESC LIMIT 4")) {
                        int n = 0;
                        while (rs.next()) {
                            n++;
                            sb.append("  ▸ ").append(rs.getString("category"))
                                    .append("  [").append(rs.getString("priority"))
                                    .append(" | ").append(rs.getString("status")).append("]\n");
                        }
                        if (n == 0) sb.append("  No issues yet.\n");
                    } catch (SQLException ignored) {}

                    // 4. Recent announcements
                    sb.append("\n  [ LATEST ANNOUNCEMENTS ]\n");
                    try (Statement st = conn.createStatement();
                         ResultSet rs = st.executeQuery(
                                 "SELECT title, priority, posted_date FROM announcements " +
                                         "ORDER BY posted_date DESC LIMIT 4")) {
                        int n = 0;
                        while (rs.next()) {
                            n++;
                            sb.append("  ▸ ").append(rs.getString("title"))
                                    .append("  [").append(rs.getString("priority"))
                                    .append(" | ").append(rs.getTimestamp("posted_date")).append("]\n");
                        }
                        if (n == 0) sb.append("  No announcements yet.\n");
                    } catch (SQLException ignored) {}

                    activity = sb.toString();

                } catch (SQLException e) {
                    activity = "  Could not load data.\n  " + e.getMessage();
                }
                return null;
            }

            @Override protected void done() {
                totalEventsLbl.setText(String.valueOf(counts[0]));
                upcomingEventsLbl.setText(String.valueOf(counts[1]));
                totalMembersLbl.setText(String.valueOf(counts[2]));
                totalIssuesLbl.setText(String.valueOf(counts[3]));
                pendingIssuesLbl.setText(String.valueOf(counts[4]));
                totalRepsLbl.setText(String.valueOf(counts[5]));
                activeRepsLbl.setText(String.valueOf(counts[6]));
                announcementsLbl.setText(String.valueOf(counts[7]));
                activePollsLbl.setText(String.valueOf(counts[8]));
                activityArea.setText(activity);
                activityArea.setCaretPosition(0);
            }
        }.execute();
    }

    private int count(Connection conn, String sql) {
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) { return 0; }
    }
}