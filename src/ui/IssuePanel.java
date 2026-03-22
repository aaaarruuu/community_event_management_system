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

    // ── Palette (Rose theme) ──────────────────────────────────────────────────
    private static final Color ROSE    = new Color(244,  63,  94);
    private static final Color ROSE2   = new Color(251, 113, 133);
    private static final Color SUCCESS = new Color(  5, 150, 105);
    private static final Color WARNING = new Color(217, 119,   6);
    private static final Color INFO    = new Color( 14, 165, 233);
    private static final Color VIOLET  = new Color(139,  92, 246);
    private static final Color BG      = new Color( 15,  23,  42);
    private static final Color BG2     = new Color( 30,  41,  59);
    private static final Color BG3     = new Color( 51,  65,  85);
    private static final Color TH_BG   = new Color(100,   7,  30);
    private static final Color TEXT    = new Color(226, 232, 240);
    private static final Color TEXT_M  = new Color(148, 163, 184);
    private static final Color SEL_BG  = new Color(100,   7,  30);
    private static final Color MEM_CLR = new Color( 20, 184, 166);

    // ── Constructor ───────────────────────────────────────────────────────────
    public IssuePanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0,0));
        setBackground(BG);
        initComponents();
        loadIssues();
    }

    // ── Layout ────────────────────────────────────────────────────────────────
    private void initComponents() {
        add(buildTitleBar(), BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout(0,14));
        body.setBackground(BG);
        body.setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        body.add(buildSearchBar(), BorderLayout.NORTH);
        body.add(buildTable(),     BorderLayout.CENTER);
        body.add(buildButtons(),   BorderLayout.SOUTH);
        add(body, BorderLayout.CENTER);
    }

    // ── Title Bar ─────────────────────────────────────────────────────────────
    private JPanel buildTitleBar() {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp=new GradientPaint(0,0,new Color(100,7,30),getWidth(),0,new Color(131,24,67));
                g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.15f));
                g2.setColor(ROSE2); g2.fill(new Ellipse2D.Float(getWidth()-150,-50,200,200));
                g2.dispose(); super.paintComponent(g);
            }
        };
        p.setOpaque(true); p.setPreferredSize(new Dimension(0,72));
        p.setBorder(BorderFactory.createEmptyBorder(0,24,0,24));

        JPanel left=new JPanel(new GridBagLayout()); left.setOpaque(false);
        GridBagConstraints gc=new GridBagConstraints();
        gc.gridx=0;gc.gridy=0;gc.anchor=GridBagConstraints.WEST;gc.insets=new Insets(0,0,2,0);
        JLabel title=new JLabel("ISSUE TRACKER");
        title.setFont(new Font("Segoe UI",Font.BOLD,22));title.setForeground(Color.WHITE);
        gc.gridy=1;
        JLabel sub=new JLabel("Report, track and resolve community maintenance issues");
        sub.setFont(new Font("Segoe UI",Font.PLAIN,12));sub.setForeground(new Color(255,180,200));
        gc.gridy=0;left.add(title,gc);gc.gridy=1;left.add(sub,gc);
        p.add(left,BorderLayout.WEST);

        JButton addBtn=solidBtn("+ Report Issue",ROSE);
        addBtn.addActionListener(e->showAddDialog());
        JPanel right=new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));right.setOpaque(false);
        right.add(addBtn);p.add(right,BorderLayout.EAST);
        return p;
    }

    // ── Search Bar ────────────────────────────────────────────────────────────
    private JPanel buildSearchBar() {
        JPanel p=new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        p.setBackground(BG2);p.setBorder(BorderFactory.createEmptyBorder(10,12,10,12));

        searchField=new JTextField(18);
        searchField.setBackground(BG3);searchField.setForeground(TEXT);
        searchField.setCaretColor(TEXT);searchField.setFont(new Font("Segoe UI",Font.PLAIN,13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ROSE,1), BorderFactory.createEmptyBorder(4,8,4,8)));
        p.add(searchField);

        JButton sb=solidBtn("Search",ROSE);sb.setPreferredSize(new Dimension(88,32));
        sb.addActionListener(e->searchIssues());p.add(sb);

        filterCombo=new JComboBox<>(new String[]{
                "All Issues","Pending","In Progress","Resolved","High Priority","Critical",
                "Plumbing","Electrical","Maintenance","Cleaning","Security","Garden"});
        filterCombo.setBackground(BG3);filterCombo.setForeground(TEXT);
        filterCombo.setFont(new Font("Segoe UI",Font.PLAIN,13));
        filterCombo.setPreferredSize(new Dimension(155,32));
        filterCombo.addActionListener(e->loadIssues());p.add(filterCombo);

        if(!currentUser.isAdmin()){
            JButton myBtn=solidBtn("My Issues",MEM_CLR);myBtn.setPreferredSize(new Dimension(95,32));
            myBtn.addActionListener(e->loadMyIssues());p.add(myBtn);
        }

        JButton rb=solidBtn("Refresh",TEXT_M);rb.setPreferredSize(new Dimension(80,32));
        rb.addActionListener(e->loadIssues());p.add(rb);
        return p;
    }

    // ── Table ─────────────────────────────────────────────────────────────────
    private JScrollPane buildTable() {
        String[] cols={"ID","Category","Priority","Description","Status","Reporter","Location","Assigned To","Reported Date"};
        tableModel=new DefaultTableModel(cols,0){
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        issuesTable=new JTable(tableModel);
        styleTable();
        return new JScrollPane(issuesTable);
    }

    private void styleTable() {
        issuesTable.setBackground(BG2);issuesTable.setForeground(TEXT);
        issuesTable.setFont(new Font("Segoe UI",Font.PLAIN,13));
        issuesTable.setRowHeight(32);issuesTable.setShowGrid(false);
        issuesTable.setIntercellSpacing(new Dimension(0,0));
        issuesTable.setSelectionBackground(SEL_BG);issuesTable.setSelectionForeground(Color.WHITE);
        issuesTable.getTableHeader().setBackground(TH_BG);
        issuesTable.getTableHeader().setForeground(ROSE2);
        issuesTable.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,12));
        issuesTable.getTableHeader().setPreferredSize(new Dimension(0,36));

        int[] w={40,100,80,260,100,120,140,120,140};
        for(int i=0;i<w.length;i++) issuesTable.getColumnModel().getColumn(i).setPreferredWidth(w[i]);

        issuesTable.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(
                    JTable t,Object val,boolean sel,boolean focus,int row,int col){
                super.getTableCellRendererComponent(t,val,sel,focus,row,col);
                setBackground(sel?SEL_BG:(row%2==0?BG2:BG));
                setForeground(TEXT);setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
                if(!sel){
                    String status=""; String priority="";
                    try{status=(String)tableModel.getValueAt(row,4);}catch(Exception ignored){}
                    try{priority=(String)tableModel.getValueAt(row,2);}catch(Exception ignored){}
                    if(col==4){
                        switch(status){
                            case "PENDING":     setForeground(new Color(245,158,11)); break;
                            case "IN_PROGRESS": setForeground(new Color(14,165,233));  break;
                            case "COMPLETED":   setForeground(new Color(34,197,94));   break;
                            case "CANCELLED":   setForeground(new Color(148,163,184)); break;
                        }
                    }
                    if(col==2){
                        switch(priority){
                            case "CRITICAL": setForeground(new Color(239,68,68));  break;
                            case "HIGH":     setForeground(new Color(245,158,11)); break;
                            case "MEDIUM":   setForeground(new Color(99,102,241)); break;
                            case "LOW":      setForeground(new Color(148,163,184));break;
                        }
                    }
                }
                return this;
            }
        });
    }

    // ── Action Buttons ────────────────────────────────────────────────────────
    private JPanel buildButtons() {
        JPanel p=new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        p.setBackground(BG2);p.setBorder(BorderFactory.createEmptyBorder(10,12,10,12));

        JButton vb=solidBtn("View Details",INFO); vb.addActionListener(e->viewDetails()); p.add(vb);

        // ── NEW: Assign to Rep (ADMIN only) ───────────────────────────────────
        if(currentUser.isAdmin()){
            JButton ab=solidBtn("Assign to Rep",VIOLET);
            ab.addActionListener(e->assignToRep()); p.add(ab);
        }

        JButton ub=solidBtn("Update Status",WARNING); ub.setPreferredSize(new Dimension(130,32));
        ub.addActionListener(e->updateStatus()); p.add(ub);

        JButton db=solidBtn("Delete",ROSE); db.addActionListener(e->deleteIssue()); p.add(db);
        return p;
    }

    // ── Data Loading ──────────────────────────────────────────────────────────
    public void loadIssues() {
        tableModel.setRowCount(0);
        String filter=(String)filterCombo.getSelectedItem();

        StringBuilder sql=new StringBuilder(
                "SELECT i.issue_id, i.category, i.priority, " +
                        "COALESCE(i.description,'') AS description, " +
                        "i.status, u.full_name AS reporter, " +
                        "COALESCE(i.location,'') AS location, " +
                        "COALESCE(r.rep_name,'Unassigned') AS assigned_to, " +
                        "i.reported_date " +
                        "FROM issues i " +
                        "JOIN users u ON i.reporter_id=u.user_id " +
                        "LEFT JOIN representatives r ON i.assigned_to=r.rep_id " +
                        "WHERE 1=1 ");

        switch(filter==null?"All Issues":filter){
            case "Pending":       sql.append("AND i.status='PENDING' ");       break;
            case "In Progress":   sql.append("AND i.status='IN_PROGRESS' ");   break;
            case "Resolved":      sql.append("AND i.status='COMPLETED' ");     break;
            case "High Priority": sql.append("AND i.priority='HIGH' ");        break;
            case "Critical":      sql.append("AND i.priority='CRITICAL' ");    break;
            case "Plumbing":case "Electrical":case "Maintenance":
            case "Cleaning":case "Security":case "Garden":
                sql.append("AND i.category='").append(filter).append("' ");    break;
        }
        sql.append("ORDER BY i.reported_date DESC");

        try(Connection conn=DBConnection.getConnection();
            Statement st=conn.createStatement();
            ResultSet rs=st.executeQuery(sql.toString())){
            while(rs.next()){
                String desc=rs.getString("description");
                if(desc!=null&&desc.length()>60) desc=desc.substring(0,60)+"…";
                tableModel.addRow(new Object[]{
                        rs.getInt("issue_id"),
                        rs.getString("category"),
                        rs.getString("priority"),
                        desc,
                        rs.getString("status"),
                        rs.getString("reporter"),
                        rs.getString("location"),
                        rs.getString("assigned_to"),
                        rs.getTimestamp("reported_date")
                });
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this,"Error loading issues:\n"+e.getMessage(),"DB Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMyIssues(){
        tableModel.setRowCount(0);
        String sql=
                "SELECT i.issue_id, i.category, i.priority, " +
                        "COALESCE(i.description,'') AS description, " +
                        "i.status, u.full_name AS reporter, " +
                        "COALESCE(i.location,'') AS location, " +
                        "COALESCE(r.rep_name,'Unassigned') AS assigned_to, " +
                        "i.reported_date " +
                        "FROM issues i " +
                        "JOIN users u ON i.reporter_id=u.user_id " +
                        "LEFT JOIN representatives r ON i.assigned_to=r.rep_id " +
                        "WHERE i.reporter_id=? ORDER BY i.reported_date DESC";
        try(Connection conn=DBConnection.getConnection(); PreparedStatement ps=conn.prepareStatement(sql)){
            ps.setInt(1,currentUser.getUserId());
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                String desc=rs.getString("description");
                if(desc!=null&&desc.length()>60) desc=desc.substring(0,60)+"…";
                tableModel.addRow(new Object[]{
                        rs.getInt("issue_id"),rs.getString("category"),rs.getString("priority"),
                        desc,rs.getString("status"),rs.getString("reporter"),
                        rs.getString("location"),rs.getString("assigned_to"),rs.getTimestamp("reported_date")
                });
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage(),"DB Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchIssues(){
        String term=searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        String sql=
                "SELECT i.issue_id, i.category, i.priority, " +
                        "COALESCE(i.description,'') AS description, " +
                        "i.status, u.full_name AS reporter, " +
                        "COALESCE(i.location,'') AS location, " +
                        "COALESCE(r.rep_name,'Unassigned') AS assigned_to, " +
                        "i.reported_date " +
                        "FROM issues i " +
                        "JOIN users u ON i.reporter_id=u.user_id " +
                        "LEFT JOIN representatives r ON i.assigned_to=r.rep_id " +
                        "WHERE LOWER(i.description) LIKE ? OR LOWER(i.category) LIKE ? OR LOWER(i.location) LIKE ? " +
                        "ORDER BY i.reported_date DESC";
        try(Connection conn=DBConnection.getConnection(); PreparedStatement ps=conn.prepareStatement(sql)){
            ps.setString(1,"%"+term+"%"); ps.setString(2,"%"+term+"%"); ps.setString(3,"%"+term+"%");
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                String desc=rs.getString("description");
                if(desc!=null&&desc.length()>60) desc=desc.substring(0,60)+"…";
                tableModel.addRow(new Object[]{
                        rs.getInt("issue_id"),rs.getString("category"),rs.getString("priority"),
                        desc,rs.getString("status"),rs.getString("reporter"),
                        rs.getString("location"),rs.getString("assigned_to"),rs.getTimestamp("reported_date")
                });
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this,"Search error: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── View Details ──────────────────────────────────────────────────────────
    private void viewDetails(){
        int row=issuesTable.getSelectedRow();
        if(row<0){JOptionPane.showMessageDialog(this,"Please select an issue.","No Selection",JOptionPane.WARNING_MESSAGE);return;}
        int id=(int)tableModel.getValueAt(row,0);

        String sql=
                "SELECT i.*, u.full_name AS reporter_name, r.rep_name AS assigned_rep " +
                        "FROM issues i " +
                        "JOIN users u ON i.reporter_id=u.user_id " +
                        "LEFT JOIN representatives r ON i.assigned_to=r.rep_id " +
                        "WHERE i.issue_id=?";
        try(Connection conn=DBConnection.getConnection(); PreparedStatement ps=conn.prepareStatement(sql)){
            ps.setInt(1,id);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                String info=
                        "Issue ID:    " + rs.getInt("issue_id") + "\n" +
                                "Category:    " + rs.getString("category") + "\n" +
                                "Priority:    " + rs.getString("priority") + "\n" +
                                "Status:      " + rs.getString("status") + "\n" +
                                "Reporter:    " + rs.getString("reporter_name") + "\n" +
                                "Location:    " + rs.getString("location") + "\n" +
                                "Assigned To: " + (rs.getString("assigned_rep")!=null?rs.getString("assigned_rep"):"Unassigned") + "\n" +
                                "Reported:    " + rs.getTimestamp("reported_date") + "\n" +
                                "Resolved:    " + (rs.getTimestamp("resolved_date")!=null?rs.getTimestamp("resolved_date"):"Not yet") + "\n" +
                                "Rating:      " + (rs.getInt("rating")>0?rs.getInt("rating")+"/5":"Not rated") + "\n\n" +
                                "Description:\n" + rs.getString("description") + "\n\n" +
                                "Resolution:\n" + (rs.getString("resolution")!=null?rs.getString("resolution"):"Pending");
                JTextArea ta=new JTextArea(info,16,50);
                ta.setEditable(false);ta.setLineWrap(true);ta.setWrapStyleWord(true);
                ta.setFont(new Font("Segoe UI",Font.PLAIN,13));
                JOptionPane.showMessageDialog(this,new JScrollPane(ta),"Issue Details – #"+id,JOptionPane.INFORMATION_MESSAGE);
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage(),"DB Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── NEW: Assign to Rep ────────────────────────────────────────────────────
    /**
     * Opens AssignmentDialog (already in your project at src/ui/AssignmentDialog.java).
     * After assignment the issue list auto-refreshes.
     */
    private void assignToRep(){
        int row=issuesTable.getSelectedRow();
        if(row<0){JOptionPane.showMessageDialog(this,"Please select an issue to assign.","No Selection",JOptionPane.WARNING_MESSAGE);return;}
        int    issueId  =(int)tableModel.getValueAt(row,0);
        String category =(String)tableModel.getValueAt(row,1);
        String status   =(String)tableModel.getValueAt(row,4);

        if("COMPLETED".equals(status)||"CANCELLED".equals(status)){
            JOptionPane.showMessageDialog(this,"Cannot assign a "+status+" issue.","Not Allowed",JOptionPane.WARNING_MESSAGE); return;
        }

        Frame parent=(Frame)SwingUtilities.getWindowAncestor(this);
        AssignmentDialog dlg=new AssignmentDialog(parent,issueId,category);
        dlg.setVisible(true);

        // Refresh after dialog closes
        if(dlg.isAssigned()) loadIssues();
    }

    // ── Update Status ─────────────────────────────────────────────────────────
    private void updateStatus(){
        int row=issuesTable.getSelectedRow();
        if(row<0){JOptionPane.showMessageDialog(this,"Please select an issue.","No Selection",JOptionPane.WARNING_MESSAGE);return;}
        int id=(int)tableModel.getValueAt(row,0);
        String cur=(String)tableModel.getValueAt(row,4);

        String[] statuses={"PENDING","IN_PROGRESS","COMPLETED","CANCELLED"};
        String chosen=(String)JOptionPane.showInputDialog(
                this,"Select new status for Issue #"+id,
                "Update Status",JOptionPane.QUESTION_MESSAGE,null,statuses,cur);
        if(chosen==null||chosen.equals(cur)) return;

        String sql;
        if("COMPLETED".equals(chosen)){
            sql="UPDATE issues SET status=?, resolved_date=NOW() WHERE issue_id=?";
        } else {
            sql="UPDATE issues SET status=? WHERE issue_id=?";
        }
        try(Connection conn=DBConnection.getConnection(); PreparedStatement ps=conn.prepareStatement(sql)){
            ps.setString(1,chosen); ps.setInt(2,id); ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"Status updated to: "+chosen,"Updated",JOptionPane.INFORMATION_MESSAGE);
            loadIssues();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage(),"DB Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    private void deleteIssue(){
        int row=issuesTable.getSelectedRow();
        if(row<0){JOptionPane.showMessageDialog(this,"Please select an issue.","No Selection",JOptionPane.WARNING_MESSAGE);return;}
        int id=(int)tableModel.getValueAt(row,0);
        String cat=(String)tableModel.getValueAt(row,1);
        int c=JOptionPane.showConfirmDialog(this,"Delete Issue #"+id+" ("+cat+")?","Confirm Delete",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
        if(c!=JOptionPane.YES_OPTION)return;
        try(Connection conn=DBConnection.getConnection();
            PreparedStatement ps=conn.prepareStatement("DELETE FROM issues WHERE issue_id=?")){
            ps.setInt(1,id); ps.executeUpdate(); loadIssues();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage(),"DB Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Report Issue Dialog ───────────────────────────────────────────────────
    private void showAddDialog(){
        JDialog dlg=new JDialog((Frame)SwingUtilities.getWindowAncestor(this),"Report New Issue",true);
        dlg.setSize(500,440); dlg.setLocationRelativeTo(this);

        JPanel main=new JPanel(new GridBagLayout());
        main.setBackground(BG2); main.setBorder(BorderFactory.createEmptyBorder(20,24,20,24));
        GridBagConstraints gc=new GridBagConstraints();
        gc.fill=GridBagConstraints.HORIZONTAL; gc.insets=new Insets(6,4,6,4); gc.weightx=1;

        JComboBox<String> catC=new JComboBox<>(new String[]{"Plumbing","Electrical","Maintenance","Cleaning","Security","Garden","Other"});
        JComboBox<String> priC=new JComboBox<>(new String[]{"LOW","MEDIUM","HIGH","CRITICAL"}); priC.setSelectedItem("MEDIUM");
        JTextField locF=styledField();
        JTextArea  descF=new JTextArea(4,30); descF.setBackground(BG3); descF.setForeground(TEXT);
        descF.setFont(new Font("Segoe UI",Font.PLAIN,13)); descF.setLineWrap(true);

        int r=0;
        gc.gridy=r; gc.gridx=0; gc.gridwidth=1; main.add(lbl("Category"),gc);
        gc.gridx=1; main.add(lbl("Priority"),gc); r++;
        gc.gridy=r; gc.gridx=0; main.add(catC,gc); gc.gridx=1; main.add(priC,gc); r++;
        gc.gridy=r; gc.gridx=0; gc.gridwidth=2; main.add(lbl("Location *"),gc); r++;
        gc.gridy=r++; main.add(locF,gc);
        gc.gridy=r; main.add(lbl("Description *"),gc); r++;
        gc.gridy=r++; main.add(new JScrollPane(descF),gc);

        JButton saveBtn=solidBtn("Submit Issue",SUCCESS);
        JButton cancelBtn=solidBtn("Cancel",TEXT_M);
        JPanel btnRow=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0)); btnRow.setBackground(BG2);
        btnRow.add(cancelBtn); btnRow.add(saveBtn);
        gc.gridy=r; main.add(btnRow,gc);

        cancelBtn.addActionListener(e->dlg.dispose());
        saveBtn.addActionListener(e->{
            String loc=locF.getText().trim(); String desc=descF.getText().trim();
            if(loc.isEmpty()||desc.isEmpty()){
                JOptionPane.showMessageDialog(dlg,"Location and description are required!","Validation",JOptionPane.WARNING_MESSAGE); return;
            }
            String sql=
                    "INSERT INTO issues(category,priority,description,status,reporter_id,location,created_by,reported_date) " +
                            "VALUES(?,?,?,'PENDING',?,?,?,NOW())";
            try(Connection conn=DBConnection.getConnection(); PreparedStatement ps=conn.prepareStatement(sql)){
                ps.setString(1,(String)catC.getSelectedItem());
                ps.setString(2,(String)priC.getSelectedItem());
                ps.setString(3,desc);
                ps.setInt(4,currentUser.getUserId());
                ps.setString(5,loc);
                ps.setInt(6,currentUser.getUserId());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(dlg,"Issue reported successfully!","Success",JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose(); loadIssues();
            }catch(SQLException ex){
                JOptionPane.showMessageDialog(dlg,"DB Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        });
        dlg.setContentPane(main); dlg.setVisible(true);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private JButton solidBtn(String text,Color bg){
        JButton b=new JButton(text){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()?bg.brighter():bg);
                g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                g2.setColor(Color.WHITE); g2.setFont(getFont());
                FontMetrics fm=g2.getFontMetrics(); String t=getText();
                g2.drawString(t,(getWidth()-fm.stringWidth(t))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        b.setFont(new Font("Segoe UI",Font.BOLD,12));
        b.setPreferredSize(new Dimension(Math.max(80,text.length()*8+16),32));
        b.setFocusPainted(false);b.setBorderPainted(false);b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return b;
    }

    private JTextField styledField(){
        JTextField f=new JTextField();
        f.setBackground(BG3);f.setForeground(TEXT);
        f.setFont(new Font("Segoe UI",Font.PLAIN,13));f.setCaretColor(TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71,85,105)),
                BorderFactory.createEmptyBorder(6,10,6,10)));
        return f;
    }

    private JLabel lbl(String t){
        JLabel l=new JLabel(t); l.setFont(new Font("Segoe UI",Font.BOLD,11)); l.setForeground(TEXT_M); return l;
    }
}