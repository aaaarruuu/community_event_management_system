//package ui;
//
//import database.DBConnection;
//
//import javax.swing.*;
//import javax.swing.table.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.awt.geom.*;
//import java.sql.*;
//
//public class EventPanel extends JPanel {
//
//    private JTable            eventsTable;
//    private DefaultTableModel tableModel;
//    private JTextField        searchField;
//    private JComboBox<String> filterCombo;
//
//    // ── Palette ───────────────────────────────────────────────────────────────
//    private static final Color H1      = new Color( 14, 165, 233);  // sky blue
//    private static final Color H2      = new Color( 56, 189, 248);  // light sky
//    private static final Color SUCCESS = new Color(21, 244, 5);
//    private static final Color WARNING = new Color(217, 119,   6);
//    private static final Color DANGER  = new Color(220,  38,  38);
//    private static final Color BG      = new Color( 15,  23,  42);  // dark base
//    private static final Color BG2     = new Color(49, 68, 99);
//    private static final Color BG3     = new Color(31, 86, 195);
//    private static final Color TH_BG   = new Color( 12,  74, 110);  // table header bg
//    private static final Color TEXT    = new Color(10, 244, 244);
//    private static final Color TEXT2   = new Color(97, 152, 228);
//    private static final Color SEL_BG  = new Color( 12,  74, 110);
//
//    private static final Color[] ROW_EVEN = { new Color(30,41,59), new Color(30,41,59) };
//    private static final Color[] ROW_ODD  = { new Color(22,33,52), new Color(22,33,52) };
//
//    public EventPanel() {
//        setLayout(new BorderLayout(0, 0));
//        setBackground(BG);
//        initComponents();
//        loadEvents();
//    }
//
//    private void initComponents() {
//        add(buildTitleBar(), BorderLayout.NORTH);
//        JPanel body = new JPanel(new BorderLayout(0, 14));
//        body.setBackground(BG);
//        body.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
//        body.add(buildSearchBar(), BorderLayout.NORTH);
//        body.add(buildTable(),     BorderLayout.CENTER);
//        add(body, BorderLayout.CENTER);
//    }
//
//    private JPanel buildTitleBar() {
//        JPanel p = new JPanel(new BorderLayout()) {
//            @Override protected void paintComponent(Graphics g) {
//                Graphics2D g2 = (Graphics2D) g.create();
//                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//                GradientPaint gp = new GradientPaint(0,0, new Color(12,74,110), getWidth(),0, new Color(3,105,161));
//                g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());
//                // Decorative wave
//                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
//                g2.setColor(H2);
//                g2.fill(new Ellipse2D.Float(getWidth()-120,-30,160,160));
//                g2.fill(new Ellipse2D.Float(getWidth()-280, 10, 80, 80));
//                g2.dispose(); super.paintComponent(g);
//            }
//        };
//        p.setOpaque(true); p.setPreferredSize(new Dimension(0, 80));
//        p.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 20));
//
//        JPanel left = new JPanel(new GridBagLayout()); left.setOpaque(false);
//        GridBagConstraints gc = new GridBagConstraints();
//        gc.gridx=0; gc.gridy=0; gc.anchor=GridBagConstraints.WEST;
//        JLabel icon = new JLabel("📅"); icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
//        left.add(icon, gc);
//        gc.gridx=1; gc.insets=new Insets(0,10,0,0);
//        JPanel titleText = new JPanel(); titleText.setLayout(new BoxLayout(titleText, BoxLayout.Y_AXIS)); titleText.setOpaque(false);
//        JLabel title = new JLabel("Community Events");
//        title.setFont(new Font("Segoe UI", Font.BOLD, 22)); title.setForeground(Color.BLACK); title.setAlignmentX(LEFT_ALIGNMENT);
//        JLabel sub = new JLabel("Manage all society events");
//        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12)); sub.setForeground(new Color(255, 240, 0)); sub.setAlignmentX(LEFT_ALIGNMENT);
//        titleText.add(title); titleText.add(sub);
//        left.add(titleText, gc);
//
//        JButton addBtn = colorBtn("＋  Add Event", SUCCESS);
//        addBtn.addActionListener(e -> showAddDialog());
//        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); right.setOpaque(false); right.add(addBtn);
//        p.add(left, BorderLayout.WEST); p.add(right, BorderLayout.EAST);
//        return p;
//    }
//
//    private JPanel buildSearchBar() {
//        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
//        p.setBackground(BG2);
//        p.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createMatteBorder(0,0,2,0, H1),
//                BorderFactory.createEmptyBorder(4,8,4,8)));
//
//        JLabel searchLabel = new JLabel("SEARCH");
//        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 11)); searchLabel.setForeground(H2);
//        searchField = new JTextField(22);
//        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        searchField.setBackground(BG3); searchField.setForeground(TEXT);
//        searchField.setCaretColor(H2);
//        searchField.setPreferredSize(new Dimension(200, 34));
//        searchField.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(H1, 1, true),
//                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
//        JButton searchBtn = colorBtn("💫 Go", H1);
//        searchBtn.setPreferredSize(new Dimension(90, 34)); searchBtn.addActionListener(e -> searchEvents());
//
//        JLabel filterLabel = new JLabel("FILTER");
//        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 11)); filterLabel.setForeground(H2);
//        filterCombo = new JComboBox<>(new String[]{"All Events","Upcoming","Past","Today","This Week","This Month"});
//        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        filterCombo.setBackground(BG3); filterCombo.setForeground(TEXT);
//        filterCombo.setPreferredSize(new Dimension(150, 34));
//        filterCombo.addActionListener(e -> loadEvents());
//
//        JButton refreshBtn = colorBtn("↺", BG3); refreshBtn.setPreferredSize(new Dimension(46,34));
//        refreshBtn.addActionListener(e -> loadEvents());
//
//        p.add(searchLabel); p.add(searchField); p.add(searchBtn);
//        p.add(Box.createHorizontalStrut(10));
//        p.add(filterLabel); p.add(filterCombo); p.add(refreshBtn);
//        return p;
//    }
//
//    private JPanel buildTable() {
//        JPanel outer = new JPanel(new BorderLayout(0,0));
//        outer.setBackground(BG2);
//        outer.setBorder(BorderFactory.createLineBorder(H1, 2, true));
//
//        // Section label
//        JPanel label = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 8));
//        label.setBackground(TH_BG);
//        JLabel sectionLbl = new JLabel("EVENT RECORDS");
//        sectionLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
//        sectionLbl.setForeground(H2);
//        label.add(sectionLbl);
//
//        String[] cols = {"ID","Event Name","Description","Date","Venue","Org.ID","Status"};
//        tableModel = new DefaultTableModel(cols, 0) {
//            @Override public boolean isCellEditable(int r, int c) { return false; }
//        };
//
//        eventsTable = new JTable(tableModel);
//        eventsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        eventsTable.setRowHeight(36); eventsTable.setShowGrid(false);
//        eventsTable.setIntercellSpacing(new Dimension(0,0));
//        eventsTable.setBackground(BG2); eventsTable.setForeground(TEXT);
//        eventsTable.setSelectionBackground(SEL_BG); eventsTable.setSelectionForeground(H2);
//        eventsTable.getColumnModel().getColumn(0).setMaxWidth(48);
//        eventsTable.getColumnModel().getColumn(5).setMaxWidth(60);
//
//        JTableHeader header = eventsTable.getTableHeader();
//        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        header.setBackground(TH_BG); header.setForeground(H2);
//        header.setPreferredSize(new Dimension(0, 40)); header.setOpaque(true);
//        header.setBorder(BorderFactory.createMatteBorder(0,0,2,0,H1));
//
//        // Row renderer
//        eventsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
//            @Override public Component getTableCellRendererComponent(JTable t, Object v,
//                                                                     boolean sel, boolean foc, int r, int c) {
//                super.getTableCellRendererComponent(t,v,sel,foc,r,c);
//                if (sel) { setBackground(SEL_BG); setForeground(H2); }
//                else { setBackground(r%2==0 ? ROW_EVEN[0] : ROW_ODD[0]); setForeground(TEXT); }
//                setBorder(BorderFactory.createEmptyBorder(4,10,4,10)); return this;
//            }
//        });
//        // Status badge renderer
//        eventsTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
//            @Override public Component getTableCellRendererComponent(JTable t, Object v,
//                                                                     boolean sel, boolean foc, int r, int c) {
//                super.getTableCellRendererComponent(t,v,sel,foc,r,c);
//                String s = v==null?"":v.toString().toUpperCase();
//                setHorizontalAlignment(CENTER);
//                setBorder(BorderFactory.createEmptyBorder(4,8,4,8));
//                if (sel) { setBackground(SEL_BG); setForeground(H2); return this; }
//                switch(s) {
//                    case "UPCOMING":  setBackground(new Color(12,74,110));  setForeground(H2);                          break;
//                    case "ONGOING":   setBackground(new Color(6,78,59));    setForeground(new Color(52,211,153));        break;
//                    case "COMPLETED": setBackground(new Color(49,46,129));  setForeground(new Color(167,139,250));       break;
//                    case "CANCELLED": setBackground(new Color(127,29,29));  setForeground(new Color(252,165,165));       break;
//                    default:          setBackground(BG3);                   setForeground(TEXT2);
//                }
//                setFont(new Font("Segoe UI", Font.BOLD, 11)); return this;
//            }
//        });
//
//        JScrollPane scroll = new JScrollPane(eventsTable);
//        scroll.setBorder(BorderFactory.createEmptyBorder());
//        scroll.getViewport().setBackground(BG2);
//        scroll.setBackground(BG2);
//        scroll.getVerticalScrollBar().setBackground(BG);
//
//        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
//        btns.setBackground(TH_BG);
//        btns.setBorder(BorderFactory.createMatteBorder(2,0,0,0,H1));
//        JButton vb = colorBtn("👁  View", H1);   vb.addActionListener(e -> viewDetails());
//        JButton eb = colorBtn("✏  Edit",  new Color(217,119,6));  eb.addActionListener(e -> editEvent());
//        JButton db = colorBtn("🗑 Delete", DANGER); db.addActionListener(e -> deleteEvent());
//        btns.add(vb); btns.add(eb); btns.add(db);
//
//        outer.add(label,  BorderLayout.NORTH);
//        outer.add(scroll, BorderLayout.CENTER);
//        outer.add(btns,   BorderLayout.SOUTH);
//        return outer;
//    }
//
//    // ── Data ──────────────────────────────────────────────────────────────────
//    public void loadEvents() {
//        tableModel.setRowCount(0);
//        String base = "SELECT event_id, event_name, COALESCE(description,'') AS description, "
//                + "event_date, COALESCE(venue,'') AS venue, COALESCE(organizer_id,'') AS oid, status FROM events ";
//        String filter = (String) filterCombo.getSelectedItem();
//        String where;
//        switch(filter) {
//            case "Upcoming":   where="WHERE event_date >= CURDATE() "; break;
//            case "Past":       where="WHERE event_date < CURDATE() ";  break;
//            case "Today":      where="WHERE event_date = CURDATE() ";  break;
//            case "This Week":  where="WHERE YEARWEEK(event_date,1)=YEARWEEK(CURDATE(),1) "; break;
//            case "This Month": where="WHERE YEAR(event_date)=YEAR(CURDATE()) AND MONTH(event_date)=MONTH(CURDATE()) "; break;
//            default:           where=""; break;
//        }
//        try (Connection conn=DBConnection.getConnection(); Statement st=conn.createStatement();
//             ResultSet rs=st.executeQuery(base+where+"ORDER BY event_date DESC")) {
//            while (rs.next())
//                tableModel.addRow(new Object[]{rs.getInt("event_id"),rs.getString("event_name"),
//                        rs.getString("description"),rs.getDate("event_date"),
//                        rs.getString("venue"),rs.getString("oid"),rs.getString("status")});
//        } catch (SQLException e) { e.printStackTrace(); error("Error: "+e.getMessage()); }
//    }
//
//    private void searchEvents() {
//        String kw=searchField.getText().trim(); if(kw.isEmpty()){loadEvents();return;}
//        tableModel.setRowCount(0);
//        String sql="SELECT event_id,event_name,COALESCE(description,'')AS description,event_date,"
//                +"COALESCE(venue,'')AS venue,COALESCE(organizer_id,'')AS oid,status FROM events "
//                +"WHERE event_name LIKE? OR description LIKE? OR venue LIKE? ORDER BY event_date DESC";
//        try(Connection conn=DBConnection.getConnection(); PreparedStatement ps=conn.prepareStatement(sql)){
//            String p="%"+kw+"%"; ps.setString(1,p);ps.setString(2,p);ps.setString(3,p);
//            ResultSet rs=ps.executeQuery();
//            while(rs.next()) tableModel.addRow(new Object[]{rs.getInt("event_id"),rs.getString("event_name"),
//                    rs.getString("description"),rs.getDate("event_date"),rs.getString("venue"),rs.getString("oid"),rs.getString("status")});
//        } catch(SQLException e){error("Search error: "+e.getMessage());}
//    }
//
//    private void showAddDialog() {
//        JDialog dlg=styledDialog("Add New Event",520,480);
//        JTextField nameF=tf();JTextField venueF=tf();JTextField dateF=tf("YYYY-MM-DD");
//        dateF.setForeground(TEXT2);
//        dateF.addFocusListener(new FocusAdapter(){
//            @Override public void focusGained(FocusEvent e){if(dateF.getText().equals("YYYY-MM-DD")){dateF.setText("");dateF.setForeground(TEXT);}}});
//        JTextArea descA=descArea();
//        JComboBox<String> statusC=new JComboBox<>(new String[]{"UPCOMING","ONGOING","COMPLETED","CANCELLED"});
//        styleCombo(statusC);
//        JPanel form=formPanel();
//        addRow(form,"Event Name:",nameF,0);addRow(form,"Description:",new JScrollPane(descA),1);
//        addRow(form,"Date:",dateF,2);addRow(form,"Venue:",venueF,3);addRow(form,"Status:",statusC,4);
//        JButton save=colorBtn("💾  Save",SUCCESS);
//        save.addActionListener(e->{
//            if(nameF.getText().trim().isEmpty()){error("Event name required!");return;}
//            try(Connection conn=DBConnection.getConnection();
//                PreparedStatement ps=conn.prepareStatement("INSERT INTO events(event_name,description,event_date,venue,status,created_date)VALUES(?,?,?,?,?,NOW())")){
//                ps.setString(1,nameF.getText().trim());ps.setString(2,descA.getText().trim());
//                ps.setString(3,dateF.getText().trim());ps.setString(4,venueF.getText().trim());
//                ps.setString(5,(String)statusC.getSelectedItem());
//                ps.executeUpdate();success("Event added! ✅");loadEvents();dlg.dispose();
//            }catch(SQLException ex){error("Error: "+ex.getMessage());}
//        });
//        JButton cancel=colorBtn("Cancel",BG3); cancel.addActionListener(e->dlg.dispose());
//        dlg.add(form,BorderLayout.CENTER); dlg.add(btnsPanel(save,cancel),BorderLayout.SOUTH); dlg.setVisible(true);
//    }
//
//    private void viewDetails() {
//        int row=eventsTable.getSelectedRow(); if(row<0){error("Select an event.");return;}
//        JOptionPane.showMessageDialog(this,buildDetails("📅 Event Details",new String[]{"ID","Name","Description","Date","Venue","Org.","Status"},row),"Event Details",JOptionPane.INFORMATION_MESSAGE);
//    }
//
//    private void editEvent() {
//        int row=eventsTable.getSelectedRow(); if(row<0){error("Select an event to edit.");return;}
//        int id=(int)tableModel.getValueAt(row,0);
//        JDialog dlg=styledDialog("Edit Event",520,480);
//        JTextField nameF=tf(str(tableModel.getValueAt(row,1)));
//        JTextField dateF=tf(str(tableModel.getValueAt(row,3)));
//        JTextField venueF=tf(str(tableModel.getValueAt(row,4)));
//        JTextArea descA=descArea(); descA.setText(str(tableModel.getValueAt(row,2)));
//        JComboBox<String> statusC=new JComboBox<>(new String[]{"UPCOMING","ONGOING","COMPLETED","CANCELLED"});
//        styleCombo(statusC); statusC.setSelectedItem(tableModel.getValueAt(row,6));
//        JPanel form=formPanel();
//        addRow(form,"Name:",nameF,0);addRow(form,"Description:",new JScrollPane(descA),1);
//        addRow(form,"Date:",dateF,2);addRow(form,"Venue:",venueF,3);addRow(form,"Status:",statusC,4);
//        JButton save=colorBtn("💾  Update",SUCCESS);
//        save.addActionListener(e->{
//            try(Connection conn=DBConnection.getConnection();
//                PreparedStatement ps=conn.prepareStatement("UPDATE events SET event_name=?,description=?,event_date=?,venue=?,status=? WHERE event_id=?")){
//                ps.setString(1,nameF.getText().trim());ps.setString(2,descA.getText().trim());
//                ps.setString(3,dateF.getText().trim());ps.setString(4,venueF.getText().trim());
//                ps.setString(5,(String)statusC.getSelectedItem());ps.setInt(6,id);
//                ps.executeUpdate();success("Updated! ✅");loadEvents();dlg.dispose();
//            }catch(SQLException ex){error("Error: "+ex.getMessage());}
//        });
//        JButton cancel=colorBtn("Cancel",BG3); cancel.addActionListener(e->dlg.dispose());
//        dlg.add(form,BorderLayout.CENTER); dlg.add(btnsPanel(save,cancel),BorderLayout.SOUTH); dlg.setVisible(true);
//    }
//
//    private void deleteEvent() {
//        int row=eventsTable.getSelectedRow(); if(row<0){error("Select an event.");return;}
//        int id=(int)tableModel.getValueAt(row,0);
//        if(JOptionPane.showConfirmDialog(this,"Delete this event?","Confirm",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION){
//            try(Connection conn=DBConnection.getConnection();
//                PreparedStatement ps=conn.prepareStatement("DELETE FROM events WHERE event_id=?")){
//                ps.setInt(1,id);ps.executeUpdate();success("Deleted!");loadEvents();
//            }catch(SQLException e){error("Error: "+e.getMessage());}
//        }
//    }
//
//    // ── Helpers ───────────────────────────────────────────────────────────────
//    private String str(Object o){return o==null?"":o.toString();}
//
//    private JButton colorBtn(String text, Color bg) {
//        JButton b = new JButton(text){
//            @Override protected void paintComponent(Graphics g){
//                Graphics2D g2=(Graphics2D)g.create();
//                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
//                g2.setColor(getModel().isPressed()?bg.darker():getModel().isRollover()?bg.brighter():bg);
//                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
//                g2.setColor(Color.WHITE); g2.setFont(getFont());
//                FontMetrics fm=g2.getFontMetrics();
//                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()-fm.getHeight())/2+1);
//                g2.dispose();
//            }};
//        b.setFont(new Font("Segoe UI",Font.BOLD,12));
//        b.setForeground(Color.WHITE);b.setFocusPainted(false);b.setBorderPainted(false);b.setContentAreaFilled(false);
//        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        b.setPreferredSize(new Dimension(118,34)); return b;
//    }
//
//    private JDialog styledDialog(String title, int w, int h) {
//        JDialog d=new JDialog((Frame)SwingUtilities.getWindowAncestor(this),title,true);
//        d.setSize(w,h); d.setLocationRelativeTo(this);
//        d.getContentPane().setBackground(BG2); d.setLayout(new BorderLayout(12,12)); return d;
//    }
//
//    private JPanel formPanel(){
//        JPanel p=new JPanel(new GridBagLayout());
//        p.setBackground(BG2); p.setBorder(BorderFactory.createEmptyBorder(20,24,20,24)); return p;
//    }
//
//    private void addRow(JPanel form,String label,Component field,int row){
//        GridBagConstraints g=new GridBagConstraints();
//        g.fill=GridBagConstraints.HORIZONTAL; g.insets=new Insets(7,5,7,5);
//        g.gridx=0;g.gridy=row;g.weightx=0.30;
//        JLabel l=new JLabel(label); l.setFont(new Font("Segoe UI",Font.BOLD,12)); l.setForeground(H2); form.add(l,g);
//        g.gridx=1;g.weightx=0.70; form.add(field,g);
//    }
//
//    private JPanel btnsPanel(JButton... buttons){
//        JPanel p=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,12));
//        p.setBackground(BG); p.setBorder(BorderFactory.createMatteBorder(1,0,0,0,BG3));
//        for(JButton b:buttons)p.add(b); return p;
//    }
//
//    private JTextField tf(){return tf("");}
//    private JTextField tf(String text){
//        JTextField f=new JTextField(text);
//        f.setFont(new Font("Segoe UI",Font.PLAIN,13));
//        f.setBackground(BG3); f.setForeground(TEXT); f.setCaretColor(H2);
//        f.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(H1,1),
//                BorderFactory.createEmptyBorder(5,10,5,10)));
//        f.setPreferredSize(new Dimension(0,34)); return f;
//    }
//
//    private JTextArea descArea(){
//        JTextArea a=new JTextArea(3,20);
//        a.setFont(new Font("Segoe UI",Font.PLAIN,13));
//        a.setBackground(BG3); a.setForeground(TEXT); a.setCaretColor(H2);
//        a.setLineWrap(true); a.setWrapStyleWord(true);
//        a.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(H1,1),
//                BorderFactory.createEmptyBorder(5,8,5,8))); return a;
//    }
//
//    private void styleCombo(JComboBox<String> c){
//        c.setBackground(BG3); c.setForeground(TEXT);
//        c.setFont(new Font("Segoe UI",Font.PLAIN,13));
//    }
//
//    private String buildDetails(String header,String[]labels,int row){
//        StringBuilder sb=new StringBuilder(header).append("\n\n");
//        for(int i=0;i<labels.length;i++) sb.append(labels[i]).append(": ").append(tableModel.getValueAt(row,i)).append("\n");
//        return sb.toString();
//    }
//
//    private void error(String msg){JOptionPane.showMessageDialog(this,msg,"Error",JOptionPane.ERROR_MESSAGE);}
//    private void success(String msg){JOptionPane.showMessageDialog(this,msg,"Success",JOptionPane.INFORMATION_MESSAGE);}
//}





package ui;

import database.DBConnection;
import models.User;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;

/**
 * EventPanel - Role-Based Access Control
 *   ADMIN  : can Add, Edit, Delete ANY event
 *   MEMBER : can Add events; can Edit/Delete only their OWN events (created_by = user_id)
 *   All    : can View all events
 * Community Event Management System | VIT Bhopal MCA 2026
 */
public class EventPanel extends JPanel {

    private final User currentUser;          // <-- logged-in user
    private JTable            eventsTable;
    private DefaultTableModel tableModel;
    private JTextField        searchField;
    private JComboBox<String> filterCombo;
    private JLabel            roleInfoLabel;

    // Column indices in tableModel
    private static final int COL_ID        = 0;
    private static final int COL_NAME      = 1;
    private static final int COL_DESC      = 2;
    private static final int COL_DATE      = 3;
    private static final int COL_VENUE     = 4;
    private static final int COL_OWNER_ID  = 5;   // hidden: created_by user_id
    private static final int COL_STATUS    = 6;

    // Palette
    private static final Color BLUE      = new Color(67,  97, 238);
    private static final Color BLUE_H    = new Color(40,  70, 200);
    private static final Color SUCCESS   = new Color(34, 175, 120);
    private static final Color WARNING   = new Color(210, 130,  30);
    private static final Color DANGER    = new Color(210,  60,  55);
    private static final Color ADMIN_CLR = new Color(155,  30, 180);  // purple = admin
    private static final Color MEM_CLR   = new Color( 34, 150, 100);  // green  = member
    private static final Color BG        = new Color(14, 82, 243);
    private static final Color CARD      = new Color(2, 46, 126);
    private static final Color HDR_BG    = new Color(239, 239, 243);
    private static final Color HDR_FG    = new Color(4, 58, 163);
    private static final Color TEXT_D    = new Color( 30,  40,  80);
    private static final Color TEXT_M    = new Color( 90, 110, 155);
    private static final Color ROW_E     = new Color(8, 54, 241);
    private static final Color ROW_O     = new Color(73, 109, 241);
    private static final Color ROW_MINE  = new Color(235, 248, 240);  // highlight own rows
    private static final Color SEL_BG    = new Color(210, 220, 255);
    private static final Color SEL_FG    = new Color( 30,  50, 140);
    private static final Color BORDER    = new Color(210, 220, 245);

    public EventPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);
        initComponents();
        loadEvents();
    }

    // ---------- Layout -------------------------------------------------------

    private void initComponents() {
        add(buildTitleBar(), BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout(0, 10));
        body.setBackground(BG);
        body.setBorder(BorderFactory.createEmptyBorder(12, 16, 14, 16));
        body.add(buildRoleBanner(), BorderLayout.NORTH);
        body.add(buildSearchBar(), BorderLayout.CENTER);
        body.add(buildTable(),     BorderLayout.SOUTH);

        // Make table fill remaining space
        JPanel mid = new JPanel(new BorderLayout(0, 10));
        mid.setBackground(BG);
        mid.add(buildRoleBanner(), BorderLayout.NORTH);
        mid.add(buildSearchBar(),  BorderLayout.CENTER);
        body = new JPanel(new BorderLayout(0, 10));
        body.setBackground(BG);
        body.setBorder(BorderFactory.createEmptyBorder(12, 16, 14, 16));
        body.add(mid,           BorderLayout.NORTH);
        body.add(buildTable(),  BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    private JPanel buildTitleBar() {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0,0,new Color(30,50,140),getWidth(),0,new Color(50,80,180));
                g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.07f));
                g2.setColor(Color.WHITE); g2.fill(new Ellipse2D.Float(getWidth()-140,-30,170,170));
                g2.dispose();
            }
        };
        p.setOpaque(true); p.setPreferredSize(new Dimension(0,72));
        p.setBorder(BorderFactory.createEmptyBorder(0,22,0,18));

        JPanel left = new JPanel(new GridBagLayout()); left.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx=0; gc.gridy=0; gc.anchor=GridBagConstraints.WEST;
        JLabel badge = letterBadge("E", new Color(100,140,255));
        gc.insets=new Insets(0,0,0,10); left.add(badge,gc);
        gc.gridx=1; gc.insets=new Insets(0,0,0,0);
        JPanel tt = new JPanel(); tt.setLayout(new BoxLayout(tt,BoxLayout.Y_AXIS)); tt.setOpaque(false);
        JLabel t1 = new JLabel("Events"); t1.setFont(new Font("Segoe UI",Font.BOLD,22)); t1.setForeground(Color.WHITE); t1.setAlignmentX(LEFT_ALIGNMENT);
        JLabel t2 = new JLabel("Schedule and manage community events"); t2.setFont(new Font("Segoe UI",Font.PLAIN,12)); t2.setForeground(new Color(185,205,245)); t2.setAlignmentX(LEFT_ALIGNMENT);
        tt.add(t1); tt.add(t2); left.add(tt,gc);

        JButton addBtn = solidBtn("+ Add Event", SUCCESS);
        addBtn.addActionListener(e -> showAddDialog());
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0)); right.setOpaque(false); right.add(addBtn);
        p.add(left,BorderLayout.WEST); p.add(right,BorderLayout.EAST);
        return p;
    }

    /** Coloured role banner */
    private JPanel buildRoleBanner() {
        JPanel banner = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        boolean isAdmin = currentUser.isAdmin();
        banner.setBackground(isAdmin ? new Color(240,230,255) : new Color(230,248,240));
        banner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0, isAdmin ? new Color(190,150,230) : new Color(150,210,185)),
                BorderFactory.createEmptyBorder(0,6,0,6)));

        JLabel roleLbl = new JLabel(isAdmin ? "  ADMIN ACCESS  " : "  MEMBER ACCESS  ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isAdmin ? ADMIN_CLR : MEM_CLR); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(Color.WHITE); g2.setFont(getFont()); FontMetrics fm=g2.getFontMetrics(); String t=getText().trim();
                g2.drawString(t,(getWidth()-fm.stringWidth(t))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2); g2.dispose();
            }
        };
        roleLbl.setFont(new Font("Segoe UI",Font.BOLD,11)); roleLbl.setPreferredSize(new Dimension(120,22)); roleLbl.setOpaque(false);

        String desc = isAdmin
                ? "You can add, edit, and delete ANY event."
                : "You can add events. You can only edit/delete events YOU created (highlighted in green).";
        roleInfoLabel = new JLabel(desc);
        roleInfoLabel.setFont(new Font("Segoe UI",Font.PLAIN,12));
        roleInfoLabel.setForeground(isAdmin ? new Color(100,30,150) : new Color(20,100,60));

        banner.add(roleLbl); banner.add(roleInfoLabel);
        return banner;
    }

    private JPanel buildSearchBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT,10,6));
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER,1,true),
                BorderFactory.createEmptyBorder(2,6,2,6)));
        p.add(slbl("Search:"));
        searchField = new JTextField(18); styleTF(searchField);
        p.add(searchField);
        JButton sb = solidBtn("Search",BLUE); sb.setPreferredSize(new Dimension(88,32)); sb.addActionListener(e->searchEvents()); p.add(sb);
        p.add(Box.createHorizontalStrut(8));
        p.add(slbl("Filter:"));
        filterCombo = new JComboBox<>(new String[]{"All Events","Upcoming","Past","Today","This Week","This Month"});
        filterCombo.setFont(new Font("Segoe UI",Font.PLAIN,13)); filterCombo.setPreferredSize(new Dimension(148,32));
        filterCombo.addActionListener(e->loadEvents()); p.add(filterCombo);
        if (!currentUser.isAdmin()) {
            p.add(Box.createHorizontalStrut(6));
            JButton myBtn = solidBtn("My Events", MEM_CLR); myBtn.setPreferredSize(new Dimension(100,32));
            myBtn.addActionListener(e->loadMyEvents()); p.add(myBtn);
        }
        JButton rb = solidBtn("Refresh",TEXT_M); rb.setPreferredSize(new Dimension(80,32)); rb.addActionListener(e->loadEvents()); p.add(rb);
        return p;
    }

    private JPanel buildTable() {
        JPanel outer = new JPanel(new BorderLayout(0,0));
        outer.setBackground(CARD);
        outer.setBorder(BorderFactory.createLineBorder(BORDER,1,true));

        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT,14,7));
        hdr.setBackground(new Color(235,240,255));
        hdr.setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER));
        JLabel hl = new JLabel("Event Records"); hl.setFont(new Font("Segoe UI",Font.BOLD,12)); hl.setForeground(BLUE); hdr.add(hl);
        if (!currentUser.isAdmin()) {
            JLabel hint = new JLabel("   Green rows = your events (editable by you)");
            hint.setFont(new Font("Segoe UI",Font.PLAIN,11)); hint.setForeground(MEM_CLR); hdr.add(hint);
        }

        // 7 visible cols + hidden owner_id (col 5 hidden)
        String[] cols = {"ID","Event Name","Description","Date","Venue","Owner","Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r,int c){return false;}
        };

        eventsTable = new JTable(tableModel);
        eventsTable.setFont(new Font("Segoe UI",Font.PLAIN,13));
        eventsTable.setRowHeight(34); eventsTable.setShowGrid(false);
        eventsTable.setIntercellSpacing(new Dimension(0,0));
        eventsTable.setBackground(CARD); eventsTable.setForeground(TEXT_D);
        eventsTable.setSelectionBackground(SEL_BG); eventsTable.setSelectionForeground(SEL_FG);
        eventsTable.getColumnModel().getColumn(0).setMaxWidth(44);
        // Hide owner_id column
        eventsTable.getColumnModel().getColumn(COL_OWNER_ID).setMinWidth(0);
        eventsTable.getColumnModel().getColumn(COL_OWNER_ID).setMaxWidth(0);
        eventsTable.getColumnModel().getColumn(COL_OWNER_ID).setWidth(0);

        JTableHeader header = eventsTable.getTableHeader();
        header.setFont(new Font("Segoe UI",Font.BOLD,12));
        header.setBackground(HDR_BG); header.setForeground(HDR_FG);
        header.setPreferredSize(new Dimension(0,36)); header.setOpaque(true);
        header.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(60,90,180)));

        eventsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
                super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                boolean isOwner = isRowOwner(r);
                if(sel){ setBackground(SEL_BG); setForeground(SEL_FG); }
                else if(isOwner){ setBackground(ROW_MINE); setForeground(new Color(20,80,50)); }
                else{ setBackground(r%2==0?ROW_E:ROW_O); setForeground(TEXT_D); }
                setBorder(BorderFactory.createEmptyBorder(4,10,4,10)); return this;
            }
        });
        // Status badge renderer
        eventsTable.getColumnModel().getColumn(COL_STATUS).setCellRenderer(new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
                super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                String s=v==null?"":v.toString().toUpperCase();
                setHorizontalAlignment(CENTER); setBorder(BorderFactory.createEmptyBorder(4,8,4,8));
                if(sel){setBackground(SEL_BG);setForeground(SEL_FG);return this;}
                switch(s){
                    case "UPCOMING":  setBackground(new Color(215,230,255)); setForeground(new Color(30,60,180));  break;
                    case "ONGOING":   setBackground(new Color(210,245,230)); setForeground(new Color(20,120,70));  break;
                    case "COMPLETED": setBackground(new Color(230,230,255)); setForeground(new Color(80,50,180));  break;
                    case "CANCELLED": setBackground(new Color(255,220,220)); setForeground(new Color(160,30,30));  break;
                    default:          setBackground(new Color(235,238,245)); setForeground(TEXT_M);
                }
                setFont(new Font("Segoe UI",Font.BOLD,11)); return this;
            }
        });

        JScrollPane scroll = new JScrollPane(eventsTable);
        scroll.setBorder(BorderFactory.createEmptyBorder()); scroll.getViewport().setBackground(CARD);

        JPanel btns = buildActionButtons();

        outer.add(hdr,    BorderLayout.NORTH);
        outer.add(scroll, BorderLayout.CENTER);
        outer.add(btns,   BorderLayout.SOUTH);
        return outer;
    }

    private JPanel buildActionButtons() {
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,8));
        btns.setBackground(new Color(240,244,255));
        btns.setBorder(BorderFactory.createMatteBorder(1,0,0,0,BORDER));

        JButton vb = solidBtn("View",    BLUE);       vb.addActionListener(e -> viewDetails()); btns.add(vb);

        // For ADMIN: always enabled. For MEMBER: enabled only when own row selected
        JButton eb = solidBtn("Edit",    WARNING);
        eb.setToolTipText(currentUser.isAdmin() ? "Edit any event" : "Edit your own events only");
        eb.addActionListener(e -> editEvent()); btns.add(eb);

        JButton db = solidBtn("Delete",  DANGER);
        db.setToolTipText(currentUser.isAdmin() ? "Delete any event" : "Delete your own events only");
        db.addActionListener(e -> deleteEvent()); btns.add(db);

        // Live enable/disable based on selection
        eventsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean canModify = canModifySelected();
                eb.setEnabled(canModify); db.setEnabled(canModify);
                eb.setBackground(canModify ? WARNING : new Color(180,180,180));
                db.setBackground(canModify ? DANGER  : new Color(180,180,180));
            }
        });
        eb.setEnabled(false); db.setEnabled(false);  // disabled until row selected
        return btns;
    }

    // ---------- RBAC helpers --------------------------------------------------

    /** True if the currently logged-in user is allowed to modify the selected row */
    private boolean canModifySelected() {
        int row = eventsTable.getSelectedRow();
        if (row < 0) return false;
        if (currentUser.isAdmin()) return true;
        return isRowOwner(row);
    }

    /** True if the current user owns the event in the given row */
    private boolean isRowOwner(int row) {
        try {
            Object ownerVal = tableModel.getValueAt(row, COL_OWNER_ID);
            if (ownerVal == null) return false;
            int ownerId = Integer.parseInt(ownerVal.toString());
            return ownerId == currentUser.getUserId();
        } catch (Exception e) { return false; }
    }

    /** Show denial message for member trying to modify someone else's row */
    private boolean checkPermission() {
        int row = eventsTable.getSelectedRow();
        if (row < 0) { error("Please select an event first."); return false; }
        if (currentUser.isAdmin()) return true;
        if (isRowOwner(row)) return true;
        JOptionPane.showMessageDialog(this,
                "Access Denied!\n\nYou can only edit or delete events that you created.\nAdmins can modify any event.",
                "Permission Denied", JOptionPane.WARNING_MESSAGE);
        return false;
    }

    // ---------- Data ----------------------------------------------------------

    public void loadEvents() {
        tableModel.setRowCount(0);
        String base = "SELECT event_id, event_name, COALESCE(description,'') AS description, "
                + "event_date, COALESCE(venue,'') AS venue, COALESCE(created_by,0) AS created_by, status FROM events ";
        String filter = (String) filterCombo.getSelectedItem(); String where;
        switch(filter){
            case "Upcoming":   where="WHERE event_date>=CURDATE() "; break;
            case "Past":       where="WHERE event_date<CURDATE() ";  break;
            case "Today":      where="WHERE event_date=CURDATE() ";  break;
            case "This Week":  where="WHERE YEARWEEK(event_date,1)=YEARWEEK(CURDATE(),1) "; break;
            case "This Month": where="WHERE YEAR(event_date)=YEAR(CURDATE()) AND MONTH(event_date)=MONTH(CURDATE()) "; break;
            default:           where=""; break;
        }
        try(Connection conn=DBConnection.getConnection();Statement st=conn.createStatement();
            ResultSet rs=st.executeQuery(base+where+"ORDER BY event_date DESC")){
            while(rs.next()) tableModel.addRow(new Object[]{
                    rs.getInt("event_id"),rs.getString("event_name"),rs.getString("description"),
                    rs.getDate("event_date"),rs.getString("venue"),rs.getInt("created_by"),rs.getString("status")});
        }catch(SQLException e){error("Error loading events: "+e.getMessage());}
    }

    private void loadMyEvents() {
        tableModel.setRowCount(0);
        String sql = "SELECT event_id,event_name,COALESCE(description,'')AS description,event_date,"
                + "COALESCE(venue,'')AS venue,COALESCE(created_by,0)AS created_by,status "
                + "FROM events WHERE created_by=? ORDER BY event_date DESC";
        try(Connection conn=DBConnection.getConnection();PreparedStatement ps=conn.prepareStatement(sql)){
            ps.setInt(1, currentUser.getUserId()); ResultSet rs=ps.executeQuery();
            while(rs.next()) tableModel.addRow(new Object[]{
                    rs.getInt("event_id"),rs.getString("event_name"),rs.getString("description"),
                    rs.getDate("event_date"),rs.getString("venue"),rs.getInt("created_by"),rs.getString("status")});
            if(tableModel.getRowCount()==0) JOptionPane.showMessageDialog(this,"You have not created any events yet.","My Events",JOptionPane.INFORMATION_MESSAGE);
        }catch(SQLException e){error("Error: "+e.getMessage());}
    }

    private void searchEvents(){
        String kw=searchField.getText().trim(); if(kw.isEmpty()){loadEvents();return;}
        tableModel.setRowCount(0);
        String sql="SELECT event_id,event_name,COALESCE(description,'')AS description,event_date,"
                +"COALESCE(venue,'')AS venue,COALESCE(created_by,0)AS created_by,status FROM events "
                +"WHERE event_name LIKE? OR description LIKE? OR venue LIKE? ORDER BY event_date DESC";
        try(Connection conn=DBConnection.getConnection();PreparedStatement ps=conn.prepareStatement(sql)){
            String p="%"+kw+"%"; ps.setString(1,p);ps.setString(2,p);ps.setString(3,p);
            ResultSet rs=ps.executeQuery();
            while(rs.next()) tableModel.addRow(new Object[]{
                    rs.getInt("event_id"),rs.getString("event_name"),rs.getString("description"),
                    rs.getDate("event_date"),rs.getString("venue"),rs.getInt("created_by"),rs.getString("status")});
        }catch(SQLException e){error("Search error: "+e.getMessage());}
    }

    // ---------- CRUD ----------------------------------------------------------

    private void showAddDialog(){
        JDialog dlg=dlg("Add New Event",500,420);
        JTextField nameF=tf(),venueF=tf(),dateF=tf("YYYY-MM-DD"); dateF.setForeground(TEXT_M);
        dateF.addFocusListener(new FocusAdapter(){@Override public void focusGained(FocusEvent e){if(dateF.getText().equals("YYYY-MM-DD")){dateF.setText("");dateF.setForeground(TEXT_D);}}});
        JTextArea descA=ta();
        JComboBox<String> statusC=new JComboBox<>(new String[]{"UPCOMING","ONGOING","COMPLETED","CANCELLED"});
        JPanel form=form(); addRow(form,"Event Name:",nameF,0);addRow(form,"Description:",new JScrollPane(descA),1);
        addRow(form,"Date (YYYY-MM-DD):",dateF,2);addRow(form,"Venue:",venueF,3);addRow(form,"Status:",statusC,4);
        JButton save=solidBtn("Save Event",SUCCESS);
        save.addActionListener(e->{
            if(nameF.getText().trim().isEmpty()){error("Event name required!");return;}
            try(Connection conn=DBConnection.getConnection();PreparedStatement ps=conn.prepareStatement(
                    "INSERT INTO events(event_name,description,event_date,venue,status,created_by,organizer_id,created_date)VALUES(?,?,?,?,?,?,?,NOW())")){
                ps.setString(1,nameF.getText().trim());ps.setString(2,descA.getText().trim());
                ps.setString(3,dateF.getText().trim());ps.setString(4,venueF.getText().trim());
                ps.setString(5,(String)statusC.getSelectedItem());
                ps.setInt(6,currentUser.getUserId());     // <-- owner
                ps.setInt(7,currentUser.getUserId());
                ps.executeUpdate(); ok("Event added!"); loadEvents(); dlg.dispose();
            }catch(SQLException ex){error("Error: "+ex.getMessage());}
        });
        JButton cancel=solidBtn("Cancel",TEXT_M); cancel.addActionListener(e->dlg.dispose());
        dlg.add(form,BorderLayout.CENTER); dlg.add(btnRow(save,cancel),BorderLayout.SOUTH); dlg.setVisible(true);
    }

    private void viewDetails(){
        int r=eventsTable.getSelectedRow(); if(r<0){error("Select an event.");return;}
        boolean mine = isRowOwner(r);
        StringBuilder sb=new StringBuilder("Event Details\n\n");
        String[] labels={"ID","Name","Description","Date","Venue","","Status"};
        for(int i=0;i<tableModel.getColumnCount();i++){
            if(i==COL_OWNER_ID) continue;
            sb.append(labels[i]).append(": ").append(tableModel.getValueAt(r,i)).append("\n");
        }
        if(!currentUser.isAdmin()) sb.append("\nOwnership: ").append(mine?"You created this event.":"Created by another member.");
        JOptionPane.showMessageDialog(this,sb.toString(),"Event Details",JOptionPane.INFORMATION_MESSAGE);
    }

    private void editEvent(){
        if(!checkPermission()) return;
        int row=eventsTable.getSelectedRow();
        int id=(int)tableModel.getValueAt(row,COL_ID);
        JDialog dlg=dlg("Edit Event",500,420);
        JTextField nameF=tf(s(tableModel.getValueAt(row,COL_NAME)));
        JTextField dateF=tf(s(tableModel.getValueAt(row,COL_DATE)));
        JTextField venueF=tf(s(tableModel.getValueAt(row,COL_VENUE)));
        JTextArea descA=ta(); descA.setText(s(tableModel.getValueAt(row,COL_DESC)));
        JComboBox<String> statusC=new JComboBox<>(new String[]{"UPCOMING","ONGOING","COMPLETED","CANCELLED"});
        statusC.setSelectedItem(tableModel.getValueAt(row,COL_STATUS));
        JPanel form=form(); addRow(form,"Event Name:",nameF,0);addRow(form,"Description:",new JScrollPane(descA),1);
        addRow(form,"Date:",dateF,2);addRow(form,"Venue:",venueF,3);addRow(form,"Status:",statusC,4);
        JButton save=solidBtn("Update Event",SUCCESS);
        save.addActionListener(e->{
            try(Connection conn=DBConnection.getConnection();PreparedStatement ps=conn.prepareStatement(
                    "UPDATE events SET event_name=?,description=?,event_date=?,venue=?,status=? WHERE event_id=?")){
                ps.setString(1,nameF.getText().trim());ps.setString(2,descA.getText().trim());
                ps.setString(3,dateF.getText().trim());ps.setString(4,venueF.getText().trim());
                ps.setString(5,(String)statusC.getSelectedItem());ps.setInt(6,id);
                ps.executeUpdate(); ok("Event updated!"); loadEvents(); dlg.dispose();
            }catch(SQLException ex){error("Error: "+ex.getMessage());}
        });
        JButton cancel=solidBtn("Cancel",TEXT_M); cancel.addActionListener(e->dlg.dispose());
        dlg.add(form,BorderLayout.CENTER); dlg.add(btnRow(save,cancel),BorderLayout.SOUTH); dlg.setVisible(true);
    }

    private void deleteEvent(){
        if(!checkPermission()) return;
        int row=eventsTable.getSelectedRow();
        int id=(int)tableModel.getValueAt(row,COL_ID);
        String name=s(tableModel.getValueAt(row,COL_NAME));
        int confirm=JOptionPane.showConfirmDialog(this,
                "Delete event: \""+name+"\"?\nThis action cannot be undone.",
                "Confirm Delete",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
        if(confirm!=JOptionPane.YES_OPTION) return;
        try(Connection conn=DBConnection.getConnection();PreparedStatement ps=conn.prepareStatement("DELETE FROM events WHERE event_id=?")){
            ps.setInt(1,id); ps.executeUpdate(); ok("Event deleted!"); loadEvents();
        }catch(SQLException e){error("Error: "+e.getMessage());}
    }

    // ---------- Helpers -------------------------------------------------------

    private String s(Object o){return o==null?"":o.toString();}
    private JLabel slbl(String t){JLabel l=new JLabel(t);l.setFont(new Font("Segoe UI",Font.BOLD,12));l.setForeground(TEXT_M);return l;}

    private JLabel letterBadge(String letter, Color bg){
        JLabel b=new JLabel(letter){@Override protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
            g2.setColor(Color.WHITE);g2.setFont(new Font("Segoe UI",Font.BOLD,17));FontMetrics fm=g2.getFontMetrics();
            g2.drawString(letter,(getWidth()-fm.stringWidth(letter))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2-1);g2.dispose();}};
        b.setPreferredSize(new Dimension(36,36));b.setOpaque(false);return b;
    }

    private JButton solidBtn(String text,Color bg){
        JButton b=new JButton(text){@Override protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(!isEnabled()?new Color(180,185,200):getModel().isRollover()?bg.darker():bg);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
            g2.setColor(Color.WHITE);g2.setFont(getFont());FontMetrics fm=g2.getFontMetrics();String t=getText();
            g2.drawString(t,(getWidth()-fm.stringWidth(t))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);g2.dispose();}};
        b.setFont(new Font("Segoe UI",Font.BOLD,12));b.setFocusPainted(false);b.setBorderPainted(false);b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));b.setPreferredSize(new Dimension(110,32));return b;}

    private void styleTF(JTextField f){f.setFont(new Font("Segoe UI",Font.PLAIN,13));f.setBackground(new Color(246,248,255));f.setForeground(TEXT_D);f.setPreferredSize(new Dimension(180,32));f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER,1,true),BorderFactory.createEmptyBorder(4,9,4,9)));}
    private JDialog dlg(String title,int w,int h){JDialog d=new JDialog((Frame)SwingUtilities.getWindowAncestor(this),title,true);d.setSize(w,h);d.setLocationRelativeTo(this);d.getContentPane().setBackground(CARD);d.setLayout(new BorderLayout(12,12));return d;}
    private JPanel form(){JPanel p=new JPanel(new GridBagLayout());p.setBackground(CARD);p.setBorder(BorderFactory.createEmptyBorder(18,22,18,22));return p;}
    private void addRow(JPanel form,String label,Component field,int row){
        GridBagConstraints g=new GridBagConstraints();g.fill=GridBagConstraints.HORIZONTAL;g.insets=new Insets(6,4,6,4);
        g.gridx=0;g.gridy=row;g.weightx=0.30;JLabel l=new JLabel(label);l.setFont(new Font("Segoe UI",Font.BOLD,12));l.setForeground(BLUE);form.add(l,g);
        g.gridx=1;g.weightx=0.70;form.add(field,g);}
    private JPanel btnRow(JButton...btns){JPanel p=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));p.setBackground(new Color(238,242,255));p.setBorder(BorderFactory.createMatteBorder(1,0,0,0,BORDER));for(JButton b:btns)p.add(b);return p;}
    private JTextField tf(){return tf("");}
    private JTextField tf(String t){JTextField f=new JTextField(t);f.setFont(new Font("Segoe UI",Font.PLAIN,13));f.setBackground(new Color(246,248,255));f.setForeground(TEXT_D);f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(190,205,240),1),BorderFactory.createEmptyBorder(6,10,6,10)));f.setPreferredSize(new Dimension(0,34));return f;}
    private JTextArea ta(){JTextArea a=new JTextArea(3,20);a.setFont(new Font("Segoe UI",Font.PLAIN,13));a.setBackground(new Color(246,248,255));a.setForeground(TEXT_D);a.setLineWrap(true);a.setWrapStyleWord(true);a.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(190,205,240),1),BorderFactory.createEmptyBorder(5,8,5,8)));return a;}
    private void error(String m){JOptionPane.showMessageDialog(this,m,"Error",JOptionPane.ERROR_MESSAGE);}
    private void ok(String m){JOptionPane.showMessageDialog(this,m,"Done",JOptionPane.INFORMATION_MESSAGE);}
}