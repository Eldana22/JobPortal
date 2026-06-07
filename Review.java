package com.example.jobportal.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reviews")
public class Review {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int jobId;
    private int userId;
    private float rating;
    private String comment;

    public Review(int jobId, int userId, float rating, String comment) {
        this.jobId = jobId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters
    public int getId() { return id; }
    public int getJobId() { return jobId; }
    public int getUserId() { return userId; }
    public float getRating() { return rating; }
    public String getComment() { return comment; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setJobId(int jobId) { this.jobId = jobId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setRating(float rating) { this.rating = rating; }
    public void setComment(String comment) { this.comment = comment; }
}