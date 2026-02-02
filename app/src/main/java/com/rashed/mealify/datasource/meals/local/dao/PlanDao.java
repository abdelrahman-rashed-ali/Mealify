package com.rashed.mealify.datasource.meals.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rashed.mealify.datasource.meals.local.entities.MealEntity;
import com.rashed.mealify.datasource.meals.local.entities.PlannedMealEntity;

import java.util.List;

@Dao
public interface PlanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addToPlan(PlannedMealEntity item);

    @Query("DELETE FROM planned_meals WHERE uid = :uid AND date = :date AND mealId = :mealId")
    void removeFromPlan(String uid, String date, String mealId);

    @Query("DELETE FROM planned_meals WHERE uid = :uid AND date = :date")
    void clearDay(String uid, String date);

    @Query("DELETE FROM planned_meals WHERE uid = :uid")
    void clearAll(String uid);

    @Query("SELECT * FROM planned_meals WHERE uid = :uid AND date = :date")
    List<PlannedMealEntity> getDayPlanRaw(String uid, String date);

    @Query("SELECT m.* FROM meals m INNER JOIN planned_meals p ON m.idMeal = p.mealId WHERE p.uid = :uid AND p.date = :date ORDER BY p.savedAt DESC")
    List<MealEntity> getDayPlanMeals(String uid, String date);

    @Query("SELECT m.* FROM meals m INNER JOIN planned_meals p ON m.idMeal = p.mealId WHERE p.uid = :uid AND p.date BETWEEN :fromDate AND :toDate ORDER BY p.date ASC, p.savedAt DESC ")
    List<MealEntity> getWeekPlanMeals(String uid, String fromDate, String toDate);
}
