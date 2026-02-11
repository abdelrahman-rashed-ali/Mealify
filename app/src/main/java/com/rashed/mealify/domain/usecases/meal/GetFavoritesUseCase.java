package com.rashed.mealify.domain.usecases.meal;

import com.rashed.mealify.common.Result;
import com.rashed.mealify.datasource.meals.local.entities.MealEntity;
import com.rashed.mealify.domain.mapper.MealMapper;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.repository.MealRepository;

import java.util.ArrayList;
import java.util.List;

public class GetFavoritesUseCase {
    private final MealRepository repository;

    public GetFavoritesUseCase(MealRepository repository) {
        this.repository = repository;
    }

    public Result<List<Meal>> execute(String uid) {
        Result<List<MealEntity>> result = repository.getFavorites(uid);

        if (result instanceof Result.Success) {
            List<MealEntity> entities = ((Result.Success<List<MealEntity>>) result).data;
            List<Meal> meals = new ArrayList<>();
            if (entities != null) {
                for (MealEntity entity : entities) {
                    meals.add(MealMapper.mapEntityToDomain(entity));
                }
            }
            return new Result.Success<>(meals);
        }
        return new Result.Error<>(((Result.Error) result).message, ((Result.Error) result).throwable);
    }
}