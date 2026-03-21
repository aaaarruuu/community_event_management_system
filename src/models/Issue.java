package models;

import java.util.Date;

public class Issue {

    private int     issueId;
    // UI layer uses "title"; service layer uses "description" as primary text
    private String  title;
    private String  category;
    private String  priority;      // LOW, MEDIUM, HIGH, CRITICAL
    private String  description;
    private String  status;        // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    private int     reporterId;
    private String  reporterName;
    private String  reportedBy;    // alias used by IssuePanel
    private Date    reportedDate;
    private Integer assignedTo;
    private String  assignedToName;
    private Date    assignedDate;
    private Date    resolvedDate;
    private String  location;
    private String  resolution;
    private double  estimatedCost;
    private double  actualCost;

    private String[]  photoUrls;
    private int       photoCount;
    private boolean   isEscalated;
    private String    escalationReason;
    private int       rating;
    private String    feedback;

    // ── Constructors ──────────────────────────────────────────────────────────

    public Issue() {
        this.status       = "PENDING";
        this.reportedDate = new Date();
        this.priority     = "MEDIUM";
        this.isEscalated  = false;
        this.photoCount   = 0;
    }

    public Issue(String category, String description, int reporterId, String location) {
        this();
        this.category    = category;
        this.description = description;
        this.title       = description; // keep both in sync
        this.reporterId  = reporterId;
        this.location    = location;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public int     getIssueId()                { return issueId; }
    public void    setIssueId(int id)          { this.issueId = id; }

    /** Primary display name used by IssuePanel. */
    public String  getTitle()                  { return title != null ? title : description; }
    public void    setTitle(String t)          { this.title = t; }

    public String  getCategory()               { return category; }
    public void    setCategory(String c)       { this.category = c; }

    public String  getPriority()               { return priority; }
    public void    setPriority(String p)       { this.priority = p; }

    public String  getDescription()            { return description; }
    public void    setDescription(String d)    { this.description = d; }

    public String  getStatus()                 { return status; }
    public void    setStatus(String s)         { this.status = s; }

    public int     getReporterId()             { return reporterId; }
    public void    setReporterId(int id)       { this.reporterId = id; }

    public String  getReporterName()           { return reporterName; }
    public void    setReporterName(String rn)  { this.reporterName = rn; }

    /** Alias for reported_by column in IssuePanel queries. */
    public String  getReportedBy()             { return reportedBy != null ? reportedBy : reporterName; }
    public void    setReportedBy(String rb)    { this.reportedBy = rb; this.reporterName = rb; }

    public Date    getReportedDate()           { return reportedDate; }
    public void    setReportedDate(Date d)     { this.reportedDate = d; }

    public Integer getAssignedTo()             { return assignedTo; }
    public void    setAssignedTo(Integer id)   { this.assignedTo = id; }

    public String  getAssignedToName()         { return assignedToName; }
    public void    setAssignedToName(String n) { this.assignedToName = n; }

    public Date    getAssignedDate()           { return assignedDate; }
    public void    setAssignedDate(Date d)     { this.assignedDate = d; }

    public Date    getResolvedDate()           { return resolvedDate; }
    public void    setResolvedDate(Date d)     { this.resolvedDate = d; }

    public String  getLocation()               { return location; }
    public void    setLocation(String l)       { this.location = l; }

    public String  getResolution()             { return resolution; }
    public void    setResolution(String r)     { this.resolution = r; }

    public double  getEstimatedCost()          { return estimatedCost; }
    public void    setEstimatedCost(double ec) { this.estimatedCost = ec; }

    public double  getActualCost()             { return actualCost; }
    public void    setActualCost(double ac)    { this.actualCost = ac; }

    public String[] getPhotoUrls()             { return photoUrls; }
    public void    setPhotoUrls(String[] pu)   { this.photoUrls = pu; this.photoCount = pu != null ? pu.length : 0; }

    public int     getPhotoCount()             { return photoCount; }
    public void    setPhotoCount(int pc)       { this.photoCount = pc; }

    public boolean isEscalated()               { return isEscalated; }
    public void    setEscalated(boolean e)     { this.isEscalated = e; }

    public String  getEscalationReason()       { return escalationReason; }
    public void    setEscalationReason(String er) { this.escalationReason = er; }

    public int     getRating()                 { return rating; }
    public void    setRating(int r)            { this.rating = r; }

    public String  getFeedback()               { return feedback; }
    public void    setFeedback(String f)       { this.feedback = f; }

    // ── Business Methods ──────────────────────────────────────────────────────

    public boolean isPending()    { return "PENDING".equals(status); }
    public boolean isInProgress() { return "IN_PROGRESS".equals(status); }
    public boolean isCompleted()  { return "COMPLETED".equals(status); }
    public boolean isCancelled()  { return "CANCELLED".equals(status); }
    public boolean isAssigned()   { return assignedTo != null; }

    public long getDaysOpen() {
        Date end = resolvedDate != null ? resolvedDate : new Date();
        return (end.getTime() - reportedDate.getTime()) / (1000 * 60 * 60 * 24);
    }

    public long getResolutionTimeHours() {
        if (resolvedDate == null || reportedDate == null) return 0;
        return (resolvedDate.getTime() - reportedDate.getTime()) / (1000 * 60 * 60);
    }

    public java.awt.Color getPriorityColor() {
        switch (priority != null ? priority : "") {
            case "CRITICAL": return java.awt.Color.RED;
            case "HIGH":     return java.awt.Color.ORANGE;
            case "MEDIUM":   return java.awt.Color.BLUE;
            case "LOW":      return java.awt.Color.GRAY;
            default:         return java.awt.Color.BLACK;
        }
    }

    public java.awt.Color getStatusColor() {
        switch (status != null ? status : "") {
            case "PENDING":     return java.awt.Color.ORANGE;
            case "IN_PROGRESS": return java.awt.Color.BLUE;
            case "COMPLETED":   return java.awt.Color.GREEN;
            case "CANCELLED":   return java.awt.Color.RED;
            default:            return java.awt.Color.GRAY;
        }
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
            default:            return "🔨";
        }
    }

    public String getPriorityIcon() {
        switch (priority != null ? priority : "") {
            case "CRITICAL": return "🚨";
            case "HIGH":     return "⚠️";
            case "MEDIUM":   return "ℹ️";
            case "LOW":      return "📝";
            default:         return "📋";
        }
    }

    public String getRatingStars() {
        if (rating == 0) return "Not rated";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) sb.append(i < rating ? "★" : "☆");
        return sb.toString();
    }

    public void escalate(String reason) {
        this.isEscalated       = true;
        this.escalationReason  = reason;
    }

    @Override public String toString() {
        return getCategoryIcon() + " " + category + " - " + status + " (ID: " + issueId + ")";
    }
}