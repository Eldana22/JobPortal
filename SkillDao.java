package com.example.jobportal.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.jobportal.model.Skill;
import java.util.List;

@Dao
public interface SkillDao {
    @Insert
    void insert(Skill skill);

    @Query("SELECT * FROM skills")
    List<Skill> getAllSkills();
}