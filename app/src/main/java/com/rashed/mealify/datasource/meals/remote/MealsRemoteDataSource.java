package com.rashed.mealify.datasource.meals.remote;

import com.rashed.mealify.common.Result;
import com.rashed.mealify.datasource.meals.remote.dto.CategoriesResponse;
import com.rashed.mealify.datasource.meals.remote.dto.ListAreas;
import com.rashed.mealify.datasource.meals.remote.dto.ListCategories;
import com.rashed.mealify.datasource.meals.remote.dto.ListIngredients;
import com.rashed.mealify.datasource.meals.remote.dto.MealsResponse;
import com.rashed.mealify.datasource.network.ApiConnection;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class MealsRemoteDataSource {
    private ApiDao apiDao;

    public MealsRemoteDataSource() {
        this.apiDao = ApiConnection.getService().create(ApiDao.class);
    }

    public Result<MealsResponse> searchMealsByName(String name) {
        return safeCall(apiDao.searchMealsByName(name), "Failed to search meals");
    }

    public Result<MealsResponse> listMealsByFirstLetter(String letter) {
        return safeCall(apiDao.listMealsByFirstLetter(letter), "Failed to load meals by letter");
    }

    public Result<MealsResponse> lookupMealById(String id) {
        return safeCall(apiDao.lookupMealById(id), "Failed to load meal details");
    }

    public Result<MealsResponse> getRandomMeal() {
        return safeCall(apiDao.getRandomMeal(), "Failed to load random meal");
    }

    public Result<CategoriesResponse> getCategories() {
        return safeCall(apiDao.getCategories(), "Failed to load categories");
    }

    public Result<ListCategories> listCategories() {
        return safeCall(apiDao.listCategories("list"), "Failed to load categories list");
    }

    public Result<ListAreas> listAreas() {
        return safeCall(apiDao.listAreas("list"), "Failed to load areas list");
    }

    public Result<ListIngredients> listIngredients() {
        return safeCall(apiDao.listIngredients("list"), "Failed to load ingredients list");
    }

    public Result<MealsResponse> filterByIngredient(String ingredient) {
        return safeCall(apiDao.filterByIngredient(ingredient), "Failed to filter by ingredient");
    }

    public Result<MealsResponse> filterByCategory(String category) {
        return safeCall(apiDao.filterByCategory(category), "Failed to filter by category");
    }

    public Result<MealsResponse> filterByArea(String area) {
        return safeCall(apiDao.filterByArea(area), "Failed to filter by area");
    }

    private <T> Result<T> safeCall(Call<T> call, String defaultError) {
        try {
            Response<T> res = call.execute();

            if (res.isSuccessful()) {
                T body = res.body();
                // TheMealDB sometimes returns { "meals": null } for no results — body still not null, fields may be null.
                return new Result.Success<>(body);
            } else {
                return new Result.Error<>(defaultError + " (HTTP " + res.code() + ")", null);
            }
        } catch (IOException e) {
            return new Result.Error<>("Network error: " + e.getMessage(), e);
        } catch (Exception e) {
            return new Result.Error<>(defaultError + ": " + e.getMessage(), e);
        }
    }
}
