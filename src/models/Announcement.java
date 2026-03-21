package models;

import java.util.Date;

/**
 * Announcement Model for Community Notices
 * VIT Bhopal MCA 2026 - Version 2.0
 */
public class Announcement {
    private int announcementId;
    private String title;
    private String message;
    private String category; // GENERAL, URGENT, MAINTENANCE, EVENT, RULE, etc.
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL
    private int postedBy;
    private String posterName;
    private Date postedDate;
    private Date expiryDate;
    private boolean isPinned;
    private boolean requiresAcknowledgment;
    private int viewCount;
    private int acknowledgmentCount;
    private String[] attachments;
    private String targetAudience; // ALL, OWNERS, TENANTS, COMMITTEE

    // Constructors
    public Announcement() {
        this.postedDate = new Date();
        this.priority = "MEDIUM";
        this.isPinned = false;
        this.requiresAcknowledgment = false;
        this.viewCount = 0;
        this.acknowledgmentCount = 0;
        this.targetAudience = "ALL";
    }

    public Announcement(String title, String message, int postedBy) {
        this();
        this.title = title;
        this.message = message;
        this.postedBy = postedBy;
    }

    // Getters and Setters
    public int getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(int announcementId) {
        this.announcementId = announcementId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public int getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(int postedBy) {
        this.postedBy = postedBy;
    }

    public String getPosterName() {
        return posterName;
    }

    public void setPosterName(String posterName) {
        this.posterName = posterName;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public boolean isRequiresAcknowledgment() {
        return requiresAcknowledgment;
    }

    public void setRequiresAcknowledgment(boolean requiresAcknowledgment) {
        this.requiresAcknowledgment = requiresAcknowledgment;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getAcknowledgmentCount() {
        return acknowledgmentCount;
    }

    public void setAcknowledgmentCount(int acknowledgmentCount) {
        this.acknowledgmentCount = acknowledgmentCount;
    }

    public String[] getAttachments() {
        return attachments;
    }

    public void setAttachments(String[] attachments) {
        this.attachments = attachments;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    // Business logic methods

    /**
     * Check if announcement is active
     */
    public boolean isActive() {
        if (expiryDate == null) return true;
        return new Date().before(expiryDate);
    }

    /**
     * Check if announcement has expired
     */
    public boolean hasExpired() {
        if (expiryDate == null) return false;
        return new Date().after(expiryDate);
    }

    /**
     * Get priority color
     */
    public java.awt.Color getPriorityColor() {
        switch (priority) {
            case "CRITICAL":
                return java.awt.Color.RED;
            case "HIGH":
                return java.awt.Color.ORANGE;
            case "MEDIUM":
                return java.awt.Color.BLUE;
            case "LOW":
                return java.awt.Color.GRAY;
            default:
                return java.awt.Color.BLACK;
        }
    }

    /**
     * Get priority icon
     */
    public String getPriorityIcon() {
        switch (priority) {
            case "CRITICAL":
                return "🚨";
            case "HIGH":
                return "⚠️";
            case "MEDIUM":
                return "ℹ️";
            case "LOW":
                return "📝";
            default:
                return "📢";
        }
    }

    /**
     * Get category icon
     */
    public String getCategoryIcon() {
        switch (category) {
            case "URGENT":
                return "🚨";
            case "MAINTENANCE":
                return "🔧";
            case "EVENT":
                return "🎉";
            case "RULE":
                return "📜";
            case "SAFETY":
                return "🛡️";
            case "GENERAL":
                return "📢";
            default:
                return "📋";
        }
    }

    /**
     * Increment view count
     */
    public void incrementViewCount() {
        this.viewCount++;
    }

    /**
     * Increment acknowledgment count
     */
    public void incrementAcknowledgmentCount() {
        this.acknowledgmentCount++;
    }

    /**
     * Get acknowledgment rate
     */
    public double getAcknowledgmentRate(int totalMembers) {
        if (totalMembers == 0) return 0.0;
        return (double) acknowledgmentCount / totalMembers * 100.0;
    }

    /**
     * Get formatted acknowledgment rate
     */
    public String getFormattedAcknowledgmentRate(int totalMembers) {
        return String.format("%.1f%%", getAcknowledgmentRate(totalMembers));
    }

    /**
     * Get truncated message for preview
     */
    public String getMessagePreview(int maxLength) {
        if (message == null || message.length() <= maxLength) {
            return message;
        }
        return message.substring(0, maxLength) + "...";
    }

    /**
     * Get days until expiry
     */
    public long getDaysUntilExpiry() {
        if (expiryDate == null) return -1;

        long diff = expiryDate.getTime() - new Date().getTime();
        return diff / (1000 * 60 * 60 * 24);
    }

    /**
     * Get pinned indicator
     */
    public String getPinnedIndicator() {
        return isPinned ? "📌 " : "";
    }

    /**
     * Toggle pin status
     */
    public void togglePin() {
        this.isPinned = !this.isPinned;
    }

    /**
     * Check if has attachments
     */
    public boolean hasAttachments() {
        return attachments != null && attachments.length > 0;
    }

    /**
     * Get attachment count
     */
    public int getAttachmentCount() {
        return attachments != null ? attachments.length : 0;
    }

    /**
     * Get formatted posted date
     */
    public String getFormattedPostedDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy HH:mm");
        return sdf.format(postedDate);
    }

    /**
     * Get time ago string
     */
    public String getTimeAgo() {
        long diff = new Date().getTime() - postedDate.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) return days + " day" + (days > 1 ? "s" : "") + " ago";
        if (hours > 0) return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        if (minutes > 0) return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        return "Just now";
    }

    @Override
    public String toString() {
        return getPriorityIcon() + " " + title + " - " + getTimeAgo();
    }
}