package com.example.jobportal.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.jobportal.model.User;
import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    User login(String email, String password);

    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE id = :userId")
    User getUserById(int userId);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Query("DELETE FROM users WHERE id = :userId")
    void deleteUser(int userId);

    @Query("SELECT * FROM users WHERE userType != 'admin'")
    List<User> getAllNonAdminUsers();

    @Query("DELETE FROM users WHERE id = :userId AND userType != 'admin'")
    void deleteUserById(int userId);

    @Query("SELECT COUNT(*) FROM users")
    int getUserCount();

    @Query("SELECT COUNT(*) FROM users WHERE userType = 'seeker'")
    int getSeekerCount();

    @Query("SELECT COUNT(*) FROM users WHERE userType = 'employer'")
    int getEmployerCount();

    @Query("SELECT * FROM users WHERE userType = 'admin'")
    List<User> getAllAdminUsers();
}