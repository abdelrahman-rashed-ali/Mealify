package com.rashed.mealify.domain.usecases.meal;

import com.rashed.mealify.domain.mapper.MealMapper;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.repository.MealRepository;
import io.reactivex.rxjava3.core.Single;

public class GetMealDetailsUseCase {
    private final MealRepository repository;

    public GetMealDetailsUseCase(MealRepository repository) {
        this.repository = repository;
    }

    public Single<Meal> execute(String mealId) {
        return repository.lookupMealById(mealId)
                .map(response -> {
                    if (response.getMeals() == null || response.getMeals().isEmpty()) {
                        throw new Exception("Meal not found");
                    }
                    return MealMapper.mapDtoToDomain(response.getMeals().get(0));
                });
    }
}