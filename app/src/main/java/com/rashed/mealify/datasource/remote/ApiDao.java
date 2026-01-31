package com.rashed.mealify.datasource.remote;

import com.rashed.mealify.datasource.remote.dto.CategoriesResponse;
import com.rashed.mealify.datasource.remote.dto.ListResponse;
import com.rashed.mealify.datasource.remote.dto.MealsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiDao {
    @GET("search.php")
    Call<MealsResponse> searchMealsByName(@Query("s") String name);

    @GET("search.php")
    Call<MealsResponse> listMealsByFirstLetter(@Query("f") String letter);

    @GET("lookup.php")
    Call<MealsResponse> lookupMealById(@Query("i") String id);

    @GET("random.php")
    Call<MealsResponse> getRandomMeal();

    @GET("categories.php")
    Call<CategoriesResponse> getCategories();

    @GET("list.php")
    Call<ListResponse> listCategories(@Query("c") String list);

    @GET("list.php")
    Call<ListResponse> listAreas(@Query("a") String list);

    @GET("list.php")
    Call<ListResponse> listIngredients(@Query("i") String list);

    @GET("filter.php")
    Call<MealsResponse> filterByIngredient(@Query("i") String ingredient);

    @GET("filter.php")
    Call<MealsResponse> filterByCategory(@Query("c") String category);

    @GET("filter.php")
    Call<MealsResponse> filterByArea(@Query("a") String area);
}
