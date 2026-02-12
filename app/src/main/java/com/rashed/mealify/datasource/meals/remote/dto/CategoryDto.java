package com.rashed.mealify.datasource.meals.remote.dto;

import com.google.gson.annotations.SerializedName;

public class CategoryDto {
    @SerializedName("strCategory")
    private String categoryName;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
