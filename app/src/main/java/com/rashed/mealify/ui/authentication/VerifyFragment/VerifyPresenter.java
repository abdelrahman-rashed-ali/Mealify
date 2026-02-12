package com.rashed.mealify.ui.authentication.VerifyFragment;

import com.rashed.mealify.common.Result;
import com.rashed.mealify.domain.usecases.auth.CheckEmailVerifiedUseCase;
import com.rashed.mealify.domain.usecases.auth.LogoutUseCase;
import com.rashed.mealify.domain.usecases.auth.SendEmailVerificationUseCase;

public class VerifyPresenter implements VerifyContract.Presenter {

    private VerifyContract.View view;
    private final SendEmailVerificationUseCase sendEmailUseCase;
    private final CheckEmailVerifiedUseCase checkEmailUseCase;
    private final LogoutUseCase logoutUseCase;

    public VerifyPresenter(SendEmailVerificationUseCase sendEmailUseCase,
                           CheckEmailVerifiedUseCase checkEmailUseCase,
                           LogoutUseCase logoutUseCase) {
        this.sendEmailUseCase = sendEmailUseCase;
        this.checkEmailUseCase = checkEmailUseCase;
        this.logoutUseCase = logoutUseCase;
    }

    @Override
    public void attach(VerifyContract.View view) {
        this.view = view;
    }

    @Override
    public void detach() {
        this.view = null;
    }

    @Override
    public void checkVerificationStatus() {
        if (view == null) return;
        view.showLoading();

        checkEmailUseCase.execute(result -> {
            if (view == null) return;
            view.hideLoading();

            if (result instanceof Result.Success) {
                boolean isVerified = ((Result.Success<Boolean>) result).data;
                if (isVerified) {
                    view.showMessage("Email Verified Successfully!");
                    view.navigateToLogin();
                } else {
                    view.showMessage("Not yet verified. Please check your email inbox/spam.");
                }
            } else {
                String msg = ((Result.Error<?>) result).message;
                view.showMessage("Error checking status: " + (msg != null ? msg : "Unknown"));
            }
        });
    }

    @Override
    public void resendVerificationEmail() {
        if (view == null) return;
        view.showLoading();

        sendEmailUseCase.execute(result -> {
            if (view == null) return;
            view.hideLoading();

            if (result instanceof Result.Success) {
                view.showMessage("Verification email sent!");
            } else {
                String msg = ((Result.Error<?>) result).message;
                view.showMessage("Failed to send: " + (msg != null ? msg : "Unknown error"));
            }
        });
    }

    @Override
    public void logout() {
        logoutUseCase.execute();
        if (view != null) {
            view.navigateToLogin();
        }
    }
}