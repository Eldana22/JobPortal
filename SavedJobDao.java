package com.example.jobportal.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.jobportal.model.SavedJob;
import java.util.List;

@Dao
public interface SavedJobDao {

    @Insert
    void insert(SavedJob savedJob);

    @Query("SELECT * FROM saved_jobs WHERE userId = :userId")
    List<SavedJob> getSavedJobsByUser(int userId);

    @Query("SELECT jobId FROM saved_jobs WHERE userId = :userId")
    List<Integer> getSavedJobIds(int userId);

    @Query("DELETE FROM saved_jobs WHERE jobId = :jobId AND userId = :userId")
    void deleteSavedJob(int jobId, int userId);

    @Query("SELECT EXISTS(SELECT 1 FROM saved_jobs WHERE jobId = :jobId AND userId = :userId)")
    boolean isJobSaved(int jobId, int userId);
}