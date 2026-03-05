package com.rashed.mealify.domain.repository;

import com.rashed.mealify.datasource.meals.local.entities.MealEntity;
import com.rashed.mealify.datasource.meals.local.dto.PlannedMealDetails;
import com.rashed.mealify.datasource.meals.remote.dto.CategoriesResponse;
import com.rashed.mealify.datasource.meals.remote.dto.ListAreas;
import com.rashed.mealify.datasource.meals.remote.dto.ListCategories;
import com.rashed.mealify.datasource.meals.remote.dto.ListIngredients;
import com.rashed.mealify.datasource.meals.remote.dto.MealsResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface MealRepository {

    Single<MealsResponse> searchMealsByName(String name);
    Single<MealsResponse> lookupMealById(String id);
    Single<MealsResponse> getRandomMeal();

    Single<CategoriesResponse> getCategories();
    Single<ListCategories> listCategories();
    Single<ListAreas> listAreas();
    Single<ListIngredients> listIngredients();

    Single<MealsResponse> filterByIngredient(String ingredient);
    Single<MealsResponse> filterByCategory(String category);
    Single<MealsResponse> filterByArea(String area);

    Completable addFavorite(String uid, MealEntity meal);
    Completable removeFavorite(String uid, String mealId);
    Single<Boolean> isFavorite(String uid, String mealId);
    Single<List<MealEntity>> getFavorites(String uid);

    Completable addMealToPlan(String uid, String date, String mealType, MealEntity meal);
    Completable removeMealFromPlan(String uid, String date, String mealId);
    Single<List<PlannedMealDetails>> getPlanForDayDetails(String uid, String date);
}