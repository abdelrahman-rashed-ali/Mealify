package com.rashed.mealify.domain.usecases.meal;

import com.rashed.mealify.common.Result;
import com.rashed.mealify.domain.repository.MealRepository;

public class CheckMealStatusUseCase {
    private final MealRepository repository;

    public CheckMealStatusUseCase(MealRepository repository) {
        this.repository = repository;
    }

    public Result<Boolean> isFavorite(String uid, String mealId) {
        return repository.isFavorite(uid, mealId);
    }

}