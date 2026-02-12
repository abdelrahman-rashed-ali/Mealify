package com.rashed.mealify.domain.mapper;

import com.rashed.mealify.datasource.meals.remote.dto.CategoryDetailsDto;
import com.rashed.mealify.domain.model.Category;

public class CategoryMapper {
    public static Category mapDtoToDomain(CategoryDetailsDto dto) {
        if (dto == null) return null;
        return new Category(
                dto.getIdCategory(),
                dto.getName(),
                dto.getThumbUrl(),
                dto.getDescription()
        );
    }
}