package com.rashed.mealify.datasource.meals.remote;

import com.rashed.mealify.datasource.meals.remote.dto.CategoriesResponse;
import com.rashed.mealify.datasource.meals.remote.dto.ListAreas;
import com.rashed.mealify.datasource.meals.remote.dto.ListCategories;
import com.rashed.mealify.datasource.meals.remote.dto.ListIngredients;
import com.rashed.mealify.datasource.meals.remote.dto.MealsResponse;
import com.rashed.mealify.datasource.network.ApiConnection;


import io.reactivex.rxjava3.core.Single;

public class MealsRemoteDataSource {
    private final ApiDao apiDao;

    public MealsRemoteDataSource() {
        this.apiDao = ApiConnection.getService().create(ApiDao.class);
    }

    public Single<MealsResponse> searchMealsByName(String name) {
        return apiDao.searchMealsByName(name);
    }


    public Single<MealsResponse> lookupMealById(String id) {
        return apiDao.lookupMealById(id);
    }

    public Single<MealsResponse> getRandomMeal() {
        return apiDao.getRandomMeal();
    }

    public Single<CategoriesResponse> getCategories() {
        return apiDao.getCategories();
    }

    public Single<ListCategories> listCategories() {
        return apiDao.listCategories("list");
    }

    public Single<ListAreas> listAreas() {
        return apiDao.listAreas("list");
    }

    public Single<ListIngredients> listIngredients() {
        return apiDao.listIngredients("list");
    }

    public Single<MealsResponse> filterByIngredient(String ingredient) {
        return apiDao.filterByIngredient(ingredient);
    }

    public Single<MealsResponse> filterByCategory(String category) {
        return apiDao.filterByCategory(category);
    }

    public Single<MealsResponse> filterByArea(String area) {
        return apiDao.filterByArea(area);
    }
}
