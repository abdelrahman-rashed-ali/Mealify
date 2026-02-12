package com.rashed.mealify.ui.home.SearchFragment;

import com.rashed.mealify.domain.model.Category;
import com.rashed.mealify.domain.model.Ingredient;
import com.rashed.mealify.domain.model.Meal;
import java.util.List;

public interface SearchContract {

    interface View {
        void showLoading();
        void hideLoading();
        void showResults(List<Meal> meals);
        void showEmptyState();
        void showErrorState(String message);

        void navigateToDetails(Meal meal);

        void populateCategories(List<Category> categories);
        void populateAreas(List<String> areas); // Assuming Area is String for simplicity
        void populateIngredients(List<Ingredient> ingredients);
    }

    interface Presenter {
        void attach(View view);
        void detach();

        void search(String query);
        void toggleCategoryFilter(String category);
        void toggleAreaFilter(String area);
        void toggleIngredientFilter(String ingredient);
        void onMealClicked(Meal meal);
        void clearFilters();
    }
}