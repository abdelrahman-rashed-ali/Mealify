package com.rashed.mealify.ui.authentication.ResetPassword;

public interface ResetPasswordContract {

    interface View {
        void showLoading();
        void hideLoading();
        void showMessage(String message);

        void navigateBack();

        void setEmailError(String error);
    }

    interface Presenter {
        void attach(View view);
        void detach();

        void resetPassword(String email);
        void onBackClicked();
    }
}