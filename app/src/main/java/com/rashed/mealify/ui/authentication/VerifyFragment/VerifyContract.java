package com.rashed.mealify.ui.authentication.VerifyFragment;

public interface VerifyContract {

    interface View {
        void showLoading();
        void hideLoading();
        void showMessage(String message);

        void navigateToLogin();
    }

    interface Presenter {
        void attach(View view);
        void detach();

        void checkVerificationStatus();
        void resendVerificationEmail();
        void logout();
    }
}