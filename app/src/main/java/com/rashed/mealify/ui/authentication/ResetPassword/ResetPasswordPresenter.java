package com.rashed.mealify.ui.authentication.ResetPassword;

import android.text.TextUtils;

import com.rashed.mealify.common.Result;
import com.rashed.mealify.domain.usecases.auth.SendPasswordResetUseCase;

public class ResetPasswordPresenter implements ResetPasswordContract.Presenter {

    private ResetPasswordContract.View view;
    private final SendPasswordResetUseCase useCase;

    public ResetPasswordPresenter(SendPasswordResetUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public void attach(ResetPasswordContract.View view) {
        this.view = view;
    }

    @Override
    public void detach() {
        this.view = null;
    }

    @Override
    public void resetPassword(String email) {
        if (view == null) return;

        if (TextUtils.isEmpty(email)) {
            view.setEmailError("Email is required");
            return;
        }

        view.showLoading();
        useCase.execute(email, result -> {
            if (view == null) return;
            view.hideLoading();

            if (result instanceof Result.Success) {
                view.showMessage("Reset link sent to your email.");
                view.navigateBack();
            } else {
                String msg = ((Result.Error<?>) result).message;
                view.showMessage("Failed: " + (msg != null ? msg : "Unknown error"));
            }
        });
    }

    @Override
    public void onBackClicked() {
        if (view != null) view.navigateBack();
    }
}