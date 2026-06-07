package com.example.jobportal.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "applications")
public class Application {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int jobId;
    private int seekerId;
    private String coverLetter;
    private String phoneNumber;
    private String email;
    private String socialMedia;
    private String cvFileName;  // Store CV file name
    private String status;      // "pending", "accepted", "rejected"
    private String appliedDate;

    // Constructor
    public Application(int jobId, int seekerId, String coverLetter, String phoneNumber,
                       String email, String socialMedia, String cvFileName, String status, String appliedDate) {
        this.jobId = jobId;
        this.seekerId = seekerId;
        this.coverLetter = coverLetter;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.socialMedia = socialMedia;
        this.cvFileName = cvFileName;
        this.status = status;
        this.appliedDate = appliedDate;
    }

    // Getters
    public int getId() { return id; }
    public int getJobId() { return jobId; }
    public int getSeekerId() { return seekerId; }
    public String getCoverLetter() { return coverLetter; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getSocialMedia() { return socialMedia; }
    public String getCvFileName() { return cvFileName; }
    public String getStatus() { return status; }
    public String getAppliedDate() { return appliedDate; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setJobId(int jobId) { this.jobId = jobId; }
    public void setSeekerId(int seekerId) { this.seekerId = seekerId; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setSocialMedia(String socialMedia) { this.socialMedia = socialMedia; }
    public void setCvFileName(String cvFileName) { this.cvFileName = cvFileName; }
    public void setStatus(String status) { this.status = status; }
    public void setAppliedDate(String appliedDate) { this.appliedDate = appliedDate; }
}