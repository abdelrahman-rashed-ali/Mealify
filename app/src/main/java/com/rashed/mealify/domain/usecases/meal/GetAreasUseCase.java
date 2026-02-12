package com.rashed.mealify.domain.usecases.meal;

import com.rashed.mealify.domain.mapper.AreasMapper;
import com.rashed.mealify.domain.model.Area;
import com.rashed.mealify.domain.repository.MealRepository;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import io.reactivex.rxjava3.core.Single;

public class GetAreasUseCase {
    private final MealRepository repository;

    public GetAreasUseCase(MealRepository repository) {
        this.repository = repository;
    }

    public Single<List<Area>> execute() {
        return repository.listAreas()
                .map(response -> {
                    if (response.getMeals() == null) return Collections.<Area>emptyList();
                    return response.getMeals().stream()
                            .limit(20)
                            .map(AreasMapper::mapDtoToDomain)
                            .collect(Collectors.toList());
                });
    }
}