package com.example.warehouse_medical.services;

import com.example.warehouse_medical.dtos.AddToCartRequest;
import com.example.warehouse_medical.dtos.ApiResponse;
import com.example.warehouse_medical.dtos.CartResponseData;
import com.example.warehouse_medical.dtos.CartUpdateRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CartService {
    @GET("cart")
    Call<ApiResponse<CartResponseData>> getCart();

    @POST("cart")
    Call<ApiResponse<CartResponseData>> addToCart(@Body AddToCartRequest request);

    @PATCH("cart/{itemId}")
    Call<ApiResponse<CartResponseData>> updateCartItem(@Path("itemId") String itemId, @Body CartUpdateRequest request);

    @DELETE("cart/{itemId}")
    Call<ApiResponse<Object>> removeCartItem(@Path("itemId") String itemId);

    @DELETE("cart")
    Call<ApiResponse<Object>> clearCart();
}
