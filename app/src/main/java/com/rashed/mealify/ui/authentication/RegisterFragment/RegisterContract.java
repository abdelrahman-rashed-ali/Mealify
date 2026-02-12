package com.rashed.mealify.ui.authentication.RegisterFragment;

public interface RegisterContract {

    interface View {
        void showLoading();
        void hideLoading();
        void showMessage(String message);

        void navigateToLogin();
        void navigateToVerifyEmail();

        // Input Errors
        void setFirstNameError(String error);
        void setLastNameError(String error);
        void setEmailError(String error);
        void setPasswordError(String error);
        void setConfirmPasswordError(String error);
    }

    interface Presenter {
        void attach(View view);
        void detach();

        void register(String firstName, String lastName, String email, String password, String confirmPassword);
        void onLoginClicked();
    }
}