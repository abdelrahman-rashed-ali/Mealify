package com.rashed.mealify.datasource.meals.local.entities;

import androidx.room.Embedded;

public class PlannedMealDetails {
    @Embedded
    public MealEntity meal;

    public String mealType;
    public String date;
}