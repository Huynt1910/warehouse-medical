package com.example.warehouse_medical.dtos;

import com.example.warehouse_medical.models.CartItem;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CartResponseData implements Serializable {
    @SerializedName(value = "id", alternate = {"_id", "cartId"})
    private String id;

    @SerializedName(value = "customerId", alternate = {"customer"})
    private String customerId;

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

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Double getTotalAmount() {
        if (totalAmount != null && totalAmount > 0) {
            return totalAmount;
        }
        return calculateTotalFromItems();
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

    private Double calculateTotalFromItems() {
        double total = 0.0;
        if (items != null) {
            for (CartItem item : items) {
                if (item.getSubtotal() != null) {
                    total += item.getSubtotal();
                } else if (item.getQuantity() != null) {
                    Double price = item.getSalePrice();
                    if (price == null && item.getItemId() != null) {
                        price = item.getItemId().getSalePrice();
                    }
                    if (price != null) {
                        total += price * item.getQuantity();
                    }
                }
            }
        }
        return total;
    }
}
