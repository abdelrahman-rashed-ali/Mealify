package com.rashed.mealify.ui.authentication.LoginFragment;

import android.content.Intent;
import android.text.TextUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.rashed.mealify.domain.model.AuthUser;
import com.rashed.mealify.domain.usecases.auth.LoginAnonymouslyUseCase;
import com.rashed.mealify.domain.usecases.auth.LoginUseCase;
import com.rashed.mealify.domain.usecases.auth.LoginWithGoogleUseCase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;
    private final LoginUseCase loginUseCase;
    private final LoginWithGoogleUseCase loginWithGoogleUseCase;
    private final LoginAnonymouslyUseCase loginAnonymouslyUseCase;
    private final GoogleSignInClient googleClient;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public LoginPresenter(LoginUseCase loginUseCase, LoginWithGoogleUseCase loginWithGoogleUseCase,
                          LoginAnonymouslyUseCase loginAnonymouslyUseCase, GoogleSignInClient googleClient) {
        this.loginUseCase = loginUseCase;
        this.loginWithGoogleUseCase = loginWithGoogleUseCase;
        this.loginAnonymouslyUseCase = loginAnonymouslyUseCase;
        this.googleClient = googleClient;
    }

    @Override
    public void attach(LoginContract.View view) { this.view = view; }

    @Override
    public void detach() {
        this.view = null;
        disposables.clear();
    }

    @Override
    public void login(String email, String password) {
        if (view == null) return;
        if (TextUtils.isEmpty(email)) { view.setEmailError("Email is required"); return; }
        if (TextUtils.isEmpty(password)) { view.setPasswordError("Password is required"); return; }

        view.showLoading();
        disposables.add(loginUseCase.execute(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    view.hideLoading();
                    view.showMessage("Welcome " + user.firstName);
                    view.navigateToHome();
                }, throwable -> {
                    view.hideLoading();
                    if ("UNVERIFIED".equals(throwable.getMessage())) view.navigateToVerifyEmail();
                    else view.showMessage(throwable.getMessage());
                }));
    }

    @Override
    public void loginGuest() {
        if (view == null) return;
        view.showLoading();
        disposables.add(loginAnonymouslyUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    view.hideLoading();
                    view.navigateToHome();
                }, throwable -> {
                    view.hideLoading();
                    view.showMessage(throwable.getMessage());
                }));
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
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null && account.getIdToken() != null) {
                disposables.add(loginWithGoogleUseCase.execute(account.getIdToken())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(user -> {
                            view.hideLoading();
                            view.navigateToHome();
                        }, throwable -> {
                            view.hideLoading();
                            view.showMessage(throwable.getMessage());
                        }));
            }
        } catch (ApiException e) {
            view.hideLoading();
            view.showMessage("Error: " + e.getStatusCode());
        }
    }

    @Override public void onRegisterClicked() { if (view != null) view.navigateToRegister(); }
    @Override public void onForgotPasswordClicked() { if (view != null) view.navigateToForgotPassword(); }
}