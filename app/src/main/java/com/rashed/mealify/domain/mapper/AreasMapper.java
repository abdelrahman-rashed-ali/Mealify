package com.rashed.mealify.domain.mapper;

import com.rashed.mealify.datasource.meals.remote.dto.AreaDto;
import com.rashed.mealify.datasource.meals.remote.dto.IngredientDto;
import com.rashed.mealify.domain.model.Area;
import com.rashed.mealify.domain.model.Ingredient;

public class AreasMapper {
    public static Area mapDtoToDomain(AreaDto dto) {
        if (dto == null) return null;
        return new Area(
                dto.getAreaName()
        );
    }
}
