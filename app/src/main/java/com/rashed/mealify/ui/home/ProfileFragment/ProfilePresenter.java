package com.rashed.mealify.ui.home.ProfileFragment;

import com.rashed.mealify.common.Result;
import com.rashed.mealify.domain.model.AuthUser;
import com.rashed.mealify.domain.usecases.auth.GetCurrentUserUseCase;
import com.rashed.mealify.domain.usecases.auth.LogoutUseCase;

public class ProfilePresenter implements ProfileContract.Presenter {

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

        getCurrentUserUseCase.execute(result -> {
            if (view == null) return;
            if (result instanceof Result.Success) {
                AuthUser user = ((Result.Success<AuthUser>) result).data;
                view.showUserName(user.firstName + " " + user.lastName);
            } else {
                view.showUserName("Guest");
            }
        });
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