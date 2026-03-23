package com.example.warehouse_medical.ui.activities;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.warehouse_medical.R;
import com.example.warehouse_medical.dtos.RegisterRequest;
import com.example.warehouse_medical.models.User;
import com.example.warehouse_medical.repositories.AuthRepository;
import com.example.warehouse_medical.repositories.RepositoryCallback;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilFullName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputEditText etFullName;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private ProgressBar progressBar;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authRepository = new AuthRepository(this);
        tilFullName = findViewById(R.id.tilFullName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        progressBar = findViewById(R.id.progressBar);
        MaterialButton btnRegister = findViewById(R.id.btnRegister);
        TextView tvBackToLogin = findViewById(R.id.tvBackToLogin);

        btnRegister.setOnClickListener(v -> attemptRegister());
        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        tilFullName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);

        String fullName = valueOf(etFullName);
        String email = valueOf(etEmail);
        String password = valueOf(etPassword);

        if (fullName.isEmpty()) {
            tilFullName.setError(getString(R.string.full_name_required));
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.invalid_email));
            return;
        }
        if (password.isEmpty()) {
            tilPassword.setError(getString(R.string.password_required));
            return;
        }

        setLoading(true);
        authRepository.register(new RegisterRequest(fullName, email, password), new RepositoryCallback<User>() {
            @Override
            public void onSuccess(User data) {
                setLoading(false);
                Toast.makeText(RegisterActivity.this, "Register success. Please login.", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private String valueOf(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}
