package com.rashed.mealify.datasource.meals.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CategoriesResponse {

    @SerializedName("categories")
    private List<CategoryDetailsDto> categories;

    public List<CategoryDetailsDto> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDetailsDto> categories) {
        this.categories = categories;
    }
}
