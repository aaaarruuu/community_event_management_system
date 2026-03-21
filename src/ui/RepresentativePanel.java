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
//public class RepresentativePanel extends JPanel {
//
//    private JTable            repsTable;
//    private DefaultTableModel tableModel;
//    private JTextField        searchField;
//    private JComboBox<String> filterCombo;
//
//    // ── Palette (Violet/Emerald theme) ───────────────────────────────────────
//    private static final Color H1      = new Color(139,  92, 246);   // violet
//    private static final Color H2      = new Color(167, 139, 250);   // lavender
//    private static final Color SUCCESS = new Color(  5, 150, 105);
//    private static final Color WARNING = new Color(217, 119,   6);
//    private static final Color DANGER  = new Color(220,  38,  38);
//    private static final Color INFO    = new Color( 14, 165, 233);
//    private static final Color BG      = new Color( 15,  23,  42);
//    private static final Color BG2     = new Color( 30,  41,  59);
//    private static final Color BG3     = new Color( 51,  65,  85);
//    private static final Color TH_BG   = new Color( 46,  16, 101);
//    private static final Color TEXT    = new Color(226, 232, 240);
//    private static final Color TEXT2   = new Color(148, 163, 184);
//    private static final Color SEL_BG  = new Color( 46,  16, 101);
//
//    public RepresentativePanel() {
//        setLayout(new BorderLayout(0,0));
//        setBackground(BG);
//        initComponents();
//        loadRepresentatives();
//    }
//
//    private void initComponents(){
//        add(buildTitleBar(),BorderLayout.NORTH);
//        JPanel body=new JPanel(new BorderLayout(0,14));
//        body.setBackground(BG); body.setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
//        body.add(buildSearchBar(),BorderLayout.NORTH);
//        body.add(buildTable(),BorderLayout.CENTER);
//        add(body,BorderLayout.CENTER);
//    }
//
//    private JPanel buildTitleBar(){
//        JPanel p=new JPanel(new BorderLayout()){
//            @Override protected void paintComponent(Graphics g){
//                Graphics2D g2=(Graphics2D)g.create();
//                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
//                GradientPaint gp=new GradientPaint(0,0,new Color(46,16,101),getWidth(),0,new Color(76,29,149));
//                g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());
//                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.15f));
//                g2.setColor(H2); g2.fill(new Ellipse2D.Float(getWidth()-150,-50,200,200));
//                g2.setColor(new Color(16,185,129)); g2.fill(new Ellipse2D.Float(-50,20,100,100));
//                g2.dispose(); super.paintComponent(g);
//            }
//        };
//        p.setOpaque(true); p.setPreferredSize(new Dimension(0,80));
//        p.setBorder(BorderFactory.createEmptyBorder(0,24,0,20));
//
//        JPanel left=new JPanel(new GridBagLayout()); left.setOpaque(false);
//        GridBagConstraints gc=new GridBagConstraints(); gc.gridx=0;gc.gridy=0;gc.anchor=GridBagConstraints.WEST;
//        JLabel icon=new JLabel("👥"); icon.setFont(new Font("Segoe UI Emoji",Font.PLAIN,30)); left.add(icon,gc);
//        gc.gridx=1;gc.insets=new Insets(0,10,0,0);
//        JPanel tt=new JPanel(); tt.setLayout(new BoxLayout(tt,BoxLayout.Y_AXIS)); tt.setOpaque(false);
//        JLabel title=new JLabel("Community Representatives");
//        title.setFont(new Font("Segoe UI",Font.BOLD,22)); title.setForeground(Color.WHITE); title.setAlignmentX(LEFT_ALIGNMENT);
//        JLabel sub=new JLabel("Manage service providers and staff");
//        sub.setFont(new Font("Segoe UI",Font.PLAIN,12)); sub.setForeground(new Color(196,181,253)); sub.setAlignmentX(LEFT_ALIGNMENT);
//        tt.add(title); tt.add(sub); left.add(tt,gc);
//
//        JButton addBtn=colorBtn("＋  Add Representative",SUCCESS);
//        addBtn.setPreferredSize(new Dimension(185,36));
//        addBtn.addActionListener(e->showAddDialog());
//        JPanel right=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0)); right.setOpaque(false); right.add(addBtn);
//        p.add(left,BorderLayout.WEST); p.add(right,BorderLayout.EAST);
//        return p;
//    }
//
//    private JPanel buildSearchBar(){
//        JPanel p=new JPanel(new FlowLayout(FlowLayout.LEFT,12,10));
//        p.setBackground(BG2);
//        p.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createMatteBorder(0,0,2,0,H1),
//                BorderFactory.createEmptyBorder(4,8,4,8)));
//
//        JLabel sl=new JLabel("SEARCH"); sl.setFont(new Font("Segoe UI",Font.BOLD,11)); sl.setForeground(H2);
//        searchField=new JTextField(22);
//        searchField.setFont(new Font("Segoe UI",Font.PLAIN,13));
//        searchField.setBackground(BG3); searchField.setForeground(TEXT); searchField.setCaretColor(H2);
//        searchField.setPreferredSize(new Dimension(200,34));
//        searchField.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(H1,1,true),BorderFactory.createEmptyBorder(4,10,4,10)));
//        JButton searchBtn=colorBtn("🔍  Go",H1); searchBtn.setPreferredSize(new Dimension(90,34));
//        searchBtn.addActionListener(e->searchReps());
//
//        JLabel fl=new JLabel("FILTER"); fl.setFont(new Font("Segoe UI",Font.BOLD,11)); fl.setForeground(H2);
//        filterCombo=new JComboBox<>(new String[]{"All Representatives","Active","Inactive","Busy"});
//        filterCombo.setFont(new Font("Segoe UI",Font.PLAIN,13));
//        filterCombo.setBackground(BG3); filterCombo.setForeground(TEXT);
//        filterCombo.setPreferredSize(new Dimension(175,34));
//        filterCombo.addActionListener(e->loadRepresentatives());
//
//        JButton refreshBtn=colorBtn("↺",BG3); refreshBtn.setPreferredSize(new Dimension(46,34));
//        refreshBtn.addActionListener(e->loadRepresentatives());
//
//        p.add(sl);p.add(searchField);p.add(searchBtn);p.add(Box.createHorizontalStrut(10));
//        p.add(fl);p.add(filterCombo);p.add(refreshBtn);
//        return p;
//    }
//
//    private JPanel buildTable(){
//        JPanel outer=new JPanel(new BorderLayout(0,0));
//        outer.setBackground(BG2);
//        outer.setBorder(BorderFactory.createLineBorder(H1,2,true));
//
//        JPanel label=new JPanel(new FlowLayout(FlowLayout.LEFT,14,8));
//        label.setBackground(TH_BG);
//        JLabel sectionLbl=new JLabel("REPRESENTATIVE RECORDS");
//        sectionLbl.setFont(new Font("Segoe UI",Font.BOLD,11)); sectionLbl.setForeground(H2);
//        label.add(sectionLbl);
//
//        String[]cols={"ID","Name","Phone","Email","Category","Status"};
//        tableModel=new DefaultTableModel(cols,0){
//            @Override public boolean isCellEditable(int r,int c){return false;}
//        };
//
//        repsTable=new JTable(tableModel);
//        repsTable.setFont(new Font("Segoe UI",Font.PLAIN,13));
//        repsTable.setRowHeight(36); repsTable.setShowGrid(false);
//        repsTable.setIntercellSpacing(new Dimension(0,0));
//        repsTable.setBackground(BG2); repsTable.setForeground(TEXT);
//        repsTable.setSelectionBackground(SEL_BG); repsTable.setSelectionForeground(H2);
//        repsTable.getColumnModel().getColumn(0).setMaxWidth(48);
//
//        JTableHeader header=repsTable.getTableHeader();
//        header.setFont(new Font("Segoe UI",Font.BOLD,12));
//        header.setBackground(TH_BG); header.setForeground(H2);
//        header.setPreferredSize(new Dimension(0,40)); header.setOpaque(true);
//        header.setBorder(BorderFactory.createMatteBorder(0,0,2,0,H1));
//
//        repsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
//            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
//                super.getTableCellRendererComponent(t,v,sel,foc,r,c);
//                if(sel){setBackground(SEL_BG);setForeground(H2);}
//                else{setBackground(r%2==0?BG2:new Color(22,33,52));setForeground(TEXT);}
//                setBorder(BorderFactory.createEmptyBorder(4,10,4,10)); return this;
//            }
//        });
//
//        // Category renderer
//        repsTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer(){
//            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
//                super.getTableCellRendererComponent(t,v,sel,foc,r,c);
//                setBorder(BorderFactory.createEmptyBorder(4,10,4,10));
//                if(sel){setBackground(SEL_BG);setForeground(H2);return this;}
//                setBackground(r%2==0?BG2:new Color(22,33,52));
//                // Color-code by category
//                String s=v==null?"":v.toString();
//                switch(s){
//                    case "Plumbing":    setForeground(new Color(125,211,252)); break;
//                    case "Electrical":  setForeground(new Color(253,230,138)); break;
//                    case "Maintenance": setForeground(new Color(167,139,250)); break;
//                    case "Cleaning":    setForeground(new Color(110,231,183)); break;
//                    case "Security":    setForeground(new Color(252,165,165)); break;
//                    case "Garden":      setForeground(new Color(134,239,172)); break;
//                    default:            setForeground(TEXT2);
//                }
//                setFont(new Font("Segoe UI",Font.BOLD,12)); return this;
//            }
//        });
//
//        // Status renderer
//        repsTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer(){
//            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
//                super.getTableCellRendererComponent(t,v,sel,foc,r,c);
//                String s=v==null?"":v.toString().toUpperCase();
//                setHorizontalAlignment(CENTER); setBorder(BorderFactory.createEmptyBorder(4,8,4,8));
//                if(sel){setBackground(SEL_BG);setForeground(H2);return this;}
//                switch(s){
//                    case "ACTIVE":   setBackground(new Color(6,78,59));    setForeground(new Color(110,231,183)); break;
//                    case "INACTIVE": setBackground(new Color(30,41,59));   setForeground(TEXT2);                  break;
//                    case "BUSY":     setBackground(new Color(120,53,15));  setForeground(new Color(253,186,116)); break;
//                    default:         setBackground(BG3);                   setForeground(TEXT2);
//                }
//                setFont(new Font("Segoe UI",Font.BOLD,11)); return this;
//            }
//        });
//
//        JScrollPane scroll=new JScrollPane(repsTable);
//        scroll.setBorder(BorderFactory.createEmptyBorder());
//        scroll.getViewport().setBackground(BG2); scroll.setBackground(BG2);
//
//        JPanel btns=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
//        btns.setBackground(TH_BG); btns.setBorder(BorderFactory.createMatteBorder(2,0,0,0,H1));
//        JButton vb=colorBtn("👁  View",INFO);   vb.addActionListener(e->viewDetails());
//        JButton eb=colorBtn("✏  Edit",WARNING); eb.addActionListener(e->editRep());
//        JButton db=colorBtn("🗑 Delete",DANGER); db.addActionListener(e->deleteRep());
//        btns.add(vb);btns.add(eb);btns.add(db);
//
//        outer.add(label,BorderLayout.NORTH);
//        outer.add(scroll,BorderLayout.CENTER);
//        outer.add(btns,BorderLayout.SOUTH);
//        return outer;
//    }
//
//    // ── Data ──────────────────────────────────────────────────────────────────
//    public void loadRepresentatives(){
//        tableModel.setRowCount(0);
//        String base="SELECT rep_id,rep_name,COALESCE(phone,'')AS phone,COALESCE(email,'')AS email,"
//                +"COALESCE(category,'')AS category,status FROM representatives ";
//        String filter=(String)filterCombo.getSelectedItem(); String where;
//        switch(filter){
//            case "Active":   where="WHERE status IN('ACTIVE','Active') ";   break;
//            case "Inactive": where="WHERE status IN('INACTIVE','Inactive')";break;
//            case "Busy":     where="WHERE status IN('BUSY','Busy') ";       break;
//            default:         where=""; break;
//        }
//        try(Connection conn=DBConnection.getConnection();Statement st=conn.createStatement();
//            ResultSet rs=st.executeQuery(base+where+"ORDER BY rep_name ASC")){
//            while(rs.next()) tableModel.addRow(new Object[]{
//                    rs.getInt("rep_id"),rs.getString("rep_name"),rs.getString("phone"),
//                    rs.getString("email"),rs.getString("category"),rs.getString("status")});
//        }catch(SQLException e){e.printStackTrace();error("Error: "+e.getMessage());}
//    }
//
//    private void searchReps(){
//        String kw=searchField.getText().trim(); if(kw.isEmpty()){loadRepresentatives();return;}
//        tableModel.setRowCount(0);
//        String sql="SELECT rep_id,rep_name,COALESCE(phone,'')AS phone,COALESCE(email,'')AS email,"
//                +"COALESCE(category,'')AS category,status FROM representatives "
//                +"WHERE rep_name LIKE? OR email LIKE? OR category LIKE? OR phone LIKE? ORDER BY rep_name";
//        try(Connection conn=DBConnection.getConnection();PreparedStatement ps=conn.prepareStatement(sql)){
//            String p="%"+kw+"%"; ps.setString(1,p);ps.setString(2,p);ps.setString(3,p);ps.setString(4,p);
//            ResultSet rs=ps.executeQuery();
//            while(rs.next()) tableModel.addRow(new Object[]{
//                    rs.getInt("rep_id"),rs.getString("rep_name"),rs.getString("phone"),
//                    rs.getString("email"),rs.getString("category"),rs.getString("status")});
//        }catch(SQLException e){error("Search error: "+e.getMessage());}
//    }
//
//    private void showAddDialog(){
//        JDialog dlg=styledDialog("Add New Representative",500,430);
//        JTextField nameF=tf(),phoneF=tf(),emailF=tf();
//        JComboBox<String> catC=new JComboBox<>(new String[]{"Plumbing","Electrical","Maintenance","Cleaning","Security","Garden","Carpentry","Painting","Other"});
//        JComboBox<String> statusC=new JComboBox<>(new String[]{"ACTIVE","INACTIVE","BUSY"});
//        styleCombo(catC); styleCombo(statusC);
//        JPanel form=formPanel();
//        addRow(form,"Name:",nameF,0);addRow(form,"Phone:",phoneF,1);addRow(form,"Email:",emailF,2);
//        addRow(form,"Category:",catC,3);addRow(form,"Status:",statusC,4);
//        JButton save=colorBtn("💾  Save",SUCCESS);
//        save.addActionListener(e->{
//            if(nameF.getText().trim().isEmpty()){error("Name required!");return;}
//            try(Connection conn=DBConnection.getConnection();
//                PreparedStatement ps=conn.prepareStatement("INSERT INTO representatives(rep_name,phone,email,category,status,is_available,registered_date)VALUES(?,?,?,?,?,?,NOW())")){
//                ps.setString(1,nameF.getText().trim());ps.setString(2,phoneF.getText().trim());
//                ps.setString(3,emailF.getText().trim());ps.setString(4,(String)catC.getSelectedItem());
//                String st=(String)statusC.getSelectedItem(); ps.setString(5,st); ps.setBoolean(6,"ACTIVE".equals(st));
//                ps.executeUpdate(); success("Representative added! ✅"); loadRepresentatives(); dlg.dispose();
//            }catch(SQLException ex){error("Error: "+ex.getMessage());}
//        });
//        JButton cancel=colorBtn("Cancel",BG3); cancel.addActionListener(e->dlg.dispose());
//        dlg.add(form,BorderLayout.CENTER); dlg.add(btnsPanel(save,cancel),BorderLayout.SOUTH); dlg.setVisible(true);
//    }
//
//    private void viewDetails(){
//        int row=repsTable.getSelectedRow(); if(row<0){error("Select a representative.");return;}
//        JOptionPane.showMessageDialog(this,buildDetails("👥 Representative Details",
//                new String[]{"ID","Name","Phone","Email","Category","Status"},row),"Details",JOptionPane.INFORMATION_MESSAGE);
//    }
//
//    private void editRep(){
//        int row=repsTable.getSelectedRow(); if(row<0){error("Select a representative to edit.");return;}
//        int id=(int)tableModel.getValueAt(row,0);
//        JDialog dlg=styledDialog("Edit Representative",500,430);
//        JTextField nameF=tf(str(tableModel.getValueAt(row,1)));
//        JTextField phoneF=tf(str(tableModel.getValueAt(row,2)));
//        JTextField emailF=tf(str(tableModel.getValueAt(row,3)));
//        JComboBox<String> catC=new JComboBox<>(new String[]{"Plumbing","Electrical","Maintenance","Cleaning","Security","Garden","Carpentry","Painting","Other"});
//        JComboBox<String> statusC=new JComboBox<>(new String[]{"ACTIVE","INACTIVE","BUSY"});
//        styleCombo(catC); styleCombo(statusC);
//        catC.setSelectedItem(tableModel.getValueAt(row,4));
//        statusC.setSelectedItem(tableModel.getValueAt(row,5));
//        JPanel form=formPanel();
//        addRow(form,"Name:",nameF,0);addRow(form,"Phone:",phoneF,1);addRow(form,"Email:",emailF,2);
//        addRow(form,"Category:",catC,3);addRow(form,"Status:",statusC,4);
//        JButton save=colorBtn("💾  Update",SUCCESS);
//        save.addActionListener(e->{
//            try(Connection conn=DBConnection.getConnection();
//                PreparedStatement ps=conn.prepareStatement("UPDATE representatives SET rep_name=?,phone=?,email=?,category=?,status=?,is_available=? WHERE rep_id=?")){
//                ps.setString(1,nameF.getText().trim());ps.setString(2,phoneF.getText().trim());
//                ps.setString(3,emailF.getText().trim());ps.setString(4,(String)catC.getSelectedItem());
//                String st=(String)statusC.getSelectedItem(); ps.setString(5,st); ps.setBoolean(6,"ACTIVE".equals(st));
//                ps.setInt(7,id); ps.executeUpdate(); success("Updated! ✅"); loadRepresentatives(); dlg.dispose();
//            }catch(SQLException ex){error("Error: "+ex.getMessage());}
//        });
//        JButton cancel=colorBtn("Cancel",BG3); cancel.addActionListener(e->dlg.dispose());
//        dlg.add(form,BorderLayout.CENTER); dlg.add(btnsPanel(save,cancel),BorderLayout.SOUTH); dlg.setVisible(true);
//    }
//
//    private void deleteRep(){
//        int row=repsTable.getSelectedRow(); if(row<0){error("Select a representative.");return;}
//        int id=(int)tableModel.getValueAt(row,0);
//        if(JOptionPane.showConfirmDialog(this,"Delete this representative?","Confirm",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION){
//            try(Connection conn=DBConnection.getConnection();
//                PreparedStatement ps=conn.prepareStatement("DELETE FROM representatives WHERE rep_id=?")){
//                ps.setInt(1,id);ps.executeUpdate();success("Deleted!");loadRepresentatives();
//            }catch(SQLException e){error("Error: "+e.getMessage());}
//        }
//    }
//
//    // ── Helpers ───────────────────────────────────────────────────────────────
//    private String str(Object o){return o==null?"":o.toString();}
//
//    private JButton colorBtn(String text,Color bg){
//        JButton b=new JButton(text){
//            @Override protected void paintComponent(Graphics g){
//                Graphics2D g2=(Graphics2D)g.create();
//                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
//                g2.setColor(getModel().isPressed()?bg.darker():getModel().isRollover()?bg.brighter():bg);
//                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
//                g2.setColor(Color.WHITE);g2.setFont(getFont());
//                FontMetrics fm=g2.getFontMetrics();
//                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()-fm.getHeight())/2+1);
//                g2.dispose();
//            }};
//        b.setFont(new Font("Segoe UI",Font.BOLD,12));
//        b.setForeground(Color.WHITE);b.setFocusPainted(false);b.setBorderPainted(false);b.setContentAreaFilled(false);
//        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        b.setPreferredSize(new Dimension(130,34)); return b;
//    }
//
//    private JDialog styledDialog(String title,int w,int h){
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
//        g.fill=GridBagConstraints.HORIZONTAL;g.insets=new Insets(7,5,7,5);
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
//        f.setBackground(BG3);f.setForeground(TEXT);f.setCaretColor(H2);
//        f.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(H1,1),BorderFactory.createEmptyBorder(5,10,5,10)));
//        f.setPreferredSize(new Dimension(0,34)); return f;
//    }
//
//    private void styleCombo(JComboBox<String> c){
//        c.setBackground(BG3);c.setForeground(TEXT);c.setFont(new Font("Segoe UI",Font.PLAIN,13));
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
 * RepresentativePanel – Role-Based Access Control
 *   ADMIN  : can Add, Edit, Delete any representative
 *   MEMBER : read-only (can view the directory, no modifications)
 * Community Event Management System | VIT Bhopal MCA 2026
 */
public class RepresentativePanel extends JPanel {

    private final User currentUser;
    private JTable            repsTable;
    private DefaultTableModel tableModel;
    private JTextField        searchField;
    private JComboBox<String> filterCombo;

    // Column indices
    private static final int COL_ID     = 0;
    private static final int COL_NAME   = 1;
    private static final int COL_PHONE  = 2;
    private static final int COL_EMAIL  = 3;
    private static final int COL_CAT    = 4;
    private static final int COL_STATUS = 5;

    // Palette
    private static final Color VIOLET    = new Color(100,  55, 190);
    private static final Color VIOLET2   = new Color(130,  90, 220);
    private static final Color SUCCESS   = new Color( 34, 175, 120);
    private static final Color WARNING   = new Color(210, 130,  30);
    private static final Color DANGER    = new Color(210,  60,  55);
    private static final Color INFO      = new Color( 67,  97, 238);
    private static final Color ADMIN_CLR = new Color(155,  30, 180);
    private static final Color MEM_CLR   = new Color( 67,  97, 238);
    private static final Color BG        = new Color(248, 245, 255);
    private static final Color CARD      = new Color(255, 255, 255);
    private static final Color HDR_BG    = new Color( 65,  30, 130);
    private static final Color HDR_FG    = new Color(235, 225, 255);
    private static final Color TEXT_D    = new Color( 35,  20,  70);
    private static final Color TEXT_M    = new Color(110,  85, 155);
    private static final Color ROW_E     = new Color(255, 255, 255);
    private static final Color ROW_O     = new Color(250, 247, 255);
    private static final Color SEL_BG    = new Color(225, 215, 255);
    private static final Color SEL_FG    = new Color( 60,  25, 130);
    private static final Color BORDER    = new Color(220, 210, 245);

    public RepresentativePanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);
        initComponents();
        loadRepresentatives();
    }

    // ── Layout ──────────────────────────────────────────────────────────────

    private void initComponents() {
        add(buildTitleBar(), BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout(0, 10));
        body.setBackground(BG);
        body.setBorder(BorderFactory.createEmptyBorder(12, 16, 14, 16));
        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.setBackground(BG);
        top.add(buildRoleBanner(), BorderLayout.NORTH);
        top.add(buildSearchBar(), BorderLayout.CENTER);
        body.add(top,          BorderLayout.NORTH);
        body.add(buildTable(), BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    // ── Title bar ──────────────────────────────────────────────────────────

    private JPanel buildTitleBar() {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0,0,new Color(60,25,130),getWidth(),0,new Color(90,45,175));
                g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.07f));
                g2.setColor(Color.WHITE);
                g2.fill(new Ellipse2D.Float(getWidth()-140,-30,170,170));
                g2.dispose();
            }
        };
        p.setOpaque(true); p.setPreferredSize(new Dimension(0,72));
        p.setBorder(BorderFactory.createEmptyBorder(0,22,0,18));

        JPanel left = new JPanel(new GridBagLayout()); left.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx=0; gc.gridy=0; gc.anchor=GridBagConstraints.WEST;
        JLabel badge = letterBadge("R", VIOLET2);
        gc.insets=new Insets(0,0,0,10); left.add(badge,gc);
        gc.gridx=1; gc.insets=new Insets(0,0,0,0);
        JPanel tt = new JPanel(); tt.setLayout(new BoxLayout(tt,BoxLayout.Y_AXIS)); tt.setOpaque(false);
        JLabel t1 = new JLabel("Representatives");
        t1.setFont(new Font("Segoe UI",Font.BOLD,22)); t1.setForeground(Color.WHITE); t1.setAlignmentX(LEFT_ALIGNMENT);
        JLabel t2 = new JLabel("Community service providers and staff");
        t2.setFont(new Font("Segoe UI",Font.PLAIN,12)); t2.setForeground(new Color(210,195,255)); t2.setAlignmentX(LEFT_ALIGNMENT);
        tt.add(t1); tt.add(t2); left.add(tt,gc);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0)); right.setOpaque(false);
        if (currentUser.isAdmin()) {
            JButton addBtn = solidBtn("+ Add Rep", SUCCESS);
            addBtn.setPreferredSize(new Dimension(120,36));
            addBtn.addActionListener(e -> showAddDialog());
            right.add(addBtn);
        }
        p.add(left, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    // ── Role banner ─────────────────────────────────────────────────────────

    private JPanel buildRoleBanner() {
        boolean isAdmin = currentUser.isAdmin();
        JPanel banner = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        banner.setBackground(isAdmin ? new Color(240,230,255) : new Color(230,240,255));
        banner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0, isAdmin ? new Color(190,150,230) : new Color(160,190,240)),
                BorderFactory.createEmptyBorder(0,6,0,6)));

        JLabel roleLbl = new JLabel(isAdmin ? "  ADMIN ACCESS  " : "  MEMBER ACCESS  ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isAdmin ? ADMIN_CLR : MEM_CLR);
                g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(Color.WHITE); g2.setFont(getFont());
                FontMetrics fm=g2.getFontMetrics(); String t=getText().trim();
                g2.drawString(t,(getWidth()-fm.stringWidth(t))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        roleLbl.setFont(new Font("Segoe UI",Font.BOLD,11));
        roleLbl.setPreferredSize(new Dimension(120,22)); roleLbl.setOpaque(false);

        String desc = isAdmin
                ? "You can add, edit, and delete any representative."
                : "View-only: Only administrators can add, edit, or delete representatives.";
        JLabel descLbl = new JLabel(desc);
        descLbl.setFont(new Font("Segoe UI",Font.PLAIN,12));
        descLbl.setForeground(isAdmin ? new Color(100,30,150) : new Color(40,70,170));

        banner.add(roleLbl); banner.add(descLbl);
        return banner;
    }

    // ── Search bar ──────────────────────────────────────────────────────────

    private JPanel buildSearchBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT,10,6));
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER,1,true),
                BorderFactory.createEmptyBorder(2,6,2,6)));
        p.add(slbl("Search:"));
        searchField = new JTextField(18); styleTF(searchField); p.add(searchField);
        JButton sb = solidBtn("Search", VIOLET);
        sb.setPreferredSize(new Dimension(88,32));
        sb.addActionListener(e -> searchReps()); p.add(sb);
        p.add(Box.createHorizontalStrut(8));
        p.add(slbl("Filter:"));
        filterCombo = new JComboBox<>(new String[]{"All","Active","Inactive","Busy"});
        filterCombo.setFont(new Font("Segoe UI",Font.PLAIN,13));
        filterCombo.setPreferredSize(new Dimension(130,32));
        filterCombo.addActionListener(e -> loadRepresentatives()); p.add(filterCombo);
        JButton rb = solidBtn("Refresh", TEXT_M);
        rb.setPreferredSize(new Dimension(80,32));
        rb.addActionListener(e -> loadRepresentatives()); p.add(rb);
        return p;
    }

    // ── Table ────────────────────────────────────────────────────────────────

    private JPanel buildTable() {
        JPanel outer = new JPanel(new BorderLayout(0,0));
        outer.setBackground(CARD);
        outer.setBorder(BorderFactory.createLineBorder(BORDER,1,true));

        // Header row
        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT,14,7));
        hdr.setBackground(new Color(242,238,255));
        hdr.setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER));
        JLabel hl = new JLabel("Representative Directory");
        hl.setFont(new Font("Segoe UI",Font.BOLD,12)); hl.setForeground(VIOLET); hdr.add(hl);
        if (!currentUser.isAdmin()) {
            JLabel note = new JLabel("   Read-only - contact an admin to make changes");
            note.setFont(new Font("Segoe UI",Font.PLAIN,11)); note.setForeground(new Color(80,100,180)); hdr.add(note);
        }

        String[] cols = {"ID","Name","Phone","Email","Category","Status"};
        tableModel = new DefaultTableModel(cols,0) {
            @Override public boolean isCellEditable(int r,int c) { return false; }
        };

        repsTable = new JTable(tableModel);
        repsTable.setFont(new Font("Segoe UI",Font.PLAIN,13));
        repsTable.setRowHeight(34); repsTable.setShowGrid(false);
        repsTable.setIntercellSpacing(new Dimension(0,0));
        repsTable.setBackground(CARD); repsTable.setForeground(TEXT_D);
        repsTable.setSelectionBackground(SEL_BG); repsTable.setSelectionForeground(SEL_FG);
        repsTable.getColumnModel().getColumn(0).setMaxWidth(46);

        JTableHeader header = repsTable.getTableHeader();
        header.setFont(new Font("Segoe UI",Font.BOLD,12));
        header.setBackground(HDR_BG); header.setForeground(HDR_FG);
        header.setPreferredSize(new Dimension(0,36)); header.setOpaque(true);
        header.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(80,30,160)));

        repsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
                super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                if(sel){ setBackground(SEL_BG); setForeground(SEL_FG); }
                else   { setBackground(r%2==0?ROW_E:ROW_O); setForeground(TEXT_D); }
                setBorder(BorderFactory.createEmptyBorder(4,10,4,10)); return this;
            }
        });

        // Category colour
        repsTable.getColumnModel().getColumn(COL_CAT).setCellRenderer(new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
                super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                setBorder(BorderFactory.createEmptyBorder(4,10,4,10));
                if(sel){ setBackground(SEL_BG); setForeground(SEL_FG); return this; }
                setBackground(r%2==0?ROW_E:ROW_O);
                switch(v==null?"":v.toString()){
                    case "Plumbing":    setForeground(new Color(20,100,180)); break;
                    case "Electrical":  setForeground(new Color(150,100,10)); break;
                    case "Maintenance": setForeground(new Color(80,50,180));  break;
                    case "Cleaning":    setForeground(new Color(20,130,80));  break;
                    case "Security":    setForeground(new Color(160,40,40));  break;
                    case "Garden":      setForeground(new Color(40,130,40));  break;
                    default:            setForeground(TEXT_M);
                }
                setFont(new Font("Segoe UI",Font.BOLD,12)); return this;
            }
        });

        // Status badge
        repsTable.getColumnModel().getColumn(COL_STATUS).setCellRenderer(new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
                super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                String s=v==null?"":v.toString().toUpperCase();
                setHorizontalAlignment(CENTER); setBorder(BorderFactory.createEmptyBorder(4,8,4,8));
                if(sel){ setBackground(SEL_BG); setForeground(SEL_FG); return this; }
                switch(s){
                    case "ACTIVE":   setBackground(new Color(210,245,225)); setForeground(new Color(20,120,70));  break;
                    case "INACTIVE": setBackground(new Color(235,235,240)); setForeground(new Color(90,90,120));  break;
                    case "BUSY":     setBackground(new Color(255,235,200)); setForeground(new Color(150,90,10));  break;
                    default:         setBackground(new Color(235,238,245)); setForeground(TEXT_M);
                }
                setFont(new Font("Segoe UI",Font.BOLD,11)); return this;
            }
        });

        JScrollPane scroll = new JScrollPane(repsTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(CARD);

        outer.add(hdr,    BorderLayout.NORTH);
        outer.add(scroll, BorderLayout.CENTER);
        outer.add(buildActionButtons(), BorderLayout.SOUTH);
        return outer;
    }

    // ── Action buttons ───────────────────────────────────────────────────────

    private JPanel buildActionButtons() {
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,8));
        btns.setBackground(new Color(242,238,255));
        btns.setBorder(BorderFactory.createMatteBorder(1,0,0,0,BORDER));

        // View is available to everyone
        JButton vb = solidBtn("View", INFO);
        vb.addActionListener(e -> viewDetails()); btns.add(vb);

        if (currentUser.isAdmin()) {
            // Edit and Delete only shown for admins
            JButton eb = solidBtn("Edit", WARNING);
            eb.addActionListener(e -> editRep()); btns.add(eb);
            JButton db = solidBtn("Delete", DANGER);
            db.addActionListener(e -> deleteRep()); btns.add(db);
        } else {
            // Members see a greyed-out label explaining the restriction
            JLabel lockNote = new JLabel("Edit / Delete: Admin only");
            lockNote.setFont(new Font("Segoe UI",Font.ITALIC,11));
            lockNote.setForeground(new Color(150,140,180));
            btns.add(lockNote);
        }
        return btns;
    }

    // ── Data ─────────────────────────────────────────────────────────────────

    public void loadRepresentatives() {
        tableModel.setRowCount(0);
        String base = "SELECT rep_id,rep_name,COALESCE(phone,'') AS phone,"
                + "COALESCE(email,'') AS email,COALESCE(category,'') AS category,status FROM representatives ";
        String filter = (String) filterCombo.getSelectedItem(); String where;
        switch(filter){
            case "Active":   where="WHERE status IN('ACTIVE','Active') ";   break;
            case "Inactive": where="WHERE status IN('INACTIVE','Inactive') ";break;
            case "Busy":     where="WHERE status IN('BUSY','Busy') ";        break;
            default:         where=""; break;
        }
        try(Connection conn=DBConnection.getConnection();Statement st=conn.createStatement();
            ResultSet rs=st.executeQuery(base+where+"ORDER BY rep_name ASC")){
            while(rs.next()) tableModel.addRow(new Object[]{
                    rs.getInt("rep_id"),rs.getString("rep_name"),rs.getString("phone"),
                    rs.getString("email"),rs.getString("category"),rs.getString("status")});
        }catch(SQLException e){ error("Error loading representatives: "+e.getMessage()); }
    }

    private void searchReps() {
        String kw = searchField.getText().trim();
        if(kw.isEmpty()){ loadRepresentatives(); return; }
        tableModel.setRowCount(0);
        String sql="SELECT rep_id,rep_name,COALESCE(phone,'')AS phone,COALESCE(email,'')AS email,"
                +"COALESCE(category,'')AS category,status FROM representatives "
                +"WHERE rep_name LIKE? OR email LIKE? OR category LIKE? OR phone LIKE? ORDER BY rep_name";
        try(Connection conn=DBConnection.getConnection();PreparedStatement ps=conn.prepareStatement(sql)){
            String p="%"+kw+"%"; ps.setString(1,p);ps.setString(2,p);ps.setString(3,p);ps.setString(4,p);
            ResultSet rs=ps.executeQuery();
            while(rs.next()) tableModel.addRow(new Object[]{
                    rs.getInt("rep_id"),rs.getString("rep_name"),rs.getString("phone"),
                    rs.getString("email"),rs.getString("category"),rs.getString("status")});
        }catch(SQLException e){ error("Search error: "+e.getMessage()); }
    }

    // ── CRUD (all guarded by admin check) ────────────────────────────────────

    private void showAddDialog() {
        // Admin-only – but we double-check
        if (!currentUser.isAdmin()) { denyAccess(); return; }

        JDialog dlg = dlg("Add New Representative", 480, 400);
        JTextField nameF=tf(), phoneF=tf(), emailF=tf();
        JComboBox<String> catC  = new JComboBox<>(new String[]{"Plumbing","Electrical","Maintenance","Cleaning","Security","Garden","Carpentry","Painting","Other"});
        JComboBox<String> statusC = new JComboBox<>(new String[]{"ACTIVE","INACTIVE","BUSY"});
        JPanel form = form();
        addRow(form,"Name:",   nameF,  0);
        addRow(form,"Phone:",  phoneF, 1);
        addRow(form,"Email:",  emailF, 2);
        addRow(form,"Category:", catC, 3);
        addRow(form,"Status:", statusC,4);

        JButton save = solidBtn("Save", SUCCESS);
        save.addActionListener(e -> {
            if(nameF.getText().trim().isEmpty()){ error("Name required!"); return; }
            try(Connection conn=DBConnection.getConnection();
                PreparedStatement ps=conn.prepareStatement(
                        "INSERT INTO representatives(rep_name,phone,email,category,status,is_available,registered_date)VALUES(?,?,?,?,?,?,NOW())")){
                String st = (String) statusC.getSelectedItem();
                ps.setString(1,nameF.getText().trim()); ps.setString(2,phoneF.getText().trim());
                ps.setString(3,emailF.getText().trim()); ps.setString(4,(String)catC.getSelectedItem());
                ps.setString(5,st); ps.setBoolean(6,"ACTIVE".equals(st));
                ps.executeUpdate(); ok("Representative added!"); loadRepresentatives(); dlg.dispose();
            }catch(SQLException ex){ error("Error: "+ex.getMessage()); }
        });
        JButton cancel = solidBtn("Cancel", TEXT_M); cancel.addActionListener(e -> dlg.dispose());
        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnRow(save, cancel), BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void viewDetails() {
        int r = repsTable.getSelectedRow(); if(r<0){ error("Select a representative."); return; }
        StringBuilder sb = new StringBuilder("Representative Details\n\n");
        String[] labels = {"ID","Name","Phone","Email","Category","Status"};
        for(int i=0;i<labels.length;i++)
            sb.append(labels[i]).append(": ").append(tableModel.getValueAt(r,i)).append("\n");
        if(!currentUser.isAdmin())
            sb.append("\nNote: Contact an admin to edit or remove this representative.");
        JOptionPane.showMessageDialog(this, sb.toString(), "Representative Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editRep() {
        if(!currentUser.isAdmin()){ denyAccess(); return; }
        int row = repsTable.getSelectedRow(); if(row<0){ error("Select a representative to edit."); return; }
        int id  = (int) tableModel.getValueAt(row, COL_ID);
        JDialog dlg = dlg("Edit Representative", 480, 400);
        JTextField nameF  = tf(s(tableModel.getValueAt(row,COL_NAME)));
        JTextField phoneF = tf(s(tableModel.getValueAt(row,COL_PHONE)));
        JTextField emailF = tf(s(tableModel.getValueAt(row,COL_EMAIL)));
        JComboBox<String> catC    = new JComboBox<>(new String[]{"Plumbing","Electrical","Maintenance","Cleaning","Security","Garden","Carpentry","Painting","Other"});
        JComboBox<String> statusC = new JComboBox<>(new String[]{"ACTIVE","INACTIVE","BUSY"});
        catC.setSelectedItem(tableModel.getValueAt(row,COL_CAT));
        statusC.setSelectedItem(tableModel.getValueAt(row,COL_STATUS));
        JPanel form = form();
        addRow(form,"Name:",   nameF,   0);
        addRow(form,"Phone:",  phoneF,  1);
        addRow(form,"Email:",  emailF,  2);
        addRow(form,"Category:", catC,  3);
        addRow(form,"Status:", statusC, 4);

        JButton save = solidBtn("Update", SUCCESS);
        save.addActionListener(e -> {
            try(Connection conn=DBConnection.getConnection();
                PreparedStatement ps=conn.prepareStatement(
                        "UPDATE representatives SET rep_name=?,phone=?,email=?,category=?,status=?,is_available=? WHERE rep_id=?")){
                String st = (String) statusC.getSelectedItem();
                ps.setString(1,nameF.getText().trim()); ps.setString(2,phoneF.getText().trim());
                ps.setString(3,emailF.getText().trim()); ps.setString(4,(String)catC.getSelectedItem());
                ps.setString(5,st); ps.setBoolean(6,"ACTIVE".equals(st)); ps.setInt(7,id);
                ps.executeUpdate(); ok("Representative updated!"); loadRepresentatives(); dlg.dispose();
            }catch(SQLException ex){ error("Error: "+ex.getMessage()); }
        });
        JButton cancel = solidBtn("Cancel", TEXT_M); cancel.addActionListener(e -> dlg.dispose());
        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnRow(save, cancel), BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void deleteRep() {
        if(!currentUser.isAdmin()){ denyAccess(); return; }
        int row = repsTable.getSelectedRow(); if(row<0){ error("Select a representative."); return; }
        int id   = (int) tableModel.getValueAt(row, COL_ID);
        String name = s(tableModel.getValueAt(row, COL_NAME));
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete representative \""+name+"\"?\nThis cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if(confirm != JOptionPane.YES_OPTION) return;
        try(Connection conn=DBConnection.getConnection();
            PreparedStatement ps=conn.prepareStatement("DELETE FROM representatives WHERE rep_id=?")){
            ps.setInt(1,id); ps.executeUpdate(); ok("Deleted!"); loadRepresentatives();
        }catch(SQLException e){ error("Error: "+e.getMessage()); }
    }

    // ── Denial helper ────────────────────────────────────────────────────────
    private void denyAccess() {
        JOptionPane.showMessageDialog(this,
                "Access Denied!\n\nOnly administrators can add, edit, or delete representatives.\n\n"
                        + "You are logged in as: " + currentUser.getUsername() + " (MEMBER)",
                "Permission Denied", JOptionPane.WARNING_MESSAGE);
    }

    // ── Generic helpers ──────────────────────────────────────────────────────

    private String s(Object o){ return o==null?"":o.toString(); }

    private JLabel slbl(String t){
        JLabel l=new JLabel(t); l.setFont(new Font("Segoe UI",Font.BOLD,12)); l.setForeground(TEXT_M); return l;
    }

    private JLabel letterBadge(String letter, Color bg){
        JLabel b=new JLabel(letter){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                g2.setColor(Color.WHITE); g2.setFont(new Font("Segoe UI",Font.BOLD,17));
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(letter,(getWidth()-fm.stringWidth(letter))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2-1);
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(36,36)); b.setOpaque(false); return b;
    }

    private JButton solidBtn(String text, Color bg){
        JButton b=new JButton(text){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(!isEnabled()?new Color(180,185,200):getModel().isRollover()?bg.darker():bg);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.setColor(Color.WHITE); g2.setFont(getFont());
                FontMetrics fm=g2.getFontMetrics(); String t=getText();
                g2.drawString(t,(getWidth()-fm.stringWidth(t))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        b.setFont(new Font("Segoe UI",Font.BOLD,12)); b.setFocusPainted(false);
        b.setBorderPainted(false); b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(110,32)); return b;
    }

    private void styleTF(JTextField f){
        f.setFont(new Font("Segoe UI",Font.PLAIN,13));
        f.setBackground(new Color(250,247,255)); f.setForeground(TEXT_D);
        f.setPreferredSize(new Dimension(180,32));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER,1,true),
                BorderFactory.createEmptyBorder(4,9,4,9)));
    }

    private JDialog dlg(String title, int w, int h){
        JDialog d=new JDialog((Frame)SwingUtilities.getWindowAncestor(this),title,true);
        d.setSize(w,h); d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(CARD); d.setLayout(new BorderLayout(12,12)); return d;
    }

    private JPanel form(){
        JPanel p=new JPanel(new GridBagLayout()); p.setBackground(CARD);
        p.setBorder(BorderFactory.createEmptyBorder(18,22,18,22)); return p;
    }

    private void addRow(JPanel form, String label, Component field, int row){
        GridBagConstraints g=new GridBagConstraints();
        g.fill=GridBagConstraints.HORIZONTAL; g.insets=new Insets(6,4,6,4);
        g.gridx=0; g.gridy=row; g.weightx=0.30;
        JLabel l=new JLabel(label); l.setFont(new Font("Segoe UI",Font.BOLD,12)); l.setForeground(VIOLET); form.add(l,g);
        g.gridx=1; g.weightx=0.70; form.add(field,g);
    }

    private JPanel btnRow(JButton... btns){
        JPanel p=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
        p.setBackground(new Color(242,238,255));
        p.setBorder(BorderFactory.createMatteBorder(1,0,0,0,BORDER));
        for(JButton b:btns) p.add(b); return p;
    }

    private JTextField tf(){ return tf(""); }
    private JTextField tf(String t){
        JTextField f=new JTextField(t);
        f.setFont(new Font("Segoe UI",Font.PLAIN,13));
        f.setBackground(new Color(250,247,255)); f.setForeground(TEXT_D);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER,1),
                BorderFactory.createEmptyBorder(6,10,6,10)));
        f.setPreferredSize(new Dimension(0,34)); return f;
    }

    private void error(String m){ JOptionPane.showMessageDialog(this,m,"Error",JOptionPane.ERROR_MESSAGE); }
    private void ok(String m)   { JOptionPane.showMessageDialog(this,m,"Done",JOptionPane.INFORMATION_MESSAGE); }
}