package com.example.warehouse_medical.dtos;

import com.example.warehouse_medical.models.Order;
import com.example.warehouse_medical.models.Pagination;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderListData {
    @SerializedName(value = "orders", alternate = {"items", "results"})
    private List<Order> orders;

    @SerializedName(value = "pagination", alternate = {"meta"})
    private Pagination pagination;

    public List<Order> getOrders() {
        return orders;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
