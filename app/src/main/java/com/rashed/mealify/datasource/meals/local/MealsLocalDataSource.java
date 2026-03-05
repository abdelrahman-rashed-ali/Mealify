package com.rashed.mealify.datasource.meals.local;

import android.content.Context;
import com.rashed.mealify.datasource.db.DatabaseConnection;
import com.rashed.mealify.datasource.meals.local.dao.FavoriteDao;
import com.rashed.mealify.datasource.meals.local.dao.MealDao;
import com.rashed.mealify.datasource.meals.local.dao.PlanDao;
import com.rashed.mealify.datasource.meals.local.entities.FavoriteEntity;
import com.rashed.mealify.datasource.meals.local.entities.MealEntity;
import com.rashed.mealify.datasource.meals.local.dto.PlannedMealDetails;
import com.rashed.mealify.datasource.meals.local.entities.PlannedMealEntity;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class MealsLocalDataSource {
    private final MealDao mealDao;
    private final FavoriteDao favoriteDao;
    private final PlanDao planDao;

    public MealsLocalDataSource(Context context) {
        DatabaseConnection db = DatabaseConnection.getInstance(context);
        this.mealDao = db.mealDao();
        this.favoriteDao = db.favoriteDao();
        this.planDao = db.planDao();
    }

    public Completable addFavorite(String uid, MealEntity meal) {
        return mealDao.upsert(meal)
                .andThen(favoriteDao.add(new FavoriteEntity(uid, meal.idMeal, System.currentTimeMillis())));
    }

    public Completable removeFavorite(String uid, String mealId) {
        return favoriteDao.remove(uid, mealId);
    }

    public Single<Boolean> isFavorite(String uid, String mealId) {
        return favoriteDao.exists(uid, mealId).map(count -> count > 0);
    }

    public Single<List<MealEntity>> getFavorites(String uid) {
        return favoriteDao.getFavoritesMeals(uid);
    }

    public Completable addMealToPlan(String uid, String date, String mealType, MealEntity meal) {
        return mealDao.upsert(meal)
                .andThen(planDao.addToPlan(new PlannedMealEntity(uid, date, meal.idMeal, mealType, System.currentTimeMillis())));
    }

    public Completable removeMealFromPlan(String uid, String date, String mealId) {
        return planDao.removeFromPlan(uid, date, mealId);
    }

    public Single<List<PlannedMealDetails>> getPlanForDay(String uid, String date) {
        return planDao.getDayPlanDetails(uid, date);
    }
}