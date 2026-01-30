package models;

import java.sql.Timestamp;

/**
 * Issue Model Class
 * Represents a community issue/problem
 */
public class Issue {
    private int issueId;
    private String category;
    private String description;
    private String location;
    private String imagePath;
    private int reporterId;
    private Timestamp dateReported;
    private String status;
    private String priority;

    // Constructors
    public Issue() {}

    public Issue(String category, String description, String location,
                 String imagePath, int reporterId, String status, String priority) {
        this.category = category;
        this.description = description;
        this.location = location;
        this.imagePath = imagePath;
        this.reporterId = reporterId;
        this.status = status;
        this.priority = priority;
    }

    // Getters and Setters
    public int getIssueId() {
        return issueId;
    }

    public void setIssueId(int issueId) {
        this.issueId = issueId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getReporterId() {
        return reporterId;
    }

    public void setReporterId(int reporterId) {
        this.reporterId = reporterId;
    }

    public Timestamp getDateReported() {
        return dateReported;
    }

    public void setDateReported(Timestamp dateReported) {
        this.dateReported = dateReported;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "issueId=" + issueId +
                ", category='" + category + '\'' +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                '}';
    }
}