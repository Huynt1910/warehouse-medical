package com.example.warehouse_medical.services;

import com.example.warehouse_medical.dtos.ApiResponse;
import com.example.warehouse_medical.dtos.CheckoutRequest;
import com.example.warehouse_medical.dtos.OrderListData;
import com.example.warehouse_medical.models.Order;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OrderService {
    @GET("orders")
    Call<ApiResponse<OrderListData>> getOrders();

    @GET("orders/{id}")
    Call<ApiResponse<Order>> getOrderDetail(@Path("id") String id);

    @PATCH("orders/{id}/confirm")
    Call<ApiResponse<Order>> confirmOrder(@Path("id") String id);

    @PATCH("orders/{id}/cancel")
    Call<ApiResponse<Order>> cancelOrder(@Path("id") String id);

    @POST("orders/checkout")
    Call<ApiResponse<Order>> checkout(@Body CheckoutRequest request);
}
