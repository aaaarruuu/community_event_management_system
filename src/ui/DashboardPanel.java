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
    private JLabel totalEventsLbl, upcomingEventsLbl, totalIssuesLbl;
    private JLabel pendingIssuesLbl, totalRepsLbl, activeRepsLbl;
    private JTextArea activityArea;

    // ── Rich Palette ──────────────────────────────────────────────────────────
    private static final Color C_OCEAN1  = new Color(6,   182, 212);   // cyan
    private static final Color C_OCEAN2  = new Color(14,  165, 233);   // sky
    private static final Color C_VIOLET1 = new Color(139,  92, 246);   // violet
    private static final Color C_VIOLET2 = new Color(168,  85, 247);   // purple
    private static final Color C_ROSE1   = new Color(244,  63,  94);   // rose
    private static final Color C_ROSE2   = new Color(251, 113, 133);   // light rose
    private static final Color C_AMBER1  = new Color(245, 158,  11);   // amber
    private static final Color C_AMBER2  = new Color(251, 191,  36);   // yellow
    private static final Color C_EMERALD1= new Color( 16, 185, 129);   // emerald
    private static final Color C_EMERALD2= new Color( 52, 211, 153);   // mint
    private static final Color C_INDIGO1 = new Color( 99, 102, 241);   // indigo
    private static final Color C_INDIGO2 = new Color(129, 140, 248);   // lavender
    private static final Color BG        = new Color( 15,  23,  42);   // dark slate
    private static final Color BG2       = new Color( 30,  41,  59);   // slightly lighter
    private static final Color TEXT_PRI  = new Color(0, 4, 4);
    private static final Color TEXT_SEC  = new Color(124, 175, 243);

    public DashboardPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);
        initComponents();
        loadData();
    }

    private void initComponents() {
        add(buildHeader(), BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout(20, 20));
        body.setBackground(BG);
        body.setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));
        body.add(buildStatsGrid(), BorderLayout.CENTER);
        body.add(buildActivity(), BorderLayout.SOUTH);
        add(body, BorderLayout.CENTER);
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Deep gradient background
                GradientPaint gp = new GradientPaint(0, 0, new Color(15,23,42), getWidth(), 0, new Color(30,58,138));
                g2.setPaint(gp); g2.fillRect(0, 0, getWidth(), getHeight());
                // Decorative glowing orbs
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
                g2.setColor(C_OCEAN1);
                g2.fill(new Ellipse2D.Float(getWidth()-160, -40, 200, 200));
                g2.setColor(C_VIOLET1);
                g2.fill(new Ellipse2D.Float(-60, -30, 180, 180));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(true); p.setPreferredSize(new Dimension(0, 108));
        p.setBorder(BorderFactory.createEmptyBorder(0, 28, 0, 28));

        JPanel text = new JPanel(new GridBagLayout());
        text.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx=0; gc.gridy=0; gc.anchor=GridBagConstraints.WEST; gc.insets=new Insets(0,0,2,0);

        JLabel welcome = new JLabel("👤 Welcome back, " + currentUser.getFullName() + "!");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcome.setForeground(TEXT_PRI);

        gc.gridy=1;
        JLabel role = new JLabel("  " + (currentUser.getRole() != null ? currentUser.getRole() : "MEMBER")
                + "  ·  Community Management Portal");
        role.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        role.setForeground(C_EMERALD1);

        gc.gridy=0; text.add(welcome, gc);
        gc.gridy=1; text.add(role, gc);
        p.add(text, BorderLayout.CENTER);

        // Right: quick stats pill
        JPanel pill = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        pill.setOpaque(false);
        JLabel onlineLabel = new JLabel("● LIVE");
        onlineLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        onlineLabel.setForeground(C_EMERALD1);
        pill.add(onlineLabel);
        p.add(pill, BorderLayout.EAST);
        return p;
    }

    // ── Stats Grid ────────────────────────────────────────────────────────────
    private JPanel buildStatsGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 18, 18));
        grid.setOpaque(false);

        totalEventsLbl    = new JLabel("–");
        upcomingEventsLbl = new JLabel("–");
        totalIssuesLbl    = new JLabel("–");
        pendingIssuesLbl  = new JLabel("–");
        totalRepsLbl      = new JLabel("–");
        activeRepsLbl     = new JLabel("–");

        grid.add(statCard("📅", "Total Events",    C_OCEAN1,   C_OCEAN2,   totalEventsLbl));
        grid.add(statCard("🔜", "Upcoming Events", C_EMERALD1, C_EMERALD2, upcomingEventsLbl));
        grid.add(statCard("🚨", "Total Issues",    C_ROSE1,    C_ROSE2,    totalIssuesLbl));
        grid.add(statCard("⏳", "Pending Issues",  C_AMBER1,   C_AMBER2,   pendingIssuesLbl));
        grid.add(statCard("👥", "Representatives", C_VIOLET1,  C_VIOLET2,  totalRepsLbl));
        grid.add(statCard("✅", "Active Reps",     C_INDIGO1,  C_INDIGO2,  activeRepsLbl));
        return grid;
    }

    private JPanel statCard(String emoji, String title, Color c1, Color c2, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(14, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Card body gradient
                GradientPaint gp = new GradientPaint(0, 0, BG2, getWidth(), getHeight(),
                        new Color(c1.getRed()/6+BG2.getRed()*5/6,
                                c1.getGreen()/6+BG2.getGreen()*5/6,
                                c1.getBlue()/6+BG2.getBlue()*5/6));
                g2.setPaint(gp); g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                // Coloured top bar
                GradientPaint bar = new GradientPaint(0,0,c1,getWidth(),0,c2);
                g2.setPaint(bar); g2.fillRoundRect(0, 0, getWidth()-1, 5, 4, 4);
                // Border
                g2.setColor(new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 60));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-2, getHeight()-2, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        // Icon circle
        JPanel iconCircle = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0,0,c1,getWidth(),getHeight(),c2);
                g2.setPaint(gp); g2.fillOval(2,2,getWidth()-4,getHeight()-4);
                g2.dispose(); super.paintComponent(g);
            }
        };
        iconCircle.setOpaque(false); iconCircle.setPreferredSize(new Dimension(54,54));
        JLabel iconLbl = new JLabel(emoji);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        iconCircle.add(iconLbl);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 38));
        valueLabel.setForeground(c2);
        valueLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel titleLbl = new JLabel(title.toUpperCase());
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLbl.setForeground(TEXT_SEC);
        titleLbl.setAlignmentX(LEFT_ALIGNMENT);

        textPanel.add(valueLabel);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(titleLbl);

        card.add(iconCircle, BorderLayout.WEST);
        card.add(textPanel,  BorderLayout.CENTER);
        return card;
    }

    // ── Activity ──────────────────────────────────────────────────────────────
    private JPanel buildActivity() {
        JPanel p = new JPanel(new BorderLayout(0, 12)) {
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
        p.setOpaque(false); p.setPreferredSize(new Dimension(0, 190));
        p.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel titleLbl = new JLabel("📊  RECENT ACTIVITY");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLbl.setForeground(C_VIOLET2);

        activityArea = new JTextArea();
        activityArea.setEditable(false);
        activityArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        activityArea.setLineWrap(true); activityArea.setWrapStyleWord(true);
        activityArea.setBackground(BG2); activityArea.setForeground(new Color(203, 213, 225));
        activityArea.setCaretColor(Color.WHITE);
        activityArea.setBorder(BorderFactory.createEmptyBorder(6, 4, 0, 0));

        JScrollPane scroll = new JScrollPane(activityArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(BG2);
        scroll.getViewport().setBackground(BG2);

        p.add(titleLbl, BorderLayout.NORTH);
        p.add(scroll,   BorderLayout.CENTER);
        return p;
    }

    // ── Data Loading ──────────────────────────────────────────────────────────
    public void refresh() { loadData(); }

    private void loadData() {
        new SwingWorker<Void, Void>() {
            int[] counts = new int[6];
            String activity = "";
            @Override protected Void doInBackground() {
                try (Connection conn = DBConnection.getConnection()) {
                    counts[0] = count(conn, "SELECT COUNT(*) FROM events");
                    counts[1] = count(conn, "SELECT COUNT(*) FROM events WHERE event_date >= CURDATE()");
                    counts[2] = count(conn, "SELECT COUNT(*) FROM issues");
                    counts[3] = count(conn, "SELECT COUNT(*) FROM issues WHERE status IN ('PENDING','Pending')");
                    counts[4] = count(conn, "SELECT COUNT(*) FROM representatives");
                    counts[5] = count(conn, "SELECT COUNT(*) FROM representatives WHERE status IN ('ACTIVE','Active')");
                    StringBuilder sb = new StringBuilder("  Latest Updates\n\n  Recent Events:\n");
                    try (Statement st = conn.createStatement();
                         ResultSet rs = st.executeQuery("SELECT event_name, event_date FROM events ORDER BY created_date DESC LIMIT 3")) {
                        while (rs.next())
                            sb.append("  ▸ ").append(rs.getString(1)).append("  [").append(rs.getDate(2)).append("]\n");
                    } catch (SQLException ignored) {}
                    sb.append("\n  Recent Issues:\n");
                    try (Statement st = conn.createStatement();
                         ResultSet rs = st.executeQuery("SELECT COALESCE(description,'No description') AS d, status FROM issues ORDER BY issue_id DESC LIMIT 3")) {
                        while (rs.next()) {
                            String d = rs.getString("d"); if (d!=null&&d.length()>50) d=d.substring(0,50)+"…";
                            sb.append("  ▸ ").append(d).append("  [").append(rs.getString("status")).append("]\n");
                        }
                    } catch (SQLException ignored) {}
                    activity = sb.toString();
                } catch (SQLException e) { activity = "  ⚠  Could not load data.\n  " + e.getMessage(); }
                return null;
            }
            @Override protected void done() {
                totalEventsLbl.setText(String.valueOf(counts[0]));
                upcomingEventsLbl.setText(String.valueOf(counts[1]));
                totalIssuesLbl.setText(String.valueOf(counts[2]));
                pendingIssuesLbl.setText(String.valueOf(counts[3]));
                totalRepsLbl.setText(String.valueOf(counts[4]));
                activeRepsLbl.setText(String.valueOf(counts[5]));
                activityArea.setText(activity); activityArea.setCaretPosition(0);
            }
        }.execute();
    }

    private int count(Connection conn, String sql) {
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) { return 0; }
    }
}