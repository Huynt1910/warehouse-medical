package com.example.warehouse_medical.dtos;

public class CheckoutRequest {
    private String note;

    public CheckoutRequest(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }
}
