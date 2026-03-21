package services;

import database.DBConnection;
import models.Representative;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepresentativeService {

    private Connection getConn() { return DBConnection.getConnection(); }

    public boolean addRepresentative(Representative rep) {
        String sql = "INSERT INTO representatives (rep_name, phone, email, category, skill_level, status, is_available, registered_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, rep.getName());
            ps.setString(2, rep.getPhone());
            ps.setString(3, rep.getEmail());
            ps.setString(4, rep.getCategory());
            ps.setString(5, rep.getSkillLevel());
            ps.setString(6, rep.getStatus());
            ps.setBoolean(7, rep.isAvailable());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet gk = ps.getGeneratedKeys();
                if (gk.next()) rep.setRepId(gk.getInt(1));
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<Representative> getAllRepresentatives() {
        List<Representative> list = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM representatives ORDER BY rep_name")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Representative getRepresentativeById(int repId) {
        String sql = "SELECT * FROM representatives WHERE rep_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, repId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Representative> getRepresentativesByCategory(String category) {
        List<Representative> list = new ArrayList<>();
        String sql = "SELECT * FROM representatives WHERE category=? ORDER BY rating DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Representative> getAvailableRepresentatives() {
        List<Representative> list = new ArrayList<>();
        String sql = "SELECT * FROM representatives WHERE is_available=TRUE AND status='ACTIVE' ORDER BY rating DESC";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean updateRepresentative(Representative rep) {
        String sql = "UPDATE representatives SET rep_name=?, phone=?, email=?, category=?, skill_level=?, status=?, is_available=? WHERE rep_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, rep.getName());
            ps.setString(2, rep.getPhone());
            ps.setString(3, rep.getEmail());
            ps.setString(4, rep.getCategory());
            ps.setString(5, rep.getSkillLevel());
            ps.setString(6, rep.getStatus());
            ps.setBoolean(7, rep.isAvailable());
            ps.setInt(8, rep.getRepId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateAvailability(int repId, boolean available) {
        String sql = "UPDATE representatives SET is_available=?, status=? WHERE rep_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setBoolean(1, available);
            ps.setString(2, available ? "ACTIVE" : "BUSY");
            ps.setInt(3, repId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateRating(int repId, double newRating) {
        String sql = "UPDATE representatives SET rating=? WHERE rep_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setDouble(1, newRating); ps.setInt(2, repId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean deleteRepresentative(int repId) {
        String sql = "DELETE FROM representatives WHERE rep_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, repId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<Representative> searchRepresentatives(String keyword) {
        List<Representative> list = new ArrayList<>();
        String sql = "SELECT * FROM representatives WHERE rep_name LIKE ? OR category LIKE ? OR phone LIKE ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            String p = "%" + keyword + "%";
            ps.setString(1, p); ps.setString(2, p); ps.setString(3, p);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ── Row Mapper ────────────────────────────────────────────────────────────

    private Representative mapRow(ResultSet rs) throws SQLException {
        Representative r = new Representative();
        r.setRepId(rs.getInt("rep_id"));
        r.setName(rs.getString("rep_name"));
        r.setPhone(rs.getString("phone"));
        r.setEmail(safeString(rs, "email"));
        r.setCategory(rs.getString("category"));
        r.setSkillLevel(safeString(rs, "skill_level"));
        r.setStatus(rs.getString("status"));
        r.setAvailable(rs.getBoolean("is_available"));
        r.setRating(rs.getDouble("rating"));
        try { r.setTotalAssignments(rs.getInt("total_assignments")); }    catch (SQLException ignored) {}
        try { r.setCompletedAssignments(rs.getInt("completed_assignments")); } catch (SQLException ignored) {}
        try { r.setAvgResolutionTime(rs.getDouble("avg_resolution_time")); } catch (SQLException ignored) {}
        try { r.setRegisteredDate(rs.getDate("registered_date")); }        catch (SQLException ignored) {}
        return r;
    }

    private String safeString(ResultSet rs, String col) {
        try { return rs.getString(col); } catch (SQLException e) { return null; }
    }
}