package com.rashed.mealify.ui.authentication.LoginFragment;

import android.content.Intent;
import android.text.TextUtils;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.rashed.mealify.common.Result;
import com.rashed.mealify.domain.model.AuthUser;
import com.rashed.mealify.domain.usecases.auth.LoginAnonymouslyUseCase;
import com.rashed.mealify.domain.usecases.auth.LoginUseCase;
import com.rashed.mealify.domain.usecases.auth.LoginWithGoogleUseCase;

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;
    private final LoginUseCase loginUseCase;
    private final LoginWithGoogleUseCase loginWithGoogleUseCase;
    private final LoginAnonymouslyUseCase loginAnonymouslyUseCase;
    private final GoogleSignInClient googleClient;

    public LoginPresenter(LoginUseCase loginUseCase,
                          LoginWithGoogleUseCase loginWithGoogleUseCase,
                          LoginAnonymouslyUseCase loginAnonymouslyUseCase,
                          GoogleSignInClient googleClient) {
        this.loginUseCase = loginUseCase;
        this.loginWithGoogleUseCase = loginWithGoogleUseCase;
        this.loginAnonymouslyUseCase = loginAnonymouslyUseCase;
        this.googleClient = googleClient;
    }

    @Override
    public void attach(LoginContract.View view) {
        this.view = view;
    }

    @Override
    public void detach() {
        this.view = null;
    }

    @Override
    public void login(String email, String password) {
        if (view == null) return;

        boolean isValid = true;
        if (TextUtils.isEmpty(email)) {
            view.setEmailError("Email is required");
            isValid = false;
        }
        if (TextUtils.isEmpty(password)) {
            view.setPasswordError("Password is required");
            isValid = false;
        }

        if (!isValid) return;

        view.showLoading();
        loginUseCase.execute(email, password, result -> {
            if (view == null) return;
            view.hideLoading();
            handleAuthResult(result);
        });
    }

    @Override
    public void loginGuest() {
        if (view == null) return;
        view.showLoading();

        loginAnonymouslyUseCase.execute(result -> {
            if (view == null) return;
            view.hideLoading();

            if (result instanceof Result.Success) {
                view.showMessage("Welcome Guest");
                view.navigateToHome();
            } else {
                String msg = ((Result.Error<?>) result).message;
                view.showMessage(msg != null ? msg : "Guest login failed");
            }
        });
    }

    @Override
    public void onGoogleSignInClicked() {
        if (view == null) return;
        view.showLoading();
        googleClient.signOut();
        view.launchGoogleSignIn(googleClient.getSignInIntent());
    }

    @Override
    public void handleGoogleResult(Intent data) {
        if (view == null) return;

        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = task.getResult(ApiException.class);

            if (account == null || account.getIdToken() == null) {
                view.hideLoading();
                view.showMessage("Google Sign-In failed.");
                return;
            }

            loginWithGoogleUseCase.execute(account.getIdToken(), result -> {
                if (view == null) return;
                view.hideLoading();
                handleAuthResult(result);
            });

        } catch (ApiException e) {
            view.hideLoading();
            view.showMessage("Google Sign-In error: " + e.getStatusCode());
        }
    }

    private void handleAuthResult(Result<AuthUser> result) {
        if (result instanceof Result.Success) {
            AuthUser user = ((Result.Success<AuthUser>) result).data;
            view.showMessage("Welcome " + user.firstName);
            view.navigateToHome();
        } else if (result instanceof Result.Error) {
            Result.Error<AuthUser> error = (Result.Error<AuthUser>) result;
            if (error.throwable != null && "UNVERIFIED".equals(error.throwable.getMessage())) {
                view.navigateToVerifyEmail();
            } else {
                view.showMessage(error.message != null ? error.message : "Authentication failed");
            }
        }
    }

    @Override
    public void onRegisterClicked() {
        if (view != null) view.navigateToRegister();
    }

    @Override
    public void onForgotPasswordClicked() {
        if (view != null) view.navigateToForgotPassword();
    }
}