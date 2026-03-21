package services;

import database.DBConnection;
import models.Event;
import models.EventFeedback;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventService {

    private Connection getConn() { return DBConnection.getConnection(); }

    public boolean createEvent(Event event) {
        String sql = "INSERT INTO events (event_name, description, event_date, venue, capacity, organizer_id, category, status, created_by, created_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, event.getEventName());
            ps.setString(2, event.getDescription());
            ps.setDate(3, new java.sql.Date(event.getEventDate().getTime()));
            ps.setString(4, event.getVenue());
            ps.setInt(5, event.getCapacity());
            ps.setInt(6, event.getOrganizerId());
            ps.setString(7, event.getCategory());
            ps.setString(8, event.getStatus());
            ps.setInt(9, event.getCreatedBy());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet gk = ps.getGeneratedKeys();
                if (gk.next()) event.setEventId(gk.getInt(1));
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<Event> getAllEvents() {
        List<Event> list = new ArrayList<>();
        String sql = "SELECT e.*, u.full_name AS organizer_name FROM events e "
                + "JOIN users u ON e.organizer_id = u.user_id ORDER BY e.event_date DESC";
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Event getEventById(int eventId) {
        String sql = "SELECT e.*, u.full_name AS organizer_name FROM events e "
                + "JOIN users u ON e.organizer_id = u.user_id WHERE e.event_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean updateEvent(Event event) {
        String sql = "UPDATE events SET event_name=?, description=?, event_date=?, venue=?, capacity=?, category=?, status=? WHERE event_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, event.getEventName());
            ps.setString(2, event.getDescription());
            ps.setDate(3, new java.sql.Date(event.getEventDate().getTime()));
            ps.setString(4, event.getVenue());
            ps.setInt(5, event.getCapacity());
            ps.setString(6, event.getCategory());
            ps.setString(7, event.getStatus());
            ps.setInt(8, event.getEventId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean deleteEvent(int eventId) {
        String sql = "DELETE FROM events WHERE event_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, eventId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<Event> searchEvents(String keyword) {
        List<Event> list = new ArrayList<>();
        String sql = "SELECT e.*, u.full_name AS organizer_name FROM events e "
                + "JOIN users u ON e.organizer_id = u.user_id "
                + "WHERE e.event_name LIKE ? OR e.description LIKE ? OR e.venue LIKE ? ORDER BY e.event_date DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            String p = "%" + keyword + "%";
            ps.setString(1, p); ps.setString(2, p); ps.setString(3, p);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Event> getUpcomingEvents() {
        List<Event> list = new ArrayList<>();
        String sql = "SELECT e.*, u.full_name AS organizer_name FROM events e "
                + "JOIN users u ON e.organizer_id = u.user_id "
                + "WHERE e.event_date >= CURDATE() AND e.status='UPCOMING' ORDER BY e.event_date ASC";
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addFeedback(EventFeedback feedback) {
        String sql = "INSERT INTO event_feedback (event_id, user_id, rating, comment, is_anonymous, verified, submitted_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, NOW())";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, feedback.getEventId());
            ps.setInt(2, feedback.getUserId());
            ps.setInt(3, feedback.getRating());
            ps.setString(4, feedback.getComment());
            ps.setBoolean(5, feedback.isAnonymous());
            ps.setBoolean(6, feedback.isVerified());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ── Row Mapper ────────────────────────────────────────────────────────────

    private Event mapRow(ResultSet rs) throws SQLException {
        Event e = new Event();
        e.setEventId(rs.getInt("event_id"));
        e.setEventName(rs.getString("event_name"));
        e.setDescription(safeStr(rs, "description"));
        e.setEventDate(rs.getDate("event_date"));
        e.setVenue(safeStr(rs, "venue"));
        e.setLocation(safeStr(rs, "venue")); // keep both in sync
        e.setCapacity(rs.getInt("capacity"));
        e.setOrganizerId(rs.getInt("organizer_id"));
        e.setOrganizerName(safeStr(rs, "organizer_name"));
        e.setOrganizer(safeStr(rs, "organizer_name"));
        e.setCategory(safeStr(rs, "category"));
        e.setStatus(safeStr(rs, "status"));
        try { e.setCreatedDate(rs.getDate("created_date")); }  catch (SQLException ignored) {}
        try { e.setCreatedBy(rs.getInt("created_by")); }        catch (SQLException ignored) {}
        return e;
    }

    private String safeStr(ResultSet rs, String col) {
        try { return rs.getString(col); } catch (SQLException ex) { return null; }
    }
}