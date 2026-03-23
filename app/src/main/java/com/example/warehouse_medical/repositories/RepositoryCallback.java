package com.example.warehouse_medical.repositories;

public interface RepositoryCallback<T> {
    void onSuccess(T data);

    void onError(String message);
}
