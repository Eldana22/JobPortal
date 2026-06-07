package com.example.jobportal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jobportal.database.AppDatabase;
import com.example.jobportal.model.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private RadioGroup rgUserType;
    private AppDatabase database;
    private String selectedUserType = "seeker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        rgUserType = findViewById(R.id.rgUserType);

        database = AppDatabase.getInstance(this);

        rgUserType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbSeeker) {
                selectedUserType = "seeker";
            } else if (checkedId == R.id.rbEmployer) {
                selectedUserType = "employer";
            }
        });

        btnRegister.setOnClickListener(v -> registerUser());

        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            User existingUser = database.userDao().getUserByEmail(email);

            if (existingUser != null) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show());
                return;
            }

            User newUser = new User(fullName, email, password, selectedUserType);
            database.userDao().insert(newUser);

            runOnUiThread(() -> {
                Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            });
        }).start();
    }
}