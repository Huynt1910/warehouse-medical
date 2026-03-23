package com.example.warehouse_medical.dtos;

public class CartUpdateRequest {
    private int quantity;

    public CartUpdateRequest(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }
}
