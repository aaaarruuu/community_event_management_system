package services;

import database.DBConnection;
import models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {

    private Connection getConn() { return DBConnection.getConnection(); }

    // ── Authentication ────────────────────────────────────────────────────────

    /**
     * Authenticates a user.  Passwords are stored as MD5 hashes in the DB.
     */
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = MD5(?) AND is_active = TRUE";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = mapRow(rs);
                updateLastLogin(u.getUserId());
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ── Registration ─────────────────────────────────────────────────────────

    public boolean registerUser(User user, String plainPassword) {
        String sql = "INSERT INTO users (username, password, full_name, email, phone, role, flat_number, is_active, created_date) "
                + "VALUES (?, MD5(?), ?, ?, ?, ?, ?, TRUE, NOW())";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, plainPassword);
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getRole());
            ps.setString(7, user.getFlatNumber());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet gk = ps.getGeneratedKeys();
                if (gk.next()) user.setUserId(gk.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── Simple Registration (plain password, used by RegistrationScreen) ─────

    /**
     * Registers a user with the supplied plain-text password (stored as MD5).
     * The User object must have username, fullName, email, phone, role pre-set.
     */
    public boolean registerUser(String username, String password, String role,
                                String contact, String email) {
        String sql = "INSERT INTO users (username, password, full_name, email, phone, role, is_active, created_date) "
                + "VALUES (?, MD5(?), ?, ?, ?, ?, TRUE, NOW())";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, username); // full_name defaults to username
            ps.setString(4, email);
            ps.setString(5, contact);
            ps.setString(6, role);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── Read ─────────────────────────────────────────────────────────────────

    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE is_active = TRUE ORDER BY full_name";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<User> getUsersByRole(String role) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? AND is_active = TRUE";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<User> searchUsers(String keyword) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE (full_name LIKE ? OR email LIKE ? OR username LIKE ?) AND is_active = TRUE";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            String p = "%" + keyword + "%";
            ps.setString(1, p); ps.setString(2, p); ps.setString(3, p);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ── Update ────────────────────────────────────────────────────────────────

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET full_name=?, email=?, phone=?, flat_number=?, bio=?, "
                + "emergency_contact=?, email_notifications=?, sms_notifications=? WHERE user_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getFlatNumber());
            ps.setString(5, user.getBio());
            ps.setString(6, user.getEmergencyContact());
            ps.setBoolean(7, user.isEmailNotifications());
            ps.setBoolean(8, user.isSmsNotifications());
            ps.setInt(9, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        String verify = "SELECT user_id FROM users WHERE user_id=? AND password=MD5(?)";
        try (PreparedStatement vps = getConn().prepareStatement(verify)) {
            vps.setInt(1, userId); vps.setString(2, oldPassword);
            ResultSet rs = vps.executeQuery();
            if (rs.next()) {
                String upd = "UPDATE users SET password=MD5(?) WHERE user_id=?";
                try (PreparedStatement ups = getConn().prepareStatement(upd)) {
                    ups.setString(1, newPassword); ups.setInt(2, userId);
                    return ups.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ── Delete (soft) ─────────────────────────────────────────────────────────

    public boolean deleteUser(int userId) {
        String sql = "UPDATE users SET is_active=FALSE WHERE user_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login=NOW(), login_count=login_count+1 WHERE user_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public Map<String, Integer> getUserStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        try (Statement st = getConn().createStatement()) {
            ResultSet rs;
            rs = st.executeQuery("SELECT COUNT(*) FROM users WHERE is_active=TRUE");
            if (rs.next()) stats.put("total", rs.getInt(1));
            rs = st.executeQuery("SELECT COUNT(*) FROM users WHERE role='ADMIN' AND is_active=TRUE");
            if (rs.next()) stats.put("admins", rs.getInt(1));
            rs = st.executeQuery("SELECT COUNT(*) FROM users WHERE role='MEMBER' AND is_active=TRUE");
            if (rs.next()) stats.put("members", rs.getInt(1));
            rs = st.executeQuery("SELECT COUNT(*) FROM users WHERE DATE(last_login)=CURDATE()");
            if (rs.next()) stats.put("activeToday", rs.getInt(1));
        } catch (SQLException e) { e.printStackTrace(); }
        return stats;
    }

    // ── Row Mapper ────────────────────────────────────────────────────────────

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setRole(rs.getString("role"));
        u.setActive(rs.getBoolean("is_active"));
        // Optional columns – guard against missing column
        trySet(u, rs);
        return u;
    }

    /** Sets optional columns that may not exist in older schemas. */
    private void trySet(User u, ResultSet rs) {
        try { u.setFlatNumber(rs.getString("flat_number")); }    catch (SQLException ignored) {}
        try { u.setCreatedDate(rs.getDate("created_date")); }    catch (SQLException ignored) {}
        try { u.setLastLogin(rs.getTimestamp("last_login")); }   catch (SQLException ignored) {}
        try { u.setBio(rs.getString("bio")); }                    catch (SQLException ignored) {}
        try { u.setEmergencyContact(rs.getString("emergency_contact")); } catch (SQLException ignored) {}
        try { u.setEmailNotifications(rs.getBoolean("email_notifications")); } catch (SQLException ignored) {}
        try { u.setSmsNotifications(rs.getBoolean("sms_notifications")); }    catch (SQLException ignored) {}
        try { u.setLoginCount(rs.getInt("login_count")); }        catch (SQLException ignored) {}
    }
}