package com.example.warehouse_medical.repositories;

import android.content.Context;

import com.example.warehouse_medical.dtos.ApiResponse;
import com.example.warehouse_medical.dtos.CreateItemRequest;
import com.example.warehouse_medical.dtos.ItemListData;
import com.example.warehouse_medical.dtos.UpdateItemRequest;
import com.example.warehouse_medical.models.Item;
import com.example.warehouse_medical.network.ApiClient;
import com.example.warehouse_medical.services.ItemService;
import com.example.warehouse_medical.utils.NetworkUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemRepository {

    private final ItemService itemService;

    public ItemRepository(Context context) {
        itemService = ApiClient.getClient(context).create(ItemService.class);
    }

    public void getItems(int page, int limit, String keyword, RepositoryCallback<ItemListData> callback) {
        itemService.getItems(page, limit, keyword).enqueue(new Callback<ApiResponse<ItemListData>>() {
            @Override
            public void onResponse(Call<ApiResponse<ItemListData>> call, Response<ApiResponse<ItemListData>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    callback.onSuccess(response.body().getData());
                    return;
                }
                callback.onError(NetworkUtils.getErrorMessage(response));
            }

            @Override
            public void onFailure(Call<ApiResponse<ItemListData>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getAvailableItems(int page, int limit, String keyword, RepositoryCallback<ItemListData> callback) {
        itemService.getAvailableItems(page, limit, keyword).enqueue(new Callback<ApiResponse<ItemListData>>() {
            @Override
            public void onResponse(Call<ApiResponse<ItemListData>> call, Response<ApiResponse<ItemListData>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    callback.onSuccess(response.body().getData());
                    return;
                }
                callback.onError(NetworkUtils.getErrorMessage(response));
            }

            @Override
            public void onFailure(Call<ApiResponse<ItemListData>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getLowStockItems(RepositoryCallback<List<Item>> callback) {
        itemService.getLowStockItems().enqueue(new Callback<ApiResponse<List<Item>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Item>>> call, Response<ApiResponse<List<Item>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    callback.onSuccess(response.body().getData());
                    return;
                }
                callback.onError(NetworkUtils.getErrorMessage(response));
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Item>>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getItemDetail(String id, RepositoryCallback<Item> callback) {
        itemService.getItemDetail(id).enqueue(new Callback<ApiResponse<Item>>() {
            @Override
            public void onResponse(Call<ApiResponse<Item>> call, Response<ApiResponse<Item>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    callback.onSuccess(response.body().getData());
                    return;
                }
                callback.onError(NetworkUtils.getErrorMessage(response));
            }

            @Override
            public void onFailure(Call<ApiResponse<Item>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void createItem(CreateItemRequest request, RepositoryCallback<Item> callback) {
        itemService.createItem(request).enqueue(new Callback<ApiResponse<Item>>() {
            @Override
            public void onResponse(Call<ApiResponse<Item>> call, Response<ApiResponse<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(NetworkUtils.getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Item>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void updateItem(String id, UpdateItemRequest request, RepositoryCallback<Item> callback) {
        itemService.updateItem(id, request).enqueue(new Callback<ApiResponse<Item>>() {
            @Override
            public void onResponse(Call<ApiResponse<Item>> call, Response<ApiResponse<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(NetworkUtils.getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Item>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void updateQuantity(String id, int quantity, RepositoryCallback<Item> callback) {
        Map<String, Integer> body = new HashMap<>();
        body.put("quantity", quantity);
        itemService.updateQuantity(id, body).enqueue(new Callback<ApiResponse<Item>>() {
            @Override
            public void onResponse(Call<ApiResponse<Item>> call, Response<ApiResponse<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(NetworkUtils.getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Item>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void uploadItemImage(String id, MultipartBody.Part image, RepositoryCallback<Item> callback) {
        itemService.uploadItemImage(id, image).enqueue(new Callback<ApiResponse<Item>>() {
            @Override
            public void onResponse(Call<ApiResponse<Item>> call, Response<ApiResponse<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(NetworkUtils.getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Item>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
