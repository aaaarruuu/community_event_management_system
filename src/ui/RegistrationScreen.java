package ui;

import models.User;
import services.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class RegistrationScreen extends JFrame {

    private JTextField     usernameField, contactField, emailField, fullNameField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> roleCombo;
    private final UserService userService = new UserService();

    private Point dragStart;
    private boolean maximized = false;
    private Rectangle normalBounds;

    // Palette
    private static final Color BG       = new Color(10,  15,  30);
    private static final Color CARD_BG  = new Color(18,  26,  50);
    private static final Color ACCENT   = new Color(16,  185, 129);  // emerald
    private static final Color ACCENT2  = new Color(6,   182, 212);  // cyan
    private static final Color VIOLET   = new Color(139,  92, 246);
    private static final Color ROSE     = new Color(244,  63,  94);
    private static final Color TEXT     = new Color(226, 232, 240);
    private static final Color TEXT2    = new Color(148, 163, 184);
    private static final Color INPUT_BG = new Color(30,  41,  59);
    private static final Color BORDER   = new Color(51,  65,  85);

    public RegistrationScreen() {
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        setUndecorated(true);
        setSize(1020, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG); g2.fillRect(0,0,getWidth(),getHeight());
                RadialGradientPaint lg = new RadialGradientPaint(new Point(120,340), 350,
                        new float[]{0f,1f}, new Color[]{new Color(16,185,129,50), new Color(0,0,0,0)});
                g2.setPaint(lg); g2.fillRect(0,0,getWidth(),getHeight());
                RadialGradientPaint rg = new RadialGradientPaint(new Point(getWidth()-120,280), 300,
                        new float[]{0f,1f}, new Color[]{new Color(6,182,212,40), new Color(0,0,0,0)});
                g2.setPaint(rg); g2.fillRect(0,0,getWidth(),getHeight());
                // Dot grid
                g2.setColor(new Color(255,255,255,10));
                for (int x=0;x<getWidth();x+=36) for(int y=0;y<getHeight();y+=36) g2.fillOval(x-1,y-1,3,3);
                g2.dispose();
            }
        };
        root.setOpaque(false);
        root.add(buildTitleBar(), BorderLayout.NORTH);

        JPanel centre = new JPanel(new GridLayout(1,2,0,0));
        centre.setOpaque(false);
        centre.add(buildLeftPanel());
        centre.add(buildFormPanel());
        root.add(centre, BorderLayout.CENTER);

        setContentPane(root);
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(16,185,129,80), 1));
    }

    // ── Custom Title Bar ──────────────────────────────────────────────────────
    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(8,12,28));
        bar.setOpaque(true);
        bar.setPreferredSize(new Dimension(0,42));
        bar.setBorder(BorderFactory.createEmptyBorder(0,18,0,12));

        JLabel lbl = new JLabel("  Community Event Management System  –  New Registration");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12)); lbl.setForeground(TEXT2);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT,6,7));
        controls.setOpaque(false);
        controls.add(winBtn("–", new Color(245,158,11), e -> setState(JFrame.ICONIFIED)));
        controls.add(winBtn("⛶", new Color(16,185,129), e -> toggleMaximize()));
        controls.add(winBtn("✕", new Color(239,68,68),  e -> System.exit(0)));

        bar.add(lbl,      BorderLayout.WEST);
        bar.add(controls, BorderLayout.EAST);

        bar.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { dragStart = e.getPoint(); }
        });
        bar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (dragStart!=null && getExtendedState()!=JFrame.MAXIMIZED_BOTH) {
                    Point loc=getLocation();
                    setLocation(loc.x+e.getX()-dragStart.x, loc.y+e.getY()-dragStart.y);
                }
            }
        });
        return bar;
    }

    private JButton winBtn(String text, Color bg, ActionListener al) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()?bg:new Color(bg.getRed(),bg.getGreen(),bg.getBlue(),120));
                g2.fillOval(0,0,getWidth()-1,getHeight()-1);
                g2.setColor(Color.WHITE); g2.setFont(new Font("Segoe UI",Font.BOLD,11));
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(text,(getWidth()-fm.stringWidth(text))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(24,24));
        b.setFocusPainted(false);b.setBorderPainted(false);b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(al); return b;
    }

    private void toggleMaximize() {
        if (!maximized) { normalBounds=getBounds(); setExtendedState(JFrame.MAXIMIZED_BOTH); }
        else { setExtendedState(JFrame.NORMAL); if(normalBounds!=null) setBounds(normalBounds); }
        maximized=!maximized;
    }

    // ── Left Branding Panel ───────────────────────────────────────────────────
    private JPanel buildLeftPanel() {
        JPanel p = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                GradientPaint gp=new GradientPaint(0,0,new Color(10,30,25),getWidth(),getHeight(),new Color(5,20,40));
                g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.18f));
                g2.setColor(ACCENT);  g2.fill(new Ellipse2D.Float(-80,-80,300,300));
                g2.setColor(ACCENT2); g2.fill(new Ellipse2D.Float(getWidth()-160,getHeight()-160,280,280));
                g2.setColor(VIOLET);  g2.fill(new Ellipse2D.Float(getWidth()/2-70,getHeight()/2-70,180,180));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        GridBagConstraints gc=new GridBagConstraints();
        gc.gridx=0; gc.gridy=0; gc.anchor=GridBagConstraints.CENTER; gc.insets=new Insets(0,0,12,0);

        JLabel icon=new JLabel("✨"); icon.setFont(new Font("Segoe UI Emoji",Font.PLAIN,64)); icon.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(icon,gc);

        gc.gridy=1; gc.insets=new Insets(0,0,6,0);
        JLabel h1=new JLabel("Join Our"); h1.setFont(new Font("Segoe UI",Font.BOLD,34)); h1.setForeground(Color.WHITE); p.add(h1,gc);
        gc.gridy=2; gc.insets=new Insets(0,0,6,0);
        JLabel h2=new JLabel("Community"); h2.setFont(new Font("Segoe UI",Font.BOLD,34)); h2.setForeground(ACCENT); p.add(h2,gc);
        gc.gridy=3; gc.insets=new Insets(0,0,30,0);
        JLabel sub=new JLabel("Create your account in seconds"); sub.setFont(new Font("Segoe UI",Font.PLAIN,14)); sub.setForeground(TEXT2); p.add(sub,gc);

        // Steps
        gc.gridy=4; gc.insets=new Insets(0,0,10,0); p.add(stepBadge("1","Fill in your details",    ACCENT),  gc);
        gc.gridy=5;                                  p.add(stepBadge("2","Choose your role",        ACCENT2), gc);
        gc.gridy=6;                                  p.add(stepBadge("3","Submit & start managing", VIOLET),  gc);

        gc.gridy=7; gc.insets=new Insets(28,0,0,0);
        JLabel already=new JLabel("Already have an account?"); already.setFont(new Font("Segoe UI",Font.PLAIN,12)); already.setForeground(TEXT2); p.add(already,gc);
        gc.gridy=8; gc.insets=new Insets(4,0,0,0);
        JButton backBtn=outlineBtn("← Back to Login", ACCENT2); backBtn.addActionListener(e->{ dispose(); new LoginScreen(); }); p.add(backBtn,gc);

        return p;
    }

    private JPanel stepBadge(String num, String text, Color color) {
        JPanel row=new JPanel(new FlowLayout(FlowLayout.LEFT,12,0)); row.setOpaque(false);
        JLabel badge=new JLabel(num) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),200));
                g2.fillOval(0,0,getWidth()-1,getHeight()-1);
                g2.setColor(Color.WHITE); g2.setFont(new Font("Segoe UI",Font.BOLD,13));
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(num,(getWidth()-fm.stringWidth(num))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        badge.setPreferredSize(new Dimension(28,28)); badge.setOpaque(false);
        JLabel lbl=new JLabel(text); lbl.setFont(new Font("Segoe UI",Font.PLAIN,13)); lbl.setForeground(TEXT2);
        row.add(badge); row.add(lbl); return row;
    }

    private JButton outlineBtn(String text, Color color) {
        JButton b=new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                if(getModel().isRollover()) { g2.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),30)); g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10); }
                g2.setColor(color); g2.setStroke(new BasicStroke(1.5f)); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(color); g2.setFont(getFont());
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2-1);
                g2.dispose();
            }
        };
        b.setFont(new Font("Segoe UI",Font.BOLD,13));
        b.setPreferredSize(new Dimension(210,38));
        b.setFocusPainted(false);b.setBorderPainted(false);b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return b;
    }

    // ── Registration Form ─────────────────────────────────────────────────────
    private JPanel buildFormPanel() {
        JPanel wrap=new JPanel(new GridBagLayout()); wrap.setOpaque(false);

        JPanel card=new JPanel(new BorderLayout(0,0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,24,24);
                GradientPaint glow=new GradientPaint(0,0,ACCENT,getWidth(),0,ACCENT2);
                g2.setPaint(glow); g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(40,1,getWidth()-40,1);
                g2.setColor(new Color(255,255,255,15)); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,24,24);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(420,560));
        card.setBorder(BorderFactory.createEmptyBorder(28,36,28,36));

        // Card header
        JPanel cardHeader=new JPanel(); cardHeader.setLayout(new BoxLayout(cardHeader,BoxLayout.Y_AXIS)); cardHeader.setOpaque(false);
        JLabel formTitle=new JLabel("Create Account");
        formTitle.setFont(new Font("Segoe UI",Font.BOLD,24)); formTitle.setForeground(TEXT); formTitle.setAlignmentX(CENTER_ALIGNMENT);
        JLabel formSub=new JLabel("Fill in all fields to register");
        formSub.setFont(new Font("Segoe UI",Font.PLAIN,13)); formSub.setForeground(TEXT2); formSub.setAlignmentX(CENTER_ALIGNMENT);
        cardHeader.add(formTitle); cardHeader.add(Box.createVerticalStrut(4)); cardHeader.add(formSub);

        // Form fields
        JPanel form=new JPanel(new GridBagLayout()); form.setOpaque(false);
        GridBagConstraints gc=new GridBagConstraints();
        gc.fill=GridBagConstraints.HORIZONTAL; gc.insets=new Insets(5,0,5,0); gc.weightx=1.0;

        int row=0;
        // Row 1: Full Name | Username side by side
        gc.gridy=row; gc.gridx=0; gc.gridwidth=1; gc.weightx=0.5; gc.insets=new Insets(6,0,2,6);
        form.add(fLabel("👤  FULL NAME", ACCENT), gc);
        gc.gridx=1; gc.insets=new Insets(6,6,2,0);
        form.add(fLabel("🔑  USERNAME", ACCENT2), gc);
        row++;
        gc.gridy=row; gc.gridx=0; gc.insets=new Insets(0,0,6,6);
        fullNameField=dField(); form.add(fullNameField, gc);
        gc.gridx=1; gc.insets=new Insets(0,6,6,0);
        usernameField=dField(); form.add(usernameField, gc);
        row++;

        // Row 2: Password | Confirm
        gc.gridy=row; gc.gridx=0; gc.insets=new Insets(6,0,2,6);
        form.add(fLabel("🔒  PASSWORD", VIOLET), gc);
        gc.gridx=1; gc.insets=new Insets(6,6,2,0);
        form.add(fLabel("🔒  CONFIRM PASSWORD", VIOLET), gc);
        row++;
        gc.gridy=row; gc.gridx=0; gc.insets=new Insets(0,0,6,6);
        passwordField=dPwd(); form.add(passwordField, gc);
        gc.gridx=1; gc.insets=new Insets(0,6,6,0);
        confirmPasswordField=dPwd(); form.add(confirmPasswordField, gc);
        confirmPasswordField.addActionListener(e->doRegister());
        row++;

        // Row 3: Contact | Role
        gc.gridy=row; gc.gridx=0; gc.insets=new Insets(6,0,2,6);
        form.add(fLabel("📞  CONTACT", new Color(245,158,11)), gc);
        gc.gridx=1; gc.insets=new Insets(6,6,2,0);
        form.add(fLabel("👥  ROLE", new Color(244,63,94)), gc);
        row++;
        gc.gridy=row; gc.gridx=0; gc.insets=new Insets(0,0,6,6);
        contactField=dField(); form.add(contactField, gc);
        gc.gridx=1; gc.insets=new Insets(0,6,6,0);
        roleCombo=new JComboBox<>(new String[]{"MEMBER","ADMIN"});
        roleCombo.setBackground(INPUT_BG); roleCombo.setForeground(TEXT);
        roleCombo.setFont(new Font("Segoe UI",Font.PLAIN,13));
        form.add(roleCombo, gc);
        row++;

        // Row 4: Email (full width)
        gc.gridy=row; gc.gridx=0; gc.gridwidth=2; gc.insets=new Insets(6,0,2,0);
        form.add(fLabel("📧  EMAIL ADDRESS", ACCENT), gc);
        row++;
        gc.gridy=row; gc.insets=new Insets(0,0,6,0);
        emailField=dField(); form.add(emailField, gc);
        row++;

        // Buttons row
        gc.gridy=row; gc.insets=new Insets(16,0,0,0); gc.gridwidth=2;
        JButton regBtn=new JButton("CREATE ACCOUNT  ✓") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp=new GradientPaint(0,0,ACCENT,getWidth(),0,ACCENT2);
                g2.setPaint(gp); g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(Color.WHITE); g2.setFont(getFont());
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2-1);
                g2.dispose();
            }
        };
        regBtn.setFont(new Font("Segoe UI",Font.BOLD,14));
        regBtn.setForeground(Color.WHITE); regBtn.setPreferredSize(new Dimension(200,44));
        regBtn.setFocusPainted(false);regBtn.setBorderPainted(false);regBtn.setContentAreaFilled(false);
        regBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        regBtn.addActionListener(e->doRegister());
        form.add(regBtn,gc);
        row++;
        gc.gridy=row; gc.insets=new Insets(8,0,0,0);
        JButton clearBtn=outlineBtn("Clear Fields", ROSE); clearBtn.addActionListener(e->clearFields());
        form.add(clearBtn,gc);

        JPanel formWrap=new JPanel(new BorderLayout(0,16)); formWrap.setOpaque(false);
        formWrap.add(form, BorderLayout.CENTER);

        card.add(cardHeader, BorderLayout.NORTH);
        card.add(formWrap,   BorderLayout.CENTER);
        wrap.add(card);
        return wrap;
    }

    // ── Registration Logic ────────────────────────────────────────────────────
    private void doRegister() {
        String fullName=fullNameField.getText().trim(), username=usernameField.getText().trim();
        String password=new String(passwordField.getPassword()).trim();
        String confirm=new String(confirmPasswordField.getPassword()).trim();
        String contact=contactField.getText().trim(), email=emailField.getText().trim();
        String role=(String)roleCombo.getSelectedItem();

        if(fullName.isEmpty())          {error("Full name is required!");              fullNameField.requestFocus(); return;}
        if(username.isEmpty())          {error("Username is required!");               usernameField.requestFocus(); return;}
        if(username.length()<4)         {error("Username must be ≥ 4 characters!");   usernameField.requestFocus(); return;}
        if(password.isEmpty())          {error("Password is required!");               passwordField.requestFocus(); return;}
        if(password.length()<6)         {error("Password must be ≥ 6 characters!");   passwordField.requestFocus(); return;}
        if(!password.equals(confirm))   {error("Passwords do not match!");             confirmPasswordField.requestFocus(); return;}
        if(contact.isEmpty())           {error("Contact number is required!");         contactField.requestFocus(); return;}
        if(!contact.matches("\\d{10}")) {error("Contact must be exactly 10 digits!"); contactField.requestFocus(); return;}
        if(email.isEmpty())             {error("Email is required!");                  emailField.requestFocus(); return;}
        if(!email.matches("^[A-Za-z0-9+_.\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$")) {
            error("Enter a valid email address!"); emailField.requestFocus(); return;}
        if(userService.usernameExists(username)) {error("Username already taken!"); usernameField.requestFocus(); return;}

        User u=new User(username,password,fullName,email,contact,role);
        if(userService.registerUser(u,password)) {
            JOptionPane.showMessageDialog(this,"✅ Registration Successful!\n\nUsername: "+username+"\nRole: "+role+"\n\nYou can now log in.","Welcome!",JOptionPane.INFORMATION_MESSAGE);
            dispose(); new LoginScreen();
        } else {
            error("Registration failed. Please try again.");
        }
    }

    private void clearFields() {
        fullNameField.setText(""); usernameField.setText(""); passwordField.setText("");
        confirmPasswordField.setText(""); contactField.setText(""); emailField.setText("");
        roleCombo.setSelectedIndex(0); fullNameField.requestFocus();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private JLabel fLabel(String text, Color color) {
        JLabel l=new JLabel(text); l.setFont(new Font("Segoe UI",Font.BOLD,10)); l.setForeground(color); return l;
    }

    private JTextField dField() {
        JTextField f=new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(INPUT_BG); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(isFocusOwner()?ACCENT:BORDER); g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        f.setOpaque(false); f.setForeground(TEXT); f.setCaretColor(ACCENT);
        f.setFont(new Font("Segoe UI",Font.PLAIN,13));
        f.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
        f.setPreferredSize(new Dimension(0,38)); return f;
    }

    private JPasswordField dPwd() {
        JPasswordField f=new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(INPUT_BG); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(isFocusOwner()?VIOLET:BORDER); g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        f.setOpaque(false); f.setForeground(TEXT); f.setCaretColor(VIOLET);
        f.setFont(new Font("Segoe UI",Font.PLAIN,13));
        f.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
        f.setPreferredSize(new Dimension(0,38)); return f;
    }

    private void error(String msg) { JOptionPane.showMessageDialog(this,msg,"Validation Error",JOptionPane.WARNING_MESSAGE); }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(RegistrationScreen::new);
    }
}