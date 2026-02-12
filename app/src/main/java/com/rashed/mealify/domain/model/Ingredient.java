package com.rashed.mealify.domain.model;

public class Ingredient {
    private final String id;
    private final String name;
    private final String description;
    private final String thumbUrl;

    public Ingredient(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbUrl = "https://www.themealdb.com/images/ingredients/" + name + "-Small.png";
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getThumbUrl() { return thumbUrl; }
}