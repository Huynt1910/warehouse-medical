package com.example.warehouse_medical.dtos;

import com.example.warehouse_medical.models.Item;
import com.example.warehouse_medical.models.Pagination;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ItemListData {
    @SerializedName("items")
    private List<Item> items;
    
    @SerializedName("pagination")
    private Pagination pagination;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
