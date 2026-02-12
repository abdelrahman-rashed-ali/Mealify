package com.rashed.mealify.ui.authentication.VerifyFragment;

import com.rashed.mealify.domain.usecases.auth.CheckEmailVerifiedUseCase;
import com.rashed.mealify.domain.usecases.auth.LogoutUseCase;
import com.rashed.mealify.domain.usecases.auth.SendEmailVerificationUseCase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class VerifyPresenter implements VerifyContract.Presenter {

    private VerifyContract.View view;
    private final SendEmailVerificationUseCase sendEmailUseCase;
    private final CheckEmailVerifiedUseCase checkEmailUseCase;
    private final LogoutUseCase logoutUseCase;
    private final CompositeDisposable disposables = new CompositeDisposable();

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
        disposables.clear();
    }

    @Override
    public void checkVerificationStatus() {
        if (view == null) return;
        view.showLoading();

        disposables.add(checkEmailUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isVerified -> {
                    view.hideLoading();
                    if (isVerified) {
                        view.showMessage("Email Verified Successfully!");
                        view.navigateToLogin();
                    } else {
                        view.showMessage("Not yet verified. Please check your email inbox/spam.");
                    }
                }, throwable -> {
                    view.hideLoading();
                    view.showMessage("Error checking status: " + throwable.getMessage());
                }));
    }

    @Override
    public void resendVerificationEmail() {
        if (view == null) return;
        view.showLoading();

        disposables.add(sendEmailUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    view.hideLoading();
                    view.showMessage("Verification email sent!");
                }, throwable -> {
                    view.hideLoading();
                    view.showMessage("Failed to send: " + throwable.getMessage());
                }));
    }

    @Override
    public void logout() {
        logoutUseCase.execute();
        if (view != null) {
            view.navigateToLogin();
        }
    }
}