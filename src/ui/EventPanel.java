package ui;

import database.DBConnection;
import models.User;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;

public class EventPanel extends JPanel {

    private final User currentUser;
    private JTable            eventsTable;
    private DefaultTableModel tableModel;
    private JTextField        searchField;
    private JComboBox<String> filterCombo;
    private JLabel            roleInfoLabel;

    // Column indices
    private static final int COL_ID       = 0;
    private static final int COL_NAME     = 1;
    private static final int COL_CAT      = 2;
    private static final int COL_DATE     = 3;
    private static final int COL_VENUE    = 4;
    private static final int COL_OWNER_ID = 5;   // hidden
    private static final int COL_STATUS   = 6;
    private static final int COL_DESC     = 7;   // hidden (used in view dialog)

    // ── Soft warm-white + teal palette ────────────────────────────────────────
    // Background layers
    private static final Color BG_PAGE    = new Color(246, 248, 250);  // very light warm grey
    private static final Color BG_CARD    = new Color(255, 255, 255);  // white card
    private static final Color BG_HDR     = new Color(250, 252, 255);  // table header bg
    private static final Color BG_ROW_E   = new Color(255, 255, 255);  // even row
    private static final Color BG_ROW_O   = new Color(247, 250, 253);  // odd row  (barely different)
    private static final Color BG_ROW_ME  = new Color(236, 252, 243);  // "my events" row – soft green
    private static final Color BG_SEL     = new Color(219, 234, 254);  // selected row (soft blue)
    private static final Color BG_INPUT   = new Color(250, 251, 253);  // input bg

    // Teal / slate accent
    private static final Color TEAL1      = new Color( 13, 148, 136);  // primary teal
    private static final Color TEAL2      = new Color( 20, 184, 166);  // lighter teal
    private static final Color SLATE      = new Color( 71,  85, 105);  // medium slate
    private static final Color SLATE_LT   = new Color(148, 163, 184);  // light slate (muted text)
    private static final Color NAVY       = new Color( 30,  58, 138);  // deep navy (IDs / accents)

    // Action colours (softer than original)
    private static final Color C_ADD      = new Color( 13, 148, 136);  // teal – add
    private static final Color C_VIEW     = new Color( 59, 130, 246);  // blue – view
    private static final Color C_EDIT     = new Color(234, 128,  10);  // warm amber – edit
    private static final Color C_DELETE   = new Color(220,  55,  55);  // red – delete
    private static final Color C_MEMBER   = new Color( 22, 163, 74);   // green – member actions
    private static final Color C_REFRESH  = new Color( 99, 115, 140);  // slate – refresh

    // Text colours
    private static final Color TEXT_DARK  = new Color( 30,  40,  55);
    private static final Color TEXT_MED   = new Color( 80, 100, 130);
    private static final Color TEXT_LIGHT = new Color(140, 155, 175);

    // Border
    private static final Color BORDER     = new Color(220, 228, 240);
    private static final Color BORDER_FCS = new Color( 13, 148, 136);  // focused = teal

    // ── Constructor ───────────────────────────────────────────────────────────
    public EventPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_PAGE);
        initComponents();
        loadEvents();
    }

    // ── Layout ────────────────────────────────────────────────────────────────
    private void initComponents() {
        add(buildTitleBar(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 10));
        body.setBackground(BG_PAGE);
        body.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setBackground(BG_PAGE);
        top.add(buildRoleBanner(), BorderLayout.NORTH);
        top.add(buildSearchBar(),  BorderLayout.CENTER);

        body.add(top,           BorderLayout.NORTH);
        body.add(buildTable(),  BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    // ── Title Bar ─────────────────────────────────────────────────────────────
    private JPanel buildTitleBar() {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0,0, new Color(8,105,95), getWidth(),0, new Color(13,148,136));
                g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());
                // Subtle texture: semi-transparent wave
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.08f));
                g2.setColor(Color.WHITE);
                g2.fill(new Ellipse2D.Float(getWidth()-180,-50,240,200));
                g2.fill(new Ellipse2D.Float(-40,-20,120,120));
                // Bottom shadow line
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.18f));
                g2.setColor(new Color(0,60,55)); g2.fillRect(0,getHeight()-1,getWidth(),1);
                g2.dispose();
            }
        };
        p.setOpaque(true); p.setPreferredSize(new Dimension(0,74));
        p.setBorder(BorderFactory.createEmptyBorder(0,22,0,20));

        // Left: badge + text
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT,12,0)); left.setOpaque(false);

        JPanel badge = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,30)); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(new Color(255,255,255,60)); g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(Color.WHITE); g2.setFont(new Font("Segoe UI",Font.BOLD,22));
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString("E",(getWidth()-fm.stringWidth("E"))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2-1);
                g2.dispose();
            }
        };
        badge.setPreferredSize(new Dimension(42,42)); badge.setOpaque(false);

        JPanel titleBlock = new JPanel(); titleBlock.setLayout(new BoxLayout(titleBlock,BoxLayout.Y_AXIS)); titleBlock.setOpaque(false);
        JLabel t1 = new JLabel("Events"); t1.setFont(new Font("Segoe UI",Font.BOLD,22)); t1.setForeground(Color.WHITE); t1.setAlignmentX(LEFT_ALIGNMENT);
        JLabel t2 = new JLabel("Schedule and manage community events"); t2.setFont(new Font("Segoe UI",Font.PLAIN,12)); t2.setForeground(new Color(200,240,235)); t2.setAlignmentX(LEFT_ALIGNMENT);
        titleBlock.add(t1); titleBlock.add(t2);

        left.add(badge); left.add(titleBlock);

        // Right: Add button
        JButton addBtn = pillBtn("+ Add Event", C_ADD, true);
        addBtn.setBackground(new Color(255,255,255,40));
        addBtn.addActionListener(e -> showAddDialog());
        // Paint override to look white-on-teal-dark
        JButton addBtnFinal = new JButton("+ Add Event") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? new Color(255,255,255,60) : new Color(255,255,255,35);
                g2.setColor(bg); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                g2.setColor(new Color(255,255,255,100)); g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                g2.setColor(Color.WHITE); g2.setFont(getFont());
                FontMetrics fm=g2.getFontMetrics(); String t=getText();
                g2.drawString(t,(getWidth()-fm.stringWidth(t))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        addBtnFinal.setFont(new Font("Segoe UI",Font.BOLD,13));
        addBtnFinal.setPreferredSize(new Dimension(130,38));
        addBtnFinal.setFocusPainted(false); addBtnFinal.setBorderPainted(false); addBtnFinal.setContentAreaFilled(false);
        addBtnFinal.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addBtnFinal.addActionListener(e -> showAddDialog());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0)); right.setOpaque(false);
        right.add(addBtnFinal);

        p.add(left,  BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    // ── Role Banner ───────────────────────────────────────────────────────────
    private JPanel buildRoleBanner() {
        boolean isAdmin = currentUser.isAdmin();
        JPanel banner = new JPanel(new FlowLayout(FlowLayout.LEFT,10,6));
        banner.setBackground(isAdmin ? new Color(240,233,255) : new Color(230,252,245));
        banner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0, isAdmin ? new Color(192,160,230) : new Color(167,220,198)),
                BorderFactory.createEmptyBorder(0,6,0,6)));

        JLabel badge = new JLabel(isAdmin ? " ADMIN " : " MEMBER ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isAdmin ? new Color(124,58,237) : new Color(5,150,105));
                g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                g2.setColor(Color.WHITE); g2.setFont(getFont()); FontMetrics fm=g2.getFontMetrics(); String t=getText().trim();
                g2.drawString(t,(getWidth()-fm.stringWidth(t))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2); g2.dispose();
            }
        };
        badge.setFont(new Font("Segoe UI",Font.BOLD,10)); badge.setPreferredSize(new Dimension(64,20)); badge.setOpaque(false);

        String desc = isAdmin
                ? "You can add, edit, and delete ANY event."
                : "You can add events. You can only edit/delete events YOU created (green highlight).";
        roleInfoLabel = new JLabel(desc);
        roleInfoLabel.setFont(new Font("Segoe UI",Font.PLAIN,12));
        roleInfoLabel.setForeground(isAdmin ? new Color(88,28,135) : new Color(6,95,70));
        banner.add(badge); banner.add(roleInfoLabel);
        return banner;
    }

    // ── Search Bar ────────────────────────────────────────────────────────────
    private JPanel buildSearchBar() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_CARD);
        outer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER,1,true),
                BorderFactory.createEmptyBorder(0,0,0,0)));

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT,10,8));
        p.setBackground(BG_CARD);

        JLabel searchIcon = new JLabel("Search:"); searchIcon.setFont(new Font("Segoe UI",Font.BOLD,12)); searchIcon.setForeground(SLATE);
        p.add(searchIcon);

        searchField = styledSearchField(18);
        p.add(searchField);

        JButton sb = pillBtn("Search", C_VIEW, false); sb.setPreferredSize(new Dimension(90,32));
        sb.addActionListener(e -> searchEvents()); p.add(sb);

        JLabel sep1 = new JLabel("  |  "); sep1.setForeground(BORDER); p.add(sep1);

        JLabel filterLbl = new JLabel("Filter:"); filterLbl.setFont(new Font("Segoe UI",Font.BOLD,12)); filterLbl.setForeground(SLATE);
        p.add(filterLbl);

        filterCombo = new JComboBox<>(new String[]{"All Events","Upcoming","Past","Today","This Week","This Month"});
        filterCombo.setBackground(BG_INPUT); filterCombo.setForeground(TEXT_DARK);
        filterCombo.setFont(new Font("Segoe UI",Font.PLAIN,13));
        filterCombo.setPreferredSize(new Dimension(150,32));
        filterCombo.setBorder(BorderFactory.createLineBorder(BORDER,1,true));
        filterCombo.addActionListener(e -> loadEvents()); p.add(filterCombo);

        if (!currentUser.isAdmin()) {
            JButton myBtn = pillBtn("My Events", C_MEMBER, false); myBtn.setPreferredSize(new Dimension(100,32));
            myBtn.addActionListener(e -> loadMyEvents()); p.add(myBtn);
        }

        JButton rb = pillBtn("Refresh", C_REFRESH, false); rb.setPreferredSize(new Dimension(82,32));
        rb.addActionListener(e -> loadEvents()); p.add(rb);

        outer.add(p, BorderLayout.CENTER);
        return outer;
    }

    // ── Table ─────────────────────────────────────────────────────────────────
    private JPanel buildTable() {
        JPanel outer = new JPanel(new BorderLayout(0,0));
        outer.setBackground(BG_CARD);
        outer.setBorder(BorderFactory.createLineBorder(BORDER,1,true));

        // Table header row (inside the card, above the JTable header)
        JPanel cardHdr = new JPanel(new FlowLayout(FlowLayout.LEFT,14,8));
        cardHdr.setBackground(new Color(248,251,255));
        cardHdr.setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER));
        JLabel hl = new JLabel("Event Records"); hl.setFont(new Font("Segoe UI",Font.BOLD,12)); hl.setForeground(TEAL1); cardHdr.add(hl);
        if (!currentUser.isAdmin()) {
            JLabel hint = new JLabel("  Green rows = your events");
            hint.setFont(new Font("Segoe UI",Font.PLAIN,11)); hint.setForeground(C_MEMBER); cardHdr.add(hint);
        }

        // Columns: added Category, hidden desc and owner_id
        String[] cols = {"ID","Event Name","Category","Date","Venue","Owner","Status","Desc"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r,int c){return false;}
        };

        eventsTable = new JTable(tableModel);
        eventsTable.setFont(new Font("Segoe UI",Font.PLAIN,13));
        eventsTable.setRowHeight(36); eventsTable.setShowGrid(false);
        eventsTable.setIntercellSpacing(new Dimension(0,1));
        eventsTable.setBackground(BG_CARD); eventsTable.setForeground(TEXT_DARK);
        eventsTable.setSelectionBackground(BG_SEL); eventsTable.setSelectionForeground(NAVY);
        eventsTable.setGridColor(BORDER);

        // Column widths
        eventsTable.getColumnModel().getColumn(0).setMaxWidth(50);
        eventsTable.getColumnModel().getColumn(0).setMinWidth(40);
        eventsTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        eventsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        eventsTable.getColumnModel().getColumn(4).setPreferredWidth(160);
        eventsTable.getColumnModel().getColumn(6).setPreferredWidth(105);

        // Hide owner_id (col 5) and desc (col 7)
        hideColumn(5); hideColumn(7);

        // Table header style
        JTableHeader header = eventsTable.getTableHeader();
        header.setFont(new Font("Segoe UI",Font.BOLD,12));
        header.setBackground(BG_HDR); header.setForeground(TEAL1);
        header.setPreferredSize(new Dimension(0,38)); header.setOpaque(true);
        header.setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER));

        // Default cell renderer
        eventsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
                super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                boolean mine = isRowOwner(r);
                if (sel)       { setBackground(BG_SEL);    setForeground(NAVY); }
                else if (mine) { setBackground(BG_ROW_ME); setForeground(new Color(20,90,55)); }
                else           { setBackground(r%2==0?BG_ROW_E:BG_ROW_O); setForeground(TEXT_DARK); }
                // ID column – muted
                if (c==0) setForeground(sel?NAVY:TEXT_LIGHT);
                setBorder(BorderFactory.createEmptyBorder(4,10,4,10));
                return this;
            }
        });

        // Status column – coloured pill badges
        eventsTable.getColumnModel().getColumn(COL_STATUS).setCellRenderer(new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
                JLabel lbl = new JLabel() {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2=(Graphics2D)g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(getBackground()); g2.fillRoundRect(2,4,getWidth()-4,getHeight()-8,10,10);
                        g2.setColor(getForeground()); g2.setFont(getFont());
                        FontMetrics fm=g2.getFontMetrics(); String tx=getText();
                        g2.drawString(tx,(getWidth()-fm.stringWidth(tx))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
                        g2.dispose();
                    }
                };
                String s = v==null?"":v.toString().toUpperCase();
                lbl.setText(s); lbl.setFont(new Font("Segoe UI",Font.BOLD,11));
                lbl.setHorizontalAlignment(CENTER); lbl.setOpaque(false);
                if (sel) { lbl.setBackground(new Color(147,197,253)); lbl.setForeground(NAVY); }
                else {
                    switch(s){
                        case "UPCOMING":  lbl.setBackground(new Color(219,234,254)); lbl.setForeground(new Color(29,78,216));  break;
                        case "ONGOING":   lbl.setBackground(new Color(209,250,229)); lbl.setForeground(new Color(6,95,70));   break;
                        case "COMPLETED": lbl.setBackground(new Color(237,233,254)); lbl.setForeground(new Color(91,33,182)); break;
                        case "CANCELLED": lbl.setBackground(new Color(254,226,226)); lbl.setForeground(new Color(185,28,28)); break;
                        default:          lbl.setBackground(new Color(241,245,249)); lbl.setForeground(SLATE_LT);
                    }
                }
                return lbl;
            }
        });

        // Category column – teal text
        eventsTable.getColumnModel().getColumn(COL_CAT).setCellRenderer(new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
                super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                boolean mine=isRowOwner(r);
                if (sel)       setBackground(BG_SEL);
                else if (mine) setBackground(BG_ROW_ME);
                else           setBackground(r%2==0?BG_ROW_E:BG_ROW_O);
                setForeground(sel?NAVY:TEAL1);
                setFont(new Font("Segoe UI",Font.BOLD,12));
                setBorder(BorderFactory.createEmptyBorder(4,10,4,10));
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(eventsTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_CARD);
        scroll.getVerticalScrollBar().setBackground(BG_CARD);

        // Action buttons bar
        JPanel actionBar = buildActionBar();

        outer.add(cardHdr,   BorderLayout.NORTH);
        outer.add(scroll,    BorderLayout.CENTER);
        outer.add(actionBar, BorderLayout.SOUTH);
        return outer;
    }

    private void hideColumn(int col) {
        eventsTable.getColumnModel().getColumn(col).setMinWidth(0);
        eventsTable.getColumnModel().getColumn(col).setMaxWidth(0);
        eventsTable.getColumnModel().getColumn(col).setWidth(0);
    }

    // ── Action Buttons Bar ────────────────────────────────────────────────────
    private JPanel buildActionBar() {
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,8));
        btns.setBackground(new Color(248,251,255));
        btns.setBorder(BorderFactory.createMatteBorder(1,0,0,0,BORDER));

        JButton vb = pillBtn("View Details", C_VIEW, false);  vb.addActionListener(e->viewDetails()); btns.add(vb);
        JButton eb = pillBtn("Edit",         C_EDIT, false);  eb.addActionListener(e->editEvent());   btns.add(eb);
        JButton db = pillBtn("Delete",       C_DELETE, false); db.addActionListener(e->deleteEvent()); btns.add(db);

        eb.setToolTipText(currentUser.isAdmin()?"Edit any event":"Edit your own events only");
        db.setToolTipText(currentUser.isAdmin()?"Delete any event":"Delete your own events only");

        eventsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean can = canModifySelected();
                eb.setEnabled(can); db.setEnabled(can);
            }
        });
        eb.setEnabled(false); db.setEnabled(false);
        return btns;
    }

    // ── RBAC ──────────────────────────────────────────────────────────────────
    private boolean canModifySelected() {
        int r = eventsTable.getSelectedRow();
        if (r < 0) return false;
        if (currentUser.isAdmin()) return true;
        return isRowOwner(r);
    }

    private boolean isRowOwner(int row) {
        try {
            Object v = tableModel.getValueAt(row, COL_OWNER_ID);
            return v != null && Integer.parseInt(v.toString()) == currentUser.getUserId();
        } catch (Exception e) { return false; }
    }

    private boolean checkPermission() {
        int row = eventsTable.getSelectedRow();
        if (row < 0) { msg("Please select an event first.", false); return false; }
        if (currentUser.isAdmin()) return true;
        if (isRowOwner(row)) return true;
        JOptionPane.showMessageDialog(this,
                "Access Denied!\nYou can only edit/delete events that you created.",
                "Permission Denied", JOptionPane.WARNING_MESSAGE);
        return false;
    }

    // ── Data Loading ──────────────────────────────────────────────────────────
    public void loadEvents() {
        tableModel.setRowCount(0);
        String filter = (String) filterCombo.getSelectedItem();
        String where;
        switch (filter == null ? "All Events" : filter) {
            case "Upcoming":   where = "WHERE event_date>=CURDATE() ";  break;
            case "Past":       where = "WHERE event_date<CURDATE() ";   break;
            case "Today":      where = "WHERE event_date=CURDATE() ";   break;
            case "This Week":  where = "WHERE YEARWEEK(event_date,1)=YEARWEEK(CURDATE(),1) "; break;
            case "This Month": where = "WHERE YEAR(event_date)=YEAR(CURDATE()) AND MONTH(event_date)=MONTH(CURDATE()) "; break;
            default:           where = ""; break;
        }
        String sql = "SELECT event_id,event_name,COALESCE(category,'GENERAL') AS category," +
                "event_date,COALESCE(venue,'') AS venue,COALESCE(created_by,0) AS created_by," +
                "status,COALESCE(description,'') AS description FROM events " + where + "ORDER BY event_date DESC";
        try (Connection conn=DBConnection.getConnection(); Statement st=conn.createStatement(); ResultSet rs=st.executeQuery(sql)) {
            while (rs.next()) tableModel.addRow(new Object[]{
                    rs.getInt("event_id"), rs.getString("event_name"), rs.getString("category"),
                    rs.getDate("event_date"), rs.getString("venue"), rs.getInt("created_by"),
                    rs.getString("status"), rs.getString("description")
            });
        } catch (SQLException e) { msg("Error loading events: "+e.getMessage(), true); }
    }

    private void loadMyEvents() {
        tableModel.setRowCount(0);
        String sql = "SELECT event_id,event_name,COALESCE(category,'GENERAL') AS category," +
                "event_date,COALESCE(venue,'') AS venue,COALESCE(created_by,0) AS created_by," +
                "status,COALESCE(description,'') AS description FROM events WHERE created_by=? ORDER BY event_date DESC";
        try (Connection conn=DBConnection.getConnection(); PreparedStatement ps=conn.prepareStatement(sql)) {
            ps.setInt(1,currentUser.getUserId());
            ResultSet rs=ps.executeQuery();
            while(rs.next()) tableModel.addRow(new Object[]{
                    rs.getInt("event_id"),rs.getString("event_name"),rs.getString("category"),
                    rs.getDate("event_date"),rs.getString("venue"),rs.getInt("created_by"),
                    rs.getString("status"),rs.getString("description")
            });
            if (tableModel.getRowCount()==0)
                JOptionPane.showMessageDialog(this,"You have not created any events yet.","My Events",JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) { msg("Error: "+e.getMessage(), true); }
    }

    private void searchEvents() {
        String kw = searchField.getText().trim(); if (kw.isEmpty()) { loadEvents(); return; }
        tableModel.setRowCount(0);
        String sql = "SELECT event_id,event_name,COALESCE(category,'GENERAL') AS category," +
                "event_date,COALESCE(venue,'') AS venue,COALESCE(created_by,0) AS created_by," +
                "status,COALESCE(description,'') AS description FROM events " +
                "WHERE event_name LIKE? OR description LIKE? OR venue LIKE? ORDER BY event_date DESC";
        try (Connection conn=DBConnection.getConnection(); PreparedStatement ps=conn.prepareStatement(sql)) {
            String p="%"+kw+"%"; ps.setString(1,p); ps.setString(2,p); ps.setString(3,p);
            ResultSet rs=ps.executeQuery();
            while(rs.next()) tableModel.addRow(new Object[]{
                    rs.getInt("event_id"),rs.getString("event_name"),rs.getString("category"),
                    rs.getDate("event_date"),rs.getString("venue"),rs.getInt("created_by"),
                    rs.getString("status"),rs.getString("description")
            });
        } catch (SQLException e) { msg("Search error: "+e.getMessage(), true); }
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────
    private void showAddDialog() {
        JDialog dlg = dialog("Add New Event", 520, 440);
        JTextField nameF=dField(""), venueF=dField(""), dateF=dField("YYYY-MM-DD");
        JTextArea descA = dArea();
        JComboBox<String> catC = new JComboBox<>(new String[]{"CULTURAL","SPORTS","EDUCATIONAL","SOCIAL","HEALTH","RELIGIOUS"});
        JComboBox<String> statusC = new JComboBox<>(new String[]{"UPCOMING","ONGOING","COMPLETED","CANCELLED"});
        styleCombo(catC); styleCombo(statusC);

        JPanel form = formPanel();
        addFormRow(form,"Event Name *",     nameF,   0);
        addFormRow(form,"Description",      new JScrollPane(descA), 1);
        addFormRow(form,"Date (YYYY-MM-DD)",dateF,   2);
        addFormRow(form,"Venue",            venueF,  3);
        addFormRow(form,"Category",         catC,    4);
        addFormRow(form,"Status",           statusC, 5);

        JButton save   = pillBtn("Save Event", C_ADD, false);
        JButton cancel = pillBtn("Cancel",     C_REFRESH, false);
        save.addActionListener(e -> {
            if (nameF.getText().trim().isEmpty()) { msg("Event name is required!",true); return; }
            String sql="INSERT INTO events(event_name,description,event_date,venue,category,status,created_by,organizer_id,created_date)VALUES(?,?,?,?,?,?,?,?,NOW())";
            try(Connection conn=DBConnection.getConnection(); PreparedStatement ps=conn.prepareStatement(sql)){
                ps.setString(1,nameF.getText().trim()); ps.setString(2,descA.getText().trim());
                ps.setString(3,dateF.getText().trim()); ps.setString(4,venueF.getText().trim());
                ps.setString(5,(String)catC.getSelectedItem()); ps.setString(6,(String)statusC.getSelectedItem());
                ps.setInt(7,currentUser.getUserId()); ps.setInt(8,currentUser.getUserId());
                ps.executeUpdate(); ok("Event added successfully!"); loadEvents(); dlg.dispose();
            } catch(SQLException ex){ msg("Error: "+ex.getMessage(),true); }
        });
        cancel.addActionListener(e->dlg.dispose());
        dlg.add(form,BorderLayout.CENTER); dlg.add(dialogBtnRow(save,cancel),BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void viewDetails() {
        int r = eventsTable.getSelectedRow(); if (r<0){msg("Select an event first.",false);return;}
        boolean mine=isRowOwner(r);
        String info =
                "ID:          " + tableModel.getValueAt(r,COL_ID)     + "\n" +
                        "Name:        " + tableModel.getValueAt(r,COL_NAME)   + "\n" +
                        "Category:    " + tableModel.getValueAt(r,COL_CAT)    + "\n" +
                        "Date:        " + tableModel.getValueAt(r,COL_DATE)   + "\n" +
                        "Venue:       " + tableModel.getValueAt(r,COL_VENUE)  + "\n" +
                        "Status:      " + tableModel.getValueAt(r,COL_STATUS) + "\n" +
                        "Ownership:   " + (currentUser.isAdmin()?"Admin view":mine?"You created this":"Another member created this") + "\n\n" +
                        "Description:\n" + tableModel.getValueAt(r,COL_DESC);
        JTextArea ta=new JTextArea(info,14,46); ta.setEditable(false); ta.setLineWrap(true); ta.setWrapStyleWord(true);
        ta.setFont(new Font("Segoe UI",Font.PLAIN,13));
        JOptionPane.showMessageDialog(this,new JScrollPane(ta),"Event Details",JOptionPane.INFORMATION_MESSAGE);
    }

    private void editEvent() {
        if (!checkPermission()) return;
        int row=eventsTable.getSelectedRow(); int id=(int)tableModel.getValueAt(row,COL_ID);
        JDialog dlg=dialog("Edit Event",520,440);
        JTextField nameF=dField(s(tableModel.getValueAt(row,COL_NAME)));
        JTextField dateF=dField(s(tableModel.getValueAt(row,COL_DATE)));
        JTextField venueF=dField(s(tableModel.getValueAt(row,COL_VENUE)));
        JTextArea descA=dArea(); descA.setText(s(tableModel.getValueAt(row,COL_DESC)));
        JComboBox<String> catC=new JComboBox<>(new String[]{"CULTURAL","SPORTS","EDUCATIONAL","SOCIAL","HEALTH","RELIGIOUS"});
        catC.setSelectedItem(tableModel.getValueAt(row,COL_CAT));
        JComboBox<String> statusC=new JComboBox<>(new String[]{"UPCOMING","ONGOING","COMPLETED","CANCELLED"});
        statusC.setSelectedItem(tableModel.getValueAt(row,COL_STATUS));
        styleCombo(catC); styleCombo(statusC);

        JPanel form=formPanel();
        addFormRow(form,"Event Name",dateF,0); // intentional: reuse rows
        addFormRow(form,"Event Name *",     nameF,  0);
        addFormRow(form,"Description",      new JScrollPane(descA),1);
        addFormRow(form,"Date",             dateF,  2);
        addFormRow(form,"Venue",            venueF, 3);
        addFormRow(form,"Category",         catC,   4);
        addFormRow(form,"Status",           statusC,5);

        JButton save=pillBtn("Update Event",C_ADD,false);
        JButton cancel=pillBtn("Cancel",C_REFRESH,false);
        save.addActionListener(e->{
            String sql="UPDATE events SET event_name=?,description=?,event_date=?,venue=?,category=?,status=? WHERE event_id=?";
            try(Connection conn=DBConnection.getConnection(); PreparedStatement ps=conn.prepareStatement(sql)){
                ps.setString(1,nameF.getText().trim()); ps.setString(2,descA.getText().trim());
                ps.setString(3,dateF.getText().trim()); ps.setString(4,venueF.getText().trim());
                ps.setString(5,(String)catC.getSelectedItem()); ps.setString(6,(String)statusC.getSelectedItem());
                ps.setInt(7,id); ps.executeUpdate(); ok("Event updated!"); loadEvents(); dlg.dispose();
            }catch(SQLException ex){msg("Error: "+ex.getMessage(),true);}
        });
        cancel.addActionListener(e->dlg.dispose());
        dlg.add(form,BorderLayout.CENTER); dlg.add(dialogBtnRow(save,cancel),BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void deleteEvent() {
        if(!checkPermission()) return;
        int row=eventsTable.getSelectedRow(); int id=(int)tableModel.getValueAt(row,COL_ID);
        String name=s(tableModel.getValueAt(row,COL_NAME));
        int c=JOptionPane.showConfirmDialog(this,"Delete event: \""+name+"\"?\nThis cannot be undone.",
                "Confirm Delete",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
        if(c!=JOptionPane.YES_OPTION) return;
        try(Connection conn=DBConnection.getConnection(); PreparedStatement ps=conn.prepareStatement("DELETE FROM events WHERE event_id=?")){
            ps.setInt(1,id); ps.executeUpdate(); ok("Event deleted!"); loadEvents();
        }catch(SQLException e){msg("Error: "+e.getMessage(),true);}
    }

    // ── Dialog Helpers ────────────────────────────────────────────────────────
    private JDialog dialog(String title,int w,int h){
        JDialog d=new JDialog((Frame)SwingUtilities.getWindowAncestor(this),title,true);
        d.setSize(w,h); d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(BG_CARD); d.setLayout(new BorderLayout(10,10)); return d;
    }

    private JPanel formPanel(){
        JPanel p=new JPanel(new GridBagLayout()); p.setBackground(BG_CARD);
        p.setBorder(BorderFactory.createEmptyBorder(18,22,10,22)); return p;
    }

    private void addFormRow(JPanel form, String label, Component field, int row){
        GridBagConstraints g=new GridBagConstraints(); g.fill=GridBagConstraints.HORIZONTAL; g.insets=new Insets(6,4,6,4);
        g.gridx=0; g.gridy=row; g.weightx=0.30;
        JLabel l=new JLabel(label); l.setFont(new Font("Segoe UI",Font.BOLD,12)); l.setForeground(TEAL1); form.add(l,g);
        g.gridx=1; g.weightx=0.70; form.add(field,g);
    }

    private JPanel dialogBtnRow(JButton...btns){
        JPanel p=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
        p.setBackground(new Color(246,249,252));
        p.setBorder(BorderFactory.createMatteBorder(1,0,0,0,BORDER));
        for(JButton b:btns) p.add(b); return p;
    }

    private JTextField dField(String t){
        JTextField f=new JTextField(t); f.setFont(new Font("Segoe UI",Font.PLAIN,13));
        f.setBackground(BG_INPUT); f.setForeground(TEXT_DARK);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER,1,true), BorderFactory.createEmptyBorder(7,10,7,10)));
        f.setPreferredSize(new Dimension(0,36)); return f;
    }

    private JTextArea dArea(){
        JTextArea a=new JTextArea(3,20); a.setFont(new Font("Segoe UI",Font.PLAIN,13));
        a.setBackground(BG_INPUT); a.setForeground(TEXT_DARK); a.setLineWrap(true); a.setWrapStyleWord(true);
        a.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER,1), BorderFactory.createEmptyBorder(6,8,6,8))); return a;
    }

    private void styleCombo(JComboBox<String> c){
        c.setBackground(BG_INPUT); c.setForeground(TEXT_DARK);
        c.setFont(new Font("Segoe UI",Font.PLAIN,13));
        c.setBorder(BorderFactory.createLineBorder(BORDER,1,true));
        c.setPreferredSize(new Dimension(0,36));
    }

    // ── Button & Field Helpers ────────────────────────────────────────────────
    private JButton pillBtn(String text, Color bg, boolean whiteText) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                if (!isEnabled()) g2.setColor(new Color(200,210,220));
                else g2.setColor(getModel().isRollover() ? bg.darker() : bg);
                g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                g2.setColor(whiteText ? Color.WHITE : Color.WHITE);
                g2.setFont(getFont()); FontMetrics fm=g2.getFontMetrics(); String t=getText();
                g2.drawString(t,(getWidth()-fm.stringWidth(t))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        b.setFont(new Font("Segoe UI",Font.BOLD,12));
        b.setPreferredSize(new Dimension(Math.max(90,text.length()*8+20),32));
        b.setFocusPainted(false); b.setBorderPainted(false); b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return b;
    }

    private JTextField styledSearchField(int cols){
        JTextField f=new JTextField(cols); f.setFont(new Font("Segoe UI",Font.PLAIN,13));
        f.setBackground(BG_INPUT); f.setForeground(TEXT_DARK); f.setCaretColor(TEAL1);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER,1,true), BorderFactory.createEmptyBorder(5,10,5,10)));
        f.setPreferredSize(new Dimension(200,32));
        f.addFocusListener(new FocusAdapter(){
            @Override public void focusGained(FocusEvent e){ f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_FCS,1,true), BorderFactory.createEmptyBorder(5,10,5,10))); }
            @Override public void focusLost(FocusEvent e){ f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER,1,true), BorderFactory.createEmptyBorder(5,10,5,10))); }
        });
        return f;
    }

    private String s(Object o){ return o==null?"":o.toString(); }
    private void msg(String m,boolean err){
        if(err) JOptionPane.showMessageDialog(this,m,"Error",JOptionPane.ERROR_MESSAGE);
        else    JOptionPane.showMessageDialog(this,m,"Notice",JOptionPane.INFORMATION_MESSAGE);
    }
    private void ok(String m){ JOptionPane.showMessageDialog(this,m,"Done",JOptionPane.INFORMATION_MESSAGE); }
}