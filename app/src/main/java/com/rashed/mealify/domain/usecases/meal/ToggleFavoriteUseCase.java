package com.rashed.mealify.domain.usecases.meal;

import com.rashed.mealify.domain.mapper.MealMapper;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.repository.MealRepository;
import io.reactivex.rxjava3.core.Completable;

public class ToggleFavoriteUseCase {
    private final MealRepository repository;

    public ToggleFavoriteUseCase(MealRepository repository) {
        this.repository = repository;
    }

    public Completable execute(String userId, Meal meal, boolean currentlyFavorite) {
        if (currentlyFavorite) {
            return repository.removeFavorite(userId, meal.getId());
        } else {
            return repository.addFavorite(userId, MealMapper.mapDomainToEntity(meal));
        }
    }
}