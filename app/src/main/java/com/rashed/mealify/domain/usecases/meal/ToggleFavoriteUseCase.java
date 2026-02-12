package com.rashed.mealify.domain.usecases.meal;

import com.rashed.mealify.domain.mapper.MealMapper;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.repository.MealRepository;

public class ToggleFavoriteUseCase {
    private final MealRepository repository;

    public ToggleFavoriteUseCase(MealRepository repository) {
        this.repository = repository;
    }

    public void execute(String userId, Meal meal, boolean currentlyFavorite) {
        new Thread(() -> {
            if (currentlyFavorite) {
                repository.removeFavorite(userId, meal.getId());
            } else {
                repository.addFavorite(userId, MealMapper.mapDomainToEntity(meal));
            }
        }).start();
    }
}