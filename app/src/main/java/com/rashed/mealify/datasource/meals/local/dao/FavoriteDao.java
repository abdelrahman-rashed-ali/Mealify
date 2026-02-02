package com.rashed.mealify.datasource.meals.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rashed.mealify.datasource.meals.local.entities.FavoriteEntity;
import com.rashed.mealify.datasource.meals.local.entities.MealEntity;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void add(FavoriteEntity fav);

    @Query("DELETE FROM favorites WHERE uid = :uid AND mealId = :mealId")
    void remove(String uid, String mealId);

    @Query("SELECT COUNT(*) FROM favorites WHERE uid = :uid AND mealId = :mealId")
    int exists(String uid, String mealId);

    @Query("SELECT m.* FROM meals m INNER JOIN favorites f ON m.idMeal = f.mealId WHERE f.uid = :uid ORDER BY f.savedAt DESC")
    List<MealEntity> getFavoritesMeals(String uid);

    @Query("SELECT mealId FROM favorites WHERE uid = :uid ORDER BY savedAt DESC")
    List<String> getFavoriteIds(String uid);
}
