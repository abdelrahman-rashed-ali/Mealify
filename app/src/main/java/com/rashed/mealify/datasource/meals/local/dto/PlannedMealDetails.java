package com.rashed.mealify.datasource.meals.local.dto;

import androidx.room.Embedded;

import com.rashed.mealify.datasource.meals.local.entities.MealEntity;

public class PlannedMealDetails {
    @Embedded
    public MealEntity meal;

    public String mealType;
    public String date;
}