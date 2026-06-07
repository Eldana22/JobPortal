package com.example.jobportal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jobportal.database.AppDatabase;
import com.example.jobportal.model.Job;


public class JobDetailActivity extends AppCompatActivity {

    private TextView tvJobTitle, tvCompany, tvSalary, tvDeadline, tvDescription;
    private Button btnApply, btnCloseJob;
    private AppDatabase database;
    private int jobId;
    private int userId;
    private String userType;
    private Job currentJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        tvJobTitle = findViewById(R.id.tvJobTitle);
        tvCompany = findViewById(R.id.tvCompany);
        tvSalary = findViewById(R.id.tvSalary);
        tvDeadline = findViewById(R.id.tvDeadline);
        tvDescription = findViewById(R.id.tvDescription);
        btnApply = findViewById(R.id.btnApply);
        btnCloseJob = findViewById(R.id.btnCloseJob);

        database = AppDatabase.getInstance(this);

        jobId = getIntent().getIntExtra("job_id", -1);
        userId = getIntent().getIntExtra("user_id", -1);
        userType = getIntent().getStringExtra("user_type");

        loadJobDetails();

        // Apply button - only for job seekers
        if ("seeker".equals(userType)) {
            btnApply.setVisibility(Button.VISIBLE);
            btnApply.setOnClickListener(v -> applyForJob());
        } else {
            btnApply.setVisibility(Button.GONE);
        }

        // Close Job button - only for employers who own this job
        // Will be shown after job loads
    }

    private void loadJobDetails() {
        new Thread(() -> {
            currentJob = database.jobDao().getJobById(jobId);
            runOnUiThread(() -> {
                if (currentJob != null) {
                    tvJobTitle.setText(currentJob.getTitle());
                    tvCompany.setText(currentJob.getCompany());
                    tvSalary.setText("💰 " + currentJob.getSalary());
                    tvDeadline.setText("📅 Deadline: " + (currentJob.getDeadline() != null ? currentJob.getDeadline() : "Not specified"));
                    tvDescription.setText(currentJob.getDescription());

                    // Show Close button only for employer who owns this job AND job is active
                    if ("employer".equals(userType) && currentJob.getEmployerId() == userId) {
                        btnCloseJob.setVisibility(Button.VISIBLE);

                        if ("closed".equals(currentJob.getStatus())) {
                            btnCloseJob.setText("✓ Job Closed");
                            btnCloseJob.setEnabled(false);
                            btnCloseJob.setAlpha(0.5f);
                        } else {
                            btnCloseJob.setText("Close this Job (Hiring Complete)");
                            btnCloseJob.setOnClickListener(v -> showCloseConfirmation());
                        }
                    } else {
                        btnCloseJob.setVisibility(Button.GONE);
                    }
                } else {
                    Toast.makeText(this, "Job not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }).start();
    }

    private void showCloseConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Close Job")
                .setMessage("Are you sure you want to close this job posting?\n\nThis will mark it as \"CLOSED\" and job seekers will no longer be able to apply.")
                .setPositiveButton("Yes, Close Job", (dialog, which) -> closeJob())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void closeJob() {
        new Thread(() -> {
            currentJob.setStatus("closed");
            database.jobDao().update(currentJob);
            runOnUiThread(() -> {
                Toast.makeText(this, "Job has been closed!", Toast.LENGTH_LONG).show();
                btnCloseJob.setText("✓ Job Closed");
                btnCloseJob.setEnabled(false);
                btnCloseJob.setAlpha(0.5f);

                // Update the display to show CLOSED badge
                tvJobTitle.setText(currentJob.getTitle() + " [CLOSED]");
            });
        }).start();
    }

    private void applyForJob() {
        // Check if job is already closed
        if (currentJob != null && "closed".equals(currentJob.getStatus())) {
            Toast.makeText(this, "This job is already closed. Applications are no longer accepted.", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            com.example.jobportal.model.Application existing = database.applicationDao().checkIfApplied(jobId, userId);
            runOnUiThread(() -> {
                if (existing != null) {
                    Toast.makeText(this, "You have already applied for this job!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(JobDetailActivity.this, ApplyJobActivity.class);
                    intent.putExtra("job_id", jobId);
                    intent.putExtra("user_id", userId);
                    startActivity(intent);
                }
            });
        }).start();
    }
}