package com.example.warehouse_medical.repositories;

import android.content.Context;

import com.example.warehouse_medical.dtos.ApiResponse;
import com.example.warehouse_medical.dtos.CreateUserRequest;
import com.example.warehouse_medical.dtos.UpdateUserRequest;
import com.example.warehouse_medical.dtos.UserListData;
import com.example.warehouse_medical.models.User;
import com.example.warehouse_medical.network.ApiClient;
import com.example.warehouse_medical.services.UserService;
import com.example.warehouse_medical.utils.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final UserService userService;

    public UserRepository(Context context) {
        userService = ApiClient.getClient(context).create(UserService.class);
    }

    /**
     * Cập nhật phương thức để hỗ trợ Phân trang và Tìm kiếm
     */
    public void getUsers(int page, int limit, String keyword, RepositoryCallback<UserListData> callback) {
        userService.getUsers(page, limit, keyword).enqueue(new Callback<ApiResponse<UserListData>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserListData>> call, Response<ApiResponse<UserListData>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    // Trả về toàn bộ UserListData (bao gồm list users và pagination)
                    callback.onSuccess(response.body().getData());
                    return;
                }
                callback.onError(NetworkUtils.getErrorMessage(response));
            }

            @Override
            public void onFailure(Call<ApiResponse<UserListData>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void createUser(CreateUserRequest request, RepositoryCallback<User> callback) {
        userService.createUser(request).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(NetworkUtils.getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void updateUser(String id, UpdateUserRequest request, RepositoryCallback<User> callback) {
        userService.updateUser(id, request).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(NetworkUtils.getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
