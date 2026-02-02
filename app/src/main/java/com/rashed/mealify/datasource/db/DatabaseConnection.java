package com.rashed.mealify.datasource.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.rashed.mealify.datasource.meals.local.dao.FavoriteDao;
import com.rashed.mealify.datasource.meals.local.dao.MealDao;
import com.rashed.mealify.datasource.meals.local.dao.PlanDao;
import com.rashed.mealify.datasource.meals.local.entities.FavoriteEntity;
import com.rashed.mealify.datasource.meals.local.entities.MealEntity;
import com.rashed.mealify.datasource.meals.local.entities.PlannedMealEntity;
import com.rashed.mealify.datasource.meals.remote.dto.MealsResponse;

@Database(entities = {MealEntity.class, PlannedMealEntity.class, FavoriteEntity.class}, version = 2)
public abstract class DatabaseConnection extends RoomDatabase {

    private static final String DATABASE_NAME = "meals_db";
    private static volatile DatabaseConnection instance;
    public abstract MealDao mealDao();
    public abstract FavoriteDao favoriteDao();
    public abstract PlanDao planDao();

    public static DatabaseConnection getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            DatabaseConnection.class,
                            DATABASE_NAME
                    )
                    .build();
        }
        return instance;
    }
}
