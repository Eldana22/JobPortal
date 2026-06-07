package com.example.jobportal.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "skills")
public class Skill {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;

    public Skill(String name) {
        this.name = name;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
}