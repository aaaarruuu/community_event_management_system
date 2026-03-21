package models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Poll Model for Community Voting System
 * VIT Bhopal MCA 2026 - Version 2.0
 */
public class Poll {
    private int pollId;
    private String question;
    private String description;
    private List<String> options;
    private int createdBy;
    private String creatorName;
    private Date startDate;
    private Date endDate;
    private boolean isAnonymous;
    private boolean allowMultipleChoice;
    private String category; // GENERAL, EVENT, AMENITY, RULE_CHANGE, etc.
    private String status; // DRAFT, ACTIVE, CLOSED, CANCELLED
    private int totalVotes;
    private Map<String, Integer> results;
    private boolean resultsVisible; // Can users see results before voting?

    // Constructors
    public Poll() {
        this.startDate = new Date();
        this.status = "DRAFT";
        this.isAnonymous = true;
        this.allowMultipleChoice = false;
        this.totalVotes = 0;
        this.results = new HashMap<>();
        this.resultsVisible = false;
    }

    public Poll(String question, List<String> options, int createdBy) {
        this();
        this.question = question;
        this.options = options;
        this.createdBy = createdBy;

        // Initialize results map
        for (String option : options) {
            results.put(option, 0);
        }
    }

    // Getters and Setters
    public int getPollId() {
        return pollId;
    }

    public void setPollId(int pollId) {
        this.pollId = pollId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
        // Initialize results
        results.clear();
        for (String option : options) {
            results.put(option, 0);
        }
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public boolean isAllowMultipleChoice() {
        return allowMultipleChoice;
    }

    public void setAllowMultipleChoice(boolean allowMultipleChoice) {
        this.allowMultipleChoice = allowMultipleChoice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }

    public Map<String, Integer> getResults() {
        return results;
    }

    public void setResults(Map<String, Integer> results) {
        this.results = results;
    }

    public boolean isResultsVisible() {
        return resultsVisible;
    }

    public void setResultsVisible(boolean resultsVisible) {
        this.resultsVisible = resultsVisible;
    }

    // Business logic methods

    /**
     * Check if poll is currently active
     */
    public boolean isActive() {
        if (!status.equals("ACTIVE")) return false;

        Date now = new Date();

        if (startDate != null && now.before(startDate)) {
            return false;
        }

        if (endDate != null && now.after(endDate)) {
            return false;
        }

        return true;
    }

    /**
     * Check if poll has ended
     */
    public boolean hasEnded() {
        if (status.equals("CLOSED") || status.equals("CANCELLED")) {
            return true;
        }

        if (endDate != null) {
            return new Date().after(endDate);
        }

        return false;
    }

    /**
     * Add a vote to an option
     */
    public void addVote(String option) {
        if (results.containsKey(option)) {
            results.put(option, results.get(option) + 1);
            totalVotes++;
        }
    }

    /**
     * Get vote count for an option
     */
    public int getVoteCount(String option) {
        return results.getOrDefault(option, 0);
    }

    /**
     * Get percentage for an option
     */
    public double getPercentage(String option) {
        if (totalVotes == 0) return 0.0;
        return (double) getVoteCount(option) / totalVotes * 100.0;
    }

    /**
     * Get formatted percentage
     */
    public String getFormattedPercentage(String option) {
        return String.format("%.1f%%", getPercentage(option));
    }

    /**
     * Get winning option
     */
    public String getWinningOption() {
        String winner = null;
        int maxVotes = 0;

        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                winner = entry.getKey();
            }
        }

        return winner;
    }

    /**
     * Get progress bar width for option (0-100)
     */
    public int getProgressBarWidth(String option) {
        return (int) getPercentage(option);
    }

    /**
     * Activate poll
     */
    public void activate() {
        this.status = "ACTIVE";
        if (this.startDate == null) {
            this.startDate = new Date();
        }
    }

    /**
     * Close poll
     */
    public void close() {
        this.status = "CLOSED";
    }

    /**
     * Cancel poll
     */
    public void cancel() {
        this.status = "CANCELLED";
    }

    /**
     * Get days remaining
     */
    public long getDaysRemaining() {
        if (endDate == null) return -1;

        long diff = endDate.getTime() - new Date().getTime();
        return diff / (1000 * 60 * 60 * 24);
    }

    /**
     * Get status display
     */
    public String getStatusDisplay() {
        if (status.equals("ACTIVE")) {
            long days = getDaysRemaining();
            if (days >= 0) {
                return "Active (" + days + " days left)";
            }
            return "Active";
        }
        return status;
    }

    /**
     * Get category icon
     */
    public String getCategoryIcon() {
        switch (category) {
            case "EVENT": return "🎉";
            case "AMENITY": return "🏊";
            case "RULE_CHANGE": return "📜";
            case "BUDGET": return "💰";
            case "GENERAL": return "📊";
            default: return "🗳️";
        }
    }

    /**
     * Calculate participation rate (if total members known)
     */
    public double getParticipationRate(int totalMembers) {
        if (totalMembers == 0) return 0.0;
        return (double) totalVotes / totalMembers * 100.0;
    }

    @Override
    public String toString() {
        return question + " (" + totalVotes + " votes)";
    }
}