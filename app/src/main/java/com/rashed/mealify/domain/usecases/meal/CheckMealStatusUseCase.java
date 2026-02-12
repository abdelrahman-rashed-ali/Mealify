package com.rashed.mealify.domain.usecases.meal;

import com.rashed.mealify.domain.repository.MealRepository;
import io.reactivex.rxjava3.core.Single;

public class CheckMealStatusUseCase {
    private final MealRepository repository;

    public CheckMealStatusUseCase(MealRepository repository) {
        this.repository = repository;
    }

    public Single<Boolean> isFavorite(String uid, String mealId) {
        return repository.isFavorite(uid, mealId);
    }
}