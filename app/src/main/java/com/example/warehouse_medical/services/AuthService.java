package com.example.warehouse_medical.services;

import com.example.warehouse_medical.dtos.ApiResponse;
import com.example.warehouse_medical.dtos.LoginRequest;
import com.example.warehouse_medical.dtos.LoginResponse;
import com.example.warehouse_medical.dtos.RegisterRequest;
import com.example.warehouse_medical.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthService {
    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<ApiResponse<User>> register(@Body RegisterRequest request);

    @GET("auth/me")
    Call<ApiResponse<User>> getCurrentUser();
}
