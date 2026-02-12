package com.rashed.mealify.ui.authentication.RegisterFragment;

import android.text.TextUtils;
import com.rashed.mealify.domain.usecases.auth.RegisterUseCase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RegisterPresenter implements RegisterContract.Presenter {

    private RegisterContract.View view;
    private final RegisterUseCase registerUseCase;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public RegisterPresenter(RegisterUseCase registerUseCase) {
        this.registerUseCase = registerUseCase;
    }

    @Override
    public void attach(RegisterContract.View view) {
        this.view = view;
    }

    @Override
    public void detach() {
        this.view = null;
        disposables.clear();
    }

    @Override
    public void register(String firstName, String lastName, String email, String password, String confirmPassword) {
        if (view == null) return;

        boolean isValid = true;
        if (TextUtils.isEmpty(firstName)) { view.setFirstNameError("First Name is required"); isValid = false; }
        if (TextUtils.isEmpty(lastName)) { view.setLastNameError("Last Name is required"); isValid = false; }
        if (TextUtils.isEmpty(email)) { view.setEmailError("Email is required"); isValid = false; }
        if (TextUtils.isEmpty(password)) { view.setPasswordError("Password is required"); isValid = false; }
        if (TextUtils.isEmpty(confirmPassword)) { view.setConfirmPasswordError("Please confirm password"); isValid = false; }
        if (!password.equals(confirmPassword)) { view.setConfirmPasswordError("Passwords do not match"); isValid = false; }

        if (!isValid) return;

        view.showLoading();
        disposables.add(registerUseCase.execute(email, password, firstName, lastName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    view.hideLoading();
                    view.navigateToVerifyEmail();
                }, throwable -> {
                    view.hideLoading();
                    view.showMessage(throwable.getMessage());
                }));
    }

    @Override
    public void onLoginClicked() {
        if (view != null) view.navigateToLogin();
    }
}