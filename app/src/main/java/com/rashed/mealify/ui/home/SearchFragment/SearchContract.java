package com.rashed.mealify.ui.home.SearchFragment;

import com.rashed.mealify.domain.model.Category;
import com.rashed.mealify.domain.model.Ingredient;
import com.rashed.mealify.domain.model.Meal;

import java.util.List;

public interface SearchContract {

    interface View {
        void populateCategories(List<Category> categories);
        void populateAreas(List<String> areas);
        void populateIngredients(List<Ingredient> ingredients);

        void showLoading();
        void hideLoading();

        void showResults(List<Meal> meals);
        void showEmptyState();
        void showErrorState(String message);

        void navigateToDetails(Meal meal);
    }

    interface Presenter {
        void attach(View view);
        void detach();

        void search(String query);

        void toggleCategoryFilter(String category);
        void toggleAreaFilter(String area);
        void toggleIngredientFilter(String ingredient);

        void clearFilters();

        void onMealClicked(Meal meal);
    }
}
