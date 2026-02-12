package com.rashed.mealify.ui.home.ProfileFragment;

public interface ProfileContract {

    interface View {
        void showLoading();
        void hideLoading();
        void showMessage(String message);
        void showUserName(String name);
        void navigateToAuth();
    }

    interface Presenter {
        void attach(View view);
        void detach();
        void loadUserProfile();
        void logout();
        void onSyncClicked();
    }
}