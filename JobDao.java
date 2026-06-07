package com.example.jobportal.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.jobportal.model.Job;
import java.util.List;

@Dao
public interface JobDao {

    @Insert
    void insert(Job job);

    @Update
    void update(Job job);

    @Delete
    void delete(Job job);

    @Query("SELECT * FROM jobs ORDER BY id DESC")
    List<Job> getAllJobs();

    @Query("SELECT * FROM jobs WHERE employerId = :employerId")
    List<Job> getJobsByEmployer(int employerId);

    @Query("SELECT * FROM jobs WHERE id = :jobId")
    Job getJobById(int jobId);

    @Query("DELETE FROM jobs WHERE id = :jobId")
    void deleteJobById(int jobId);

    @Query("SELECT COUNT(*) FROM jobs")
    int getJobCount();

    @Query("SELECT COUNT(*) FROM jobs WHERE status = 'active'")
    int getActiveJobCount();
}