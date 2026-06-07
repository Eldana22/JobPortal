package com.example.jobportal.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "saved_jobs")
public class SavedJob {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int userId;
    private int jobId;

    public SavedJob(int userId, int jobId) {
        this.userId = userId;
        this.jobId = jobId;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getJobId() { return jobId; }

    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setJobId(int jobId) { this.jobId = jobId; }
}