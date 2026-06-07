package com.example.jobportal;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.example.jobportal.database.AppDatabase;
import com.example.jobportal.model.User;

public class AdminDashboardActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TextView tvTotalUsers, tvTotalJobs, tvTotalApplications;
    private AppDatabase database;
    private int adminId;
    private String adminEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize views
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalJobs = findViewById(R.id.tvTotalJobs);
        tvTotalApplications = findViewById(R.id.tvTotalApplications);
        ImageButton btnChangePassword = findViewById(R.id.btnChangePassword);
        ImageButton btnLogout = findViewById(R.id.btnLogout);

        // Initialize database
        database = AppDatabase.getInstance(this);

        // Get logged-in admin info
        adminId = getIntent().getIntExtra("admin_id", -1);
        adminEmail = getIntent().getStringExtra("admin_email");

        // Setup ViewPager with adapter
        AdminViewPagerAdapter adapter = new AdminViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Setup TabLayout with ViewPager
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Users");
                            break;
                        case 1:
                            tab.setText("Jobs");
                            break;
                        case 2:
                            tab.setText("Applications");
                            break;
                    }
                }
        ).attach();

        // Load statistics
        loadStats();

        // Logout button
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // Change Password button
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
    }

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

            changePassword(current, newPass);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void changePassword(String currentPassword, String newPassword) {
        new Thread(() -> {
            // Verify current password
            User admin = database.userDao().login(adminEmail, currentPassword);
            if (admin == null) {
                runOnUiThread(() -> Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show());
                return;
            }

            // Update password
            admin.setPassword(newPassword);
            database.userDao().update(admin);

            runOnUiThread(() -> Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show());
        }).start();
    }

    private void loadStats() {
        new Thread(() -> {
            int userCount = database.userDao().getUserCount();
            int jobCount = database.jobDao().getJobCount();
            int appCount = database.applicationDao().getApplicationCount();

            runOnUiThread(() -> {
                tvTotalUsers.setText(String.valueOf(userCount));
                tvTotalJobs.setText(String.valueOf(jobCount));
                tvTotalApplications.setText(String.valueOf(appCount));
            });
        }).start();
    }
}