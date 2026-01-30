package models;

/**
 * User Model Class
 * Represents a user in the system
 */
public class User {
    private int userId;
    private String username;
    private String password;
    private String role;
    private String contact;
    private String email;

    // Constructors
    public User() {}

    public User(int userId, String username, String role, String contact, String email) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.contact = contact;
        this.email = email;
    }

    public User(String username, String password, String role, String contact, String email) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.contact = contact;
        this.email = email;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", contact='" + contact + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}