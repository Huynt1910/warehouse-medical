package com.example.warehouse_medical.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.warehouse_medical.R;
import com.example.warehouse_medical.models.User;
import com.example.warehouse_medical.repositories.AuthRepository;
import com.example.warehouse_medical.repositories.RepositoryCallback;
import com.example.warehouse_medical.storage.SessionManager;
import com.example.warehouse_medical.ui.activities.LoginActivity;
import com.google.android.material.button.MaterialButton;

public class BaseProfileFragment extends Fragment {

    private AuthRepository authRepository;
    private SessionManager sessionManager;
    private TextView tvFullName;
    private TextView tvEmail;
    private TextView tvRole;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvRole = view.findViewById(R.id.tvRole);
        progressBar = view.findViewById(R.id.progressBar);
        MaterialButton btnLogout = view.findViewById(R.id.btnLogout);

        sessionManager = new SessionManager(requireContext());
        authRepository = new AuthRepository(requireContext());

        btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        loadProfile();
        return view;
    }

    private void loadProfile() {
        progressBar.setVisibility(View.VISIBLE);
        authRepository.getCurrentUser(new RepositoryCallback<User>() {
            @Override
            public void onSuccess(User data) {
                progressBar.setVisibility(View.GONE);
                sessionManager.saveUser(data);
                tvFullName.setText(data.getFullName());
                tvEmail.setText(data.getEmail());
                tvRole.setText(getString(R.string.role_label, data.getRole()));
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                tvFullName.setText(sessionManager.getFullName());
                tvEmail.setText(sessionManager.getEmail());
                tvRole.setText(getString(R.string.role_label, sessionManager.getRole()));
            }
        });
    }
}
