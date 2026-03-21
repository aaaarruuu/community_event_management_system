package models;

import java.util.Date;

/**
 * Amenity Model for Community Facilities
 * VIT Bhopal MCA 2026 - Version 2.0
 */
public class Amenity {
    private int amenityId;
    private String name;
    private String description;
    private String category; // GYM, POOL, CLUBHOUSE, COURT, HALL, etc.
    private int capacity;
    private double costPerHour;
    private String operatingHours; // e.g., "06:00-22:00"
    private boolean requiresApproval;
    private int minimumBookingHours;
    private int maximumBookingHours;
    private String amenityRules;
    private boolean isActive;
    private String location;
    private String[] photos;
    private double rating;
    private int bookingCount;

    // Constructors
    public Amenity() {
        this.isActive = true;
        this.requiresApproval = false;
        this.minimumBookingHours = 1;
        this.maximumBookingHours = 4;
        this.rating = 0.0;
        this.bookingCount = 0;
    }

    public Amenity(int amenityId, String name, String category, int capacity) {
        this();
        this.amenityId = amenityId;
        this.name = name;
        this.category = category;
        this.capacity = capacity;
    }

    // Getters and Setters
    public int getAmenityId() {
        return amenityId;
    }

    public void setAmenityId(int amenityId) {
        this.amenityId = amenityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getCostPerHour() {
        return costPerHour;
    }

    public void setCostPerHour(double costPerHour) {
        this.costPerHour = costPerHour;
    }

    public String getOperatingHours() {
        return operatingHours;
    }

    public void setOperatingHours(String operatingHours) {
        this.operatingHours = operatingHours;
    }

    public boolean isRequiresApproval() {
        return requiresApproval;
    }

    public void setRequiresApproval(boolean requiresApproval) {
        this.requiresApproval = requiresApproval;
    }

    public int getMinimumBookingHours() {
        return minimumBookingHours;
    }

    public void setMinimumBookingHours(int minimumBookingHours) {
        this.minimumBookingHours = minimumBookingHours;
    }

    public int getMaximumBookingHours() {
        return maximumBookingHours;
    }

    public void setMaximumBookingHours(int maximumBookingHours) {
        this.maximumBookingHours = maximumBookingHours;
    }

    public String getAmenityRules() {
        return amenityRules;
    }

    public void setAmenityRules(String amenityRules) {
        this.amenityRules = amenityRules;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String[] getPhotos() {
        return photos;
    }

    public void setPhotos(String[] photos) {
        this.photos = photos;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(int bookingCount) {
        this.bookingCount = bookingCount;
    }

    // Business logic methods

    /**
     * Get category icon
     */
    public String getCategoryIcon() {
        switch (category) {
            case "GYM": return "🏋️";
            case "POOL": return "🏊";
            case "CLUBHOUSE": return "🏠";
            case "COURT": return "🎾";
            case "HALL": return "🎪";
            case "GARDEN": return "🌳";
            case "PARKING": return "🚗";
            default: return "📍";
        }
    }

    /**
     * Get rating stars display
     */
    public String getRatingStars() {
        int fullStars = (int) Math.round(rating);
        StringBuilder stars = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            if (i < fullStars) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }

        return stars.toString();
    }

    /**
     * Check if amenity is available at given time
     */
    public boolean isOperatingAt(String time) {
        if (operatingHours == null || operatingHours.isEmpty()) {
            return true; // 24/7 if not specified
        }

        String[] hours = operatingHours.split("-");
        if (hours.length != 2) return true;

        try {
            int openHour = Integer.parseInt(hours[0].split(":")[0]);
            int closeHour = Integer.parseInt(hours[1].split(":")[0]);
            int checkHour = Integer.parseInt(time.split(":")[0]);

            return checkHour >= openHour && checkHour < closeHour;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Calculate cost for duration
     */
    public double calculateCost(int hours) {
        return costPerHour * hours;
    }

    /**
     * Get formatted cost display
     */
    public String getCostDisplay() {
        if (costPerHour == 0) {
            return "Free";
        }
        return String.format("₹%.2f/hour", costPerHour);
    }

    @Override
    public String toString() {
        return name + " (" + category + ") - " + getCostDisplay();
    }
}