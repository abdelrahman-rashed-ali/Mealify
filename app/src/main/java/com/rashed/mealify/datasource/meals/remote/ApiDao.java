package com.rashed.mealify.datasource.meals.remote;

import com.rashed.mealify.datasource.meals.remote.dto.CategoriesResponse;
import com.rashed.mealify.datasource.meals.remote.dto.ListAreas;
import com.rashed.mealify.datasource.meals.remote.dto.ListCategories;
import com.rashed.mealify.datasource.meals.remote.dto.ListIngredients;
import com.rashed.mealify.datasource.meals.remote.dto.MealsResponse;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiDao {
    @GET("search.php")
    Single<MealsResponse> searchMealsByName(@Query("s") String name);

    @GET("lookup.php")
    Single<MealsResponse> lookupMealById(@Query("i") String id);

    @GET("random.php")
    Single<MealsResponse> getRandomMeal();

    @GET("categories.php")
    Single<CategoriesResponse> getCategories();

    @GET("list.php")
    Single<ListCategories> listCategories(@Query("c") String list);

    @GET("list.php")
    Single<ListAreas> listAreas(@Query("a") String list);

    @GET("list.php")
    Single<ListIngredients> listIngredients(@Query("i") String list);

    @GET("filter.php")
    Single<MealsResponse> filterByIngredient(@Query("i") String ingredient);

    @GET("filter.php")
    Single<MealsResponse> filterByCategory(@Query("c") String category);

    @GET("filter.php")
    Single<MealsResponse> filterByArea(@Query("a") String area);
}
