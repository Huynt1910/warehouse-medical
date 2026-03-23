package com.example.warehouse_medical.repositories;

import android.content.Context;

import com.example.warehouse_medical.dtos.AddToCartRequest;
import com.example.warehouse_medical.dtos.ApiResponse;
import com.example.warehouse_medical.dtos.CartResponseData;
import com.example.warehouse_medical.dtos.CartUpdateRequest;
import com.example.warehouse_medical.network.ApiClient;
import com.example.warehouse_medical.services.CartService;
import com.example.warehouse_medical.utils.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepository {

    private final CartService cartService;

    public CartRepository(Context context) {
        cartService = ApiClient.getClient(context).create(CartService.class);
    }

    public void getCart(RepositoryCallback<CartResponseData> callback) {
        cartService.getCart().enqueue(new Callback<ApiResponse<CartResponseData>>() {
            @Override
            public void onResponse(Call<ApiResponse<CartResponseData>> call, Response<ApiResponse<CartResponseData>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    callback.onSuccess(response.body().getData());
                    return;
                }
                callback.onError(NetworkUtils.getErrorMessage(response));
            }

            @Override
            public void onFailure(Call<ApiResponse<CartResponseData>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void addToCart(AddToCartRequest request, RepositoryCallback<CartResponseData> callback) {
        cartService.addToCart(request).enqueue(new Callback<ApiResponse<CartResponseData>>() {
            @Override
            public void onResponse(Call<ApiResponse<CartResponseData>> call, Response<ApiResponse<CartResponseData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                    return;
                }
                callback.onError(NetworkUtils.getErrorMessage(response));
            }

            @Override
            public void onFailure(Call<ApiResponse<CartResponseData>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void updateCartItem(String itemId, int quantity, RepositoryCallback<CartResponseData> callback) {
        cartService.updateCartItem(itemId, new CartUpdateRequest(quantity))
                .enqueue(new Callback<ApiResponse<CartResponseData>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<CartResponseData>> call, Response<ApiResponse<CartResponseData>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body().getData());
                            return;
                        }
                        callback.onError(NetworkUtils.getErrorMessage(response));
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<CartResponseData>> call, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    public void removeCartItem(String itemId, RepositoryCallback<Object> callback) {
        cartService.removeCartItem(itemId).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body() != null ? response.body().getData() : null);
                    return;
                }
                callback.onError(NetworkUtils.getErrorMessage(response));
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void clearCart(RepositoryCallback<Object> callback) {
        cartService.clearCart().enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body() != null ? response.body().getData() : null);
                    return;
                }
                callback.onError(NetworkUtils.getErrorMessage(response));
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
