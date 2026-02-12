package com.rashed.mealify.datasource.meals.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListAreas {
    @SerializedName("meals")
    private List<AreaDto> meals;

    public List<AreaDto> getMeals() {
        return meals;
    }

    public void setMeals(List<AreaDto> meals) {
        this.meals = meals;
    }
}
