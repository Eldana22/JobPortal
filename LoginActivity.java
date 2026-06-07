package com.example.jobportal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jobportal.database.AppDatabase;
import com.example.jobportal.model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        database = AppDatabase.getInstance(this);

        // Create admin user if not exists
        new Thread(() -> {
            User existingAdmin = database.userDao().getUserByEmail("admin@jobportal.com");
            if (existingAdmin == null) {
                User admin = new User("Administrator", "admin@jobportal.com", "admin123", "admin");
                database.userDao().insert(admin);
            }
        }).start();

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            User user = database.userDao().login(email, password);
            runOnUiThread(() -> {
                if (user != null) {
                    Toast.makeText(this, "Welcome " + user.getFullName(), Toast.LENGTH_SHORT).show();

                    if (user.getUserType().equals("admin")) {
                        Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                        intent.putExtra("admin_id", user.getId());
                        intent.putExtra("admin_email", user.getEmail());
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("user_id", user.getId());
                        intent.putExtra("user_type", user.getUserType());
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}