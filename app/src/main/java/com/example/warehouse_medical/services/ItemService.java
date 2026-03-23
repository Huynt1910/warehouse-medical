package com.example.warehouse_medical.services;

import com.example.warehouse_medical.dtos.ApiResponse;
import com.example.warehouse_medical.dtos.CreateItemRequest;
import com.example.warehouse_medical.dtos.ItemListData;
import com.example.warehouse_medical.dtos.UpdateItemRequest;
import com.example.warehouse_medical.models.Item;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ItemService {
    @GET("items")
    Call<ApiResponse<ItemListData>> getItems(
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("keyword") String keyword
    );

    @GET("items/public/available")
    Call<ApiResponse<ItemListData>> getAvailableItems(
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("keyword") String keyword
    );

    @GET("items/low-stock")
    Call<ApiResponse<List<Item>>> getLowStockItems();

    @GET("items/{id}")
    Call<ApiResponse<Item>> getItemDetail(@Path("id") String id);

    @POST("items")
    Call<ApiResponse<Item>> createItem(@Body CreateItemRequest request);

    @PATCH("items/{id}")
    Call<ApiResponse<Item>> updateItem(@Path("id") String id, @Body UpdateItemRequest request);

    @PATCH("items/{id}/quantity")
    Call<ApiResponse<Item>> updateQuantity(@Path("id") String id, @Body Map<String, Integer> body);

    @Multipart
    @POST("items/{id}/image")
    Call<ApiResponse<Item>> uploadItemImage(
            @Path("id") String id,
            @Part MultipartBody.Part image
    );
}
