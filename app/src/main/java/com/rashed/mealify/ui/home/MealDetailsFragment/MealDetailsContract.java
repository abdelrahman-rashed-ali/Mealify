package com.rashed.mealify.ui.home.MealDetailsFragment;

import com.rashed.mealify.domain.model.Meal;

public interface MealDetailsContract {

    interface View {
        void showLoading();
        void hideLoading();
        void displayMealDetails(Meal meal);
        void updateFavoriteIcon(boolean isFavorite);
        void showMessage(String message);
        void showPlanDatePicker();
        void showMealTypeDialog(long dateSelection);
        void showGuestModeDialog();
    }

    interface Presenter {
        void attach(View view);
        void detach();
        void initMealData(Meal meal);
        void toggleFavorite();
        void addToPlan(long date, String mealType);
    }
}