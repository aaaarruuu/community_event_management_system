package models;

import java.util.Date;

public class EventFeedback {

    private int     feedbackId;
    private int     eventId;
    private int     userId;
    private String  userName;
    private int     rating;      // 1-5
    private String  comment;
    private Date    submittedDate;
    private boolean isAnonymous;
    private String  aspectRatings; // JSON
    private boolean verified;
    private int     helpfulCount;

    public EventFeedback() {
        this.submittedDate = new Date();
        this.isAnonymous   = false;
        this.verified      = false;
        this.helpfulCount  = 0;
    }

    public EventFeedback(int eventId, int userId, int rating, String comment) {
        this();
        this.eventId = eventId;
        this.userId  = userId;
        setRating(rating);
        this.comment = comment;
    }

    // Getters & Setters
    public int     getFeedbackId()              { return feedbackId; }
    public void    setFeedbackId(int id)        { this.feedbackId = id; }

    public int     getEventId()                 { return eventId; }
    public void    setEventId(int id)           { this.eventId = id; }

    public int     getUserId()                  { return userId; }
    public void    setUserId(int id)            { this.userId = id; }

    public String  getUserName()                { return userName; }
    public void    setUserName(String n)        { this.userName = n; }

    public int     getRating()                  { return rating; }
    public void    setRating(int rating) {
        if (rating < 1 || rating > 5)
            throw new IllegalArgumentException("Rating must be 1-5");
        this.rating = rating;
    }

    public String  getComment()                 { return comment; }
    public void    setComment(String c)         { this.comment = c; }

    public Date    getSubmittedDate()           { return submittedDate; }
    public void    setSubmittedDate(Date d)     { this.submittedDate = d; }

    public boolean isAnonymous()                { return isAnonymous; }
    public void    setAnonymous(boolean a)      { this.isAnonymous = a; }

    public String  getAspectRatings()           { return aspectRatings; }
    public void    setAspectRatings(String ar)  { this.aspectRatings = ar; }

    public boolean isVerified()                 { return verified; }
    public void    setVerified(boolean v)       { this.verified = v; }

    public int     getHelpfulCount()            { return helpfulCount; }
    public void    setHelpfulCount(int hc)      { this.helpfulCount = hc; }

    // Business methods
    public String getStarDisplay() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) sb.append(i < rating ? "★" : "☆");
        return sb.toString();
    }

    public String getDisplayName()    { return isAnonymous ? "Anonymous User" : userName; }
    public String getVerifiedBadge()  { return verified ? "✓ Verified Attendee" : ""; }

    public String getCommentPreview(int maxLength) {
        if (comment == null || comment.length() <= maxLength) return comment;
        return comment.substring(0, maxLength) + "...";
    }

    @Override public String toString() {
        return getStarDisplay() + " - " + getDisplayName() + (verified ? " ✓" : "");
    }
}