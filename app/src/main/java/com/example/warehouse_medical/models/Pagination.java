package com.example.warehouse_medical.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Pagination implements Serializable {
    @SerializedName(value = "page", alternate = {"currentPage"})
    private Integer page;

    @SerializedName(value = "limit", alternate = {"pageSize"})
    private Integer limit;

    @SerializedName(value = "total", alternate = {"totalItems", "count"})
    private Integer total;

    @SerializedName(value = "totalPages", alternate = {"pageCount"})
    private Integer totalPages;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
}
