package com.rashed.mealify.domain.usecases.meal;

import com.rashed.mealify.common.Result;
import com.rashed.mealify.datasource.meals.remote.dto.IngredientDto;
import com.rashed.mealify.datasource.meals.remote.dto.ListIngredients;
import com.rashed.mealify.domain.mapper.IngredientMapper;
import com.rashed.mealify.domain.model.Ingredient;
import com.rashed.mealify.domain.repository.MealRepository;

import java.util.ArrayList;
import java.util.List;

public class GetIngredientsUseCase {
    private final MealRepository repository;

    public GetIngredientsUseCase(MealRepository repository) {
        this.repository = repository;
    }

    public Result<List<Ingredient>> execute() {
        Result<ListIngredients> result = repository.listIngredients();
        if (result instanceof Result.Success) {
            ListIngredients response = ((Result.Success<ListIngredients>) result).data;
            List<Ingredient> list = new ArrayList<>();
            if (response != null && response.getMeals() != null) {
                int limit = Math.min(response.getMeals().size(), 20);
                for (int i = 0; i < limit; i++) {
                    list.add(IngredientMapper.mapDtoToDomain(response.getMeals().get(i)));
                }
            }
            return new Result.Success<>(list);
        }
        return new Result.Error<>(((Result.Error) result).message, ((Result.Error) result).throwable);
    }
}