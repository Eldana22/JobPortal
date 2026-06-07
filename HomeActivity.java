package com.example.jobportal;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jobportal.adapters.JobAdapter;
import com.example.jobportal.database.AppDatabase;
import com.example.jobportal.model.Job;
import com.example.jobportal.model.SavedJob;
import com.example.jobportal.model.User;
import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.widget.Toolbar;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvJobs;
    private JobAdapter jobAdapter;
    private AppDatabase database;
    private List<Job> allJobs = new ArrayList<>();
    private int userId;
    private String userType;
    private EditText etSearchFilter;
    private ImageButton btnSaved;
    private boolean showingSavedOnly = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userId = getIntent().getIntExtra("user_id", -1);
        userType = getIntent().getStringExtra("user_type");

        Toast.makeText(this, "Welcome! Logged in as " + userType, Toast.LENGTH_SHORT).show();

        rvJobs = findViewById(R.id.rvJobs);
        etSearchFilter = findViewById(R.id.etSearchFilter);
        btnSaved = findViewById(R.id.btnSaved);

        rvJobs.setLayoutManager(new LinearLayoutManager(this));

        // Create adapter with delete listener
        jobAdapter = new JobAdapter(
                jobId -> {
                    Intent intent = new Intent(HomeActivity.this, JobDetailActivity.class);
                    intent.putExtra("job_id", jobId);
                    intent.putExtra("user_id", userId);
                    intent.putExtra("user_type", userType);
                    startActivity(intent);
                },
                (jobId, isSaved) -> {
                    if (isSaved) {
                        saveJob(jobId);
                    } else {
                        unsaveJob(jobId);
                    }
                },
                jobId -> {
                    deleteJob(jobId);
                },
                userType,
                userId
        );
        rvJobs.setAdapter(jobAdapter);

        database = AppDatabase.getInstance(this);
        loadJobs();

        if ("seeker".equals(userType)) {
            loadSavedJobs();
        }

        // Search filter
        etSearchFilter.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterJobs(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Saved Jobs button - only for seekers
        if ("seeker".equals(userType)) {
            btnSaved.setVisibility(View.VISIBLE);
            btnSaved.setOnClickListener(v -> toggleSavedJobs());
        } else {
            btnSaved.setVisibility(View.GONE);
        }

        // FAB button - only for employers
        findViewById(R.id.fabAddJob).setOnClickListener(v -> {
            if ("employer".equals(userType)) {
                Intent intent = new Intent(HomeActivity.this, PostJobActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Only employers can post jobs", Toast.LENGTH_SHORT).show();
            }
        });

        if (!"employer".equals(userType)) {
            findViewById(R.id.fabAddJob).setVisibility(View.GONE);
        }
    }

    // ==================== MENU METHODS ====================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_profile) {
            openProfile();
            return true;
        } else if (id == R.id.menu_change_password) {
            showChangePasswordDialog();
            return true;
        } else if (id == R.id.menu_help) {
            showHelpDialog();
            return true;
        } else if (id == R.id.menu_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ==================== PROFILE METHODS ====================

    private void openProfile() {
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        intent.putExtra("user_id", userId);
        startActivity(intent);
    }

    // ==================== CHANGE PASSWORD METHODS ====================

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        builder.setView(view);

        EditText etCurrentPassword = view.findViewById(R.id.etCurrentPassword);
        EditText etNewPassword = view.findViewById(R.id.etNewPassword);
        EditText etConfirmPassword = view.findViewById(R.id.etConfirmPassword);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String current = etCurrentPassword.getText().toString().trim();
            String newPass = etNewPassword.getText().toString().trim();
            String confirm = etConfirmPassword.getText().toString().trim();

            if (current.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirm)) {
                Toast.makeText(this, "New passwords don't match", Toast.LENGTH_SHORT).show();
                return;
            }

            changeUserPassword(current, newPass);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void changeUserPassword(String currentPassword, String newPassword) {
        new Thread(() -> {
            User user = database.userDao().getUserById(userId);
            if (user == null || !user.getPassword().equals(currentPassword)) {
                runOnUiThread(() -> Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show());
                return;
            }

            user.setPassword(newPassword);
            database.userDao().update(user);

            runOnUiThread(() -> Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show());
        }).start();
    }

    // ==================== HELP METHODS ====================

    private void showHelpDialog() {
        String adminEmail = "admin@jobportal.com";

        new AlertDialog.Builder(this)
                .setTitle("Help")
                .setMessage("Contact admin: " + adminEmail + "\n\nTap 'Copy Email' to copy admin email to clipboard.")
                .setPositiveButton("Copy Email", (dialog, which) -> {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Admin Email", adminEmail);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this, "Admin email copied to clipboard!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ==================== LOGOUT METHODS ====================

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    // ==================== JOB METHODS ====================

    private void saveJob(int jobId) {
        new Thread(() -> {
            SavedJob savedJob = new SavedJob(userId, jobId);
            database.savedJobDao().insert(savedJob);
            runOnUiThread(() -> Toast.makeText(this, "Job saved!", Toast.LENGTH_SHORT).show());
        }).start();
    }

    private void unsaveJob(int jobId) {
        new Thread(() -> {
            database.savedJobDao().deleteSavedJob(jobId, userId);
            runOnUiThread(() -> Toast.makeText(this, "Job removed from saved", Toast.LENGTH_SHORT).show());
        }).start();
    }

    private void deleteJob(int jobId) {
        new Thread(() -> {
            database.jobDao().deleteJobById(jobId);
            runOnUiThread(() -> {
                Toast.makeText(this, "Job deleted", Toast.LENGTH_SHORT).show();
                loadJobs();
                if ("seeker".equals(userType)) {
                    loadSavedJobs();
                }
            });
        }).start();
    }

    private void loadSavedJobs() {
        new Thread(() -> {
            List<Integer> savedIds = database.savedJobDao().getSavedJobIds(userId);
            runOnUiThread(() -> jobAdapter.setSavedJobIds(savedIds));
        }).start();
    }

    private void loadJobs() {
        new Thread(() -> {
            if ("employer".equals(userType)) {
                allJobs = database.jobDao().getJobsByEmployer(userId);
            } else {
                allJobs = database.jobDao().getAllJobs();
            }
            runOnUiThread(() -> {
                jobAdapter.setJobs(allJobs);
                if (allJobs.isEmpty()) {
                    Toast.makeText(this, "No jobs to display.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void filterJobs(String query) {
        if (query.isEmpty()) {
            jobAdapter.setJobs(allJobs);
        } else {
            List<Job> filtered = new ArrayList<>();
            for (Job job : allJobs) {
                if (job.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        job.getCompany().toLowerCase().contains(query.toLowerCase())) {
                    filtered.add(job);
                }
            }
            jobAdapter.setJobs(filtered);
        }
    }

    private void toggleSavedJobs() {
        showingSavedOnly = !showingSavedOnly;
        if (showingSavedOnly) {
            new Thread(() -> {
                List<Integer> savedIds = database.savedJobDao().getSavedJobIds(userId);
                List<Job> savedJobs = new ArrayList<>();
                for (Job job : allJobs) {
                    if (savedIds.contains(job.getId())) {
                        savedJobs.add(job);
                    }
                }
                runOnUiThread(() -> {
                    jobAdapter.setSavedJobsView(true);
                    jobAdapter.setJobs(savedJobs);
                    btnSaved.setAlpha(0.7f);
                    if (savedJobs.isEmpty()) {
                        Toast.makeText(this, "No saved jobs", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        } else {
            jobAdapter.setSavedJobsView(false);
            jobAdapter.setJobs(allJobs);
            btnSaved.setAlpha(1.0f);
            loadSavedJobs();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadJobs();
        if ("seeker".equals(userType)) {
            loadSavedJobs();
        }
    }
}