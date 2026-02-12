package com.rashed.mealify.datasource.meals.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rashed.mealify.datasource.meals.local.entities.FavoriteEntity;
import com.rashed.mealify.datasource.meals.local.entities.MealEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;


@Dao
public interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable add(FavoriteEntity fav);

    @Query("DELETE FROM favorites WHERE uid = :uid AND mealId = :mealId")
    Completable remove(String uid, String mealId);

    @Query("SELECT COUNT(*) FROM favorites WHERE uid = :uid AND mealId = :mealId")
    Single<Integer> exists(String uid, String mealId);

    @Query("SELECT m.* FROM meals m INNER JOIN favorites f ON m.idMeal = f.mealId WHERE f.uid = :uid ORDER BY f.savedAt DESC")
    Single<List<MealEntity>> getFavoritesMeals(String uid);
}
