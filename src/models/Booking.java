package models;

import java.sql.Time;
import java.util.Date;

/**
 * Booking Model for Amenity Reservations
 * VIT Bhopal MCA 2026 - Version 2.0
 */
public class Booking {
    private int bookingId;
    private int amenityId;
    private String amenityName;
    private int userId;
    private String userName;
    private Date bookingDate;
    private Time startTime;
    private Time endTime;
    private int durationHours;
    private double totalCost;
    private String status; // PENDING, CONFIRMED, CANCELLED, COMPLETED
    private String purpose;
    private int attendees;
    private Date createdAt;
    private Date confirmedAt;
    private Date cancelledAt;
    private String cancellationReason;
    private String specialRequests;

    // Payment fields
    private boolean isPaid;
    private String paymentMethod;
    private String paymentTransactionId;
    private Date paymentDate;

    // Constructors
    public Booking() {
        this.createdAt = new Date();
        this.status = "PENDING";
        this.isPaid = false;
    }

    public Booking(int amenityId, int userId, Date bookingDate,
                   Time startTime, Time endTime) {
        this();
        this.amenityId = amenityId;
        this.userId = userId;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        calculateDuration();
    }

    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getAmenityId() {
        return amenityId;
    }

    public void setAmenityId(int amenityId) {
        this.amenityId = amenityId;
    }

    public String getAmenityName() {
        return amenityName;
    }

    public void setAmenityName(String amenityName) {
        this.amenityName = amenityName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
        calculateDuration();
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
        calculateDuration();
    }

    public int getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(int durationHours) {
        this.durationHours = durationHours;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public int getAttendees() {
        return attendees;
    }

    public void setAttendees(int attendees) {
        this.attendees = attendees;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Date confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public Date getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(Date cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentTransactionId() {
        return paymentTransactionId;
    }

    public void setPaymentTransactionId(String paymentTransactionId) {
        this.paymentTransactionId = paymentTransactionId;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    // Business logic methods

    /**
     * Calculate duration in hours
     */
    private void calculateDuration() {
        if (startTime != null && endTime != null) {
            long diff = endTime.getTime() - startTime.getTime();
            this.durationHours = (int) (diff / (1000 * 60 * 60));
        }
    }

    /**
     * Check if booking is active
     */
    public boolean isActive() {
        return status.equals("CONFIRMED") || status.equals("PENDING");
    }

    /**
     * Check if booking can be cancelled
     */
    public boolean canBeCancelled() {
        if (!isActive()) return false;

        // Can't cancel if booking date has passed
        Date now = new Date();
        return bookingDate.after(now);
    }

    /**
     * Check if booking has passed
     */
    public boolean hasPassed() {
        Date now = new Date();
        return bookingDate.before(now);
    }

    /**
     * Get status color
     */
    public java.awt.Color getStatusColor() {
        switch (status) {
            case "CONFIRMED":
                return java.awt.Color.GREEN;
            case "PENDING":
                return java.awt.Color.ORANGE;
            case "CANCELLED":
                return java.awt.Color.RED;
            case "COMPLETED":
                return java.awt.Color.BLUE;
            default:
                return java.awt.Color.GRAY;
        }
    }

    /**
     * Get formatted time range
     */
    public String getTimeRange() {
        return startTime.toString() + " - " + endTime.toString();
    }

    /**
     * Get formatted cost display
     */
    public String getCostDisplay() {
        return String.format("₹%.2f", totalCost);
    }

    /**
     * Get payment status display
     */
    public String getPaymentStatus() {
        if (totalCost == 0) return "Free";
        return isPaid ? "✓ Paid" : "⚠ Payment Pending";
    }

    /**
     * Confirm booking
     */
    public void confirm() {
        this.status = "CONFIRMED";
        this.confirmedAt = new Date();
    }

    /**
     * Cancel booking
     */
    public void cancel(String reason) {
        this.status = "CANCELLED";
        this.cancelledAt = new Date();
        this.cancellationReason = reason;
    }

    /**
     * Complete booking
     */
    public void complete() {
        this.status = "COMPLETED";
    }

    /**
     * Mark as paid
     */
    public void markPaid(String paymentMethod, String transactionId) {
        this.isPaid = true;
        this.paymentMethod = paymentMethod;
        this.paymentTransactionId = transactionId;
        this.paymentDate = new Date();
    }

    @Override
    public String toString() {
        return amenityName + " - " + bookingDate + " " + getTimeRange() +
                " (" + status + ")";
    }
}