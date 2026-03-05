package com.rashed.mealify.ui.home.ProfileFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rashed.mealify.datasource.repository.SyncRepository;
import com.rashed.mealify.domain.usecases.auth.GetCurrentUserUseCase;
import com.rashed.mealify.domain.usecases.auth.LogoutUseCase;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProfilePresenter implements ProfileContract.Presenter {
    private final CompositeDisposable disposables = new CompositeDisposable();

    private ProfileContract.View view;
    private final LogoutUseCase logoutUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final SyncRepository syncRepository;

    public ProfilePresenter(LogoutUseCase logoutUseCase,
                            GetCurrentUserUseCase getCurrentUserUseCase,
                            SyncRepository syncRepository) {
        this.logoutUseCase = logoutUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.syncRepository = syncRepository;
    }

    @Override
    public void attach(ProfileContract.View view) {
        this.view = view;
    }

    @Override
    public void detach() {
        this.view = null;
        disposables.clear();
    }

    @Override
    public void loadUserProfile() {
        if (view == null) return;

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser == null || firebaseUser.isAnonymous()) {
            view.showUserName("Guest");
            view.setGuestMode();
        } else {
            view.setUserMode();

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
                                    view.showUserName("User");
                                }
                            }
                    ));
        }
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
    public void onLoginClicked() {
        if (view != null) {
            logoutUseCase.execute();
            view.navigateToAuth();
        }
    }

    @Override
    public void onSyncClicked() {
        if (view == null) return;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null || user.isAnonymous()) {
            view.showLoginDialog();
            return;
        }

        view.showLoading();

        disposables.add(syncRepository.syncData()
                .subscribeOn(Schedulers.io())
                .timeout(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            if (view != null) {
                                view.hideLoading();
                                view.showMessage("Sync completed successfully!");
                            }
                        },
                        throwable -> {
                            if (view != null) {
                                view.hideLoading();
                                view.showMessage("Sync failed: " + throwable.getMessage());
                            }
                        }

                ));
    }
}