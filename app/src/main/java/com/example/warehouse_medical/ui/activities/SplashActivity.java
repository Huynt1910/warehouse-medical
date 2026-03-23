package com.example.warehouse_medical.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.warehouse_medical.R;
import com.example.warehouse_medical.models.User;
import com.example.warehouse_medical.repositories.AuthRepository;
import com.example.warehouse_medical.repositories.RepositoryCallback;
import com.example.warehouse_medical.storage.SessionManager;
import com.example.warehouse_medical.utils.AppConstants;

public class SplashActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sessionManager = new SessionManager(this);
        authRepository = new AuthRepository(this);

        if (!sessionManager.hasToken()) {
            openLogin();
            return;
        }

        authRepository.getCurrentUser(new RepositoryCallback<User>() {
            @Override
            public void onSuccess(User data) {
                sessionManager.saveUser(data);
                openMain(data.getRole());
            }

            @Override
            public void onError(String message) {
                sessionManager.clearSession();
                Toast.makeText(SplashActivity.this, getString(R.string.session_expired), Toast.LENGTH_SHORT).show();
                openLogin();
            }
        });
    }

    private void openLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void openMain(String role) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(AppConstants.EXTRA_ROLE, role);
        startActivity(intent);
        finish();
    }
}
