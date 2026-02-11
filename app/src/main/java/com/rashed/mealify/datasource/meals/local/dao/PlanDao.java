package com.rashed.mealify.datasource.meals.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rashed.mealify.datasource.meals.local.entities.PlannedMealDetails;
import com.rashed.mealify.datasource.meals.local.entities.PlannedMealEntity;

import java.util.List;

@Dao
public interface PlanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addToPlan(PlannedMealEntity item);

    @Query("DELETE FROM planned_meals WHERE uid = :uid AND date = :date AND mealId = :mealId")
    void removeFromPlan(String uid, String date, String mealId);

    @Query("SELECT m.*, p.mealType, p.date FROM meals m " +
            "INNER JOIN planned_meals p ON m.idMeal = p.mealId " +
            "WHERE p.uid = :uid AND p.date = :date " +
            "ORDER BY CASE " +
            "WHEN p.mealType = 'Breakfast' THEN 1 " +
            "WHEN p.mealType = 'Lunch' THEN 2 " +
            "WHEN p.mealType = 'Dinner' THEN 3 " +
            "ELSE 4 END")
    List<PlannedMealDetails> getDayPlanDetails(String uid, String date);
}