package com.rashed.mealify.datasource.meals.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "meals")
public class MealEntity {
    @PrimaryKey @NonNull
    public String idMeal;
    public String strMeal;
    public String strMealAlternate;
    public String strCategory;
    public String strArea;
    public String strInstructions;
    public String strMealThumb;
    public String strTags;
    public String strYoutube;

    // JSON: [{ingredient:"", measure:""}...]
    public String ingredientsJson;

    public String strSource;
    public String strImageSource;
    public String strCreativeCommonsConfirmed;
    public String dateModified;

    public long cachedAt;

    public MealEntity(@NonNull String idMeal, String strMeal, String strMealAlternate,
                      String strCategory, String strArea, String strInstructions,
                      String strMealThumb, String strTags, String strYoutube,
                      String ingredientsJson, String strSource, String strImageSource,
                      String strCreativeCommonsConfirmed, String dateModified,
                      long cachedAt) {
        this.idMeal = idMeal;
        this.strMeal = strMeal;
        this.strMealAlternate = strMealAlternate;
        this.strCategory = strCategory;
        this.strArea = strArea;
        this.strInstructions = strInstructions;
        this.strMealThumb = strMealThumb;
        this.strTags = strTags;
        this.strYoutube = strYoutube;
        this.ingredientsJson = ingredientsJson;
        this.strSource = strSource;
        this.strImageSource = strImageSource;
        this.strCreativeCommonsConfirmed = strCreativeCommonsConfirmed;
        this.dateModified = dateModified;
        this.cachedAt = cachedAt;
    }
}
