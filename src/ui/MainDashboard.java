package ui;

import models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class MainDashboard extends JFrame {

    private final User currentUser;

    private JTabbedPane         tabbedPane;
    private DashboardPanel      dashboardPanel;
    private EventPanel          eventPanel;
    private IssuePanel          issuePanel;
    private RepresentativePanel representativePanel;
    private AnnouncementPanel   announcementPanel;   // NEW
    private PollPanel           pollPanel;            // NEW

    // Palette
    static final Color NAV_BG   = new Color(28,  40,  80);
    static final Color NAV_BG2  = new Color(38,  55, 110);
    static final Color BODY_BG  = new Color(242, 245, 252);
    static final Color FOOTER   = new Color(50,  60, 100);
    static final Color ACCENT_B = new Color(67,  97, 238);
    static final Color ACCENT_G = new Color(34, 175, 120);
    static final Color ACCENT_O = new Color(230, 100,  50);
    static final Color ACCENT_V = new Color(120,  70, 210);
    static final Color ACCENT_N = new Color(249, 115,  22);  // notices / announcements
    static final Color ACCENT_P = new Color(236,  72, 153);  // polls

    public MainDashboard(User user) {
        this.currentUser = user;
        setTitle("Community Event Management System");
        setSize(1280, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BODY_BG);
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabPane(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, NAV_BG, getWidth(), 0, NAV_BG2);
                g2.setPaint(gp); g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255,255,255,30));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        p.setOpaque(true);
        p.setPreferredSize(new Dimension(0, 68));
        p.setBorder(BorderFactory.createEmptyBorder(0, 22, 0, 18));

        // Left: logo + title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JPanel logoCircle = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ACCENT_B, getWidth(), getHeight(), ACCENT_V);
                g2.setPaint(gp); g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.setColor(Color.WHITE); g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("C",(getWidth()-fm.stringWidth("C"))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2-2);
                g2.dispose();
            }
        };
        logoCircle.setPreferredSize(new Dimension(44, 44)); logoCircle.setOpaque(false);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);
        JLabel appTitle = new JLabel("Community Event Management");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 20)); appTitle.setForeground(Color.WHITE);
        JLabel appSub = new JLabel("VIT Bhopal  |  MCA 2026");
        appSub.setFont(new Font("Segoe UI", Font.PLAIN, 11)); appSub.setForeground(new Color(180, 200, 240));
        titleBlock.add(appTitle); titleBlock.add(appSub);
        left.add(logoCircle); left.add(titleBlock);

        // Right: user badge + logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JLabel userBadge = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,20)); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,20,20);
                g2.setColor(new Color(255,255,255,50)); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,20,20);
                g2.dispose(); super.paintComponent(g);
            }
        };
        userBadge.setText("  [" + currentUser.getRole() + "]  " + currentUser.getUsername() + "  ");
        userBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userBadge.setForeground(new Color(220, 235, 255));
        userBadge.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        userBadge.setOpaque(false);

        JButton logoutBtn = new JButton("Logout") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = new Color(210, 60, 60);
                g2.setColor(getModel().isRollover() ? base.brighter() : base);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.setColor(Color.WHITE); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics(); String t = getText();
                g2.drawString(t,(getWidth()-fm.stringWidth(t))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutBtn.setPreferredSize(new Dimension(90, 36));
        logoutBtn.setFocusPainted(false); logoutBtn.setBorderPainted(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> logout());

        right.add(userBadge); right.add(logoutBtn);
        p.add(left, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    // ── Tab Pane (now 6 tabs) ─────────────────────────────────────────────────
    private JTabbedPane buildTabPane() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(BODY_BG); g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(BODY_BG);
        tabbedPane.setOpaque(true);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder());

        // Instantiate all panels
        dashboardPanel      = new DashboardPanel(currentUser);
        eventPanel          = new EventPanel(currentUser);
        issuePanel          = new IssuePanel(currentUser);
        representativePanel = new RepresentativePanel(currentUser);
        announcementPanel   = new AnnouncementPanel(currentUser);   // NEW
        pollPanel           = new PollPanel(currentUser);            // NEW

        // Add tabs (null title – using custom tab component below)
        tabbedPane.addTab(null, dashboardPanel);
        tabbedPane.addTab(null, eventPanel);
        tabbedPane.addTab(null, issuePanel);
        tabbedPane.addTab(null, representativePanel);
        tabbedPane.addTab(null, announcementPanel);   // NEW  index 4
        tabbedPane.addTab(null, pollPanel);            // NEW  index 5

        // Custom tab labels
        tabbedPane.setTabComponentAt(0, tabLabel("Dashboard", "[ D ]", ACCENT_B));
        tabbedPane.setTabComponentAt(1, tabLabel("Events",    "[ E ]", ACCENT_G));
        tabbedPane.setTabComponentAt(2, tabLabel("Issues",    "[ I ]", ACCENT_O));
        tabbedPane.setTabComponentAt(3, tabLabel("Reps",      "[ R ]", ACCENT_V));
        tabbedPane.setTabComponentAt(4, tabLabel("Notices",   "[ N ]", ACCENT_N));  // NEW
        tabbedPane.setTabComponentAt(5, tabLabel("Polls",     "[ P ]", ACCENT_P));  // NEW

        return tabbedPane;
    }

    private JPanel tabLabel(String name, String badge, Color accent) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));

        JLabel badgeLbl = new JLabel(badge) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accent); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,6,6);
                g2.setColor(Color.WHITE); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics(); String t = getText();
                g2.drawString(t,(getWidth()-fm.stringWidth(t))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        badgeLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badgeLbl.setPreferredSize(new Dimension(36, 20));
        badgeLbl.setForeground(Color.WHITE); badgeLbl.setOpaque(false);

        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLbl.setForeground(new Color(40, 55, 110));

        p.add(badgeLbl); p.add(nameLbl);
        return p;
    }

    // ── Footer ────────────────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(FOOTER);
        p.setPreferredSize(new Dimension(0, 30));
        p.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));

        JLabel left = new JLabel("(c) 2026 Community Event Management System  |  Version 2.0");
        left.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        left.setForeground(new Color(170, 185, 225));

        JLabel right = new JLabel("VIT Bhopal  |  MCA 2026  ");
        right.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        right.setForeground(new Color(140, 160, 210));

        p.add(left, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    // ── Actions ───────────────────────────────────────────────────────────────
    private void logout() {
        int c = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (c == JOptionPane.YES_OPTION) { dispose(); new LoginScreen(); }
    }

    /** Refresh all 6 panels */
    public void refreshAll() {
        if (dashboardPanel      != null) dashboardPanel.refresh();
        if (eventPanel          != null) eventPanel.loadEvents();
        if (issuePanel          != null) issuePanel.loadIssues();
        if (representativePanel != null) representativePanel.loadRepresentatives();
        if (announcementPanel   != null) announcementPanel.loadAnnouncements();
        if (pollPanel           != null) pollPanel.loadPolls();
        tabbedPane.revalidate(); tabbedPane.repaint();
    }
}