package com.example.warehouse_medical.services;

import com.example.warehouse_medical.dtos.ApiResponse;
import com.example.warehouse_medical.dtos.CreateUserRequest;
import com.example.warehouse_medical.dtos.UpdateUserRequest;
import com.example.warehouse_medical.dtos.UserListData;
import com.example.warehouse_medical.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {
    @GET("users")
    Call<ApiResponse<UserListData>> getUsers(
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("keyword") String keyword
    );

    @POST("users")
    Call<ApiResponse<User>> createUser(@Body CreateUserRequest request);

    @PATCH("users/{id}")
    Call<ApiResponse<User>> updateUser(@Path("id") String id, @Body UpdateUserRequest request);
}
