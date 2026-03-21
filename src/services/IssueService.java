package services;

import database.DBConnection;
import models.Issue;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IssueService {

    private Connection getConn() { return DBConnection.getConnection(); }

    public boolean reportIssue(Issue issue) {
        // Support both the old schema (title column) and the new one (description).
        String sql = "INSERT INTO issues (title, category, priority, description, status, reporter_id, reported_by, location, reported_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, issue.getTitle());
            ps.setString(2, issue.getCategory());
            ps.setString(3, issue.getPriority());
            ps.setString(4, issue.getDescription());
            ps.setString(5, issue.getStatus());
            ps.setInt(6, issue.getReporterId());
            ps.setString(7, issue.getReportedBy());
            ps.setString(8, issue.getLocation());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet gk = ps.getGeneratedKeys();
                if (gk.next()) issue.setIssueId(gk.getInt(1));
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<Issue> getAllIssues() {
        List<Issue> list = new ArrayList<>();
        String sql = "SELECT i.*, u.full_name AS reporter_name, r.rep_name AS assigned_to_name "
                + "FROM issues i "
                + "JOIN users u ON i.reporter_id = u.user_id "
                + "LEFT JOIN representatives r ON i.assigned_to = r.rep_id "
                + "ORDER BY i.reported_date DESC";
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Issue getIssueById(int issueId) {
        String sql = "SELECT i.*, u.full_name AS reporter_name, r.rep_name AS assigned_to_name "
                + "FROM issues i "
                + "JOIN users u ON i.reporter_id = u.user_id "
                + "LEFT JOIN representatives r ON i.assigned_to = r.rep_id "
                + "WHERE i.issue_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, issueId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean updateIssue(Issue issue) {
        String sql = "UPDATE issues SET category=?, priority=?, description=?, status=?, location=? WHERE issue_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, issue.getCategory());
            ps.setString(2, issue.getPriority());
            ps.setString(3, issue.getDescription());
            ps.setString(4, issue.getStatus());
            ps.setString(5, issue.getLocation());
            ps.setInt(6, issue.getIssueId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean assignIssue(int issueId, int repId) {
        String sql = "UPDATE issues SET assigned_to=?, assigned_date=NOW(), status='IN_PROGRESS' WHERE issue_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, repId); ps.setInt(2, issueId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean completeIssue(int issueId, String resolution) {
        String sql = "UPDATE issues SET status='COMPLETED', resolution=?, resolved_date=NOW() WHERE issue_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, resolution); ps.setInt(2, issueId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<Issue> getIssuesByStatus(String status) {
        return filterIssues("i.status=?", status);
    }

    public List<Issue> getIssuesByCategory(String category) {
        return filterIssues("i.category=?", category);
    }

    private List<Issue> filterIssues(String whereClause, String value) {
        List<Issue> list = new ArrayList<>();
        String sql = "SELECT i.*, u.full_name AS reporter_name, r.rep_name AS assigned_to_name "
                + "FROM issues i "
                + "JOIN users u ON i.reporter_id = u.user_id "
                + "LEFT JOIN representatives r ON i.assigned_to = r.rep_id "
                + "WHERE " + whereClause + " ORDER BY i.reported_date DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, value);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Issue> searchIssues(String keyword) {
        List<Issue> list = new ArrayList<>();
        String sql = "SELECT i.*, u.full_name AS reporter_name, r.rep_name AS assigned_to_name "
                + "FROM issues i "
                + "JOIN users u ON i.reporter_id = u.user_id "
                + "LEFT JOIN representatives r ON i.assigned_to = r.rep_id "
                + "WHERE i.description LIKE ? OR i.location LIKE ? OR i.category LIKE ? "
                + "ORDER BY i.reported_date DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            String p = "%" + keyword + "%";
            ps.setString(1, p); ps.setString(2, p); ps.setString(3, p);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean deleteIssue(int issueId) {
        String sql = "DELETE FROM issues WHERE issue_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, issueId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ── Row Mapper ────────────────────────────────────────────────────────────

    private Issue mapRow(ResultSet rs) throws SQLException {
        Issue issue = new Issue();
        issue.setIssueId(rs.getInt("issue_id"));
        issue.setCategory(safeStr(rs, "category"));
        issue.setPriority(safeStr(rs, "priority"));
        issue.setDescription(safeStr(rs, "description"));
        issue.setTitle(safeStr(rs, "title") != null ? safeStr(rs, "title") : safeStr(rs, "description"));
        issue.setStatus(safeStr(rs, "status"));
        issue.setReporterId(rs.getInt("reporter_id"));
        issue.setReporterName(safeStr(rs, "reporter_name"));
        issue.setReportedBy(safeStr(rs, "reported_by") != null ? safeStr(rs, "reported_by") : safeStr(rs, "reporter_name"));
        issue.setReportedDate(rs.getDate("reported_date"));
        issue.setLocation(safeStr(rs, "location"));
        try {
            Object asgn = rs.getObject("assigned_to");
            if (asgn != null) { issue.setAssignedTo((Integer) asgn); issue.setAssignedToName(safeStr(rs, "assigned_to_name")); }
        } catch (SQLException ignored) {}
        try { issue.setAssignedDate(rs.getDate("assigned_date")); }  catch (SQLException ignored) {}
        try { issue.setResolvedDate(rs.getDate("resolved_date")); }   catch (SQLException ignored) {}
        try { issue.setResolution(rs.getString("resolution")); }       catch (SQLException ignored) {}
        return issue;
    }

    private String safeStr(ResultSet rs, String col) {
        try { return rs.getString(col); } catch (SQLException e) { return null; }
    }
}