package com.rashed.mealify.domain.usecases.meal;

import com.rashed.mealify.domain.mapper.MealMapper;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.repository.MealRepository;
import java.util.List;
import java.util.stream.Collectors;
import io.reactivex.rxjava3.core.Single;

public class GetFavoritesUseCase {
    private final MealRepository repository;

    public GetFavoritesUseCase(MealRepository repository) {
        this.repository = repository;
    }

    public Single<List<Meal>> execute(String uid) {
        return repository.getFavorites(uid)
                .map(entities -> entities.stream()
                        .map(MealMapper::mapEntityToDomain)
                        .collect(Collectors.toList()));
    }
}