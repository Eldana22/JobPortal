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
import androidx.appcompat.widget.Toolbar;
import com.example.jobportal.database.AppDatabase;
import com.example.jobportal.model.User;

public class ProfileActivity extends AppCompatActivity {

    private EditText etName, etEmail, etAddress;
    private Button btnUploadCV, btnSave;
    private TextView tvCVFileName;
    private AppDatabase database;
    private int userId;
    private String selectedCvPath = "";
    private User currentUser;

    private final ActivityResultLauncher<Intent> cvPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        selectedCvPath = uri.toString();
                        tvCVFileName.setText(uri.getLastPathSegment());
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // ← BACK BUTTON
        getSupportActionBar().setTitle("Profile");              // ← TITLE

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        btnUploadCV = findViewById(R.id.btnUploadCV);
        btnSave = findViewById(R.id.btnSave);
        tvCVFileName = findViewById(R.id.tvCVFileName);

        database = AppDatabase.getInstance(this);
        userId = getIntent().getIntExtra("user_id", -1);

        loadUserProfile();

        btnUploadCV.setOnClickListener(v -> openFilePicker());
        btnSave.setOnClickListener(v -> saveProfile());
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadUserProfile() {
        new Thread(() -> {
            currentUser = database.userDao().getUserById(userId);
            runOnUiThread(() -> {
                if (currentUser != null) {
                    etName.setText(currentUser.getFullName());
                    etEmail.setText(currentUser.getEmail());
                    etAddress.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "");
                    if (currentUser.getCvPath() != null && !currentUser.getCvPath().isEmpty()) {
                        tvCVFileName.setText("CV uploaded");
                    }
                }
            });
        }).start();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf|application/msword|application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        cvPickerLauncher.launch(intent);
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            if (currentUser != null) {
                currentUser.setFullName(name);
                currentUser.setAddress(address);
                if (!selectedCvPath.isEmpty()) {
                    currentUser.setCvPath(selectedCvPath);
                }
                database.userDao().update(currentUser);
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}