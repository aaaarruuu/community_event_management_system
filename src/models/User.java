package models;

import java.util.Date;

public class User {

    private int    userId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String role;         // ADMIN, MEMBER
    private String flatNumber;
    private String profilePicture;
    private Date   createdDate;
    private Date   lastLogin;
    private boolean isActive;

    // Enhanced fields
    private String  emergencyContact;
    private String  bio;
    private boolean emailNotifications;
    private boolean smsNotifications;
    private String  preferredLanguage;
    private int     loginCount;

    // ── Constructors ──────────────────────────────────────────────────────────

    public User() {
        this.isActive           = true;
        this.emailNotifications = true;
        this.smsNotifications   = false;
        this.preferredLanguage  = "English";
        this.loginCount         = 0;
        this.createdDate        = new Date();
    }

    public User(int userId, String username, String fullName, String role) {
        this();
        this.userId   = userId;
        this.username = username;
        this.fullName = fullName;
        this.role     = role;
    }

    public User(int userId, String username, String password, String fullName, String role) {
        this();
        this.userId   = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role     = role;
    }

    public User(String username, String password, String fullName,
                String email, String phone, String role) {
        this();
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email    = email;
        this.phone    = phone;
        this.role     = role;
    }

    public User(int userId, String username, String password, String fullName,
                String email, String phone, String role) {
        this();
        this.userId   = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email    = email;
        this.phone    = phone;
        this.role     = role;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public int     getUserId()            { return userId; }
    public void    setUserId(int id)      { this.userId = id; }

    public String  getUsername()          { return username; }
    public void    setUsername(String u)  { this.username = u; }

    public String  getPassword()          { return password; }
    public void    setPassword(String p)  { this.password = p; }

    public String  getFullName()          { return fullName; }
    public void    setFullName(String n)  { this.fullName = n; }

    public String  getEmail()             { return email; }
    public void    setEmail(String e)     { this.email = e; }

    public String  getPhone()             { return phone; }
    public void    setPhone(String p)     { this.phone = p; }

    public String  getRole()              { return role; }
    public void    setRole(String r)      { this.role = r; }

    public String  getFlatNumber()        { return flatNumber; }
    public void    setFlatNumber(String f){ this.flatNumber = f; }

    public String  getProfilePicture()    { return profilePicture; }
    public void    setProfilePicture(String pp) { this.profilePicture = pp; }

    public Date    getCreatedDate()       { return createdDate; }
    public void    setCreatedDate(Date d) { this.createdDate = d; }

    public Date    getLastLogin()         { return lastLogin; }
    public void    setLastLogin(Date d)   { this.lastLogin = d; }

    public boolean isActive()             { return isActive; }
    public void    setActive(boolean a)   { this.isActive = a; }

    public String  getEmergencyContact()  { return emergencyContact; }
    public void    setEmergencyContact(String ec) { this.emergencyContact = ec; }

    public String  getBio()               { return bio; }
    public void    setBio(String b)       { this.bio = b; }

    public boolean isEmailNotifications() { return emailNotifications; }
    public void    setEmailNotifications(boolean en) { this.emailNotifications = en; }

    public boolean isSmsNotifications()   { return smsNotifications; }
    public void    setSmsNotifications(boolean sn) { this.smsNotifications = sn; }

    public String  getPreferredLanguage() { return preferredLanguage; }
    public void    setPreferredLanguage(String pl) { this.preferredLanguage = pl; }

    public int     getLoginCount()        { return loginCount; }
    public void    setLoginCount(int lc)  { this.loginCount = lc; }

    // ── Business Methods ──────────────────────────────────────────────────────

    public boolean isAdmin()  { return "ADMIN".equalsIgnoreCase(role); }
    public boolean isMember() { return "MEMBER".equalsIgnoreCase(role); }

    public void incrementLoginCount() { this.loginCount++; }
    public void updateLastLogin()     { this.lastLogin = new Date(); }

    public String getInitials() {
        if (fullName == null || fullName.isEmpty()) return "U";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length >= 2) {
            return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
        }
        return fullName.substring(0, Math.min(2, fullName.length())).toUpperCase();
    }

    public String getDisplayNameWithRole() {
        return fullName + " (" + role + ")";
    }

    public boolean hasValidEmail() {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public boolean hasValidPhone() {
        return phone != null && phone.matches("^[0-9]{10}$");
    }

    @Override public String toString()    { return fullName + " (" + role + ")"; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User)) return false;
        return userId == ((User) obj).userId;
    }

    @Override public int hashCode()       { return Integer.hashCode(userId); }
}