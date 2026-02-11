package com.rashed.mealify.domain.usecases.meal;

import com.rashed.mealify.common.Result;
import com.rashed.mealify.datasource.meals.remote.dto.CategoriesResponse;
import com.rashed.mealify.datasource.meals.remote.dto.CategoryDetailsDto;
import com.rashed.mealify.domain.mapper.CategoryMapper;
import com.rashed.mealify.domain.model.Category;
import com.rashed.mealify.domain.repository.MealRepository;

import java.util.ArrayList;
import java.util.List;

public class GetCategoriesUseCase {
    private final MealRepository repository;

    public GetCategoriesUseCase(MealRepository repository) {
        this.repository = repository;
    }

    public Result<List<Category>> execute() {
        Result<CategoriesResponse> result = repository.getCategories();
        if (result instanceof Result.Success) {
            CategoriesResponse response = ((Result.Success<CategoriesResponse>) result).data;
            List<Category> list = new ArrayList<>();
            if (response != null && response.getCategories() != null) {
                for (CategoryDetailsDto dto : response.getCategories()) {
                    list.add(CategoryMapper.mapDtoToDomain(dto));
                }
            }
            return new Result.Success<>(list);
        }
        return new Result.Error<>(((Result.Error) result).message, ((Result.Error) result).throwable);
    }
}