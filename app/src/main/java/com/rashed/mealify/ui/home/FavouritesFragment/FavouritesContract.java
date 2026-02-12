package com.rashed.mealify.ui.home.FavouritesFragment;

import com.rashed.mealify.domain.model.Meal;
import java.util.List;

public interface FavouritesContract {

    interface View {
        void showLoading();
        void hideLoading();
        void showFavorites(List<Meal> meals);
        void showEmptyState();
        void showErrorState(String message);
        void navigateToDetails(Meal meal);
        void showDeleteConfirmation(Meal meal, int position);
    }

    interface Presenter {
        void attach(View view);
        void detach();
        void loadFavorites();
        void onMealClicked(Meal meal);
        void deleteMeal(Meal meal, int position);
        void undoDelete(Meal meal);
    }
}