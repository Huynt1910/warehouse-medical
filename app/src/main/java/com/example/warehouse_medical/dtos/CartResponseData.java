package com.example.warehouse_medical.dtos;

import com.example.warehouse_medical.models.CartItem;
import com.example.warehouse_medical.models.User;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CartResponseData implements Serializable {
    @SerializedName(value = "id", alternate = {"_id", "cartId"})
    private String id;

    @SerializedName(value = "customerId", alternate = {"customer"})
    private User customerId;

    private List<CartItem> items;
    private Double totalAmount;
    private Integer totalItems;
    private String createdAt;
    private String updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getCustomerId() {
        return customerId;
    }

    public void setCustomerId(User customerId) {
        this.customerId = customerId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
