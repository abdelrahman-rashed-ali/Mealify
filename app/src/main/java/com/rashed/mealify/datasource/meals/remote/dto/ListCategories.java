package com.rashed.mealify.datasource.meals.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListCategories {

    @SerializedName("meals")
    private List<CategoryDto> meals;

    public List<CategoryDto> getMeals() {
        return meals;
    }

    public void setMeals(List<CategoryDto> meals) {
        this.meals = meals;
    }
}
