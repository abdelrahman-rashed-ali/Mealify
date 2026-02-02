package com.rashed.mealify.datasource.meals.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(
        tableName = "favorites",
        primaryKeys = {"uid", "mealId"}
)
public class FavoriteEntity {
    @NonNull public String uid;
    @NonNull public String mealId;

    public long savedAt;

    public FavoriteEntity(@NonNull String uid, @NonNull String mealId, long savedAt) {
        this.uid = uid;
        this.mealId = mealId;
        this.savedAt = savedAt;
    }
}
