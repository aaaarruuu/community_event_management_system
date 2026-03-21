//package models;
//
//import java.util.Date;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Event {
//    // Basic fields
//    private int eventId;
//    private String eventName;
//    private String description;
//    private Date eventDate;
//    private String venue;
//    private int capacity;
//    private int organizerId;
//    private String organizerName;
//    private Date createdDate;
//    private int createdBy;
//    private String status; // UPCOMING, ONGOING, COMPLETED, CANCELLED
//    private String category; // CULTURAL, SPORTS, EDUCATIONAL, SOCIAL, RELIGIOUS, HEALTH
//
//    // Enhanced fields
//    private int registeredCount;
//    private int attendedCount;
//    private double averageRating;
//    private int feedbackCount;
//    private List<EventFeedback> feedbacks;
//    private List<Integer> waitlist;
//
//    // Recurring event fields
//    private boolean isRecurring;
//    private RecurrenceType recurrenceType; // DAILY, WEEKLY, MONTHLY
//    private int recurrenceInterval; // Every X days/weeks/months
//    private Date recurrenceEndDate;
//    private Integer parentEventId; // For recurring event instances
//
//    // Registration management
//    private Date registrationStartDate;
//    private Date registrationEndDate;
//    private boolean allowWaitlist;
//    private int waitlistCapacity;
//
//    // Constructors
//    public Event() {
//        this.feedbacks = new ArrayList<>();
//        this.waitlist = new ArrayList<>();
//        this.allowWaitlist = true;
//        this.waitlistCapacity = 50;
//        this.status = "UPCOMING";
//    }
//
//    public Event(int eventId, String eventName, Date eventDate, String venue,
//                 int capacity, int organizerId) {
//        this();
//        this.eventId = eventId;
//        this.eventName = eventName;
//        this.eventDate = eventDate;
//        this.venue = venue;
//        this.capacity = capacity;
//        this.organizerId = organizerId;
//    }
//
//    // Getters and Setters
//    public int getEventId() {
//        return eventId;
//    }
//
//    public void setEventId(int eventId) {
//        this.eventId = eventId;
//    }
//
//    public String getEventName() {
//        return eventName;
//    }
//
//    public void setEventName(String eventName) {
//        this.eventName = eventName;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public Date getEventDate() {
//        return eventDate;
//    }
//
//    public void setEventDate(Date eventDate) {
//        this.eventDate = eventDate;
//    }
//
//    public String getVenue() {
//        return venue;
//    }
//
//    public void setVenue(String venue) {
//        this.venue = venue;
//    }
//
//    public int getCapacity() {
//        return capacity;
//    }
//
//    public void setCapacity(int capacity) {
//        this.capacity = capacity;
//    }
//
//    public int getOrganizerId() {
//        return organizerId;
//    }
//
//    public void setOrganizerId(int organizerId) {
//        this.organizerId = organizerId;
//    }
//
//    public String getOrganizerName() {
//        return organizerName;
//    }
//
//    public void setOrganizerName(String organizerName) {
//        this.organizerName = organizerName;
//    }
//
//    public Date getCreatedDate() {
//        return createdDate;
//    }
//
//    public void setCreatedDate(Date createdDate) {
//        this.createdDate = createdDate;
//    }
//
//    public int getCreatedBy() {
//        return createdBy;
//    }
//
//    public void setCreatedBy(int createdBy) {
//        this.createdBy = createdBy;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public String getCategory() {
//        return category;
//    }
//
//    public void setCategory(String category) {
//        this.category = category;
//    }
//
//    public int getRegisteredCount() {
//        return registeredCount;
//    }
//
//    public void setRegisteredCount(int registeredCount) {
//        this.registeredCount = registeredCount;
//    }
//
//    public int getAttendedCount() {
//        return attendedCount;
//    }
//
//    public void setAttendedCount(int attendedCount) {
//        this.attendedCount = attendedCount;
//    }
//
//    public double getAverageRating() {
//        return averageRating;
//    }
//
//    public void setAverageRating(double averageRating) {
//        this.averageRating = averageRating;
//    }
//
//    public int getFeedbackCount() {
//        return feedbackCount;
//    }
//
//    public void setFeedbackCount(int feedbackCount) {
//        this.feedbackCount = feedbackCount;
//    }
//
//    public List<EventFeedback> getFeedbacks() {
//        return feedbacks;
//    }
//
//    public void setFeedbacks(List<EventFeedback> feedbacks) {
//        this.feedbacks = feedbacks;
//    }
//
//    public void addFeedback(EventFeedback feedback) {
//        this.feedbacks.add(feedback);
//        updateAverageRating();
//    }
//
//    public List<Integer> getWaitlist() {
//        return waitlist;
//    }
//
//    public void setWaitlist(List<Integer> waitlist) {
//        this.waitlist = waitlist;
//    }
//
//    public boolean isRecurring() {
//        return isRecurring;
//    }
//
//    public void setRecurring(boolean recurring) {
//        isRecurring = recurring;
//    }
//
//    public RecurrenceType getRecurrenceType() {
//        return recurrenceType;
//    }
//
//    public void setRecurrenceType(RecurrenceType recurrenceType) {
//        this.recurrenceType = recurrenceType;
//    }
//
//    public int getRecurrenceInterval() {
//        return recurrenceInterval;
//    }
//
//    public void setRecurrenceInterval(int recurrenceInterval) {
//        this.recurrenceInterval = recurrenceInterval;
//    }
//
//    public Date getRecurrenceEndDate() {
//        return recurrenceEndDate;
//    }
//
//    public void setRecurrenceEndDate(Date recurrenceEndDate) {
//        this.recurrenceEndDate = recurrenceEndDate;
//    }
//
//    public Integer getParentEventId() {
//        return parentEventId;
//    }
//
//    public void setParentEventId(Integer parentEventId) {
//        this.parentEventId = parentEventId;
//    }
//
//    public Date getRegistrationStartDate() {
//        return registrationStartDate;
//    }
//
//    public void setRegistrationStartDate(Date registrationStartDate) {
//        this.registrationStartDate = registrationStartDate;
//    }
//
//    public Date getRegistrationEndDate() {
//        return registrationEndDate;
//    }
//
//    public void setRegistrationEndDate(Date registrationEndDate) {
//        this.registrationEndDate = registrationEndDate;
//    }
//
//    public boolean isAllowWaitlist() {
//        return allowWaitlist;
//    }
//
//    public void setAllowWaitlist(boolean allowWaitlist) {
//        this.allowWaitlist = allowWaitlist;
//    }
//
//    public int getWaitlistCapacity() {
//        return waitlistCapacity;
//    }
//
//    public void setWaitlistCapacity(int waitlistCapacity) {
//        this.waitlistCapacity = waitlistCapacity;
//    }
//
//    // Business logic methods
//
//    /**
//     * Check if event is full
//     */
//    public boolean isFull() {
//        return registeredCount >= capacity;
//    }
//
//    /**
//     * Check if waitlist is full
//     */
//    public boolean isWaitlistFull() {
//        return waitlist.size() >= waitlistCapacity;
//    }
//
//    /**
//     * Get available spots
//     */
//    public int getAvailableSpots() {
//        return capacity - registeredCount;
//    }
//
//    /**
//     * Check if registration is open
//     */
//    public boolean isRegistrationOpen() {
//        Date now = new Date();
//
//        if (registrationStartDate != null && now.before(registrationStartDate)) {
//            return false;
//        }
//
//        if (registrationEndDate != null && now.after(registrationEndDate)) {
//            return false;
//        }
//
//        return !status.equals("CANCELLED") && !status.equals("COMPLETED");
//    }
//
//    /**
//     * Calculate attendance rate
//     */
//    public double getAttendanceRate() {
//        if (registeredCount == 0) return 0.0;
//        return (double) attendedCount / registeredCount * 100.0;
//    }
//
//    /**
//     * Update average rating from feedbacks
//     */
//    private void updateAverageRating() {
//        if (feedbacks.isEmpty()) {
//            averageRating = 0.0;
//            feedbackCount = 0;
//            return;
//        }
//
//        double sum = 0.0;
//        for (EventFeedback feedback : feedbacks) {
//            sum += feedback.getRating();
//        }
//
//        averageRating = sum / feedbacks.size();
//        feedbackCount = feedbacks.size();
//    }
//
//    /**
//     * Get rating stars display
//     */
//    public String getRatingStars() {
//        int fullStars = (int) Math.round(averageRating);
//        StringBuilder stars = new StringBuilder();
//
//        for (int i = 0; i < 5; i++) {
//            if (i < fullStars) {
//                stars.append("★");
//            } else {
//                stars.append("☆");
//            }
//        }
//
//        return stars.toString();
//    }
//
//    /**
//     * Check if event has ended
//     */
//    public boolean hasEnded() {
//        return new Date().after(eventDate) || status.equals("COMPLETED");
//    }
//
//    /**
//     * Get event category icon
//     */
//    public String getCategoryIcon() {
//        switch (category) {
//            case "CULTURAL": return "🎭";
//            case "SPORTS": return "⚽";
//            case "EDUCATIONAL": return "📚";
//            case "SOCIAL": return "🎉";
//            case "RELIGIOUS": return "🕉️";
//            case "HEALTH": return "🏥";
//            default: return "📅";
//        }
//    }
//
//    @Override
//    public String toString() {
//        return eventName + " - " + eventDate + " (" + registeredCount + "/" + capacity + ")";
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) return true;
//        if (obj == null || getClass() != obj.getClass()) return false;
//        Event event = (Event) obj;
//        return eventId == event.eventId;
//    }
//
//    @Override
//    public int hashCode() {
//        return Integer.hashCode(eventId);
//    }
//}
//



package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Event Model
 * Community Event Management System
 * VIT Bhopal MCA 2026
 */
public class Event {

    private int    eventId;
    private String eventName;
    private String description;
    private Date   eventDate;
    private String venue;
    // IssuePanel / EventPanel uses "location"; service uses "venue"
    private String location;
    private int    capacity;
    private int    organizerId;
    private String organizerName;
    // EventPanel stores organizer as plain text column "organizer"
    private String organizer;
    private Date   createdDate;
    private int    createdBy;
    private String status;   // UPCOMING, ONGOING, COMPLETED, CANCELLED
    private String category; // CULTURAL, SPORTS, EDUCATIONAL, SOCIAL, RELIGIOUS, HEALTH

    // Enhanced fields
    private int    registeredCount;
    private int    attendedCount;
    private double averageRating;
    private int    feedbackCount;
    private List<EventFeedback> feedbacks;
    private List<Integer>       waitlist;

    // Recurring
    private boolean       isRecurring;
    private RecurrenceType recurrenceType;
    private int            recurrenceInterval;
    private Date           recurrenceEndDate;
    private Integer        parentEventId;

    // Registration management
    private Date    registrationStartDate;
    private Date    registrationEndDate;
    private boolean allowWaitlist;
    private int     waitlistCapacity;

    // ── Constructors ──────────────────────────────────────────────────────────

    public Event() {
        this.feedbacks       = new ArrayList<>();
        this.waitlist        = new ArrayList<>();
        this.allowWaitlist   = true;
        this.waitlistCapacity= 50;
        this.status          = "UPCOMING";
    }

    public Event(int eventId, String eventName, Date eventDate, String venue,
                 int capacity, int organizerId) {
        this();
        this.eventId     = eventId;
        this.eventName   = eventName;
        this.eventDate   = eventDate;
        this.venue       = venue;
        this.location    = venue;
        this.capacity    = capacity;
        this.organizerId = organizerId;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public int     getEventId()               { return eventId; }
    public void    setEventId(int id)         { this.eventId = id; }

    public String  getEventName()             { return eventName; }
    public void    setEventName(String n)     { this.eventName = n; }

    public String  getDescription()           { return description; }
    public void    setDescription(String d)   { this.description = d; }

    public Date    getEventDate()             { return eventDate; }
    public void    setEventDate(Date d)       { this.eventDate = d; }

    /** Returns venue; falls back to location if venue is null. */
    public String  getVenue()                 { return venue != null ? venue : location; }
    public void    setVenue(String v)         { this.venue = v; this.location = v; }

    /** Alias for venue used in EventPanel queries. */
    public String  getLocation()              { return location != null ? location : venue; }
    public void    setLocation(String l)      { this.location = l; this.venue = l; }

    public int     getCapacity()              { return capacity; }
    public void    setCapacity(int c)         { this.capacity = c; }

    public int     getOrganizerId()           { return organizerId; }
    public void    setOrganizerId(int id)     { this.organizerId = id; }

    public String  getOrganizerName()         { return organizerName; }
    public void    setOrganizerName(String n) { this.organizerName = n; this.organizer = n; }

    /** Alias for organizer plain-text column in EventPanel. */
    public String  getOrganizer()             { return organizer != null ? organizer : organizerName; }
    public void    setOrganizer(String o)     { this.organizer = o; this.organizerName = o; }

    public Date    getCreatedDate()           { return createdDate; }
    public void    setCreatedDate(Date d)     { this.createdDate = d; }

    public int     getCreatedBy()             { return createdBy; }
    public void    setCreatedBy(int id)       { this.createdBy = id; }

    public String  getStatus()               { return status; }
    public void    setStatus(String s)        { this.status = s; }

    public String  getCategory()              { return category; }
    public void    setCategory(String c)      { this.category = c; }

    public int     getRegisteredCount()       { return registeredCount; }
    public void    setRegisteredCount(int rc) { this.registeredCount = rc; }

    public int     getAttendedCount()         { return attendedCount; }
    public void    setAttendedCount(int ac)   { this.attendedCount = ac; }

    public double  getAverageRating()         { return averageRating; }
    public void    setAverageRating(double ar){ this.averageRating = ar; }

    public int     getFeedbackCount()         { return feedbackCount; }
    public void    setFeedbackCount(int fc)   { this.feedbackCount = fc; }

    public List<EventFeedback> getFeedbacks() { return feedbacks; }
    public void setFeedbacks(List<EventFeedback> fb) { this.feedbacks = fb; }
    public void addFeedback(EventFeedback fb) { this.feedbacks.add(fb); updateAverageRating(); }

    public List<Integer> getWaitlist()        { return waitlist; }
    public void setWaitlist(List<Integer> wl) { this.waitlist = wl; }

    public boolean       isRecurring()        { return isRecurring; }
    public void          setRecurring(boolean r) { this.isRecurring = r; }

    public RecurrenceType getRecurrenceType() { return recurrenceType; }
    public void setRecurrenceType(RecurrenceType rt) { this.recurrenceType = rt; }

    public int     getRecurrenceInterval()    { return recurrenceInterval; }
    public void    setRecurrenceInterval(int ri) { this.recurrenceInterval = ri; }

    public Date    getRecurrenceEndDate()     { return recurrenceEndDate; }
    public void    setRecurrenceEndDate(Date d) { this.recurrenceEndDate = d; }

    public Integer getParentEventId()         { return parentEventId; }
    public void    setParentEventId(Integer id) { this.parentEventId = id; }

    public Date    getRegistrationStartDate() { return registrationStartDate; }
    public void    setRegistrationStartDate(Date d) { this.registrationStartDate = d; }

    public Date    getRegistrationEndDate()   { return registrationEndDate; }
    public void    setRegistrationEndDate(Date d) { this.registrationEndDate = d; }

    public boolean isAllowWaitlist()          { return allowWaitlist; }
    public void    setAllowWaitlist(boolean a){ this.allowWaitlist = a; }

    public int     getWaitlistCapacity()      { return waitlistCapacity; }
    public void    setWaitlistCapacity(int wc){ this.waitlistCapacity = wc; }

    // ── Business Methods ──────────────────────────────────────────────────────

    public boolean isFull()            { return registeredCount >= capacity; }
    public boolean isWaitlistFull()    { return waitlist.size() >= waitlistCapacity; }
    public int     getAvailableSpots() { return capacity - registeredCount; }

    public boolean isRegistrationOpen() {
        Date now = new Date();
        if (registrationStartDate != null && now.before(registrationStartDate)) return false;
        if (registrationEndDate   != null && now.after(registrationEndDate))    return false;
        return !"CANCELLED".equals(status) && !"COMPLETED".equals(status);
    }

    public double getAttendanceRate() {
        if (registeredCount == 0) return 0.0;
        return (double) attendedCount / registeredCount * 100.0;
    }

    private void updateAverageRating() {
        if (feedbacks.isEmpty()) { averageRating = 0.0; feedbackCount = 0; return; }
        double sum = 0;
        for (EventFeedback fb : feedbacks) sum += fb.getRating();
        averageRating = sum / feedbacks.size();
        feedbackCount = feedbacks.size();
    }

    public String getRatingStars() {
        int full = (int) Math.round(averageRating);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) sb.append(i < full ? "★" : "☆");
        return sb.toString();
    }

    public boolean hasEnded() {
        return new Date().after(eventDate) || "COMPLETED".equals(status);
    }

    public String getCategoryIcon() {
        if (category == null) return "📅";
        switch (category) {
            case "CULTURAL":    return "🎭";
            case "SPORTS":      return "⚽";
            case "EDUCATIONAL": return "📚";
            case "SOCIAL":      return "🎉";
            case "RELIGIOUS":   return "🕉️";
            case "HEALTH":      return "🏥";
            default:            return "📅";
        }
    }

    @Override public String toString() {
        return eventName + " - " + eventDate + " (" + registeredCount + "/" + capacity + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Event)) return false;
        return eventId == ((Event) obj).eventId;
    }

    @Override public int hashCode() { return Integer.hashCode(eventId); }
}

/**
 * Recurrence type for repeating events.
 */
 enum RecurrenceType {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly");

    private final String displayName;

    RecurrenceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }
}