package com.rashed.mealify.ui.authentication.LoginFragment;

import android.content.Intent;

public interface LoginContract {

    interface View {
        void showLoading();
        void hideLoading();
        void showMessage(String message);

        void navigateToHome();
        void navigateToRegister();
        void navigateToForgotPassword();
        void navigateToVerifyEmail();

        void launchGoogleSignIn(Intent signInIntent);

        void setEmailError(String error);
        void setPasswordError(String error);
    }

    interface Presenter {
        void attach(View view);
        void detach();

        void login(String email, String password);
        void loginGuest();
        void onGoogleSignInClicked();
        void handleGoogleResult(Intent data);

        void onRegisterClicked();
        void onForgotPasswordClicked();
    }
}