package models;

import java.util.Date;

public class Representative {

    private int    representativeId;
    private String name;
    private String phone;
    private String email;
    private String category;    // Plumbing, Electrical, Maintenance, Cleaning, Security, Garden …
    private String skillLevel;  // Beginner, Intermediate, Expert
    private String status;      // ACTIVE, INACTIVE, BUSY
    private boolean isAvailable;
    private double rating;
    private int    totalAssignments;
    private int    completedAssignments;
    private double avgResolutionTime; // hours
    private Date   registeredDate;
    // UI-layer aliases (area / position) used in RepresentativePanel
    private String areaCovered;
    private String position;

    // ── Constructors ──────────────────────────────────────────────────────────

    public Representative() {
        this.isAvailable         = true;
        this.status              = "ACTIVE";
        this.rating              = 0.0;
        this.totalAssignments    = 0;
        this.completedAssignments= 0;
        this.registeredDate      = new Date();
    }

    public Representative(String name, String phone, String category) {
        this();
        this.name     = name;
        this.phone    = phone;
        this.category = category;
    }

    public Representative(int repId, String name, String phone, String category) {
        this();
        this.representativeId = repId;
        this.name     = name;
        this.phone    = phone;
        this.category = category;
    }

    public Representative(int repId, String name, String phone, String category, String email) {
        this(repId, name, phone, category);
        this.email = email;
    }

    // ── ID ────────────────────────────────────────────────────────────────────

    public int  getRepresentativeId()               { return representativeId; }
    public void setRepresentativeId(int id)         { this.representativeId = id; }
    /** Alias kept for backward-compatibility with service layer. */
    public int  getRepId()                          { return representativeId; }
    public void setRepId(int id)                    { this.representativeId = id; }

    // ── Name ─────────────────────────────────────────────────────────────────

    public String getName()                         { return name; }
    public void   setName(String name)              { this.name = name; }
    /** Alias used by RepresentativeService mapper. */
    public String getRepName()                      { return name; }
    public void   setRepName(String name)           { this.name = name; }

    // ── Phone / Contact ───────────────────────────────────────────────────────

    public String getPhone()                        { return phone; }
    public void   setPhone(String phone)            { this.phone = phone; }
    /** Alias used by AssignmentDialog. */
    public String getContact()                      { return phone; }
    public void   setContact(String contact)        { this.phone = contact; }
    /** Alias used by RepresentativePanel (column contact_number). */
    public String getContactNumber()                { return phone; }
    public void   setContactNumber(String cn)       { this.phone = cn; }

    // ── Email ─────────────────────────────────────────────────────────────────

    public String getEmail()                        { return email; }
    public void   setEmail(String email)            { this.email = email; }

    // ── Category ─────────────────────────────────────────────────────────────

    public String getCategory()                     { return category; }
    public void   setCategory(String category)      { this.category = category; }

    // ── Skill Level ───────────────────────────────────────────────────────────

    public String getSkillLevel()                   { return skillLevel; }
    public void   setSkillLevel(String sl)          { this.skillLevel = sl; }

    // ── Status / Availability ─────────────────────────────────────────────────

    public String getStatus()                       { return status; }
    public void setStatus(String status) {
        this.status = status;
        this.isAvailable = "ACTIVE".equalsIgnoreCase(status);
    }

    public boolean isAvailable()                    { return isAvailable; }
    public void setAvailable(boolean available) {
        this.isAvailable = available;
        this.status = available ? "ACTIVE" : "BUSY";
    }

    public String getAvailabilityStatus()           { return isAvailable ? "Available" : "Busy"; }

    // ── Area / Position (used in RepresentativePanel UI) ─────────────────────

    public String getAreaCovered()                  { return areaCovered; }
    public void   setAreaCovered(String a)          { this.areaCovered = a; }

    public String getPosition()                     { return position; }
    public void   setPosition(String p)             { this.position = p; }

    // ── Rating ────────────────────────────────────────────────────────────────

    public double getRating()                       { return rating; }
    public void   setRating(double rating)          { this.rating = rating; }

    public String getRatingStars() {
        int full = (int) Math.round(rating);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) sb.append(i < full ? "★" : "☆");
        return sb.toString();
    }

    // ── Assignments ───────────────────────────────────────────────────────────

    public int  getTotalAssignments()               { return totalAssignments; }
    public void setTotalAssignments(int ta)         { this.totalAssignments = ta; }

    public int  getCompletedAssignments()           { return completedAssignments; }
    public void setCompletedAssignments(int ca)     { this.completedAssignments = ca; }

    public double getAvgResolutionTime()            { return avgResolutionTime; }
    public void   setAvgResolutionTime(double art)  { this.avgResolutionTime = art; }

    // ── Date ─────────────────────────────────────────────────────────────────

    public Date getRegisteredDate()                 { return registeredDate; }
    public void setRegisteredDate(Date d)           { this.registeredDate = d; }

    // ── Business Methods ──────────────────────────────────────────────────────

    public double getCompletionRate() {
        if (totalAssignments == 0) return 0.0;
        return (double) completedAssignments / totalAssignments * 100.0;
    }

    public String getFormattedCompletionRate() {
        return String.format("%.1f%%", getCompletionRate());
    }

    public String getCategoryIcon() {
        if (category == null) return "🔨";
        switch (category) {
            case "Plumbing":    return "🚿";
            case "Electrical":  return "⚡";
            case "Maintenance": return "🔧";
            case "Cleaning":    return "🧹";
            case "Security":    return "🔒";
            case "Garden":      return "🌳";
            case "Carpentry":   return "🪚";
            case "Painting":    return "🎨";
            default:            return "🔨";
        }
    }

    public java.awt.Color getStatusColor() {
        if (status == null) return java.awt.Color.GRAY;
        switch (status.toUpperCase()) {
            case "ACTIVE":   return java.awt.Color.GREEN;
            case "BUSY":     return java.awt.Color.ORANGE;
            case "INACTIVE": return java.awt.Color.RED;
            default:         return java.awt.Color.GRAY;
        }
    }

    public boolean isExperienced() {
        return totalAssignments >= 10 && getCompletionRate() >= 80.0;
    }

    public String getExperienceLevel() {
        if (totalAssignments == 0)  return "New";
        if (totalAssignments < 5)   return "Beginner";
        if (totalAssignments < 20)  return "Intermediate";
        if (totalAssignments < 50)  return "Experienced";
        return "Expert";
    }

    public void incrementAssignments()  { this.totalAssignments++; }
    public void incrementCompleted()    { this.completedAssignments++; }

    public void updateAvgResolutionTime(double newTime) {
        if (completedAssignments == 0) {
            this.avgResolutionTime = newTime;
        } else {
            this.avgResolutionTime =
                    (avgResolutionTime * completedAssignments + newTime) / (completedAssignments + 1);
        }
    }

    public String getFormattedResolutionTime() {
        if (avgResolutionTime < 1)   return String.format("%.0f minutes", avgResolutionTime * 60);
        if (avgResolutionTime < 24)  return String.format("%.1f hours",   avgResolutionTime);
        return String.format("%.1f days", avgResolutionTime / 24);
    }

    public boolean canTakeAssignment() {
        return isAvailable && "ACTIVE".equalsIgnoreCase(status);
    }

    @Override public String toString() { return name + " (" + category + ") - " + status; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Representative)) return false;
        return representativeId == ((Representative) obj).representativeId;
    }

    @Override public int hashCode() { return Integer.hashCode(representativeId); }
}