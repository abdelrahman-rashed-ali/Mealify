package com.rashed.mealify.domain.mapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rashed.mealify.datasource.meals.local.entities.MealEntity;
import com.rashed.mealify.datasource.meals.remote.dto.Meals;
import com.rashed.mealify.domain.model.Meal;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MealMapper {

    private static final Gson gson = new Gson();

    public static Meal mapDtoToDomain(Meals dto) {
        if (dto == null) return null;

        List<Meal.Ingredient> ingredients = extractIngredientsFromDto(dto);
        List<String> tags = (dto.getStrTags() != null && !dto.getStrTags().isEmpty())
                ? Arrays.asList(dto.getStrTags().split(","))
                : new ArrayList<>();

        return new Meal(
                dto.getIdMeal(),
                dto.getStrMeal(),
                dto.getStrCategory(),
                dto.getStrArea(),
                dto.getStrInstructions(),
                dto.getStrMealThumb(),
                dto.getStrYoutube(),
                dto.getStrSource(),
                tags,
                ingredients
        );
    }

    public static Meal mapEntityToDomain(MealEntity entity) {
        if (entity == null) return null;

        Type listType = new TypeToken<List<Meal.Ingredient>>() {}.getType();
        List<Meal.Ingredient> ingredients = gson.fromJson(entity.ingredientsJson, listType);
        if (ingredients == null) ingredients = new ArrayList<>();

        List<String> tags = (entity.strTags != null && !entity.strTags.isEmpty())
                ? Arrays.asList(entity.strTags.split(","))
                : new ArrayList<>();

        return new Meal(
                entity.idMeal,
                entity.strMeal,
                entity.strCategory,
                entity.strArea,
                entity.strInstructions,
                entity.strMealThumb,
                entity.strYoutube,
                entity.strSource,
                tags,
                ingredients
        );
    }

    public static MealEntity mapDomainToEntity(Meal meal) {
        if (meal == null) return null;

        String ingredientsJson = gson.toJson(meal.getIngredients());
        String tagsString = (meal.getTags() != null) ? String.join(",", meal.getTags()) : "";

        return new MealEntity(
                meal.getId(),
                meal.getName(),
                null,
                meal.getCategory(),
                meal.getArea(),
                meal.getInstructions(),
                meal.getThumbUrl(),
                tagsString,
                meal.getYoutubeUrl(),
                ingredientsJson,
                meal.getSourceUrl(),
                null,
                null,
                null,
                System.currentTimeMillis()
        );
    }

    private static List<Meal.Ingredient> extractIngredientsFromDto(Meals dto) {
        List<Meal.Ingredient> list = new ArrayList<>();
        addIfValid(list, dto.getStrIngredient1(), dto.getStrMeasure1());
        addIfValid(list, dto.getStrIngredient2(), dto.getStrMeasure2());
        addIfValid(list, dto.getStrIngredient3(), dto.getStrMeasure3());
        addIfValid(list, dto.getStrIngredient4(), dto.getStrMeasure4());
        addIfValid(list, dto.getStrIngredient5(), dto.getStrMeasure5());
        addIfValid(list, dto.getStrIngredient6(), dto.getStrMeasure6());
        addIfValid(list, dto.getStrIngredient7(), dto.getStrMeasure7());
        addIfValid(list, dto.getStrIngredient8(), dto.getStrMeasure8());
        addIfValid(list, dto.getStrIngredient9(), dto.getStrMeasure9());
        addIfValid(list, dto.getStrIngredient10(), dto.getStrMeasure10());
        addIfValid(list, dto.getStrIngredient11(), dto.getStrMeasure11());
        addIfValid(list, dto.getStrIngredient12(), dto.getStrMeasure12());
        addIfValid(list, dto.getStrIngredient13(), dto.getStrMeasure13());
        addIfValid(list, dto.getStrIngredient14(), dto.getStrMeasure14());
        addIfValid(list, dto.getStrIngredient15(), dto.getStrMeasure15());
        addIfValid(list, dto.getStrIngredient16(), dto.getStrMeasure16());
        addIfValid(list, dto.getStrIngredient17(), dto.getStrMeasure17());
        addIfValid(list, dto.getStrIngredient18(), dto.getStrMeasure18());
        addIfValid(list, dto.getStrIngredient19(), dto.getStrMeasure19());
        addIfValid(list, dto.getStrIngredient20(), dto.getStrMeasure20());
        return list;
    }

    private static void addIfValid(List<Meal.Ingredient> list, String ing, String measure) {
        if (ing != null && !ing.trim().isEmpty()) {
            list.add(new Meal.Ingredient(ing, measure));
        }
    }
}