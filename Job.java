package com.example.jobportal.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "jobs")
public class Job {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String company;
    private String description;
    private String salary;
    private int employerId;  // Which user posted this job
    private String category;
    private String deadline;
    private String status;  // "active" or "closed"

    // Constructor
    public Job(String title, String company, String description, String salary,
               int employerId, String category, String deadline, String status) {
        this.title = title;
        this.company = company;
        this.description = description;
        this.salary = salary;
        this.employerId = employerId;
        this.category = category;
        this.deadline = deadline;
        this.status = status;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCompany() { return company; }
    public String getDescription() { return description; }
    public String getSalary() { return salary; }
    public int getEmployerId() { return employerId; }
    public String getCategory() { return category; }
    public String getDeadline() { return deadline; }

    public String getStatus() { return status; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setCompany(String company) { this.company = company; }
    public void setDescription(String description) { this.description = description; }
    public void setSalary(String salary) { this.salary = salary; }
    public void setEmployerId(int employerId) { this.employerId = employerId; }
    public void setCategory(String category) { this.category = category; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public void setStatus(String status) { this.status = status; }

}