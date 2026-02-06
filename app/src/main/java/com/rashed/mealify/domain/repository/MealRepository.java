package com.rashed.mealify.domain.repository;

import com.rashed.mealify.common.Result;
import com.rashed.mealify.datasource.meals.local.entities.MealEntity;
import com.rashed.mealify.datasource.meals.remote.dto.CategoriesResponse;
import com.rashed.mealify.datasource.meals.remote.dto.ListResponse;
import com.rashed.mealify.datasource.meals.remote.dto.MealsResponse;

import java.util.List;

public interface MealRepository {

    Result<MealsResponse> searchMealsByName(String name);
    Result<MealsResponse> listMealsByFirstLetter(String letter);
    Result<MealsResponse> lookupMealById(String id);
    Result<MealsResponse> getRandomMeal();

    Result<CategoriesResponse> getCategories();
    Result<ListResponse> listCategories();
    Result<ListResponse> listAreas();
    Result<ListResponse> listIngredients();

    Result<MealsResponse> filterByIngredient(String ingredient);
    Result<MealsResponse> filterByCategory(String category);
    Result<MealsResponse> filterByArea(String area);

    Result<Void> addFavorite(String uid, MealEntity meal);
    Result<Void> removeFavorite(String uid, String mealId);
    Result<Boolean> isFavorite(String uid, String mealId);
    Result<List<MealEntity>> getFavorites(String uid);

    Result<Void> addMealToPlan(String uid, String date, String mealType, MealEntity meal);
    Result<Void> removeMealFromPlan(String uid, String date, String mealId);
    Result<List<MealEntity>> getPlanForDay(String uid, String date);
    Result<List<MealEntity>> getPlanForWeek(String uid, String fromDate, String toDate);
    Result<Void> clearPlanDay(String uid, String date);
}
