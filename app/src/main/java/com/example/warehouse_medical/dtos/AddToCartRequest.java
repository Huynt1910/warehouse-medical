package com.example.warehouse_medical.dtos;

import com.google.gson.annotations.SerializedName;

public class AddToCartRequest {
    @SerializedName("itemId")
    private final String itemId;
    @SerializedName("quantity")
    private final int quantity;

    public AddToCartRequest(String itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public String getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }
}
