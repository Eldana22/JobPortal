package com.example.jobportal.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.jobportal.model.Application;
import java.util.List;

@Dao
public interface ApplicationDao {

    @Insert
    void insert(Application application);

    @Query("SELECT * FROM applications WHERE seekerId = :seekerId")
    List<Application> getApplicationsBySeeker(int seekerId);

    @Query("SELECT * FROM applications WHERE jobId = :jobId")
    List<Application> getApplicationsByJob(int jobId);

    @Query("UPDATE applications SET status = :status WHERE id = :applicationId")
    void updateStatus(int applicationId, String status);

    @Query("SELECT * FROM applications WHERE jobId IN (SELECT id FROM jobs WHERE employerId = :employerId)")
    List<Application> getApplicationsForEmployerJobs(int employerId);

    @Query("SELECT * FROM applications WHERE jobId = :jobId AND seekerId = :seekerId")
    Application checkIfApplied(int jobId, int seekerId);

    @Query("SELECT * FROM applications")
    List<Application> getAllApplications();

    @Query("SELECT COUNT(*) FROM applications")
    int getApplicationCount();
}