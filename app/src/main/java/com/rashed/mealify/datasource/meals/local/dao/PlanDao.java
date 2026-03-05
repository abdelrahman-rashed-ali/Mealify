package com.rashed.mealify.datasource.meals.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rashed.mealify.datasource.meals.local.dto.PlannedMealDetails;
import com.rashed.mealify.datasource.meals.local.entities.PlannedMealEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface PlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable addToPlan(PlannedMealEntity item);

    @Query("DELETE FROM planned_meals WHERE uid = :uid AND date = :date AND mealId = :mealId")
    Completable removeFromPlan(String uid, String date, String mealId);

    @Query("SELECT m.*, p.mealType, p.date FROM meals m " +
            "INNER JOIN planned_meals p ON m.idMeal = p.mealId " +
            "WHERE p.uid = :uid AND p.date = :date")
    Single<List<PlannedMealDetails>> getDayPlanDetails(String uid, String date);
}