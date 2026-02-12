package com.rashed.mealify.domain.usecases.meal;

import com.rashed.mealify.domain.mapper.IngredientMapper;
import com.rashed.mealify.domain.model.Ingredient;
import com.rashed.mealify.domain.repository.MealRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Single;

public class GetIngredientsUseCase {
    private final MealRepository repository;

    public GetIngredientsUseCase(MealRepository repository) {
        this.repository = repository;
    }

    public Single<List<Ingredient>> execute() {
        return repository.listIngredients()
                .map(response -> {
                    if (response == null || response.getMeals() == null) {
                        return Collections.<Ingredient>emptyList();
                    }

                    return response.getMeals().stream()
                            .limit(20)
                            .map(IngredientMapper::mapDtoToDomain)
                            .collect(Collectors.toList());
                });
    }
}