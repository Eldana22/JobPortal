package com.example.jobportal.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.jobportal.model.User;
import com.example.jobportal.model.Job;
import com.example.jobportal.model.Application;
import com.example.jobportal.model.Category;
import com.example.jobportal.model.SavedJob;
import com.example.jobportal.model.Review;
import com.example.jobportal.model.Skill;
import com.example.jobportal.dao.UserDao;
import com.example.jobportal.dao.JobDao;
import com.example.jobportal.dao.ApplicationDao;
import com.example.jobportal.dao.CategoryDao;
import com.example.jobportal.dao.SavedJobDao;
import com.example.jobportal.dao.ReviewDao;
import com.example.jobportal.dao.SkillDao;

@Database(entities = {User.class, Job.class, Application.class, Category.class, SavedJob.class, Review.class, Skill.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract JobDao jobDao();
    public abstract ApplicationDao applicationDao();
    public abstract CategoryDao categoryDao();
    public abstract SavedJobDao savedJobDao();
    public abstract ReviewDao reviewDao();
    public abstract SkillDao skillDao();

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "jobportal_database"
            ).build();
        }
        return instance;
    }
}