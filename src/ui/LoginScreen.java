package ui;

import database.DBConnection;
import models.User;
import services.UserService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * LoginScreen — Deep navy + cyan/violet theme
 * Username & password panels are perfectly aligned with consistent width.
 * Replace src/ui/LoginScreen.java with this file.
 */
public class LoginScreen extends JFrame {

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JButton        loginButton;
    private JLabel         clockLabel;
    private final UserService userService = new UserService();
    private Point dragStart;
    private boolean maximized = false;
    private Rectangle normalBounds;

    // ── Palette: deep navy + cyan + violet ────────────────────────────────────
    private static final Color BG_DEEP  = new Color(  8,  10,  24);
    private static final Color BG_LEFT  = new Color( 12,  16,  38);
    private static final Color BG_CARD  = new Color( 16,  22,  52);
    private static final Color BG_INPUT = new Color( 22,  32,  68);
    private static final Color CYAN     = new Color(  6, 182, 212);
    private static final Color CYAN2    = new Color( 34, 211, 238);
    private static final Color VIOLET   = new Color(139,  92, 246);
    private static final Color EMERALD  = new Color( 16, 185, 129);
    private static final Color ROSE     = new Color(244,  63,  94);
    private static final Color AMBER    = new Color(245, 158,  11);
    private static final Color TEXT_W   = new Color(241, 245, 249);
    private static final Color TEXT_M   = new Color(148, 163, 184);
    private static final Color BORDER_D = new Color( 38,  54,  96);

    // Particles
    private final float[] px = new float[28];
    private final float[] py = new float[28];
    private final float[] pspd = new float[28];
    private Timer particleTimer;

    public LoginScreen() {
        for (int i=0;i<28;i++) { px[i]=(float)(Math.random()*1020); py[i]=(float)(Math.random()*580); pspd[i]=0.15f+(float)(Math.random()*0.3f); }
        buildUI();
        startClock();
        startParticles();
        setVisible(true);
    }

    private void startClock() {
        new Timer(1000, e -> { if(clockLabel!=null) clockLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH : mm : ss"))); }).start();
    }

    private void startParticles() {
        particleTimer = new Timer(45, e -> {
            for (int i=0;i<28;i++) { py[i]-=pspd[i]; if(py[i]<0){py[i]=580;px[i]=(float)(Math.random()*1020);} }
            if (getContentPane()!=null) getContentPane().repaint();
        });
        particleTimer.start();
    }

    // ── Root layout ───────────────────────────────────────────────────────────
    private void buildUI() {
        setUndecorated(true); setSize(1020,620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_DEEP); g2.fillRect(0,0,getWidth(),getHeight());
                RadialGradientPaint gl=new RadialGradientPaint(new Point(190,310),370,new float[]{0f,1f},new Color[]{new Color(139,92,246,50),new Color(0,0,0,0)});
                g2.setPaint(gl); g2.fillRect(0,0,getWidth(),getHeight());
                RadialGradientPaint gr=new RadialGradientPaint(new Point(840,260),300,new float[]{0f,1f},new Color[]{new Color(6,182,212,38),new Color(0,0,0,0)});
                g2.setPaint(gr); g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(new Color(6,182,212,45));
                for (int i=0;i<28;i++) g2.fillOval((int)px[i],(int)py[i],2+(i%3),2+(i%3));
                g2.setColor(new Color(255,255,255,4));
                for (int x=0;x<getWidth();x+=52) g2.drawLine(x,0,x,getHeight());
                for (int y=0;y<getHeight();y+=52) g2.drawLine(0,y,getWidth(),y);
                g2.dispose();
            }
        };
        root.setOpaque(true);
        root.add(buildTitleBar(), BorderLayout.NORTH);
        JPanel centre=new JPanel(new GridLayout(1,2,0,0)); centre.setOpaque(false);
        centre.add(buildLeft()); centre.add(buildCard());
        root.add(centre, BorderLayout.CENTER);
        setContentPane(root);
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(CYAN.getRed(),CYAN.getGreen(),CYAN.getBlue(),65),1));
    }

    // ── Title bar ─────────────────────────────────────────────────────────────
    private JPanel buildTitleBar() {
        JPanel bar=new JPanel(new BorderLayout());
        bar.setBackground(new Color(6,8,20)); bar.setOpaque(true);
        bar.setPreferredSize(new Dimension(0,42)); bar.setBorder(BorderFactory.createEmptyBorder(0,20,0,14));

        JPanel left=new JPanel(new FlowLayout(FlowLayout.LEFT,8,0)); left.setOpaque(false);
        JLabel dot=new JLabel(){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);GradientPaint gp=new GradientPaint(0,0,CYAN,getWidth(),getHeight(),VIOLET);g2.setPaint(gp);g2.fillOval(0,0,getWidth()-1,getHeight()-1);g2.dispose();}};
        dot.setPreferredSize(new Dimension(10,10));
        JLabel appLbl=new JLabel("COMMUNITY EVENT MANAGEMENT SYSTEM");
        appLbl.setFont(new Font("Segoe UI",Font.BOLD,11)); appLbl.setForeground(new Color(90,120,175));
        left.add(dot); left.add(appLbl);

        clockLabel=new JLabel(LocalTime.now().format(DateTimeFormatter.ofPattern("HH : mm : ss")));
        clockLabel.setFont(new Font("Consolas",Font.BOLD,13)); clockLabel.setForeground(CYAN); clockLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel ctrl=new JPanel(new FlowLayout(FlowLayout.RIGHT,6,10)); ctrl.setOpaque(false);
        ctrl.add(wBtn("–",AMBER,e->setState(JFrame.ICONIFIED)));
        ctrl.add(wBtn("□",EMERALD,e->toggleMax()));
        ctrl.add(wBtn("×",ROSE,e->System.exit(0)));

        bar.add(left,BorderLayout.WEST); bar.add(clockLabel,BorderLayout.CENTER); bar.add(ctrl,BorderLayout.EAST);

        MouseAdapter drag=new MouseAdapter(){
            @Override public void mousePressed(MouseEvent e){dragStart=e.getPoint();}
            @Override public void mouseDragged(MouseEvent e){if(dragStart!=null&&getExtendedState()!=JFrame.MAXIMIZED_BOTH){Point loc=getLocation();setLocation(loc.x+e.getX()-dragStart.x,loc.y+e.getY()-dragStart.y);}}
        };
        bar.addMouseListener(drag); bar.addMouseMotionListener(drag);

        JPanel wrap=new JPanel(new BorderLayout()); wrap.setOpaque(false); wrap.add(bar,BorderLayout.CENTER);
        JPanel sep=new JPanel(){
            @Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();GradientPaint a=new GradientPaint(0,0,new Color(0,0,0,0),getWidth()/2,0,CYAN);g2.setPaint(a);g2.fillRect(0,0,getWidth()/2,1);GradientPaint b=new GradientPaint(getWidth()/2,0,CYAN,getWidth(),0,new Color(0,0,0,0));g2.setPaint(b);g2.fillRect(getWidth()/2,0,getWidth()/2,1);g2.dispose();}
        };
        sep.setPreferredSize(new Dimension(0,1)); sep.setOpaque(false); wrap.add(sep,BorderLayout.SOUTH);
        return wrap;
    }

    private JButton wBtn(String txt,Color col,ActionListener al){
        JButton b=new JButton(txt){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(getModel().isRollover()?col:new Color(col.getRed(),col.getGreen(),col.getBlue(),100));g2.fillOval(0,0,getWidth()-1,getHeight()-1);g2.setColor(Color.WHITE);g2.setFont(new Font("Segoe UI",Font.BOLD,10));FontMetrics fm=g2.getFontMetrics();g2.drawString(txt,(getWidth()-fm.stringWidth(txt))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);g2.dispose();}};
        b.setPreferredSize(new Dimension(22,22));b.setFocusPainted(false);b.setBorderPainted(false);b.setContentAreaFilled(false);b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));b.addActionListener(al);return b;
    }

    private void toggleMax(){if(!maximized){normalBounds=getBounds();setExtendedState(JFrame.MAXIMIZED_BOTH);}else{setExtendedState(JFrame.NORMAL);if(normalBounds!=null)setBounds(normalBounds);}maximized=!maximized;}

    // ── Left panel ────────────────────────────────────────────────────────────
    private JPanel buildLeft(){
        JPanel p=new JPanel(new GridBagLayout()){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp=new GradientPaint(0,0,BG_LEFT,getWidth(),getHeight(),new Color(10,14,34));g2.setPaint(gp);g2.fillRect(0,0,getWidth(),getHeight());
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.10f));g2.setColor(CYAN);g2.setStroke(new BasicStroke(38f));g2.drawOval(-80,-80,260,260);
                g2.setColor(VIOLET);g2.drawOval(getWidth()-160,getHeight()-160,260,260);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
                GradientPaint d1=new GradientPaint(getWidth()-1,0,new Color(CYAN.getRed(),CYAN.getGreen(),CYAN.getBlue(),0),getWidth()-1,getHeight()/2,new Color(CYAN.getRed(),CYAN.getGreen(),CYAN.getBlue(),70));
                g2.setPaint(d1);g2.fillRect(getWidth()-1,0,1,getHeight()/2);
                GradientPaint d2=new GradientPaint(getWidth()-1,getHeight()/2,new Color(CYAN.getRed(),CYAN.getGreen(),CYAN.getBlue(),70),getWidth()-1,getHeight(),new Color(CYAN.getRed(),CYAN.getGreen(),CYAN.getBlue(),0));
                g2.setPaint(d2);g2.fillRect(getWidth()-1,getHeight()/2,1,getHeight()/2);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        GridBagConstraints gc=new GridBagConstraints(); gc.gridx=0; gc.anchor=GridBagConstraints.CENTER;

        // Logo
        JPanel logo=new JPanel(new GridBagLayout()){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);GradientPaint gp=new GradientPaint(0,0,CYAN,getWidth(),getHeight(),VIOLET);g2.setPaint(gp);g2.fillOval(0,0,getWidth()-1,getHeight()-1);g2.setColor(new Color(255,255,255,30));g2.setStroke(new BasicStroke(3f));g2.drawOval(5,5,getWidth()-11,getHeight()-11);g2.setColor(new Color(8,10,24));g2.setFont(new Font("Segoe UI",Font.BOLD,36));FontMetrics fm=g2.getFontMetrics();g2.drawString("C",(getWidth()-fm.stringWidth("C"))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2-2);g2.dispose();}};
        logo.setPreferredSize(new Dimension(86,86)); logo.setOpaque(false);
        gc.gridy=0; gc.insets=new Insets(0,0,18,0); p.add(logo,gc);

        gc.gridy=1; gc.insets=new Insets(0,0,4,0);
        JLabel n1=new JLabel("COMMUNITY EVENT"); n1.setFont(new Font("Segoe UI",Font.BOLD,27)); n1.setForeground(TEXT_W); p.add(n1,gc);
        gc.gridy=2; gc.insets=new Insets(0,0,4,0);
        JLabel n2=new JLabel("MANAGEMENT SYSTEM"); n2.setFont(new Font("Segoe UI",Font.BOLD,16)); n2.setForeground(CYAN); p.add(n2,gc);
        gc.gridy=3; gc.insets=new Insets(0,0,28,0);
        JLabel tm=new JLabel("VIT Bhopal  ·  MCA 2026  ·  Team 07"); tm.setFont(new Font("Segoe UI",Font.PLAIN,12)); tm.setForeground(TEXT_M); p.add(tm,gc);

        String[] labs={"Event Scheduling","Issue Tracking","Rep Management","Notices & Polls"};
        Color[] cols={CYAN,ROSE,EMERALD,VIOLET};
        for(int i=0;i<4;i++){gc.gridy=4+i;gc.insets=new Insets(0,0,9,0);p.add(pill(labs[i],cols[i]),gc);}
        return p;
    }

    private JPanel pill(String text,Color col){
        JPanel p=new JPanel(new FlowLayout(FlowLayout.CENTER,10,0)){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(new Color(col.getRed(),col.getGreen(),col.getBlue(),18));g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);g2.setColor(new Color(col.getRed(),col.getGreen(),col.getBlue(),75));g2.setStroke(new BasicStroke(1f));g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);g2.dispose();}};
        p.setOpaque(false);p.setPreferredSize(new Dimension(225,33));
        JLabel dot=new JLabel(){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(col);g2.fillOval(0,2,8,8);g2.dispose();}};
        dot.setPreferredSize(new Dimension(10,12));
        JLabel lbl=new JLabel(text);lbl.setFont(new Font("Segoe UI",Font.BOLD,13));lbl.setForeground(col);
        p.add(dot);p.add(lbl);return p;
    }

    // ── Login card ────────────────────────────────────────────────────────────
    private JPanel buildCard(){
        JPanel wrap=new JPanel(new GridBagLayout()); wrap.setOpaque(false);

        // Card panel with custom paint
        JPanel card=new JPanel(null){ // null layout — we position inner panel manually
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,20,20);
                GradientPaint l1=new GradientPaint(30,0,new Color(0,0,0,0),getWidth()/2,0,CYAN);g2.setPaint(l1);g2.setStroke(new BasicStroke(2f));g2.drawLine(30,1,getWidth()/2,1);
                GradientPaint l2=new GradientPaint(getWidth()/2,0,CYAN,getWidth()-30,0,new Color(0,0,0,0));g2.setPaint(l2);g2.drawLine(getWidth()/2,1,getWidth()-30,1);
                g2.setColor(new Color(255,255,255,10));g2.setStroke(new BasicStroke(1f));g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,20,20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(390,510));

        // Inner panel uses BoxLayout for a perfectly aligned single column
        JPanel inner=new JPanel();
        inner.setLayout(new BoxLayout(inner,BoxLayout.Y_AXIS));
        inner.setOpaque(false);
        // Position inner inside card with padding
        inner.setBounds(0,0,390,510);
        card.add(inner);
        card.addComponentListener(new ComponentAdapter(){
            @Override public void componentResized(ComponentEvent e){inner.setBounds(0,0,card.getWidth(),card.getHeight());}
        });
        inner.setBorder(BorderFactory.createEmptyBorder(34,42,30,42));

        // ── Header ────────────────────────────────────────────────────────────
        JLabel ico=lockIcon(); ico.setAlignmentX(CENTER_ALIGNMENT); inner.add(ico);
        inner.add(Box.createVerticalStrut(12));

        JLabel title=new JLabel("Welcome Back");
        title.setFont(new Font("Segoe UI",Font.BOLD,26));title.setForeground(TEXT_W);title.setAlignmentX(CENTER_ALIGNMENT);inner.add(title);
        inner.add(Box.createVerticalStrut(4));

        JLabel sub=new JLabel("Sign in to your community portal");
        sub.setFont(new Font("Segoe UI",Font.PLAIN,13));sub.setForeground(TEXT_M);sub.setAlignmentX(CENTER_ALIGNMENT);inner.add(sub);
        inner.add(Box.createVerticalStrut(26));

        // ── USERNAME ──────────────────────────────────────────────────────────
        JLabel ulbl=flabel("USERNAME"); ulbl.setAlignmentX(LEFT_ALIGNMENT); inner.add(ulbl);
        inner.add(Box.createVerticalStrut(6));

        usernameField=makeField(false);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE,48));
        usernameField.setAlignmentX(CENTER_ALIGNMENT);
        usernameField.addActionListener(e->handleLogin());
        inner.add(usernameField);
        inner.add(Box.createVerticalStrut(16));

        // ── PASSWORD ──────────────────────────────────────────────────────────
        JLabel plbl=flabel("PASSWORD"); plbl.setAlignmentX(CENTER_ALIGNMENT); inner.add(plbl);
        inner.add(Box.createVerticalStrut(6));

        // Password row = password field + show/hide button at same height
        JPanel pwRow=new JPanel(new BorderLayout(0,0));
        pwRow.setOpaque(false);
        pwRow.setMaximumSize(new Dimension(Integer.MAX_VALUE,48));
        pwRow.setAlignmentX(CENTER_ALIGNMENT);

        passwordField=makePwd();
        passwordField.addActionListener(e->handleLogin());

        JButton eye=new JButton("SHOW"){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_INPUT);g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(BORDER_D);g2.setStroke(new BasicStroke(1.5f));g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(CYAN);g2.setFont(new Font("Segoe UI",Font.BOLD,10));
                FontMetrics fm=g2.getFontMetrics();String t=getText();
                g2.drawString(t,(getWidth()-fm.stringWidth(t))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);g2.dispose();
            }
        };
        eye.setPreferredSize(new Dimension(58,48));eye.setFocusPainted(false);eye.setBorderPainted(false);eye.setContentAreaFilled(false);
        eye.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eye.addActionListener(e->{
            if(passwordField.getEchoChar()!=(char)0){passwordField.setEchoChar((char)0);eye.setText("HIDE");}
            else{passwordField.setEchoChar('●');eye.setText("SHOW");}eye.repaint();
        });
        pwRow.add(passwordField,BorderLayout.CENTER);pwRow.add(eye,BorderLayout.EAST);
        inner.add(pwRow);
        inner.add(Box.createVerticalStrut(24));

        // ── SIGN IN BUTTON ────────────────────────────────────────────────────
        loginButton=new JButton("SIGN  IN"){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                if(!isEnabled()){g2.setColor(new Color(38,54,96));g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);}
                else{GradientPaint gp=new GradientPaint(0,0,getModel().isRollover()?CYAN2:CYAN,getWidth(),0,VIOLET);g2.setPaint(gp);g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.17f));g2.setColor(Color.WHITE);g2.fillRoundRect(4,2,getWidth()-8,getHeight()/2-2,8,8);}
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
                g2.setColor(new Color(8,10,24));g2.setFont(new Font("Segoe UI",Font.BOLD,15));
                FontMetrics fm=g2.getFontMetrics();String t=getText();g2.drawString(t,(getWidth()-fm.stringWidth(t))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2-1);g2.dispose();
            }
        };
        loginButton.setFont(new Font("Segoe UI",Font.BOLD,15));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE,48));loginButton.setAlignmentX(LEFT_ALIGNMENT);
        loginButton.setFocusPainted(false);loginButton.setBorderPainted(false);loginButton.setContentAreaFilled(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e->handleLogin());
        inner.add(loginButton);
        inner.add(Box.createVerticalStrut(14));

        // ── OR divider ────────────────────────────────────────────────────────
        JPanel div=new JPanel(new BorderLayout(8,0));div.setOpaque(false);div.setMaximumSize(new Dimension(Integer.MAX_VALUE,16));div.setAlignmentX(LEFT_ALIGNMENT);
        JPanel dl=hLine();JPanel dr=hLine();
        JLabel or=new JLabel("OR");or.setFont(new Font("Segoe UI",Font.BOLD,10));or.setForeground(TEXT_M);or.setHorizontalAlignment(SwingConstants.CENTER);
        div.add(dl,BorderLayout.WEST);div.add(or,BorderLayout.CENTER);div.add(dr,BorderLayout.EAST);
        inner.add(div);inner.add(Box.createVerticalStrut(14));

        // ── REGISTER button ───────────────────────────────────────────────────
        JButton reg=new JButton("CREATE NEW ACCOUNT"){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                if(getModel().isRollover()){g2.setColor(new Color(CYAN.getRed(),CYAN.getGreen(),CYAN.getBlue(),22));g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);}
                g2.setColor(new Color(CYAN.getRed(),CYAN.getGreen(),CYAN.getBlue(),95));g2.setStroke(new BasicStroke(1.5f));g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                g2.setColor(CYAN);g2.setFont(getFont());FontMetrics fm=g2.getFontMetrics();String t=getText();
                g2.drawString(t,(getWidth()-fm.stringWidth(t))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2-1);g2.dispose();
            }
        };
        reg.setFont(new Font("Segoe UI",Font.BOLD,12));
        reg.setMaximumSize(new Dimension(Integer.MAX_VALUE,42));reg.setAlignmentX(LEFT_ALIGNMENT);
        reg.setFocusPainted(false);reg.setBorderPainted(false);reg.setContentAreaFilled(false);
        reg.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        reg.addActionListener(e->{dispose();new RegistrationScreen();});
        inner.add(reg);inner.add(Box.createVerticalStrut(16));

        JLabel hint=new JLabel("Demo:  aryan / aryan123   (Admin)");
        hint.setFont(new Font("Segoe UI",Font.PLAIN,10));
        hint.setForeground(new Color(CYAN.getRed(),CYAN.getGreen(),CYAN.getBlue(),100));
        hint.setAlignmentX(CENTER_ALIGNMENT);inner.add(hint);

        wrap.add(card);return wrap;
    }

    private JLabel lockIcon(){
        JLabel l=new JLabel(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                int cx=getWidth()/2,cy=getHeight()/2,r=22;
                GradientPaint gp=new GradientPaint(cx-r,cy-r,CYAN,cx+r,cy+r,VIOLET);g2.setPaint(gp);g2.fillOval(cx-r,cy-r,r*2,r*2);
                g2.setColor(Color.WHITE);g2.setStroke(new BasicStroke(2.2f));
                g2.drawRoundRect(cx-7,cy-1,14,11,3,3);g2.drawArc(cx-5,cy-9,10,12,0,180);g2.fillOval(cx-2,cy+3,4,4);g2.dispose();
            }
        };
        l.setPreferredSize(new Dimension(52,52));l.setMaximumSize(new Dimension(52,52));return l;
    }

    private JPanel hLine(){
        return new JPanel(){
            @Override protected void paintComponent(Graphics g){g.setColor(new Color(48,66,108));g.fillRect(0,getHeight()/2,getWidth(),1);}
            @Override public Dimension getPreferredSize(){return new Dimension(55,14);}
        };
    }

    private JLabel flabel(String txt){
        JLabel l=new JLabel(txt);l.setFont(new Font("Segoe UI",Font.BOLD,10));l.setForeground(CYAN);return l;
    }

    private JTextField makeField(boolean pwd){
        return new JTextField(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_INPUT);g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(isFocusOwner()?CYAN:BORDER_D);g2.setStroke(new BasicStroke(isFocusOwner()?2f:1.5f));g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                if(isFocusOwner()){g2.setColor(new Color(CYAN.getRed(),CYAN.getGreen(),CYAN.getBlue(),14));g2.fillRoundRect(1,1,getWidth()-3,getHeight()-3,9,9);}
                g2.dispose();super.paintComponent(g);
            }
        };
    }

    private JPasswordField makePwd(){
        JPasswordField f=new JPasswordField(){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_INPUT);g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(isFocusOwner()?CYAN:BORDER_D);g2.setStroke(new BasicStroke(isFocusOwner()?2f:1.5f));g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                if(isFocusOwner()){g2.setColor(new Color(CYAN.getRed(),CYAN.getGreen(),CYAN.getBlue(),14));g2.fillRoundRect(1,1,getWidth()-3,getHeight()-3,9,9);}
                g2.dispose();super.paintComponent(g);
            }
        };
        f.setEchoChar('●');f.setOpaque(false);f.setForeground(TEXT_W);f.setCaretColor(CYAN);
        f.setFont(new Font("Segoe UI",Font.PLAIN,14));f.setBorder(BorderFactory.createEmptyBorder(11,14,11,14));return f;
    }

    // ── Login logic ───────────────────────────────────────────────────────────
    private void handleLogin(){
        String user=usernameField.getText().trim();String pass=new String(passwordField.getPassword());
        if(user.isEmpty()||pass.isEmpty()){shake(loginButton);JOptionPane.showMessageDialog(this,"Please enter both username and password!","Missing Fields",JOptionPane.WARNING_MESSAGE);return;}
        loginButton.setEnabled(false);loginButton.setText("AUTHENTICATING...");loginButton.repaint();
        new SwingWorker<User,Void>(){
            @Override protected User doInBackground(){return userService.authenticate(user,pass);}
            @Override protected void done(){
                try{User u=get();if(u!=null){if(particleTimer!=null)particleTimer.stop();new MainDashboard(u);dispose();}
                else{shake(loginButton);JOptionPane.showMessageDialog(LoginScreen.this,"Invalid username or password!\n\nTry: aryan / aryan123","Login Failed",JOptionPane.ERROR_MESSAGE);passwordField.setText("");usernameField.requestFocus();}}
                catch(Exception ex){JOptionPane.showMessageDialog(LoginScreen.this,"Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
                finally{loginButton.setEnabled(true);loginButton.setText("SIGN  IN");loginButton.repaint();}
            }
        }.execute();
    }

    private void shake(Component c){
        Point orig=c.getLocation();Timer t=new Timer(28,null);int[]off={10,-10,8,-8,5,-5,3,-3,0};int[]idx={0};
        t.addActionListener(e->{if(idx[0]<off.length)c.setLocation(orig.x+off[idx[0]++],orig.y);else{c.setLocation(orig);t.stop();}});t.start();
    }

    // ── Field styling ─────────────────────────────────────────────────────────
    {
        usernameField = makeField(false);
        usernameField.setOpaque(false); usernameField.setForeground(TEXT_W); usernameField.setCaretColor(CYAN);
        usernameField.setFont(new Font("Segoe UI",Font.PLAIN,14));
        usernameField.setBorder(BorderFactory.createEmptyBorder(11,14,11,14));
    }

    public static void main(String[] args){
        try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch(Exception ignored){}
        if(!DBConnection.testConnection()){JOptionPane.showMessageDialog(null,"Cannot connect to database!\n\n1. MySQL is running\n2. Database 'community_event_db' exists\n3. Check DBConnection.java","DB Error",JOptionPane.ERROR_MESSAGE);System.exit(1);}
        SwingUtilities.invokeLater(LoginScreen::new);
    }
}