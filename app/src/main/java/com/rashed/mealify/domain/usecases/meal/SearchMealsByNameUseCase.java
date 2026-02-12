package com.rashed.mealify.domain.usecases.meal;

import com.rashed.mealify.common.Result;
import com.rashed.mealify.datasource.meals.remote.dto.MealsResponse;
import com.rashed.mealify.domain.mapper.MealMapper;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.repository.MealRepository;

public class SearchMealsByNameUseCase {
    private final MealRepository repository;

    public SearchMealsByNameUseCase(MealRepository repository) {
        this.repository = repository;
    }

    public Result<Meal> execute(String search) {
        Result<MealsResponse> result = repository.searchMealsByName(search);
        if (result instanceof Result.Success) {
            MealsResponse response = ((Result.Success<MealsResponse>) result).data;
            if (response != null && response.getMeals() != null && !response.getMeals().isEmpty()) {
                return new Result.Success<>(MealMapper.mapDtoToDomain(response.getMeals().get(0)));
            }
            return new Result.Error<>("No meal found", null);
        }
        return new Result.Error<>(((Result.Error) result).message, ((Result.Error) result).throwable);
    }
}
