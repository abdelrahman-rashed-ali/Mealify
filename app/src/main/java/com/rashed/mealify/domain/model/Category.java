package com.rashed.mealify.domain.model;

public class Category {
    private final String id;
    private final String name;
    private final String thumbUrl;
    private final String description;

    public Category(String id, String name, String thumbUrl, String description) {
        this.id = id;
        this.name = name;
        this.thumbUrl = thumbUrl;
        this.description = description;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getThumbUrl() { return thumbUrl; }
    public String getDescription() { return description; }
}