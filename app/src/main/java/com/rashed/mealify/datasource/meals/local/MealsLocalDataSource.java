package com.rashed.mealify.datasource.meals.local;

import android.content.Context;
import com.rashed.mealify.datasource.db.DatabaseConnection;
import com.rashed.mealify.datasource.meals.local.dao.FavoriteDao;
import com.rashed.mealify.datasource.meals.local.dao.MealDao;
import com.rashed.mealify.datasource.meals.local.dao.PlanDao;
import com.rashed.mealify.datasource.meals.local.entities.FavoriteEntity;
import com.rashed.mealify.datasource.meals.local.entities.MealEntity;
import com.rashed.mealify.datasource.meals.local.entities.PlannedMealDetails;
import com.rashed.mealify.datasource.meals.local.entities.PlannedMealEntity;
import java.util.List;

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

    public void upsertMeal(MealEntity meal) { mealDao.upsert(meal); }
    public MealEntity getMealById(String mealId) { return mealDao.getById(mealId); }
    public void addFavorite(String uid, MealEntity meal) {
        mealDao.upsert(meal);
        favoriteDao.add(new FavoriteEntity(uid, meal.idMeal, System.currentTimeMillis()));
    }
    public void removeFavorite(String uid, String mealId) { favoriteDao.remove(uid, mealId); }
    public boolean isFavorite(String uid, String mealId) { return favoriteDao.exists(uid, mealId) > 0; }
    public List<MealEntity> getFavorites(String uid) { return favoriteDao.getFavoritesMeals(uid); }


    public void addMealToPlan(String uid, String date, String mealType, MealEntity meal) {
        mealDao.upsert(meal);
        planDao.addToPlan(new PlannedMealEntity(uid, date, meal.idMeal, mealType, System.currentTimeMillis()));
    }

    public void removeMealFromPlan(String uid, String date, String mealId) {
        planDao.removeFromPlan(uid, date, mealId);
    }

    public List<PlannedMealDetails> getPlanForDay(String uid, String date) {
        return planDao.getDayPlanDetails(uid, date);
    }
}