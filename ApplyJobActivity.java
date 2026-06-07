package com.example.jobportal;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jobportal.database.AppDatabase;
import com.example.jobportal.model.Application;
import com.example.jobportal.model.User;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ApplyJobActivity extends AppCompatActivity {

    private EditText etCoverLetter, etPhone, etEmail, etSocialMedia;
    private Button btnUploadCV, btnSubmit;
    private TextView tvFileName;
    private AppDatabase database;
    private int jobId;
    private int seekerId;
    private String selectedCvPath = "";
    private String seekerEmail;

    private final ActivityResultLauncher<Intent> cvPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        selectedCvPath = uri.toString();
                        tvFileName.setText(uri.getLastPathSegment());
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_job);

        etCoverLetter = findViewById(R.id.etCoverLetter);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etSocialMedia = findViewById(R.id.etSocialMedia);
        btnUploadCV = findViewById(R.id.btnUploadCV);
        btnSubmit = findViewById(R.id.btnSubmitApplication);
        tvFileName = findViewById(R.id.tvFileName);

        database = AppDatabase.getInstance(this);

        jobId = getIntent().getIntExtra("job_id", -1);
        seekerId = getIntent().getIntExtra("user_id", -1);

        // Load user email
        loadUserEmail();

        btnUploadCV.setOnClickListener(v -> openFilePicker());

        btnSubmit.setOnClickListener(v -> submitApplication());
    }

    private void loadUserEmail() {
        new Thread(() -> {
            User user = database.userDao().getUserById(seekerId);
            runOnUiThread(() -> {
                if (user != null) {
                    seekerEmail = user.getEmail();
                    etEmail.setText(seekerEmail);
                    etEmail.setEnabled(false); // Make read-only
                }
            });
        }).start();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf|application/msword|application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        cvPickerLauncher.launch(intent);
    }

    private void submitApplication() {
        String coverLetter = etCoverLetter.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String socialMedia = etSocialMedia.getText().toString().trim();

        if (coverLetter.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields (*)", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        Application application = new Application(
                jobId, seekerId, coverLetter, phone, email,
                socialMedia, selectedCvPath, "pending", currentDate
        );

        new Thread(() -> {
            // Check if already applied
            Application existing = database.applicationDao().checkIfApplied(jobId, seekerId);
            if (existing != null) {
                runOnUiThread(() -> Toast.makeText(this, "You have already applied for this job!", Toast.LENGTH_SHORT).show());
                return;
            }

            database.applicationDao().insert(application);
            runOnUiThread(() -> {
                Toast.makeText(this, "Application submitted successfully!", Toast.LENGTH_LONG).show();
                finish();
            });
        }).start();
    }
}