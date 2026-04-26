package com.miniProjectApp.entity;

import java.sql.Timestamp;

public class User {
    public enum Role { STUDENT, TEACHER }

    private int id;
    private String fullName;
    private String email;
    private String password;
    private Role role;
    private String avatarColor;
    private Timestamp createdAt;

    public User() {}
    public User(String fullName, String email, String password, Role role) {
        this.fullName = fullName; this.email = email;
        this.password = password; this.role = role;
    }

    public int getId()                    { return id; }
    public void setId(int id)             { this.id = id; }
    public String getFullName()           { return fullName; }
    public void setFullName(String n)     { this.fullName = n; }
    public String getEmail()              { return email; }
    public void setEmail(String e)        { this.email = e; }
    public String getPassword()           { return password; }
    public void setPassword(String p)     { this.password = p; }
    public Role getRole()                 { return role; }
    public void setRole(Role r)           { this.role = r; }
    public String getAvatarColor()        { return avatarColor; }
    public void setAvatarColor(String c)  { this.avatarColor = c; }
    public Timestamp getCreatedAt()       { return createdAt; }
    public void setCreatedAt(Timestamp t) { this.createdAt = t; }

    public String getInitials() {
        if (fullName == null || fullName.isEmpty()) return "?";
        String[] parts = fullName.trim().split("\\s+");
        return parts.length >= 2
                ? String.valueOf(parts[0].charAt(0)) + parts[parts.length-1].charAt(0)
                : String.valueOf(parts[0].charAt(0));
    }

    @Override public String toString() { return fullName; }
}
