package com.rashed.mealify.datasource.repository;

import android.content.Context;

import com.rashed.mealify.common.Result;
import com.rashed.mealify.datasource.meals.local.MealsLocalDataSource;
import com.rashed.mealify.datasource.meals.local.entities.MealEntity;
import com.rashed.mealify.datasource.meals.remote.MealsRemoteDataSource;
import com.rashed.mealify.datasource.meals.remote.dto.CategoriesResponse;
import com.rashed.mealify.datasource.meals.remote.dto.ListResponse;
import com.rashed.mealify.datasource.meals.remote.dto.MealsResponse;
import com.rashed.mealify.domain.repository.MealRepository;

import java.util.List;

public class MealRepositoryImpl implements MealRepository {

    private final MealsRemoteDataSource remote;
    private final MealsLocalDataSource local;

    public MealRepositoryImpl(Context context) {
        this.remote = new MealsRemoteDataSource();
        this.local = new MealsLocalDataSource(context);
    }

    // ---------------- Remote (TheMealDB) ----------------

    @Override
    public Result<MealsResponse> searchMealsByName(String name) {
        Result<MealsResponse> res = remote.searchMealsByName(name);
        cacheMealsIfPossible(res);
        return res;
    }

    @Override
    public Result<MealsResponse> listMealsByFirstLetter(String letter) {
        Result<MealsResponse> res = remote.listMealsByFirstLetter(letter);
        cacheMealsIfPossible(res);
        return res;
    }

    @Override
    public Result<MealsResponse> lookupMealById(String id) {
        // 1) try cache first
        MealEntity cached = local.getMealById(id);
        if (cached != null) {
            // لو عندك mapper من MealEntity -> MealsResponse اعمله هنا
            // لكن بما إن DTOs مختلفة، هنرجّع remote دائمًا لو محتاج DTO.
            // (اختياري) تقدر تعمل method في domain ترجع MealEntity مباشرة.
        }

        // 2) fetch from remote
        Result<MealsResponse> res = remote.lookupMealById(id);
        cacheMealsIfPossible(res);
        return res;
    }

    @Override
    public Result<MealsResponse> getRandomMeal() {
        Result<MealsResponse> res = remote.getRandomMeal();
        cacheMealsIfPossible(res);
        return res;
    }

    @Override
    public Result<CategoriesResponse> getCategories() {
        return remote.getCategories();
    }

    @Override
    public Result<ListResponse> listCategories() {
        return remote.listCategories();
    }

    @Override
    public Result<ListResponse> listAreas() {
        return remote.listAreas();
    }

    @Override
    public Result<ListResponse> listIngredients() {
        return remote.listIngredients();
    }

    @Override
    public Result<MealsResponse> filterByIngredient(String ingredient) {
        Result<MealsResponse> res = remote.filterByIngredient(ingredient);
        cacheMealsIfPossible(res);
        return res;
    }

    @Override
    public Result<MealsResponse> filterByCategory(String category) {
        Result<MealsResponse> res = remote.filterByCategory(category);
        cacheMealsIfPossible(res);
        return res;
    }

    @Override
    public Result<MealsResponse> filterByArea(String area) {
        Result<MealsResponse> res = remote.filterByArea(area);
        cacheMealsIfPossible(res);
        return res;
    }

    // ---------------- Local (Room) Favorites ----------------

    @Override
    public Result<Void> addFavorite(String uid, MealEntity meal) {
        try {
            local.addFavorite(uid, meal);
            return new Result.Success<>(null);
        } catch (Exception e) {
            return new Result.Error<>("Failed to add favorite: " + e.getMessage(), e);
        }
    }

    @Override
    public Result<Void> removeFavorite(String uid, String mealId) {
        try {
            local.removeFavorite(uid, mealId);
            return new Result.Success<>(null);
        } catch (Exception e) {
            return new Result.Error<>("Failed to remove favorite: " + e.getMessage(), e);
        }
    }

    @Override
    public Result<Boolean> isFavorite(String uid, String mealId) {
        try {
            boolean exists = local.isFavorite(uid, mealId);
            return new Result.Success<>(exists);
        } catch (Exception e) {
            return new Result.Error<>("Failed to check favorite: " + e.getMessage(), e);
        }
    }

    @Override
    public Result<List<MealEntity>> getFavorites(String uid) {
        try {
            List<MealEntity> list = local.getFavorites(uid);
            return new Result.Success<>(list);
        } catch (Exception e) {
            return new Result.Error<>("Failed to load favorites: " + e.getMessage(), e);
        }
    }


    // ---------------- Local (Room) Plan ----------------

    @Override
    public Result<Void> addMealToPlan(String uid, String date, String mealType, MealEntity meal) {
        try {
            local.addMealToPlan(uid, date, mealType, meal);
            return new Result.Success<>(null);
        } catch (Exception e) {
            return new Result.Error<>("Failed to add meal to plan: " + e.getMessage(), e);
        }
    }

    @Override
    public Result<Void> removeMealFromPlan(String uid, String date, String mealId) {
        try {
            local.removeMealFromPlan(uid, date, mealId);
            return new Result.Success<>(null);
        } catch (Exception e) {
            return new Result.Error<>("Failed to remove meal from plan: " + e.getMessage(), e);
        }
    }

    @Override
    public Result<List<MealEntity>> getPlanForDay(String uid, String date) {
        try {
            List<MealEntity> list = local.getPlanForDay(uid, date);
            return new Result.Success<>(list);
        } catch (Exception e) {
            return new Result.Error<>("Failed to load day plan: " + e.getMessage(), e);
        }
    }

    @Override
    public Result<List<MealEntity>> getPlanForWeek(String uid, String fromDate, String toDate) {
        try {
            List<MealEntity> list = local.getPlanForWeek(uid, fromDate, toDate);
            return new Result.Success<>(list);
        } catch (Exception e) {
            return new Result.Error<>("Failed to load week plan: " + e.getMessage(), e);
        }
    }

    @Override
    public Result<Void> clearPlanDay(String uid, String date) {
        try {
            local.clearPlanDay(uid, date);
            return new Result.Success<>(null);
        } catch (Exception e) {
            return new Result.Error<>("Failed to clear day plan: " + e.getMessage(), e);
        }
    }

    // ---------------- Helpers ----------------

    /**
     * Cache meals from DTO response into Room IF you have a mapper.
     * حالياً: لازم تعمل Mapping من DTO meal -> MealEntity.
     */
    private void cacheMealsIfPossible(Result<MealsResponse> res) {
        if (!(res instanceof Result.Success)) return;

        MealsResponse body = ((Result.Success<MealsResponse>) res).data;
        if (body == null || body.getMeals() == null) return;

        // ⚠️ هنا محتاج مابّينج من DTO -> Entity
        // مثال:
        // for (MealDto dto : body.meals) {
        //     local.upsertMeal(MealEntityMapper.fromDto(dto));
        // }

        // لو إنت مش عامل DTO/Entity mapper، سيبها فاضية لحد ما تعملها.
    }
}
