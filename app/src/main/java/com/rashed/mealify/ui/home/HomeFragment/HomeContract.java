package com.rashed.mealify.ui.home.HomeFragment;

import com.rashed.mealify.domain.model.Category;
import com.rashed.mealify.domain.model.Meal;

import java.util.List;

public interface HomeContract {

    interface View {
        void showLoading();
        void hideLoading();
        void showContent();
        void showGridLoading();
        void hideGridLoading();

        void displayHeroMeal(Meal meal);
        void displayCategories(List<Category> categories);
        void displayCategoryMeals(List<Meal> meals);
        void updateCategoryTitle(String title);

        void navigateToDetails(Meal meal);
        void showErrorMessage(String message);
        void showUserName(String userName);
    }

    interface Presenter {
        void attach(View view);
        void detach();
        void loadInitialData();
        void selectCategory(Category category);
        void onMealClicked(String mealId);
        void onHeroClicked();
    }
}