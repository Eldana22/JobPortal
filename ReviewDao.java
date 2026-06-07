package com.example.jobportal.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.jobportal.model.Review;
import java.util.List;

@Dao
public interface ReviewDao {
    @Insert
    void insert(Review review);

    @Query("SELECT * FROM reviews WHERE jobId = :jobId")
    List<Review> getReviewsByJob(int jobId);
}