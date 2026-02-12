package com.rashed.mealify.domain.usecases.meal;

import com.rashed.mealify.common.Result;
import com.rashed.mealify.datasource.meals.remote.dto.ListAreas;
import com.rashed.mealify.datasource.meals.remote.dto.ListIngredients;
import com.rashed.mealify.domain.mapper.AreasMapper;
import com.rashed.mealify.domain.mapper.IngredientMapper;
import com.rashed.mealify.domain.model.Area;
import com.rashed.mealify.domain.model.Ingredient;
import com.rashed.mealify.domain.repository.MealRepository;

import java.util.ArrayList;
import java.util.List;

public class GetAreasUseCase {
    private final MealRepository repository;

    public GetAreasUseCase(MealRepository repository) {
        this.repository = repository;
    }

    public Result<List<Area>> execute() {
        Result<ListAreas> result = repository.listAreas();
        if (result instanceof Result.Success) {
            ListAreas response = ((Result.Success<ListAreas>) result).data;
            List<Area> list = new ArrayList<>();
            if (response != null && response.getMeals() != null) {
                int limit = Math.min(response.getMeals().size(), 20);
                for (int i = 0; i < limit; i++) {
                    list.add(AreasMapper.mapDtoToDomain(response.getMeals().get(i)));
                }
            }
            return new Result.Success<>(list);
        }
        return new Result.Error<>(((Result.Error) result).message, ((Result.Error) result).throwable);
    }
}
