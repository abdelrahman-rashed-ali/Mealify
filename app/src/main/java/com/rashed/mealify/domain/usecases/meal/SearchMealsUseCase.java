package com.rashed.mealify.domain.usecases.meal;

import com.rashed.mealify.common.Result;
import com.rashed.mealify.datasource.meals.remote.dto.MealsResponse;
import com.rashed.mealify.datasource.meals.remote.dto.Meals;
import com.rashed.mealify.domain.mapper.MealMapper;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.repository.MealRepository;

import java.util.ArrayList;
import java.util.List;

public class SearchMealsUseCase {
    private final MealRepository repository;

    public SearchMealsUseCase(MealRepository repository) {
        this.repository = repository;
    }

    public Result<List<Meal>> execute(String query) {
        Result<MealsResponse> result = repository.searchMealsByName(query);

        if (result instanceof Result.Success) {
            MealsResponse data = ((Result.Success<MealsResponse>) result).data;
            List<Meal> meals = new ArrayList<>();
            if (data != null && data.getMeals() != null) {
                for (Meals dto : data.getMeals()) {
                    meals.add(MealMapper.mapDtoToDomain(dto));
                }
            }
            return new Result.Success<>(meals);
        } else {
            return new Result.Error<>(((Result.Error) result).message, ((Result.Error) result).throwable);
        }
    }
}