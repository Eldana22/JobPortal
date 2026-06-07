package com.example.jobportal.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String fullName;
    private String email;
    private String password;
    private String userType; // "seeker", "employer", or "admin"
    private String address;   // ADD THIS
    private String cvPath;    // ADD THIS

    // Constructor
    public User(String fullName, String email, String password, String userType) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.address = "";     // ADD THIS
        this.cvPath = "";      // ADD THIS
    }

    // Getters
    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getUserType() { return userType; }
    public String getAddress() { return address; }      // ADD THIS
    public String getCvPath() { return cvPath; }        // ADD THIS

    // Setters
    public void setId(int id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setUserType(String userType) { this.userType = userType; }
    public void setAddress(String address) { this.address = address; }    // ADD THIS
    public void setCvPath(String cvPath) { this.cvPath = cvPath; }        // ADD THIS
}