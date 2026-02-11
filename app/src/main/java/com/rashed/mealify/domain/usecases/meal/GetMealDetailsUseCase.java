package com.rashed.mealify.domain.usecases.meal;

import com.rashed.mealify.common.Result;
import com.rashed.mealify.datasource.meals.remote.dto.MealsResponse;
import com.rashed.mealify.domain.mapper.MealMapper;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.repository.MealRepository;

public class GetMealDetailsUseCase {
    private final MealRepository repository;

    public GetMealDetailsUseCase(MealRepository repository) {
        this.repository = repository;
    }

    public Result<Meal> execute(String mealId) {
        Result<MealsResponse> result = repository.lookupMealById(mealId);

        if (result instanceof Result.Success) {
            MealsResponse data = ((Result.Success<MealsResponse>) result).data;
            if (data != null && data.getMeals() != null && !data.getMeals().isEmpty()) {
                return new Result.Success<>(MealMapper.mapDtoToDomain(data.getMeals().get(0)));
            }
            return new Result.Error<>("Meal not found", null);
        } else {
            return new Result.Error<>(((Result.Error) result).message, ((Result.Error) result).throwable);
        }
    }
}