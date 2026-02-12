package com.rashed.mealify.ui.authentication.ResetPassword;

import android.text.TextUtils;
import com.rashed.mealify.domain.usecases.auth.SendPasswordResetUseCase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ResetPasswordPresenter implements ResetPasswordContract.Presenter {

    private ResetPasswordContract.View view;
    private final SendPasswordResetUseCase useCase;
    private final CompositeDisposable disposables = new CompositeDisposable();

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
        disposables.clear();
    }

    @Override
    public void resetPassword(String email) {
        if (view == null) return;
        if (TextUtils.isEmpty(email)) {
            view.setEmailError("Email is required");
            return;
        }

        view.showLoading();
        disposables.add(useCase.execute(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    view.hideLoading();
                    view.showMessage("Reset link sent to your email.");
                    view.navigateBack();
                }, throwable -> {
                    view.hideLoading();
                    view.showMessage("Failed: " + throwable.getMessage());
                }));
    }

    @Override
    public void onBackClicked() {
        if (view != null) view.navigateBack();
    }
}