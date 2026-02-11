package com.rashed.mealify.domain.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public class Meal implements Parcelable {
    private final String id;
    private final String name;
    private final String category;
    private final String area;
    private final String instructions;
    private final String thumbUrl;
    private final String youtubeUrl;
    private final String sourceUrl;
    private final List<String> tags;
    private final List<Ingredient> ingredients;

    public Meal(String id, String name, String category, String area, String instructions,
                String thumbUrl, String youtubeUrl, String sourceUrl,
                List<String> tags, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.area = area;
        this.instructions = instructions;
        this.thumbUrl = thumbUrl;
        this.youtubeUrl = youtubeUrl;
        this.sourceUrl = sourceUrl;
        this.tags = tags;
        this.ingredients = ingredients;
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getArea() { return area; }
    public String getInstructions() { return instructions; }
    public String getThumbUrl() { return thumbUrl; }
    public String getYoutubeUrl() { return youtubeUrl; }
    public String getSourceUrl() { return sourceUrl; }
    public List<String> getTags() { return tags; }
    public List<Ingredient> getIngredients() { return ingredients; }

    // --- Parcelable Implementation ---

    protected Meal(Parcel in) {
        id = in.readString();
        name = in.readString();
        category = in.readString();
        area = in.readString();
        instructions = in.readString();
        thumbUrl = in.readString();
        youtubeUrl = in.readString();
        sourceUrl = in.readString();
        tags = in.createStringArrayList();
        ingredients = in.createTypedArrayList(Ingredient.CREATOR);
    }

    public static final Creator<Meal> CREATOR = new Creator<Meal>() {
        @Override
        public Meal createFromParcel(Parcel in) {
            return new Meal(in);
        }

        @Override
        public Meal[] newArray(int size) {
            return new Meal[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(area);
        dest.writeString(instructions);
        dest.writeString(thumbUrl);
        dest.writeString(youtubeUrl);
        dest.writeString(sourceUrl);
        dest.writeStringList(tags);
        dest.writeTypedList(ingredients);
    }

    // --- Nested Ingredient Class ---

    public static class Ingredient implements Parcelable {
        public String name;
        public String measure;

        public Ingredient(String name, String measure) {
            this.name = name;
            this.measure = measure;
        }

        protected Ingredient(Parcel in) {
            name = in.readString();
            measure = in.readString();
        }

        public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
            @Override
            public Ingredient createFromParcel(Parcel in) {
                return new Ingredient(in);
            }

            @Override
            public Ingredient[] newArray(int size) {
                return new Ingredient[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeString(measure);
        }
    }
}