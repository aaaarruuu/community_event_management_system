package ui;

import database.DBConnection;
import models.User;
import services.UserService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class LoginScreen extends JFrame {

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JButton        loginButton;
    private final UserService userService = new UserService();

    // Drag support for undecorated window
    private Point dragStart;

    // Colour palette
    // ── Colour palette: deep navy + cyan + emerald ─────────────────────────
    private static final Color BG       = new Color(  8,  12,  28);   // deepest navy bg
    private static final Color CARD_BG  = new Color( 14,  20,  48);   // card background
    private static final Color ACCENT   = new Color(  6, 182, 212);   // cyan (primary)
    private static final Color ACCENT2  = new Color( 20, 184, 166);   // teal (secondary)
    private static final Color CYAN     = new Color( 34, 211, 238);   // bright cyan
    private static final Color GREEN    = new Color( 16, 185, 129);   // emerald success
    private static final Color TEXT     = new Color(226, 232, 240);   // near-white
    private static final Color TEXT2    = new Color(148, 163, 184);   // muted slate
    private static final Color INPUT_BG = new Color( 20,  30,  60);   // input dark
    private static final Color BORDER   = new Color( 38,  56, 100);   // dim border

    public LoginScreen() {
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        setUndecorated(true);
        setSize(1060, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG); g2.fillRect(0, 0, getWidth(), getHeight());
                // Cyan glow top-right
                RadialGradientPaint rg = new RadialGradientPaint(
                        new Point(getWidth()-120, 80), 340,
                        new float[]{0f, 1f},
                        new Color[]{new Color(6,182,212,50), new Color(0,0,0,0)});
                g2.setPaint(rg); g2.fillRect(0, 0, getWidth(), getHeight());
                // Violet glow bottom-left
                RadialGradientPaint lg = new RadialGradientPaint(
                        new Point(120, getHeight()-80), 300,
                        new float[]{0f, 1f},
                        new Color[]{new Color(99,102,241,45), new Color(0,0,0,0)});
                g2.setPaint(lg); g2.fillRect(0, 0, getWidth(), getHeight());
                // Fine dot grid
                g2.setColor(new Color(255,255,255,9));
                for (int x=0; x<getWidth(); x+=40)
                    for (int y=0; y<getHeight(); y+=40)
                        g2.fillOval(x-1, y-1, 2, 2);
                g2.dispose();
            }
        };
        root.setOpaque(true);
        root.add(buildTitleBar(), BorderLayout.NORTH);

        // Left branding panel (wider) + right login card (fixed width)
        JPanel centre = new JPanel(new BorderLayout());
        centre.setOpaque(false);
        JPanel left  = buildLeftPanel();
        JPanel right = buildLoginCard();
        left.setPreferredSize(new Dimension(560, 0));
        centre.add(left,  BorderLayout.CENTER);
        centre.add(right, BorderLayout.EAST);
        root.add(centre, BorderLayout.CENTER);

        setContentPane(root);
        getRootPane().setBorder(BorderFactory.createLineBorder(
                new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 80), 1));
    }

    // ── Custom Title Bar ──────────────────────────────────────────────────────
    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(6, 9, 22));
        bar.setOpaque(true);
        bar.setPreferredSize(new Dimension(0, 44));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 14));

        // Left: accent dot + app name
        JPanel leftSide = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 15));
        leftSide.setOpaque(false);

        // Animated-style dot
        JLabel dot = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ACCENT, getWidth(), getHeight(), ACCENT2);
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(10, 10));

        JLabel appName = new JLabel("COMMUNITY EVENT MANAGEMENT SYSTEM");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 11));
        appName.setForeground(new Color(100, 130, 175));

        JLabel version = new JLabel("v2.0");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        version.setForeground(new Color(60, 85, 130));

        leftSide.add(dot); leftSide.add(appName); leftSide.add(version);

        // Right: window control buttons
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 10));
        controls.setOpaque(false);
        controls.add(winBtn("–", new Color(250, 236, 11), e -> setState(JFrame.ICONIFIED)));
        controls.add(winBtn("⛶", new Color(4, 248, 108), e -> toggleMaximize()));
        controls.add(winBtn("X", new Color(239,68,68),  e -> System.exit(0)));

        bar.add(leftSide, BorderLayout.WEST);
        bar.add(controls, BorderLayout.EAST);

        // Drag to move
        bar.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { dragStart = e.getPoint(); }
        });
        bar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (dragStart != null && getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                    Point loc = getLocation();
                    setLocation(loc.x + e.getX() - dragStart.x, loc.y + e.getY() - dragStart.y);
                }
            }
        });

        // Thin cyan separator line at bottom of title bar
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(bar, BorderLayout.CENTER);
        JPanel sep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0,0, new Color(0,0,0,0),
                        getWidth()/2, 0, new Color(ACCENT.getRed(),ACCENT.getGreen(),ACCENT.getBlue(),90));
                g2.setPaint(gp); g2.fillRect(0, 0, getWidth()/2, 1);
                GradientPaint gp2 = new GradientPaint(getWidth()/2,0, new Color(ACCENT.getRed(),ACCENT.getGreen(),ACCENT.getBlue(),90),
                        getWidth(), 0, new Color(0,0,0,0));
                g2.setPaint(gp2); g2.fillRect(getWidth()/2, 0, getWidth()/2, 1);
                g2.dispose();
            }
        };
        sep.setPreferredSize(new Dimension(0, 1)); sep.setOpaque(false);
        wrapper.add(sep, BorderLayout.SOUTH);
        return wrapper;
    }

    private JButton winBtn(String text, Color bg, ActionListener al) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg : new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 120));
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.setColor(Color.WHITE); g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(text, (getWidth()-fm.stringWidth(text))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(24, 24));
        b.setFocusPainted(false); b.setBorderPainted(false); b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(al);
        return b;
    }

    private boolean maximized = false;
    private Rectangle normalBounds;
    private void toggleMaximize() {
        if (!maximized) {
            normalBounds = getBounds();
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            setExtendedState(JFrame.NORMAL);
            if (normalBounds != null) setBounds(normalBounds);
        }
        maximized = !maximized;
    }

    // ── Left Branding Panel ───────────────────────────────────────────────────
    private JPanel buildLeftPanel() {
        JPanel p = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient background
                GradientPaint gp = new GradientPaint(0, 0, new Color(10,16,42),
                        getWidth(), getHeight(), new Color(6, 12, 32));
                g2.setPaint(gp); g2.fillRect(0, 0, getWidth(), getHeight());
                // Large decorative arc top-left
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
                g2.setColor(ACCENT);
                g2.setStroke(new BasicStroke(60f));
                g2.drawOval(-120, -120, 380, 380);
                // Bottom-right arc
                g2.setColor(new Color(99,102,241));
                g2.drawOval(getWidth()-200, getHeight()-200, 340, 340);
                // Vertical right divider line
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                GradientPaint div = new GradientPaint(
                        getWidth()-1, 0,   new Color(ACCENT.getRed(),ACCENT.getGreen(),ACCENT.getBlue(),0),
                        getWidth()-1, getHeight()/2, new Color(ACCENT.getRed(),ACCENT.getGreen(),ACCENT.getBlue(),70));
                g2.setPaint(div); g2.setStroke(new BasicStroke(1f));
                g2.fillRect(getWidth()-1, 0, 1, getHeight()/2);
                GradientPaint div2 = new GradientPaint(
                        getWidth()-1, getHeight()/2, new Color(ACCENT.getRed(),ACCENT.getGreen(),ACCENT.getBlue(),70),
                        getWidth()-1, getHeight(),   new Color(ACCENT.getRed(),ACCENT.getGreen(),ACCENT.getBlue(),0));
                g2.setPaint(div2);
                g2.fillRect(getWidth()-1, getHeight()/2, 1, getHeight()/2);
                g2.dispose();
            }
        };
        p.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.anchor = GridBagConstraints.CENTER;

        // ── Logo circle ───────────────────────────────────────────────────────
        JPanel logoCircle = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Outer glow ring
                g2.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 30));
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                // Inner filled circle
                GradientPaint gp = new GradientPaint(8, 8, ACCENT, getWidth()-8, getHeight()-8, ACCENT2);
                g2.setPaint(gp);
                g2.fillOval(8, 8, getWidth()-17, getHeight()-17);
                // Letter
                g2.setColor(new Color(8, 12, 28));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 38));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("C", (getWidth()-fm.stringWidth("C"))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2 - 2);
                g2.dispose();
            }
        };
        logoCircle.setPreferredSize(new Dimension(96, 96));
        logoCircle.setOpaque(false);

        gc.gridy = 0; gc.insets = new Insets(0, 0, 22, 0);
        p.add(logoCircle, gc);

        // ── App name ──────────────────────────────────────────────────────────
        gc.gridy = 1; gc.insets = new Insets(0, 0, 4, 0);
        JLabel name1 = new JLabel("COMMUNITY EVENT");
        name1.setFont(new Font("Segoe UI", Font.BOLD, 32));
        name1.setForeground(Color.WHITE);
        p.add(name1, gc);

        gc.gridy = 2; gc.insets = new Insets(0, 0, 4, 0);
        JLabel name2 = new JLabel("MANAGEMENT SYSTEM");
        name2.setFont(new Font("Segoe UI", Font.BOLD, 19));
        name2.setForeground(ACCENT);
        p.add(name2, gc);

        gc.gridy = 3; gc.insets = new Insets(0, 0, 32, 0);
        JLabel sub = new JLabel("VIT Bhopal  ·  MCA 2026  ·  Team 07");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(TEXT2);
        p.add(sub, gc);

        // ── Feature pills ─────────────────────────────────────────────────────
        gc.gridy = 4; gc.insets = new Insets(0, 0, 10, 0);
        p.add(featurePill("Event Scheduling",  ACCENT),                  gc);
        gc.gridy = 5;
        p.add(featurePill("Issue Tracking",    new Color(244,63,94)),     gc);
        gc.gridy = 6;
        p.add(featurePill("Rep Management",    GREEN),                    gc);
        gc.gridy = 7;
        p.add(featurePill("Notices & Polls",   CYAN),                     gc);

        // ── Stats strip ───────────────────────────────────────────────────────
        gc.gridy = 8; gc.insets = new Insets(28, 0, 0, 0);
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        statsRow.setOpaque(false);
        statsRow.add(statBadge("14",  "Tables"));
        statsRow.add(statSep());
        statsRow.add(statBadge("10+", "Users"));
        statsRow.add(statSep());
        statsRow.add(statBadge("4",   "Modules"));
        p.add(statsRow, gc);

        return p;
    }

    private JPanel statBadge(String value, String label) {
        JPanel b = new JPanel();
        b.setLayout(new BoxLayout(b, BoxLayout.Y_AXIS));
        b.setOpaque(false);
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 20));
        val.setForeground(ACCENT);
        val.setAlignmentX(CENTER_ALIGNMENT);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lbl.setForeground(TEXT2);
        lbl.setAlignmentX(CENTER_ALIGNMENT);
        b.add(val); b.add(lbl);
        return b;
    }

    private JLabel statSep() {
        JLabel s = new JLabel("|");
        s.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        s.setForeground(new Color(50, 70, 110));
        return s;
    }

    private JLabel featurePill(String text, Color color) {
        JLabel l = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose(); super.paintComponent(g);
            }
        };
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(color);
        l.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 18));
        l.setOpaque(false);
        return l;
    }

    // ── Login Card ────────────────────────────────────────────────────────────
    private JPanel buildLoginCard() {
        // Outer wrapper fills the right side and centres the card
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);
        wrap.setPreferredSize(new Dimension(500, 0));

        // Card background with rounded corners
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Card fill
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                // Top accent line (cyan → teal)
                GradientPaint glow = new GradientPaint(30,0, new Color(0,0,0,0),
                        getWidth()/2, 0, ACCENT);
                g2.setPaint(glow); g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(30, 1, getWidth()/2, 1);
                GradientPaint glow2 = new GradientPaint(getWidth()/2, 0, ACCENT,
                        getWidth()-30, 0, new Color(0,0,0,0));
                g2.setPaint(glow2);
                g2.drawLine(getWidth()/2, 1, getWidth()-30, 1);
                // Subtle border
                g2.setColor(new Color(255,255,255,12));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(390, 510));
        card.setLayout(new GridBagLayout());

        // Inner column — all elements share the same fixed width FW
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);
        final int FW = 310;  // uniform width for all form elements

        // ── Icon + heading ────────────────────────────────────────────────────
        JLabel lockIcon = new JLabel("🔐");
        lockIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 38));
        lockIcon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel loginTitle = new JLabel("Welcome Back!");
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        loginTitle.setForeground(TEXT);
        loginTitle.setAlignmentX(CENTER_ALIGNMENT);

        JLabel loginSub = new JLabel("Sign in to your community portal");
        loginSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loginSub.setForeground(TEXT2);
        loginSub.setAlignmentX(CENTER_ALIGNMENT);

        col.add(Box.createVerticalStrut(10));
        col.add(lockIcon);
        col.add(Box.createVerticalStrut(12));
        col.add(loginTitle);
        col.add(Box.createVerticalStrut(5));
        col.add(loginSub);
        col.add(Box.createVerticalStrut(30));

        // ── USERNAME ──────────────────────────────────────────────────────────
        JLabel userLbl = inputLabel("USERNAME");
        userLbl.setAlignmentX(CENTER_ALIGNMENT);
        col.add(userLbl);
        col.add(Box.createVerticalStrut(7));

        usernameField = darkField("Enter your username");
        usernameField.setPreferredSize(new Dimension(FW, 46));
        usernameField.setMaximumSize(new Dimension(FW, 46));
        usernameField.setMinimumSize(new Dimension(FW, 46));
        usernameField.setAlignmentX(CENTER_ALIGNMENT);
        usernameField.addActionListener(e -> handleLogin());
        col.add(usernameField);
        col.add(Box.createVerticalStrut(18));

        // ── PASSWORD ──────────────────────────────────────────────────────────
        JLabel passLbl = inputLabel("PASSWORD");
        passLbl.setAlignmentX(CENTER_ALIGNMENT);
        col.add(passLbl);
        col.add(Box.createVerticalStrut(7));

        // Password field
        passwordField = darkPasswordField("Enter your password");
        passwordField.setPreferredSize(new Dimension(FW - 62, 46));
        passwordField.setMaximumSize(new Dimension(FW - 62, 46));
        passwordField.setMinimumSize(new Dimension(FW - 62, 46));
        passwordField.addActionListener(e -> handleLogin());

        // Show/hide toggle button
        JButton eyeBtn = new JButton("SHOW") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(INPUT_BG); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(BORDER); g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(ACCENT); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics(); String t = getText();
                g2.drawString(t,(getWidth()-fm.stringWidth(t))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        eyeBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        eyeBtn.setPreferredSize(new Dimension(58, 46));
        eyeBtn.setMaximumSize(new Dimension(58, 46));
        eyeBtn.setMinimumSize(new Dimension(58, 46));
        eyeBtn.setFocusPainted(false); eyeBtn.setBorderPainted(false); eyeBtn.setContentAreaFilled(false);
        eyeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eyeBtn.addActionListener(e -> {
            if (passwordField.getEchoChar() != (char)0) {
                passwordField.setEchoChar((char)0); eyeBtn.setText("HIDE");
            } else {
                passwordField.setEchoChar('●'); eyeBtn.setText("SHOW");
            }
            eyeBtn.repaint();
        });

        // Password row — same total width as username field
        JPanel pwRow = new JPanel(new BorderLayout(4, 0));
        pwRow.setOpaque(false);
        pwRow.setPreferredSize(new Dimension(FW, 46));
        pwRow.setMaximumSize(new Dimension(FW, 46));
        pwRow.setMinimumSize(new Dimension(FW, 46));
        pwRow.setAlignmentX(CENTER_ALIGNMENT);
        pwRow.add(passwordField, BorderLayout.CENTER);
        pwRow.add(eyeBtn,        BorderLayout.EAST);
        col.add(pwRow);
        col.add(Box.createVerticalStrut(26));

        // ── SIGN IN button ────────────────────────────────────────────────────
        loginButton = new JButton("SIGN IN  →") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = isEnabled()
                        ? new GradientPaint(0,0,ACCENT,getWidth(),0,ACCENT2)
                        : new GradientPaint(0,0,BORDER,getWidth(),0,BORDER);
                g2.setPaint(gp); g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                // Shine highlight
                if (isEnabled()) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(4, 2, getWidth()-8, getHeight()/2-2, 8, 8);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }
                g2.setColor(new Color(8,12,28)); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2-1);
                g2.dispose();
            }
        };
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(FW, 48));
        loginButton.setMaximumSize(new Dimension(FW, 48));
        loginButton.setMinimumSize(new Dimension(FW, 48));
        loginButton.setAlignmentX(CENTER_ALIGNMENT);
        loginButton.setFocusPainted(false); loginButton.setBorderPainted(false); loginButton.setContentAreaFilled(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> handleLogin());
        col.add(loginButton);
        col.add(Box.createVerticalStrut(16));

        // ── OR divider ────────────────────────────────────────────────────────
        JPanel divider = new JPanel(new BorderLayout(8, 0));
        divider.setOpaque(false);
        divider.setPreferredSize(new Dimension(FW, 20));
        divider.setMaximumSize(new Dimension(FW, 20));
        divider.setAlignmentX(CENTER_ALIGNMENT);
        JSeparator s1 = new JSeparator(); s1.setForeground(BORDER);
        JLabel or = new JLabel("OR"); or.setFont(new Font("Segoe UI", Font.BOLD, 11));
        or.setForeground(TEXT2); or.setHorizontalAlignment(SwingConstants.CENTER);
        JSeparator s2 = new JSeparator(); s2.setForeground(BORDER);
        divider.add(s1, BorderLayout.WEST); divider.add(or, BorderLayout.CENTER); divider.add(s2, BorderLayout.EAST);
        col.add(divider);
        col.add(Box.createVerticalStrut(14));

        // ── Register button ───────────────────────────────────────────────────
        JButton regBtn = new JButton("CREATE NEW ACCOUNT") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()
                        ? new Color(ACCENT.getRed(),ACCENT.getGreen(),ACCENT.getBlue(),35)
                        : new Color(0,0,0,0));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(new Color(ACCENT.getRed(),ACCENT.getGreen(),ACCENT.getBlue(),130));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(ACCENT); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2-1);
                g2.dispose();
            }
        };
        regBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        regBtn.setPreferredSize(new Dimension(FW, 44));
        regBtn.setMaximumSize(new Dimension(FW, 44));
        regBtn.setMinimumSize(new Dimension(FW, 44));
        regBtn.setAlignmentX(CENTER_ALIGNMENT);
        regBtn.setFocusPainted(false); regBtn.setBorderPainted(false); regBtn.setContentAreaFilled(false);
        regBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        regBtn.addActionListener(e -> { dispose(); new RegistrationScreen(); });
        col.add(regBtn);

        // ── Credentials hint ──────────────────────────────────────────────────
        col.add(Box.createVerticalStrut(18));
        JLabel hint = new JLabel("Demo:  abcd / abcd123 ");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hint.setForeground(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 150));
        hint.setAlignmentX(CENTER_ALIGNMENT);
        col.add(hint);
        col.add(Box.createVerticalStrut(6));

        card.add(col, new GridBagConstraints());
        wrap.add(card);
        return wrap;
    }

    // ── Login Logic ───────────────────────────────────────────────────────────
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            shake(loginButton);
            JOptionPane.showMessageDialog(this, "Please enter both username and password!", "Missing Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }
        loginButton.setEnabled(false); loginButton.setText("AUTHENTICATING…");
        new SwingWorker<User, Void>() {
            @Override protected User doInBackground() { return userService.authenticate(username, password); }
            @Override protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        new MainDashboard(user); dispose();
                    } else {
                        shake(loginButton);
                        JOptionPane.showMessageDialog(LoginScreen.this,
                                "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
                        passwordField.setText(""); usernameField.requestFocus();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LoginScreen.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    loginButton.setEnabled(true); loginButton.setText("SIGN IN  →");
                }
            }
        }.execute();
    }

    private void shake(Component c) {
        Point orig = c.getLocation();
        Timer t = new Timer(30, null);
        int[] offsets = {8,-8,6,-6,4,-4,2,-2,0};
        int[] idx = {0};
        t.addActionListener(e -> {
            if (idx[0] < offsets.length) {
                c.setLocation(orig.x + offsets[idx[0]++], orig.y);
            } else {
                c.setLocation(orig); t.stop();
            }
        });
        t.start();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private JLabel inputLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(ACCENT);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JTextField darkField(String placeholder) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(INPUT_BG); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(isFocusOwner() ? ACCENT : BORDER); g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        f.setOpaque(false); f.setForeground(TEXT); f.setCaretColor(ACCENT);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        f.putClientProperty("placeholder", placeholder);
        return f;
    }

    private JPasswordField darkPasswordField(String placeholder) {
        JPasswordField f = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(INPUT_BG); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(isFocusOwner() ? ACCENT : BORDER); g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        f.setOpaque(false); f.setForeground(TEXT); f.setCaretColor(ACCENT);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        return f;
    }

    // ── Entry Point ───────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        if (!DBConnection.testConnection()) {
            JOptionPane.showMessageDialog(null,
                    "Cannot connect to database!\n\n1. MySQL is running\n2. Database 'community_event_db' exists\n3. Password in DBConnection.java is correct",
                    "Database Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        SwingUtilities.invokeLater(LoginScreen::new);
    }
}