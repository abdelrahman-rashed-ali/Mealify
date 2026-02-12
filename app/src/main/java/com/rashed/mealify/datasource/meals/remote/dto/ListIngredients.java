package com.rashed.mealify.datasource.meals.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListIngredients {
    @SerializedName("meals")
    private List<IngredientDto> meals;

    public List<IngredientDto> getMeals() {
        return meals;
    }

    public void setMeals(List<IngredientDto> meals) {
        this.meals = meals;
    }
}
