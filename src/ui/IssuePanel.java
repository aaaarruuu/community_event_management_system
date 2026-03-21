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
//public class IssuePanel extends JPanel {
//
//    private JTable            issuesTable;
//    private DefaultTableModel tableModel;
//    private JTextField        searchField;
//    private JComboBox<String> filterCombo;
//
//    private boolean hasTitle=false, hasReportedBy=false, hasReporterId=false, hasReportedDate=false;
//
//    // ── Palette (Rose/Pink theme) ─────────────────────────────────────────────
//    private static final Color H1      = new Color(244,  63,  94);   // rose
//    private static final Color H2      = new Color(251, 113, 133);   // light rose
//    private static final Color SUCCESS = new Color(  5, 150, 105);
//    private static final Color WARNING = new Color(217, 119,   6);
//    private static final Color INFO    = new Color( 14, 165, 233);
//    private static final Color BG      = new Color( 15,  23,  42);
//    private static final Color BG2     = new Color( 30,  41,  59);
//    private static final Color BG3     = new Color( 51,  65,  85);
//    private static final Color TH_BG   = new Color(100,   7,  30);
//    private static final Color TEXT    = new Color(226, 232, 240);
//    private static final Color TEXT2   = new Color(148, 163, 184);
//    private static final Color SEL_BG  = new Color(100,   7,  30);
//
//    public IssuePanel() {
//        setLayout(new BorderLayout(0,0));
//        setBackground(BG);
//        detectColumns();
//        initComponents();
//        loadIssues();
//    }
//
//    private void detectColumns() {
//        try(Connection conn=DBConnection.getConnection()){
//            DatabaseMetaData meta=conn.getMetaData();
//            ResultSet cols=meta.getColumns(null,null,"issues",null);
//            while(cols.next()){
//                String col=cols.getString("COLUMN_NAME").toLowerCase();
//                switch(col){
//                    case "title":         hasTitle=true;        break;
//                    case "reported_by":   hasReportedBy=true;   break;
//                    case "reporter_id":   hasReporterId=true;   break;
//                    case "reported_date": hasReportedDate=true; break;
//                }
//            }
//        }catch(SQLException e){ e.printStackTrace(); }
//    }
//
//    private String buildSelectClause(){
//        StringBuilder sb=new StringBuilder("SELECT issue_id, ");
//        sb.append(hasTitle?"COALESCE(title,description,'N/A')":"COALESCE(description,'N/A')").append(" AS display_title, ");
//        sb.append("category, priority, status, ");
//        if(hasReportedBy&&hasReporterId) sb.append("COALESCE(reported_by,CAST(reporter_id AS CHAR),'') AS reporter, ");
//        else if(hasReportedBy)           sb.append("COALESCE(reported_by,'') AS reporter, ");
//        else if(hasReporterId)           sb.append("COALESCE(CAST(reporter_id AS CHAR),'') AS reporter, ");
//        else                             sb.append("'' AS reporter, ");
//        sb.append(hasReportedDate?"reported_date":"NOW() AS reported_date").append(" FROM issues ");
//        return sb.toString();
//    }
//
//    private String buildInsertSQL(){
//        StringBuilder cols=new StringBuilder("INSERT INTO issues (description,category,priority,status");
//        StringBuilder vals=new StringBuilder("VALUES (?,?,?,'PENDING'");
//        if(hasTitle){cols.append(",title");vals.append(",?");}
//        if(hasReportedBy){cols.append(",reported_by");vals.append(",?");}
//        if(hasReportedDate){cols.append(",reported_date");vals.append(",NOW()");}
//        cols.append(") "); vals.append(")");
//        return cols+vals.toString();
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
//                GradientPaint gp=new GradientPaint(0,0,new Color(136,19,55),getWidth(),0,new Color(190,18,60));
//                g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());
//                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.15f));
//                g2.setColor(H2); g2.fill(new Ellipse2D.Float(getWidth()-130,-40,170,170));
//                g2.fill(new Ellipse2D.Float(-40,10,90,90));
//                g2.dispose(); super.paintComponent(g);
//            }
//        };
//        p.setOpaque(true); p.setPreferredSize(new Dimension(0,80));
//        p.setBorder(BorderFactory.createEmptyBorder(0,24,0,20));
//
//        JPanel left=new JPanel(new GridBagLayout()); left.setOpaque(false);
//        GridBagConstraints gc=new GridBagConstraints(); gc.gridx=0;gc.gridy=0;gc.anchor=GridBagConstraints.WEST;
//        JLabel icon=new JLabel("🚨"); icon.setFont(new Font("Segoe UI Emoji",Font.PLAIN,30)); left.add(icon,gc);
//        gc.gridx=1;gc.insets=new Insets(0,10,0,0);
//        JPanel tt=new JPanel(); tt.setLayout(new BoxLayout(tt,BoxLayout.Y_AXIS)); tt.setOpaque(false);
//        JLabel title=new JLabel("Community Issues");
//        title.setFont(new Font("Segoe UI",Font.BOLD,22)); title.setForeground(Color.WHITE); title.setAlignmentX(LEFT_ALIGNMENT);
//        JLabel sub=new JLabel("Track and resolve society complaints");
//        sub.setFont(new Font("Segoe UI",Font.PLAIN,12)); sub.setForeground(new Color(253,164,175)); sub.setAlignmentX(LEFT_ALIGNMENT);
//        tt.add(title); tt.add(sub); left.add(tt,gc);
//
//        JButton addBtn=colorBtn("＋  Report Issue",H1);
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
//        searchBtn.addActionListener(e->searchIssues());
//
//        JLabel fl=new JLabel("FILTER"); fl.setFont(new Font("Segoe UI",Font.BOLD,11)); fl.setForeground(H2);
//        filterCombo=new JComboBox<>(new String[]{"All Issues","Pending","In Progress","Resolved","High Priority","Critical"});
//        filterCombo.setFont(new Font("Segoe UI",Font.PLAIN,13));
//        filterCombo.setBackground(BG3); filterCombo.setForeground(TEXT);
//        filterCombo.setPreferredSize(new Dimension(155,34));
//        filterCombo.addActionListener(e->loadIssues());
//
//        JButton refreshBtn=colorBtn("↺",BG3); refreshBtn.setPreferredSize(new Dimension(46,34));
//        refreshBtn.addActionListener(e->loadIssues());
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
//        JLabel sectionLbl=new JLabel("ISSUE RECORDS");
//        sectionLbl.setFont(new Font("Segoe UI",Font.BOLD,11)); sectionLbl.setForeground(H2);
//        label.add(sectionLbl);
//
//        String[]cols={"ID","Title / Description","Category","Priority","Status","Reported By","Date"};
//        tableModel=new DefaultTableModel(cols,0){
//            @Override public boolean isCellEditable(int r,int c){return false;}
//        };
//
//        issuesTable=new JTable(tableModel);
//        issuesTable.setFont(new Font("Segoe UI",Font.PLAIN,13));
//        issuesTable.setRowHeight(36); issuesTable.setShowGrid(false);
//        issuesTable.setIntercellSpacing(new Dimension(0,0));
//        issuesTable.setBackground(BG2); issuesTable.setForeground(TEXT);
//        issuesTable.setSelectionBackground(SEL_BG); issuesTable.setSelectionForeground(H2);
//        issuesTable.getColumnModel().getColumn(0).setMaxWidth(48);
//
//        JTableHeader header=issuesTable.getTableHeader();
//        header.setFont(new Font("Segoe UI",Font.BOLD,12));
//        header.setBackground(TH_BG); header.setForeground(H2);
//        header.setPreferredSize(new Dimension(0,40)); header.setOpaque(true);
//        header.setBorder(BorderFactory.createMatteBorder(0,0,2,0,H1));
//
//        issuesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
//            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
//                super.getTableCellRendererComponent(t,v,sel,foc,r,c);
//                if(sel){setBackground(SEL_BG);setForeground(H2);}
//                else{setBackground(r%2==0?BG2:new Color(22,33,52));setForeground(TEXT);}
//                setBorder(BorderFactory.createEmptyBorder(4,10,4,10)); return this;
//            }
//        });
//
//        // Priority renderer
//        issuesTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer(){
//            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
//                super.getTableCellRendererComponent(t,v,sel,foc,r,c);
//                String s=v==null?"":v.toString().toUpperCase();
//                setHorizontalAlignment(CENTER); setBorder(BorderFactory.createEmptyBorder(4,8,4,8));
//                if(sel){setBackground(SEL_BG);setForeground(H2);return this;}
//                switch(s){
//                    case "CRITICAL": setBackground(new Color(127,29,29));   setForeground(new Color(252,165,165)); break;
//                    case "HIGH":     setBackground(new Color(124,45,18));   setForeground(new Color(253,186,116)); break;
//                    case "MEDIUM":   setBackground(new Color(120,53,15));   setForeground(new Color(253,230,138)); break;
//                    case "LOW":      setBackground(new Color(6,78,59));     setForeground(new Color(110,231,183)); break;
//                    default:         setBackground(BG3);                    setForeground(TEXT2);
//                }
//                setFont(new Font("Segoe UI",Font.BOLD,11)); return this;
//            }
//        });
//
//        // Status renderer
//        issuesTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer(){
//            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
//                super.getTableCellRendererComponent(t,v,sel,foc,r,c);
//                String s=v==null?"":v.toString().toUpperCase();
//                setHorizontalAlignment(CENTER); setBorder(BorderFactory.createEmptyBorder(4,8,4,8));
//                if(sel){setBackground(SEL_BG);setForeground(H2);return this;}
//                switch(s){
//                    case "PENDING":     setBackground(new Color(120,53,15));   setForeground(new Color(253,230,138)); break;
//                    case "IN_PROGRESS": setBackground(new Color(12,74,110));   setForeground(new Color(125,211,252)); break;
//                    case "COMPLETED":   setBackground(new Color(6,78,59));     setForeground(new Color(110,231,183)); break;
//                    case "CANCELLED":   setBackground(new Color(49,46,129));   setForeground(new Color(167,139,250)); break;
//                    default:            setBackground(BG3);                    setForeground(TEXT2);
//                }
//                setFont(new Font("Segoe UI",Font.BOLD,11)); return this;
//            }
//        });
//
//        JScrollPane scroll=new JScrollPane(issuesTable);
//        scroll.setBorder(BorderFactory.createEmptyBorder());
//        scroll.getViewport().setBackground(BG2); scroll.setBackground(BG2);
//
//        JPanel btns=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
//        btns.setBackground(TH_BG); btns.setBorder(BorderFactory.createMatteBorder(2,0,0,0,H1));
//        JButton vb=colorBtn("👁  View",INFO);   vb.addActionListener(e->viewDetails());
//        JButton ub=colorBtn("🔄  Update",WARNING); ub.addActionListener(e->updateStatus());
//        JButton db=colorBtn("🗑 Delete",H1);    db.addActionListener(e->deleteIssue());
//        btns.add(vb);btns.add(ub);btns.add(db);
//
//        outer.add(label,BorderLayout.NORTH);
//        outer.add(scroll,BorderLayout.CENTER);
//        outer.add(btns,BorderLayout.SOUTH);
//        return outer;
//    }
//
//    // ── Data ──────────────────────────────────────────────────────────────────
//    public void loadIssues(){
//        if(tableModel==null)return; tableModel.setRowCount(0);
//        String base=buildSelectClause();
//        String filter=(String)filterCombo.getSelectedItem(); String where;
//        switch(filter){
//            case "Pending":       where="WHERE status IN('PENDING','Pending') ";              break;
//            case "In Progress":   where="WHERE status IN('IN_PROGRESS','In Progress') ";      break;
//            case "Resolved":      where="WHERE status IN('COMPLETED','Resolved') ";           break;
//            case "High Priority": where="WHERE priority IN('HIGH','High') ";                  break;
//            case "Critical":      where="WHERE priority IN('CRITICAL','Critical') ";          break;
//            default:              where=""; break;
//        }
//        String order=hasReportedDate?"ORDER BY reported_date DESC":"ORDER BY issue_id DESC";
//        try(Connection conn=DBConnection.getConnection();Statement st=conn.createStatement();
//            ResultSet rs=st.executeQuery(base+where+order)){
//            while(rs.next()) tableModel.addRow(new Object[]{
//                    rs.getInt("issue_id"),rs.getString("display_title"),rs.getString("category"),
//                    rs.getString("priority"),rs.getString("status"),rs.getString("reporter"),
//                    hasReportedDate?rs.getDate("reported_date"):null});
//        }catch(SQLException e){e.printStackTrace();error("Error: "+e.getMessage());}
//    }
//
//    private void searchIssues(){
//        String kw=searchField.getText().trim(); if(kw.isEmpty()){loadIssues();return;}
//        tableModel.setRowCount(0);
//        String tp=hasTitle?"COALESCE(title,description,'')":"COALESCE(description,'')";
//        String rp=hasReportedBy?"COALESCE(reported_by,'')":"''";
//        String order=hasReportedDate?"ORDER BY reported_date DESC":"ORDER BY issue_id DESC";
//        String sql=buildSelectClause()+"WHERE "+tp+" LIKE? OR COALESCE(category,'') LIKE? OR "+rp+" LIKE? "+order;
//        try(Connection conn=DBConnection.getConnection();PreparedStatement ps=conn.prepareStatement(sql)){
//            String p="%"+kw+"%"; ps.setString(1,p);ps.setString(2,p);ps.setString(3,p);
//            ResultSet rs=ps.executeQuery();
//            while(rs.next()) tableModel.addRow(new Object[]{
//                    rs.getInt("issue_id"),rs.getString("display_title"),rs.getString("category"),
//                    rs.getString("priority"),rs.getString("status"),rs.getString("reporter"),
//                    hasReportedDate?rs.getDate("reported_date"):null});
//        }catch(SQLException e){error("Search error: "+e.getMessage());}
//    }
//
//    private void showAddDialog(){
//        JDialog dlg=styledDialog("Report New Issue",520,500);
//        JTextField titleF=tf(), reportedByF=tf();
//        JTextArea descA=descArea();
//        JComboBox<String> catC=new JComboBox<>(new String[]{"Plumbing","Electrical","Maintenance","Cleaning","Security","Garden","Other"});
//        JComboBox<String> priC=new JComboBox<>(new String[]{"LOW","MEDIUM","HIGH","CRITICAL"});
//        styleCombo(catC); styleCombo(priC); priC.setSelectedItem("MEDIUM");
//        JPanel form=formPanel();
//        addRow(form,"Title:",titleF,0); addRow(form,"Description:",new JScrollPane(descA),1);
//        addRow(form,"Category:",catC,2); addRow(form,"Priority:",priC,3);
//        if(hasReportedBy) addRow(form,"Reported By:",reportedByF,4);
//        JButton save=colorBtn("💾  Submit",SUCCESS);
//        save.addActionListener(e->{
//            if(titleF.getText().trim().isEmpty()){error("Title required!");return;}
//            String descVal=titleF.getText().trim()+(descA.getText().trim().isEmpty()?"":" - "+descA.getText().trim());
//            String sql=buildInsertSQL();
//            try(Connection conn=DBConnection.getConnection();PreparedStatement ps=conn.prepareStatement(sql)){
//                int idx=1;
//                ps.setString(idx++,descVal);
//                ps.setString(idx++,(String)catC.getSelectedItem());
//                ps.setString(idx++,(String)priC.getSelectedItem());
//                if(hasTitle)      ps.setString(idx++,titleF.getText().trim());
//                if(hasReportedBy) ps.setString(idx++,reportedByF.getText().trim());
//                ps.executeUpdate(); success("Issue reported! 🚨"); loadIssues(); dlg.dispose();
//            }catch(SQLException ex){ex.printStackTrace();error("Error: "+ex.getMessage());}
//        });
//        JButton cancel=colorBtn("Cancel",BG3); cancel.addActionListener(e->dlg.dispose());
//        dlg.add(form,BorderLayout.CENTER); dlg.add(btnsPanel(save,cancel),BorderLayout.SOUTH); dlg.setVisible(true);
//    }
//
//    private void viewDetails(){
//        int row=issuesTable.getSelectedRow(); if(row<0){error("Select an issue.");return;}
//        JOptionPane.showMessageDialog(this,buildDetails("🚨 Issue Details",
//                new String[]{"ID","Title","Category","Priority","Status","Reported By","Date"},row),"Issue Details",JOptionPane.INFORMATION_MESSAGE);
//    }
//
//    private void updateStatus(){
//        int row=issuesTable.getSelectedRow(); if(row<0){error("Select an issue.");return;}
//        int id=(int)tableModel.getValueAt(row,0);
//        String[] statuses={"PENDING","IN_PROGRESS","COMPLETED","CANCELLED"};
//        String ns=(String)JOptionPane.showInputDialog(this,"Select new status:","Update Status",
//                JOptionPane.QUESTION_MESSAGE,null,statuses,statuses[0]);
//        if(ns==null)return;
//        try(Connection conn=DBConnection.getConnection();
//            PreparedStatement ps=conn.prepareStatement("UPDATE issues SET status=? WHERE issue_id=?")){
//            ps.setString(1,ns);ps.setInt(2,id);ps.executeUpdate();success("Status updated! ✅");loadIssues();
//        }catch(SQLException e){error("Error: "+e.getMessage());}
//    }
//
//    private void deleteIssue(){
//        int row=issuesTable.getSelectedRow(); if(row<0){error("Select an issue.");return;}
//        int id=(int)tableModel.getValueAt(row,0);
//        if(JOptionPane.showConfirmDialog(this,"Delete this issue?","Confirm",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION){
//            try(Connection conn=DBConnection.getConnection();
//                PreparedStatement ps=conn.prepareStatement("DELETE FROM issues WHERE issue_id=?")){
//                ps.setInt(1,id);ps.executeUpdate();success("Deleted!");loadIssues();
//            }catch(SQLException e){error("Error: "+e.getMessage());}
//        }
//    }
//
//    // ── Helpers ───────────────────────────────────────────────────────────────
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
//    private JTextArea descArea(){
//        JTextArea a=new JTextArea(3,20);
//        a.setFont(new Font("Segoe UI",Font.PLAIN,13));
//        a.setBackground(BG3);a.setForeground(TEXT);a.setCaretColor(H2);
//        a.setLineWrap(true);a.setWrapStyleWord(true);
//        a.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(H1,1),BorderFactory.createEmptyBorder(5,8,5,8))); return a;
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

public class IssuePanel extends JPanel {

    private final User currentUser;
    private JTable            issuesTable;
    private DefaultTableModel tableModel;
    private JTextField        searchField;
    private JComboBox<String> filterCombo;

    // Dynamic column detection
    private boolean hasTitle=false, hasReportedBy=false, hasReporterId=false, hasReportedDate=false;

    // Column indices
    private static final int COL_ID       = 0;
    private static final int COL_TITLE    = 1;
    private static final int COL_CAT      = 2;
    private static final int COL_PRIO     = 3;
    private static final int COL_STATUS   = 4;
    private static final int COL_REPORTER = 5;
    private static final int COL_DATE     = 6;
    private static final int COL_OWNER_ID = 7;  // hidden: reporter_id (integer)

    // Palette
    private static final Color ROSE      = new Color(195,  55,  55);
    private static final Color ROSE2     = new Color(220,  85,  85);
    private static final Color SUCCESS   = new Color( 34, 175, 120);
    private static final Color WARNING   = new Color(210, 130,  30);
    private static final Color INFO      = new Color( 67,  97, 238);
    private static final Color ADMIN_CLR = new Color(155,  30, 180);
    private static final Color MEM_CLR   = new Color( 34, 150, 100);
    private static final Color BG        = new Color(2, 10, 94);
    private static final Color CARD      = new Color(243, 165, 165);
    private static final Color HDR_BG    = new Color(120,  30,  30);
    private static final Color HDR_FG    = new Color(255, 225, 225);
    private static final Color TEXT_D    = new Color( 60,  20,  20);
    private static final Color TEXT_M    = new Color(130,  80,  80);
    private static final Color ROW_E     = new Color(228, 62, 4);
    private static final Color ROW_O     = new Color(241, 85, 26);
    private static final Color ROW_MINE  = new Color(243, 232, 69);
    private static final Color SEL_BG    = new Color(39, 255, 255);
    private static final Color SEL_FG    = new Color(100,  20,  20);
    private static final Color BORDER    = new Color(241, 15, 15);

    public IssuePanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0,0));
        setBackground(BG);
        detectColumns();
        initComponents();
        loadIssues();
    }

    // ---------- Column detection ---------------------------------------------

    private void detectColumns(){
        try(Connection conn=DBConnection.getConnection()){
            DatabaseMetaData meta=conn.getMetaData();
            ResultSet cols=meta.getColumns(null,null,"issues",null);
            while(cols.next()){switch(cols.getString("COLUMN_NAME").toLowerCase()){
                case "title":hasTitle=true;break;
                case "reported_by":hasReportedBy=true;break;
                case "reporter_id":hasReporterId=true;break;
                case "reported_date":hasReportedDate=true;break;
            }}
        }catch(SQLException e){e.printStackTrace();}
    }

    private String buildSelectClause(){
        StringBuilder sb=new StringBuilder("SELECT issue_id, ");
        sb.append(hasTitle?"COALESCE(title,description,'N/A')":"COALESCE(description,'N/A')").append(" AS display_title, ");
        sb.append("category, priority, status, ");
        if(hasReportedBy) sb.append("COALESCE(reported_by,'') AS reporter, ");
        else              sb.append("'' AS reporter, ");
        sb.append(hasReportedDate?"reported_date":"NOW() AS reported_date");
        // Always fetch reporter_id for RBAC
        sb.append(", COALESCE(reporter_id, 0) AS reporter_id FROM issues ");
        return sb.toString();
    }

    // ---------- Layout -------------------------------------------------------

    private void initComponents(){
        add(buildTitleBar(),BorderLayout.NORTH);
        JPanel body=new JPanel(new BorderLayout(0,10));
        body.setBackground(BG); body.setBorder(BorderFactory.createEmptyBorder(12,16,14,16));
        JPanel mid=new JPanel(new BorderLayout(0,10)); mid.setBackground(BG);
        mid.add(buildRoleBanner(),BorderLayout.NORTH);
        mid.add(buildSearchBar(),BorderLayout.CENTER);
        body.add(mid,BorderLayout.NORTH);
        body.add(buildTable(),BorderLayout.CENTER);
        add(body,BorderLayout.CENTER);
    }

    private JPanel buildTitleBar(){
        JPanel p=new JPanel(new BorderLayout()){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                GradientPaint gp=new GradientPaint(0,0,new Color(120,25,25),getWidth(),0,new Color(170,40,40));
                g2.setPaint(gp);g2.fillRect(0,0,getWidth(),getHeight());
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.07f));
                g2.setColor(Color.WHITE);g2.fill(new Ellipse2D.Float(getWidth()-140,-30,170,170));
                g2.dispose();
            }
        };
        p.setOpaque(true);p.setPreferredSize(new Dimension(0,72));p.setBorder(BorderFactory.createEmptyBorder(0,22,0,18));

        JPanel left=new JPanel(new GridBagLayout());left.setOpaque(false);
        GridBagConstraints gc=new GridBagConstraints();gc.gridx=0;gc.gridy=0;gc.anchor=GridBagConstraints.WEST;
        JLabel badge=letterBadge("I",ROSE2);gc.insets=new Insets(0,0,0,10);left.add(badge,gc);
        gc.gridx=1;gc.insets=new Insets(0,0,0,0);
        JPanel tt=new JPanel();tt.setLayout(new BoxLayout(tt,BoxLayout.Y_AXIS));tt.setOpaque(false);
        JLabel t1=new JLabel("Issues");t1.setFont(new Font("Segoe UI",Font.BOLD,22));t1.setForeground(Color.WHITE);t1.setAlignmentX(LEFT_ALIGNMENT);
        JLabel t2=new JLabel("Track and resolve community complaints");t2.setFont(new Font("Segoe UI",Font.PLAIN,12));t2.setForeground(new Color(255,195,195));t2.setAlignmentX(LEFT_ALIGNMENT);
        tt.add(t1);tt.add(t2);left.add(tt,gc);
        JButton addBtn=solidBtn("+ Report Issue",ROSE);addBtn.addActionListener(e->showAddDialog());
        JPanel right=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));right.setOpaque(false);right.add(addBtn);
        p.add(left,BorderLayout.WEST);p.add(right,BorderLayout.EAST);return p;
    }

    private JPanel buildRoleBanner(){
        JPanel banner=new JPanel(new FlowLayout(FlowLayout.LEFT,10,6));
        boolean isAdmin=currentUser.isAdmin();
        banner.setBackground(isAdmin?new Color(240,230,255):new Color(230,248,240));
        banner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0,isAdmin?new Color(190,150,230):new Color(150,210,185)),
                BorderFactory.createEmptyBorder(0,6,0,6)));

        JLabel roleLbl=new JLabel(isAdmin?"  ADMIN ACCESS  ":"  MEMBER ACCESS  "){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isAdmin?ADMIN_CLR:MEM_CLR);g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(Color.WHITE);g2.setFont(getFont());FontMetrics fm=g2.getFontMetrics();String t=getText().trim();
                g2.drawString(t,(getWidth()-fm.stringWidth(t))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);g2.dispose();}};
        roleLbl.setFont(new Font("Segoe UI",Font.BOLD,11));roleLbl.setPreferredSize(new Dimension(120,22));roleLbl.setOpaque(false);

        String desc=isAdmin?"You can view, update status, and delete ANY issue."
                :"You can report issues. You can only edit/delete issues YOU reported (highlighted in green).";
        JLabel descLbl=new JLabel(desc);descLbl.setFont(new Font("Segoe UI",Font.PLAIN,12));
        descLbl.setForeground(isAdmin?new Color(100,30,150):new Color(20,100,60));
        banner.add(roleLbl);banner.add(descLbl);return banner;
    }

    private JPanel buildSearchBar(){
        JPanel p=new JPanel(new FlowLayout(FlowLayout.LEFT,10,6));
        p.setBackground(CARD);p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER,1,true),BorderFactory.createEmptyBorder(2,6,2,6)));
        p.add(slbl("Search:"));
        searchField=new JTextField(18);styleTF(searchField);p.add(searchField);
        JButton sb=solidBtn("Search",ROSE);sb.setPreferredSize(new Dimension(88,32));sb.addActionListener(e->searchIssues());p.add(sb);
        p.add(Box.createHorizontalStrut(8));p.add(slbl("Filter:"));
        filterCombo=new JComboBox<>(new String[]{"All Issues","Pending","In Progress","Resolved","High Priority","Critical"});
        filterCombo.setFont(new Font("Segoe UI",Font.PLAIN,13));filterCombo.setPreferredSize(new Dimension(155,32));
        filterCombo.addActionListener(e->loadIssues());p.add(filterCombo);
        if(!currentUser.isAdmin()){
            p.add(Box.createHorizontalStrut(6));
            JButton myBtn=solidBtn("My Issues",MEM_CLR);myBtn.setPreferredSize(new Dimension(95,32));
            myBtn.addActionListener(e->loadMyIssues());p.add(myBtn);
        }
        JButton rb=solidBtn("Refresh",TEXT_M);rb.setPreferredSize(new Dimension(80,32));rb.addActionListener(e->loadIssues());p.add(rb);
        return p;
    }

    private JPanel buildTable(){
        JPanel outer=new JPanel(new BorderLayout(0,0));
        outer.setBackground(CARD);outer.setBorder(BorderFactory.createLineBorder(BORDER,1,true));

        JPanel hdr=new JPanel(new FlowLayout(FlowLayout.LEFT,14,7));hdr.setBackground(new Color(255,238,238));
        hdr.setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER));
        JLabel hl=new JLabel("Issue Records");hl.setFont(new Font("Segoe UI",Font.BOLD,12));hl.setForeground(ROSE);hdr.add(hl);
        if(!currentUser.isAdmin()){
            JLabel hint=new JLabel("   Green rows = your issues (editable by you)");
            hint.setFont(new Font("Segoe UI",Font.PLAIN,11));hint.setForeground(MEM_CLR);hdr.add(hint);
        }

        String[] cols={"ID","Title","Category","Priority","Status","Reporter","Date","OwnerID"};
        tableModel=new DefaultTableModel(cols,0){@Override public boolean isCellEditable(int r,int c){return false;}};

        issuesTable=new JTable(tableModel);
        issuesTable.setFont(new Font("Segoe UI",Font.PLAIN,13));issuesTable.setRowHeight(34);
        issuesTable.setShowGrid(false);issuesTable.setIntercellSpacing(new Dimension(0,0));
        issuesTable.setBackground(CARD);issuesTable.setForeground(TEXT_D);
        issuesTable.setSelectionBackground(SEL_BG);issuesTable.setSelectionForeground(SEL_FG);
        issuesTable.getColumnModel().getColumn(0).setMaxWidth(44);
        // Hide OwnerID column
        issuesTable.getColumnModel().getColumn(COL_OWNER_ID).setMinWidth(0);
        issuesTable.getColumnModel().getColumn(COL_OWNER_ID).setMaxWidth(0);
        issuesTable.getColumnModel().getColumn(COL_OWNER_ID).setWidth(0);

        JTableHeader header=issuesTable.getTableHeader();
        header.setFont(new Font("Segoe UI",Font.BOLD,12));header.setBackground(HDR_BG);header.setForeground(HDR_FG);
        header.setPreferredSize(new Dimension(0,36));header.setOpaque(true);
        header.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(160,30,30)));

        issuesTable.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
                super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                boolean mine=isRowOwner(r);
                if(sel){setBackground(SEL_BG);setForeground(SEL_FG);}
                else if(mine){setBackground(ROW_MINE);setForeground(new Color(20,80,50));}
                else{setBackground(r%2==0?ROW_E:ROW_O);setForeground(TEXT_D);}
                setBorder(BorderFactory.createEmptyBorder(4,10,4,10));return this;
            }
        });

        // Priority badge
        issuesTable.getColumnModel().getColumn(COL_PRIO).setCellRenderer(new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
                super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                String s=v==null?"":v.toString().toUpperCase();
                setHorizontalAlignment(CENTER);setBorder(BorderFactory.createEmptyBorder(4,8,4,8));
                if(sel){setBackground(SEL_BG);setForeground(SEL_FG);return this;}
                switch(s){
                    case "CRITICAL":setBackground(new Color(255,210,210));setForeground(new Color(160,20,20));break;
                    case "HIGH":    setBackground(new Color(255,230,200));setForeground(new Color(160,80,10));break;
                    case "MEDIUM":  setBackground(new Color(255,245,200));setForeground(new Color(140,100,10));break;
                    case "LOW":     setBackground(new Color(210,245,225));setForeground(new Color(20,120,70));break;
                    default:        setBackground(new Color(235,238,245));setForeground(TEXT_M);
                }
                setFont(new Font("Segoe UI",Font.BOLD,11));return this;
            }
        });
        // Status badge
        issuesTable.getColumnModel().getColumn(COL_STATUS).setCellRenderer(new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
                super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                String s=v==null?"":v.toString().toUpperCase();
                setHorizontalAlignment(CENTER);setBorder(BorderFactory.createEmptyBorder(4,8,4,8));
                if(sel){setBackground(SEL_BG);setForeground(SEL_FG);return this;}
                switch(s){
                    case "PENDING":     setBackground(new Color(255,240,200));setForeground(new Color(140,90,10));break;
                    case "IN_PROGRESS": setBackground(new Color(215,230,255));setForeground(new Color(30,60,180));break;
                    case "COMPLETED":   setBackground(new Color(210,245,225));setForeground(new Color(20,120,70));break;
                    case "CANCELLED":   setBackground(new Color(230,225,255));setForeground(new Color(80,50,180));break;
                    default:            setBackground(new Color(235,238,245));setForeground(TEXT_M);
                }
                setFont(new Font("Segoe UI",Font.BOLD,11));return this;
            }
        });

        JScrollPane scroll=new JScrollPane(issuesTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());scroll.getViewport().setBackground(CARD);

        JPanel btns=buildActionButtons();
        outer.add(hdr,BorderLayout.NORTH);outer.add(scroll,BorderLayout.CENTER);outer.add(btns,BorderLayout.SOUTH);
        return outer;
    }

    private JPanel buildActionButtons(){
        JPanel btns=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,8));
        btns.setBackground(new Color(255,240,240));btns.setBorder(BorderFactory.createMatteBorder(1,0,0,0,BORDER));
        JButton vb=solidBtn("View",INFO);vb.addActionListener(e->viewDetails());btns.add(vb);
        JButton ub=solidBtn("Update Status",WARNING);ub.setPreferredSize(new Dimension(130,32));
        ub.setToolTipText(currentUser.isAdmin()?"Update status of any issue":"Update status of your own issues only");
        ub.addActionListener(e->updateStatus());btns.add(ub);
        JButton db=solidBtn("Delete",ROSE);
        db.setToolTipText(currentUser.isAdmin()?"Delete any issue":"Delete your own issues only");
        db.addActionListener(e->deleteIssue());btns.add(db);

        issuesTable.getSelectionModel().addListSelectionListener(e->{
            if(!e.getValueIsAdjusting()){
                boolean can=canModifySelected();
                ub.setEnabled(can);db.setEnabled(can);
            }
        });
        ub.setEnabled(false);db.setEnabled(false);
        return btns;
    }

    // ---------- RBAC helpers -------------------------------------------------

    private boolean canModifySelected(){
        int row=issuesTable.getSelectedRow();
        if(row<0) return false;
        if(currentUser.isAdmin()) return true;
        return isRowOwner(row);
    }

    private boolean isRowOwner(int row){
        try{
            Object v=tableModel.getValueAt(row,COL_OWNER_ID);
            if(v==null) return false;
            return Integer.parseInt(v.toString())==currentUser.getUserId();
        }catch(Exception e){return false;}
    }

    private boolean checkPermission(){
        int row=issuesTable.getSelectedRow();
        if(row<0){error("Please select an issue first.");return false;}
        if(currentUser.isAdmin()) return true;
        if(isRowOwner(row)) return true;
        JOptionPane.showMessageDialog(this,
                "Access Denied!\n\nYou can only edit or delete issues that you reported.\nAdmins can modify any issue.",
                "Permission Denied",JOptionPane.WARNING_MESSAGE);
        return false;
    }

    // ---------- Data ---------------------------------------------------------

    public void loadIssues(){
        if(tableModel==null) return;
        tableModel.setRowCount(0);
        String base=buildSelectClause();
        String filter=(String)filterCombo.getSelectedItem();String where;
        switch(filter){
            case "Pending":       where="WHERE status IN('PENDING','Pending') ";        break;
            case "In Progress":   where="WHERE status IN('IN_PROGRESS','In Progress') ";break;
            case "Resolved":      where="WHERE status IN('COMPLETED','Resolved') ";     break;
            case "High Priority": where="WHERE priority IN('HIGH','High') ";            break;
            case "Critical":      where="WHERE priority IN('CRITICAL','Critical') ";    break;
            default:              where=""; break;
        }
        String order=hasReportedDate?"ORDER BY reported_date DESC":"ORDER BY issue_id DESC";
        try(Connection conn=DBConnection.getConnection();Statement st=conn.createStatement();
            ResultSet rs=st.executeQuery(base+where+order)){
            while(rs.next()) tableModel.addRow(new Object[]{
                    rs.getInt("issue_id"),rs.getString("display_title"),rs.getString("category"),
                    rs.getString("priority"),rs.getString("status"),rs.getString("reporter"),
                    hasReportedDate?rs.getDate("reported_date"):null,
                    rs.getInt("reporter_id")});   // <-- owner id (hidden)
        }catch(SQLException e){e.printStackTrace();error("Error: "+e.getMessage());}
    }

    private void loadMyIssues(){
        tableModel.setRowCount(0);
        String base=buildSelectClause();
        String order=hasReportedDate?"ORDER BY reported_date DESC":"ORDER BY issue_id DESC";
        try(Connection conn=DBConnection.getConnection();
            PreparedStatement ps=conn.prepareStatement(base+"WHERE reporter_id=? "+order)){
            ps.setInt(1,currentUser.getUserId());ResultSet rs=ps.executeQuery();
            while(rs.next()) tableModel.addRow(new Object[]{
                    rs.getInt("issue_id"),rs.getString("display_title"),rs.getString("category"),
                    rs.getString("priority"),rs.getString("status"),rs.getString("reporter"),
                    hasReportedDate?rs.getDate("reported_date"):null,
                    rs.getInt("reporter_id")});
            if(tableModel.getRowCount()==0) JOptionPane.showMessageDialog(this,"You have not reported any issues yet.","My Issues",JOptionPane.INFORMATION_MESSAGE);
        }catch(SQLException e){error("Error: "+e.getMessage());}
    }

    private void searchIssues(){
        String kw=searchField.getText().trim();if(kw.isEmpty()){loadIssues();return;}
        tableModel.setRowCount(0);
        String tp=hasTitle?"COALESCE(title,description,'')":"COALESCE(description,'')";
        String rp=hasReportedBy?"COALESCE(reported_by,'')":"''";
        String sql=buildSelectClause()+"WHERE "+tp+" LIKE? OR COALESCE(category,'') LIKE? OR "+rp+" LIKE? "+(hasReportedDate?"ORDER BY reported_date DESC":"ORDER BY issue_id DESC");
        try(Connection conn=DBConnection.getConnection();PreparedStatement ps=conn.prepareStatement(sql)){
            String p="%"+kw+"%";ps.setString(1,p);ps.setString(2,p);ps.setString(3,p);
            ResultSet rs=ps.executeQuery();
            while(rs.next()) tableModel.addRow(new Object[]{
                    rs.getInt("issue_id"),rs.getString("display_title"),rs.getString("category"),
                    rs.getString("priority"),rs.getString("status"),rs.getString("reporter"),
                    hasReportedDate?rs.getDate("reported_date"):null,
                    rs.getInt("reporter_id")});
        }catch(SQLException e){error("Search error: "+e.getMessage());}
    }

    // ---------- CRUD ---------------------------------------------------------

    private void showAddDialog(){
        JDialog dlg=dlg("Report New Issue",500,460);
        JTextField titleF=tf(),reportedByF=tf(currentUser.getFullName());
        JTextArea descA=ta();
        JComboBox<String> catC=new JComboBox<>(new String[]{"Plumbing","Electrical","Maintenance","Cleaning","Security","Garden","Other"});
        JComboBox<String> priC=new JComboBox<>(new String[]{"LOW","MEDIUM","HIGH","CRITICAL"});priC.setSelectedItem("MEDIUM");
        JPanel form=form();addRow(form,"Title:",titleF,0);addRow(form,"Description:",new JScrollPane(descA),1);
        addRow(form,"Category:",catC,2);addRow(form,"Priority:",priC,3);
        if(hasReportedBy) addRow(form,"Reported By:",reportedByF,4);
        JButton save=solidBtn("Submit Issue",SUCCESS);
        save.addActionListener(e->{
            if(titleF.getText().trim().isEmpty()){error("Title required!");return;}
            String descVal=titleF.getText().trim()+(descA.getText().trim().isEmpty()?"":" - "+descA.getText().trim());
            StringBuilder colSql=new StringBuilder("INSERT INTO issues (description,category,priority,status,reporter_id");
            StringBuilder valSql=new StringBuilder("VALUES (?,?,?,'PENDING',?");
            if(hasTitle){colSql.append(",title");valSql.append(",?");}
            if(hasReportedBy){colSql.append(",reported_by");valSql.append(",?");}
            if(hasReportedDate){colSql.append(",reported_date");valSql.append(",NOW()");}
            colSql.append(") ");valSql.append(")");
            try(Connection conn=DBConnection.getConnection();PreparedStatement ps=conn.prepareStatement(colSql+valSql.toString())){
                int idx=1;
                ps.setString(idx++,descVal);
                ps.setString(idx++,(String)catC.getSelectedItem());
                ps.setString(idx++,(String)priC.getSelectedItem());
                ps.setInt(idx++,currentUser.getUserId());   // <-- reporter_id = current user
                if(hasTitle)     ps.setString(idx++,titleF.getText().trim());
                if(hasReportedBy)ps.setString(idx++,reportedByF.getText().trim());
                ps.executeUpdate();ok("Issue reported!");loadIssues();dlg.dispose();
            }catch(SQLException ex){ex.printStackTrace();error("Error: "+ex.getMessage());}
        });
        JButton cancel=solidBtn("Cancel",TEXT_M);cancel.addActionListener(e->dlg.dispose());
        dlg.add(form,BorderLayout.CENTER);dlg.add(btnRow(save,cancel),BorderLayout.SOUTH);dlg.setVisible(true);
    }

    private void viewDetails(){
        int r=issuesTable.getSelectedRow();if(r<0){error("Select an issue.");return;}
        boolean mine=isRowOwner(r);
        StringBuilder sb=new StringBuilder("Issue Details\n\n");
        String[] labels={"ID","Title","Category","Priority","Status","Reporter","Date",""};
        for(int i=0;i<COL_OWNER_ID;i++) sb.append(labels[i]).append(": ").append(tableModel.getValueAt(r,i)).append("\n");
        if(!currentUser.isAdmin()) sb.append("\nOwnership: ").append(mine?"You reported this issue.":"Reported by another member.");
        JOptionPane.showMessageDialog(this,sb.toString(),"Issue Details",JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateStatus(){
        if(!checkPermission()) return;
        int row=issuesTable.getSelectedRow();
        int id=(int)tableModel.getValueAt(row,COL_ID);
        String[] statuses={"PENDING","IN_PROGRESS","COMPLETED","CANCELLED"};
        String current=s(tableModel.getValueAt(row,COL_STATUS));
        String ns=(String)JOptionPane.showInputDialog(this,"Select new status:","Update Status",
                JOptionPane.QUESTION_MESSAGE,null,statuses,current);
        if(ns==null) return;
        try(Connection conn=DBConnection.getConnection();PreparedStatement ps=conn.prepareStatement("UPDATE issues SET status=? WHERE issue_id=?")){
            ps.setString(1,ns);ps.setInt(2,id);ps.executeUpdate();ok("Status updated!");loadIssues();
        }catch(SQLException e){error("Error: "+e.getMessage());}
    }

    private void deleteIssue(){
        if(!checkPermission()) return;
        int row=issuesTable.getSelectedRow();
        int id=(int)tableModel.getValueAt(row,COL_ID);
        String title=s(tableModel.getValueAt(row,COL_TITLE));
        int confirm=JOptionPane.showConfirmDialog(this,
                "Delete issue: \""+title+"\"?\nThis action cannot be undone.",
                "Confirm Delete",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
        if(confirm!=JOptionPane.YES_OPTION) return;
        try(Connection conn=DBConnection.getConnection();PreparedStatement ps=conn.prepareStatement("DELETE FROM issues WHERE issue_id=?")){
            ps.setInt(1,id);ps.executeUpdate();ok("Issue deleted!");loadIssues();
        }catch(SQLException e){error("Error: "+e.getMessage());}
    }

    // ---------- Helpers -------------------------------------------------------
    private String s(Object o){return o==null?"":o.toString();}
    private JLabel slbl(String t){JLabel l=new JLabel(t);l.setFont(new Font("Segoe UI",Font.BOLD,12));l.setForeground(TEXT_M);return l;}
    private JLabel letterBadge(String letter,Color bg){
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
    private void styleTF(JTextField f){f.setFont(new Font("Segoe UI",Font.PLAIN,13));f.setBackground(new Color(255,248,248));f.setForeground(TEXT_D);f.setPreferredSize(new Dimension(180,32));f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER,1,true),BorderFactory.createEmptyBorder(4,9,4,9)));}
    private JDialog dlg(String title,int w,int h){JDialog d=new JDialog((Frame)SwingUtilities.getWindowAncestor(this),title,true);d.setSize(w,h);d.setLocationRelativeTo(this);d.getContentPane().setBackground(CARD);d.setLayout(new BorderLayout(12,12));return d;}
    private JPanel form(){JPanel p=new JPanel(new GridBagLayout());p.setBackground(CARD);p.setBorder(BorderFactory.createEmptyBorder(18,22,18,22));return p;}
    private void addRow(JPanel form,String label,Component field,int row){
        GridBagConstraints g=new GridBagConstraints();g.fill=GridBagConstraints.HORIZONTAL;g.insets=new Insets(6,4,6,4);
        g.gridx=0;g.gridy=row;g.weightx=0.30;JLabel l=new JLabel(label);l.setFont(new Font("Segoe UI",Font.BOLD,12));l.setForeground(ROSE);form.add(l,g);
        g.gridx=1;g.weightx=0.70;form.add(field,g);}
    private JPanel btnRow(JButton...btns){JPanel p=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));p.setBackground(new Color(255,240,240));p.setBorder(BorderFactory.createMatteBorder(1,0,0,0,BORDER));for(JButton b:btns)p.add(b);return p;}
    private JTextField tf(){return tf("");}
    private JTextField tf(String t){JTextField f=new JTextField(t);f.setFont(new Font("Segoe UI",Font.PLAIN,13));f.setBackground(new Color(255,248,248));f.setForeground(TEXT_D);f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER,1),BorderFactory.createEmptyBorder(6,10,6,10)));f.setPreferredSize(new Dimension(0,34));return f;}
    private JTextArea ta(){JTextArea a=new JTextArea(3,20);a.setFont(new Font("Segoe UI",Font.PLAIN,13));a.setBackground(new Color(255,248,248));a.setForeground(TEXT_D);a.setLineWrap(true);a.setWrapStyleWord(true);a.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER,1),BorderFactory.createEmptyBorder(5,8,5,8)));return a;}
    private void error(String m){JOptionPane.showMessageDialog(this,m,"Error",JOptionPane.ERROR_MESSAGE);}
    private void ok(String m){JOptionPane.showMessageDialog(this,m,"Done",JOptionPane.INFORMATION_MESSAGE);}
}