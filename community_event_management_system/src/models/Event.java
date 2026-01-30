package models;

import java.sql.Date;
import java.sql.Time;

/**
 * Event Model Class
 * Represents a community event
 */
public class Event {
    private int eventId;
    private String title;
    private Date eventDate;
    private Time eventTime;
    private String description;
    private String venue;
    private String organizer;
    private String imagePath;
    private int createdBy;

    // Constructors
    public Event() {}

    public Event(String title, Date eventDate, Time eventTime, String description,
                 String venue, String organizer, String imagePath, int createdBy) {
        this.title = title;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.description = description;
        this.venue = venue;
        this.organizer = organizer;
        this.imagePath = imagePath;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Time getEventTime() {
        return eventTime;
    }

    public void setEventTime(Time eventTime) {
        this.eventTime = eventTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", title='" + title + '\'' +
                ", eventDate=" + eventDate +
                ", eventTime=" + eventTime +
                ", venue='" + venue + '\'' +
                '}';
    }
}