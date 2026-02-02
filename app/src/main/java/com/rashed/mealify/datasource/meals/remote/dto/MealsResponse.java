package com.rashed.mealify.datasource.meals.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class MealsResponse {

    @SerializedName("meals")
    List<Meals> meals;


    public void setMeals(List<Meals> meals) {
        this.meals = meals;
    }
    public List<Meals> getMeals() {
        return meals;
    }

}
