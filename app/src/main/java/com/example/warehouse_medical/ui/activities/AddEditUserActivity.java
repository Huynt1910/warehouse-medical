package com.example.warehouse_medical.ui.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.warehouse_medical.R;
import com.example.warehouse_medical.databinding.ActivityAddEditUserBinding;
import com.example.warehouse_medical.dtos.CreateUserRequest;
import com.example.warehouse_medical.dtos.UpdateUserRequest;
import com.example.warehouse_medical.models.User;
import com.example.warehouse_medical.repositories.UserRepository;
import com.example.warehouse_medical.repositories.RepositoryCallback;
import com.google.android.material.appbar.MaterialToolbar;

public class AddEditUserActivity extends AppCompatActivity {

    private ActivityAddEditUserBinding binding;
    private UserRepository userRepository;
    private User currentUser;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userRepository = new UserRepository(this);
        setupToolbar();
        setupRoleSpinner();

        if (getIntent().hasExtra("user")) {
            currentUser = (User) getIntent().getSerializableExtra("user");
            isEditMode = true;
            fillData();
        }
        binding.btnSave.setOnClickListener(v -> saveUser());
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(isEditMode ? "Edit User" : "Add New User");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRoleSpinner() {
        String[] roles = { "STAFF", "CUSTOMER"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roles);
        binding.actvRole.setAdapter(adapter);
    }

    private void fillData() {
        if (currentUser != null) {
            binding.etFullName.setText(currentUser.getFullName());
            binding.etEmail.setText(currentUser.getEmail());
            binding.etEmail.setEnabled(false); // Email usually cannot be changed
            binding.tilPassword.setVisibility(View.GONE); // Hide password field in edit mode
            binding.actvRole.setText(currentUser.getRole(), false);
            binding.btnSave.setText("Update User");
        }
    }


    private void saveUser() {
        String fullName = binding.etFullName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String role = binding.actvRole.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Full Name and Email are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditMode) {
            UpdateUserRequest request = new UpdateUserRequest();
            request.setFullName(fullName);
            request.setRole(role);

            userRepository.updateUser(currentUser.getId(), request, new RepositoryCallback<User>() {
                @Override
                public void onSuccess(User data) {
                    Toast.makeText(AddEditUserActivity.this, "User updated successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(AddEditUserActivity.this, "Update failed: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Password is required for new users", Toast.LENGTH_SHORT).show();
                return;
            }

            CreateUserRequest request = new CreateUserRequest();
            request.setFullName(fullName);
            request.setEmail(email);
            request.setPassword(password);
            request.setRole(role);

            userRepository.createUser(request, new RepositoryCallback<User>() {
                @Override
                public void onSuccess(User data) {
                    Toast.makeText(AddEditUserActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(AddEditUserActivity.this, "Creation failed: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
