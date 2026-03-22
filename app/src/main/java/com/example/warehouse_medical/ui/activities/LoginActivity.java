package com.example.warehouse_medical.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.warehouse_medical.R;
import com.example.warehouse_medical.dtos.LoginRequest;
import com.example.warehouse_medical.dtos.LoginResponse;
import com.example.warehouse_medical.models.User;
import com.example.warehouse_medical.repositories.AuthRepository;
import com.example.warehouse_medical.repositories.RepositoryCallback;
import com.example.warehouse_medical.storage.SessionManager;
import com.example.warehouse_medical.utils.AppConstants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private ProgressBar progressBar;
    private AuthRepository authRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authRepository = new AuthRepository(this);
        sessionManager = new SessionManager(this);

        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        progressBar = findViewById(R.id.progressBar);
        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        TextView tvCreateAccount = findViewById(R.id.tvCreateAccount);

        btnLogin.setOnClickListener(v -> attemptLogin());
        tvCreateAccount.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        tilEmail.setError(null);
        tilPassword.setError(null);

        String email = valueOf(etEmail);
        String password = valueOf(etPassword);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.invalid_email));
            return;
        }
        if (password.isEmpty()) {
            tilPassword.setError(getString(R.string.password_required));
            return;
        }

        setLoading(true);
        authRepository.login(new LoginRequest(email, password), new RepositoryCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse data) {
                if (data == null || data.getToken() == null) {
                    setLoading(false);
                    Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                    return;
                }
                sessionManager.saveToken(data.getToken());
                User loginUser = data.getUser();
                if (loginUser != null && loginUser.getRole() != null) {
                    sessionManager.saveUser(loginUser);
                    setLoading(false);
                    openMain(loginUser.getRole());
                } else {
                    fetchCurrentUserAfterLogin();
                }
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCurrentUserAfterLogin() {
        authRepository.getCurrentUser(new RepositoryCallback<User>() {
            @Override
            public void onSuccess(User data) {
                setLoading(false);
                sessionManager.saveUser(data);
                openMain(data.getRole());
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openMain(String role) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(AppConstants.EXTRA_ROLE, role);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private String valueOf(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}
