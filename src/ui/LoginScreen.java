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
    private static final Color BG       = new Color(10,  15,  30);
    private static final Color CARD_BG  = new Color(18,  26,  50);
    private static final Color ACCENT   = new Color(99,  102, 241);  // indigo
    private static final Color ACCENT2  = new Color(139,  92, 246);  // violet
    private static final Color CYAN     = new Color(6,   182, 212);
    private static final Color GREEN    = new Color(16,  185, 129);
    private static final Color TEXT     = new Color(226, 232, 240);
    private static final Color TEXT2    = new Color(148, 163, 184);
    private static final Color INPUT_BG = new Color(30,  41,  59);
    private static final Color BORDER   = new Color(51,  65,  85);

    public LoginScreen() {
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        setUndecorated(true);               // remove OS chrome
        setSize(980, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main layered panel
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Deep dark bg
                g2.setColor(BG); g2.fillRect(0, 0, getWidth(), getHeight());
                // Left glow
                RadialGradientPaint lg = new RadialGradientPaint(
                        new Point(150, 300), 320,
                        new float[]{0f, 1f},
                        new Color[]{new Color(99,102,241,60), new Color(0,0,0,0)});
                g2.setPaint(lg); g2.fillRect(0, 0, getWidth(), getHeight());
                // Right glow
                RadialGradientPaint rg = new RadialGradientPaint(
                        new Point(getWidth()-180, 280), 280,
                        new float[]{0f, 1f},
                        new Color[]{new Color(6,182,212,45), new Color(0,0,0,0)});
                g2.setPaint(rg); g2.fillRect(0, 0, getWidth(), getHeight());
                // Subtle grid dots
                g2.setColor(new Color(255,255,255,12));
                for (int x=0; x<getWidth(); x+=36)
                    for (int y=0; y<getHeight(); y+=36)
                        g2.fillOval(x-1, y-1, 3, 3);
                g2.dispose();
            }
        };
        root.setOpaque(false);

        // Custom title bar
        root.add(buildTitleBar(), BorderLayout.NORTH);

        // Centre: left panel + right card
        JPanel centre = new JPanel(new GridLayout(1, 2, 0, 0));
        centre.setOpaque(false);
        centre.add(buildLeftPanel());
        centre.add(buildLoginCard());
        root.add(centre, BorderLayout.CENTER);

        setContentPane(root);
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 100), 1));
    }

    // ── Custom Title Bar ──────────────────────────────────────────────────────
    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(0,0,0,0));
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 42));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 12));
        bar.setBackground(new Color(8, 12, 28, 200));
        bar.setOpaque(true);

        JLabel appName = new JLabel("COMMUNITY EVENT MANAGEMENT SYSTEM");
        appName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        appName.setForeground(TEXT2);

        // Window control buttons
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 7));
        controls.setOpaque(false);
        controls.add(winBtn("–", new Color(245,158,11), e -> setState(JFrame.ICONIFIED)));        // minimize
        controls.add(winBtn("⛶", new Color(16,185,129),  e -> toggleMaximize()));                 // maximize
        controls.add(winBtn("X", new Color(239,68,68),   e -> System.exit(0)));                   // close

        bar.add(appName,  BorderLayout.WEST);
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
        return bar;
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
                GradientPaint gp = new GradientPaint(0, 0, new Color(17,24,64), getWidth(), getHeight(), new Color(6,20,50));
                g2.setPaint(gp); g2.fillRect(0, 0, getWidth(), getHeight());
                // Decorative circles
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
                g2.setColor(ACCENT);  g2.fill(new Ellipse2D.Float(-60, -60, 280, 280));
                g2.setColor(CYAN);    g2.fill(new Ellipse2D.Float(getWidth()-160, getHeight()-160, 280, 280));
                g2.setColor(ACCENT2); g2.fill(new Ellipse2D.Float(getWidth()/2-80, getHeight()/2-80, 200, 200));
                g2.dispose();
            }
        };
        p.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx=0; gc.gridy=0; gc.anchor=GridBagConstraints.CENTER; gc.insets=new Insets(0,0,14,0);

        // Big icon
        JLabel bigIcon = new JLabel("🏘️");
        bigIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 92));
        bigIcon.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(bigIcon, gc);

        gc.gridy=1; gc.insets=new Insets(0,0,8,0);
        JLabel name = new JLabel("COMMUNITY EVENT");
        name.setFont(new Font("Segoe UI", Font.BOLD, 36));
        name.setForeground(Color.WHITE);
        p.add(name, gc);

        gc.gridy=2; gc.insets=new Insets(0,0,6,0);
        JLabel name2 = new JLabel("MANAGEMENT SYSTEM");
        name2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        name2.setForeground(ACCENT2);
        p.add(name2, gc);

        gc.gridy=3; gc.insets=new Insets(0,0,32,0);
        JLabel sub = new JLabel("TEAM -- 07");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT2);
        p.add(sub, gc);

        // Feature pills
        gc.gridy=4; gc.insets=new Insets(0,0,8,0);
        p.add(featurePill("Event Scheduling",   ACCENT),  gc);
        gc.gridy=5; p.add(featurePill("Issue Tracking",    new Color(244,63,94)), gc);
        gc.gridy=6; p.add(featurePill("Rep Management",    GREEN),  gc);
        gc.gridy=7; p.add(featurePill("Live Dashboard",    CYAN),   gc);

        return p;
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
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);

        JPanel card = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Glass card
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 24, 24);
                // Top glow line
                GradientPaint glow = new GradientPaint(0,0,ACCENT,getWidth(),0,ACCENT2);
                g2.setPaint(glow);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(40, 1, getWidth()-40, 1);
                // Border
                g2.setColor(new Color(255,255,255,15));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 24, 24);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(380, 420));
        card.setBorder(BorderFactory.createEmptyBorder(36, 40, 36, 40));

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel lockIcon = new JLabel("🔐");
        lockIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        lockIcon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel loginTitle = new JLabel("Welcome Back!");
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        loginTitle.setForeground(TEXT);
        loginTitle.setAlignmentX(CENTER_ALIGNMENT);

        JLabel loginSub = new JLabel("Sign in to your account");
        loginSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loginSub.setForeground(TEXT2);
        loginSub.setAlignmentX(CENTER_ALIGNMENT);

        header.add(lockIcon);
        header.add(Box.createVerticalStrut(8));
        header.add(loginTitle);
        header.add(Box.createVerticalStrut(4));
        header.add(loginSub);

        // Fields
        JPanel fields = new JPanel();
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));
        fields.setOpaque(false);

        fields.add(Box.createVerticalStrut(28));
        fields.add(inputLabel("USERNAME"));
        fields.add(Box.createVerticalStrut(6));
        usernameField = darkField("Enter your username");
        fields.add(usernameField);

        fields.add(Box.createVerticalStrut(18));
        fields.add(inputLabel("PASSWORD"));
        fields.add(Box.createVerticalStrut(6));
        passwordField = darkPasswordField("Enter your password");
        fields.add(passwordField);
        passwordField.addActionListener(e -> handleLogin());

        fields.add(Box.createVerticalStrut(28));

        // Login button
        loginButton = new JButton("SIGN IN →") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = isEnabled()
                        ? new GradientPaint(0,0,ACCENT,getWidth(),0,ACCENT2)
                        : new GradientPaint(0,0,BORDER,getWidth(),0,BORDER);
                g2.setPaint(gp); g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(Color.WHITE); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2-1);
                g2.dispose();
            }
        };
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(300, 46));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        loginButton.setAlignmentX(CENTER_ALIGNMENT);
        loginButton.setFocusPainted(false); loginButton.setBorderPainted(false); loginButton.setContentAreaFilled(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> handleLogin());
        fields.add(loginButton);

        fields.add(Box.createVerticalStrut(14));

        // Divider
        JPanel divider = new JPanel(new BorderLayout(8, 0));
        divider.setOpaque(false);
        JSeparator s1 = new JSeparator(); s1.setForeground(BORDER);
        JLabel or = new JLabel("OR"); or.setFont(new Font("Segoe UI", Font.BOLD, 11));
        or.setForeground(TEXT2); or.setHorizontalAlignment(SwingConstants.CENTER);
        JSeparator s2 = new JSeparator(); s2.setForeground(BORDER);
        divider.add(s1, BorderLayout.WEST); divider.add(or, BorderLayout.CENTER); divider.add(s2, BorderLayout.EAST);
        fields.add(divider);

        fields.add(Box.createVerticalStrut(14));

        // Register button
        JButton regBtn = new JButton("CREATE NEW ACCOUNT") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(99,102,241,40) : new Color(0,0,0,0));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(ACCENT); g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(ACCENT); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2-1);
                g2.dispose();
            }
        };
        regBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        regBtn.setPreferredSize(new Dimension(300, 40));
        regBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        regBtn.setAlignmentX(CENTER_ALIGNMENT);
        regBtn.setFocusPainted(false); regBtn.setBorderPainted(false); regBtn.setContentAreaFilled(false);
        regBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        regBtn.addActionListener(e -> { dispose(); new RegistrationScreen(); });
        fields.add(regBtn);

        // Demo hint
        fields.add(Box.createVerticalStrut(18));
        JLabel hint = new JLabel("Demo:  admin / admin123   or   member / member123");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hint.setForeground(new Color(99,102,241,180));
        hint.setAlignmentX(CENTER_ALIGNMENT);
        fields.add(hint);

        card.add(header, BorderLayout.NORTH);
        card.add(fields, BorderLayout.CENTER);
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
        l.setForeground(new Color(99,102,241));
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