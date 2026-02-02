package com.rashed.mealify.datasource.meals.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(
        tableName = "planned_meals",
        primaryKeys = {"uid", "date", "mealId"}
)
public class PlannedMealEntity {
    @NonNull public String uid;

    @NonNull public String date;

    @NonNull public String mealId;

    public String mealType;

    public long savedAt;

    public PlannedMealEntity(@NonNull String uid, @NonNull String date, @NonNull String mealId,
                             String mealType, long savedAt) {
        this.uid = uid;
        this.date = date;
        this.mealId = mealId;
        this.mealType = mealType;
        this.savedAt = savedAt;
    }
}
