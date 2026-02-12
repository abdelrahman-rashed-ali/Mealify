package com.rashed.mealify.common;

public interface ResultCallback<T> {
    void onResult(Result<T> result);
}
