package com.rashed.mealify.ui.home.ProfileFragment;

import com.rashed.mealify.common.Result;
import com.rashed.mealify.domain.model.AuthUser;
import com.rashed.mealify.domain.usecases.auth.GetCurrentUserUseCase;
import com.rashed.mealify.domain.usecases.auth.LogoutUseCase;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProfilePresenter implements ProfileContract.Presenter {
    private final CompositeDisposable disposables = new CompositeDisposable();

    private ProfileContract.View view;
    private final LogoutUseCase logoutUseCase;
    private GetCurrentUserUseCase getCurrentUserUseCase;

    public ProfilePresenter(LogoutUseCase logoutUseCase, GetCurrentUserUseCase getCurrentUserUseCase) {
        this.logoutUseCase = logoutUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }

    @Override
    public void attach(ProfileContract.View view) {
        this.view = view;
    }

    @Override
    public void detach() {
        this.view = null;
    }

    @Override
    public void loadUserProfile() {
        if (view == null) return;

        disposables.add(getCurrentUserUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        user -> {
                            if (view != null) {
                                view.showUserName(user.firstName + " " + user.lastName);
                            }
                        },
                        throwable -> {
                            if (view != null) {
                                view.showUserName("Guest");
                            }
                        }
                ));
    }

    @Override
    public void logout() {
        if (view == null) return;
        view.showLoading();

        logoutUseCase.execute();

        view.hideLoading();
        view.navigateToAuth();
    }

    @Override
    public void onSyncClicked() {
        if (view != null) {
            view.showMessage("Sync feature coming soon");
        }
    }
}