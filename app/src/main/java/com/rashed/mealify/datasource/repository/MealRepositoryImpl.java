package com.rashed.mealify.datasource.repository;

import android.content.Context;

import com.rashed.mealify.common.NetworkUtils;
import com.rashed.mealify.datasource.meals.local.MealsLocalDataSource;
import com.rashed.mealify.datasource.meals.local.entities.MealEntity;
import com.rashed.mealify.datasource.meals.local.entities.PlannedMealDetails;
import com.rashed.mealify.datasource.meals.remote.MealsRemoteDataSource;
import com.rashed.mealify.datasource.meals.remote.dto.CategoriesResponse;
import com.rashed.mealify.datasource.meals.remote.dto.ListAreas;
import com.rashed.mealify.datasource.meals.remote.dto.ListCategories;
import com.rashed.mealify.datasource.meals.remote.dto.ListIngredients;
import com.rashed.mealify.datasource.meals.remote.dto.MealsResponse;
import com.rashed.mealify.domain.repository.MealRepository;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class MealRepositoryImpl implements MealRepository {

    private final MealsRemoteDataSource remote;
    private final MealsLocalDataSource local;
    private final Context context;

    public MealRepositoryImpl(Context context) {
        this.remote = new MealsRemoteDataSource();
        this.local = new MealsLocalDataSource(context);
        this.context = context;
    }

    private <T> Single<T> withNetwork(Single<T> remoteCall) {
        return Single.defer(() -> {
            if (!NetworkUtils.isInternetAvailable(context)) {
                return Single.error(new Exception("No internet connection available."));
            }
            return remoteCall;
        });
    }

    @Override
    public Single<MealsResponse> searchMealsByName(String name) {
        return withNetwork(remote.searchMealsByName(name));
    }


    @Override
    public Single<MealsResponse> lookupMealById(String id) {
        return withNetwork(remote.lookupMealById(id));
    }

    @Override
    public Single<MealsResponse> getRandomMeal() {
        return withNetwork(remote.getRandomMeal());
    }

    @Override
    public Single<CategoriesResponse> getCategories() {
        return withNetwork(remote.getCategories());
    }

    @Override
    public Single<ListCategories> listCategories() {
        return withNetwork(remote.listCategories());
    }

    @Override
    public Single<ListAreas> listAreas() {
        return withNetwork(remote.listAreas());
    }

    @Override
    public Single<ListIngredients> listIngredients() {
        return withNetwork(remote.listIngredients());
    }

    @Override
    public Single<MealsResponse> filterByIngredient(String ingredient) {
        return withNetwork(remote.filterByIngredient(ingredient));
    }

    @Override
    public Single<MealsResponse> filterByCategory(String category) {
        return withNetwork(remote.filterByCategory(category));
    }

    @Override
    public Single<MealsResponse> filterByArea(String area) {
        return withNetwork(remote.filterByArea(area));
    }

    @Override
    public Completable addFavorite(String uid, MealEntity meal) {
        return local.addFavorite(uid, meal);
    }

    @Override
    public Completable removeFavorite(String uid, String mealId) {
        return local.removeFavorite(uid, mealId);
    }

    @Override
    public Single<Boolean> isFavorite(String uid, String mealId) {
        return local.isFavorite(uid, mealId);
    }

    @Override
    public Single<List<MealEntity>> getFavorites(String uid) {
        return local.getFavorites(uid);
    }

    @Override
    public Completable addMealToPlan(String uid, String date, String mealType, MealEntity meal) {
        return local.addMealToPlan(uid, date, mealType, meal);
    }

    @Override
    public Completable removeMealFromPlan(String uid, String date, String mealId) {
        return local.removeMealFromPlan(uid, date, mealId);
    }

    @Override
    public Single<List<PlannedMealDetails>> getPlanForDayDetails(String uid, String date) {
        return local.getPlanForDay(uid, date);
    }
}