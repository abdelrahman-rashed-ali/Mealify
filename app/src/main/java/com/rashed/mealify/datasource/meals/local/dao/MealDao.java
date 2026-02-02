package com.rashed.mealify.datasource.meals.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rashed.mealify.datasource.meals.local.entities.MealEntity;

import java.util.List;

@Dao
public interface MealDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(MealEntity meal);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertAll(List<MealEntity> meals);

    @Query("SELECT * FROM meals WHERE idMeal = :mealId LIMIT 1")
    MealEntity getById(String mealId);

    @Query("DELETE FROM meals")
    void clearAll();
}
