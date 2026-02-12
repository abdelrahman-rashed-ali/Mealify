package com.rashed.mealify.domain.usecases.meal;

import com.rashed.mealify.domain.mapper.MealMapper;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.repository.MealRepository;
import io.reactivex.rxjava3.core.Single;

public class GetRandomMealUseCase {
    private final MealRepository repository;

    public GetRandomMealUseCase(MealRepository repository) {
        this.repository = repository;
    }

    public Single<Meal> execute() {
        return repository.getRandomMeal()
                .map(response -> {
                    if (response.getMeals() == null || response.getMeals().isEmpty()) {
                        throw new Exception("No meal found");
                    }
                    return MealMapper.mapDtoToDomain(response.getMeals().get(0));
                });
    }
}