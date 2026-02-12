package com.rashed.mealify.domain.mapper;

import com.rashed.mealify.datasource.meals.remote.dto.IngredientDto;
import com.rashed.mealify.domain.model.Ingredient;

public class IngredientMapper {
    public static Ingredient mapDtoToDomain(IngredientDto dto) {
        if (dto == null) return null;
        return new Ingredient(
                dto.getIdIngredient(),
                dto.getName(),
                dto.getDescription()
        );
    }
}