package com.example.jobportal;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jobportal.database.AppDatabase;
import com.example.jobportal.model.Job;

public class PostJobActivity extends AppCompatActivity {

    private EditText etJobTitle, etCompany, etSalary, etDescription, etDeadline;
    private Button btnPostJob;
    private AppDatabase database;
    private int employerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);

        etJobTitle = findViewById(R.id.etJobTitle);
        etCompany = findViewById(R.id.etCompany);
        etSalary = findViewById(R.id.etSalary);
        etDescription = findViewById(R.id.etDescription);
        etDeadline = findViewById(R.id.etDeadline);  // NEW
        btnPostJob = findViewById(R.id.btnPostJob);

        database = AppDatabase.getInstance(this);
        employerId = getIntent().getIntExtra("user_id", -1);

        btnPostJob.setOnClickListener(v -> postJob());
    }

    private void postJob() {
        String title = etJobTitle.getText().toString().trim();
        String company = etCompany.getText().toString().trim();
        String salary = etSalary.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String deadline = etDeadline.getText().toString().trim();  // NEW

        if (title.isEmpty() || company.isEmpty() || salary.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Updated constructor with deadline parameter
        Job newJob = new Job(title, company, description, salary, employerId, "General", deadline, "active");

        new Thread(() -> {
            database.jobDao().insert(newJob);
            runOnUiThread(() -> {
                Toast.makeText(this, "Job posted successfully!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}