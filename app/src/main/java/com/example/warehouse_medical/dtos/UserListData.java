package com.example.warehouse_medical.dtos;

import com.example.warehouse_medical.models.Pagination;
import com.example.warehouse_medical.models.User;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserListData {
    @SerializedName(value = "users", alternate = {"items", "results"})
    private List<User> users;

    @SerializedName(value = "pagination", alternate = {"meta"})
    private Pagination pagination;

    public List<User> getUsers() {
        return users;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
