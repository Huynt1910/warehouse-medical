package com.example.warehouse_medical.repositories;

import android.content.Context;

import com.example.warehouse_medical.dtos.ApiResponse;
import com.example.warehouse_medical.dtos.CheckoutRequest;
import com.example.warehouse_medical.dtos.OrderListData;
import com.example.warehouse_medical.models.Order;
import com.example.warehouse_medical.network.ApiClient;
import com.example.warehouse_medical.services.OrderService;
import com.example.warehouse_medical.utils.NetworkUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {

    private final OrderService orderService;

    public OrderRepository(Context context) {
        orderService = ApiClient.getClient(context).create(OrderService.class);
    }

    public void getOrders(RepositoryCallback<List<Order>> callback) {
        orderService.getOrders().enqueue(new Callback<ApiResponse<OrderListData>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderListData>> call, Response<ApiResponse<OrderListData>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getData() != null
                        && response.body().getData().getOrders() != null) {
                    callback.onSuccess(response.body().getData().getOrders());
                    return;
                }
                callback.onError(NetworkUtils.getErrorMessage(response));
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderListData>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getOrderDetail(String orderId, RepositoryCallback<Order> callback) {
        orderService.getOrderDetail(orderId).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    callback.onSuccess(response.body().getData());
                    return;
                }
                callback.onError(NetworkUtils.getErrorMessage(response));
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void checkout(String note, RepositoryCallback<Order> callback) {
        orderService.checkout(new CheckoutRequest(note)).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    callback.onSuccess(response.body().getData());
                    return;
                }
                callback.onError(NetworkUtils.getErrorMessage(response));
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void confirmOrder(String orderId, RepositoryCallback<Order> callback) {
        orderService.confirmOrder(orderId).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(NetworkUtils.getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void cancelOrder(String orderId, RepositoryCallback<Order> callback) {
        orderService.cancelOrder(orderId).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    callback.onSuccess(response.body().getData());
                    return;
                }
                callback.onError(NetworkUtils.getErrorMessage(response));
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
