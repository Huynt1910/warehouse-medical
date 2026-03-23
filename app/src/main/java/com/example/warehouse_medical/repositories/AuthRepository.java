package com.example.warehouse_medical.repositories;

import android.content.Context;

import com.example.warehouse_medical.dtos.ApiResponse;
import com.example.warehouse_medical.dtos.LoginRequest;
import com.example.warehouse_medical.dtos.LoginResponse;
import com.example.warehouse_medical.dtos.RegisterRequest;
import com.example.warehouse_medical.models.User;
import com.example.warehouse_medical.network.ApiClient;
import com.example.warehouse_medical.services.AuthService;
import com.example.warehouse_medical.utils.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final AuthService authService;

    public AuthRepository(Context context) {
        authService = ApiClient.getClient(context).create(AuthService.class);
    }

    public void login(LoginRequest request, RepositoryCallback<LoginResponse> callback) {
        authService.login(request).enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    callback.onSuccess(response.body().getData());
                    return;
                }
                callback.onError(NetworkUtils.getErrorMessage(response));
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void register(RegisterRequest request, RepositoryCallback<User> callback) {
        authService.register(request).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    callback.onSuccess(response.body().getData());
                    return;
                }
                callback.onError(NetworkUtils.getErrorMessage(response));
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getCurrentUser(RepositoryCallback<User> callback) {
        authService.getCurrentUser().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    callback.onSuccess(response.body().getData());
                    return;
                }
                callback.onError(NetworkUtils.getErrorMessage(response));
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
