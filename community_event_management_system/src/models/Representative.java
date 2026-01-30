package models;

/**
 * Representative Model Class
 * Represents service representatives (plumber, electrician, etc.)
 */
public class Representative {
    private int repId;
    private String name;
    private String category;
    private String contact;
    private String email;
    private String imagePath;
    private String status;

    // Constructors
    public Representative() {}

    public Representative(String name, String category, String contact,
                          String email, String imagePath, String status) {
        this.name = name;
        this.category = category;
        this.contact = contact;
        this.email = email;
        this.imagePath = imagePath;
        this.status = status;
    }

    // Getters and Setters
    public int getRepId() {
        return repId;
    }

    public void setRepId(int repId) {
        this.repId = repId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return name + " (" + category + ")";
    }
}