package com.rashed.mealify.domain.usecases.meal;

import com.rashed.mealify.domain.mapper.CategoryMapper;
import com.rashed.mealify.domain.model.Category;
import com.rashed.mealify.domain.repository.MealRepository;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import io.reactivex.rxjava3.core.Single;

public class GetCategoriesUseCase {
    private final MealRepository repository;

    public GetCategoriesUseCase(MealRepository repository) {
        this.repository = repository;
    }

    public Single<List<Category>> execute() {
        return repository.getCategories()
                .map(response -> {
                    if (response.getCategories() == null) return Collections.<Category>emptyList();
                    return response.getCategories().stream()
                            .map(CategoryMapper::mapDtoToDomain)
                            .collect(Collectors.toList());
                });
    }
}