package com.rashed.mealify.domain.usecases.meal;

import com.rashed.mealify.domain.mapper.MealMapper;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.repository.MealRepository;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import io.reactivex.rxjava3.core.Single;

public class SearchMealsUseCase {
    private final MealRepository repository;

    public SearchMealsUseCase(MealRepository repository) {
        this.repository = repository;
    }

    public Single<List<Meal>> execute(String query) {
        return repository.searchMealsByName(query)
                .map(response -> {
                    if (response.getMeals() == null) return Collections.<Meal>emptyList();
                    return response.getMeals().stream()
                            .map(MealMapper::mapDtoToDomain)
                            .collect(Collectors.toList());
                });
    }
}