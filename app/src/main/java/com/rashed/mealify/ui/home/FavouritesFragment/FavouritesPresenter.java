package com.rashed.mealify.ui.home.FavouritesFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.usecases.meal.GetFavoritesUseCase;
import com.rashed.mealify.domain.usecases.meal.ToggleFavoriteUseCase;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FavouritesPresenter implements FavouritesContract.Presenter {

    private FavouritesContract.View view;
    private final GetFavoritesUseCase getFavoritesUseCase;
    private final ToggleFavoriteUseCase toggleFavoriteUseCase;

    private final CompositeDisposable disposables = new CompositeDisposable();
    private String currentUserId;

    public FavouritesPresenter(GetFavoritesUseCase getFavoritesUseCase, ToggleFavoriteUseCase toggleFavoriteUseCase) {
        this.getFavoritesUseCase = getFavoritesUseCase;
        this.toggleFavoriteUseCase = toggleFavoriteUseCase;

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            currentUserId = "";
        }
    }

    @Override
    public void attach(FavouritesContract.View view) {
        this.view = view;
        loadFavorites();
    }

    @Override
    public void detach() {
        this.view = null;
        disposables.clear();
    }

    @Override
    public void loadFavorites() {
        if (view == null) return;

        if (currentUserId.isEmpty()) {
            view.showErrorState("User not logged in");
            return;
        }

        view.showLoading();

        disposables.add(getFavoritesUseCase.execute(currentUserId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        data -> {
                            view.hideLoading();
                            if (data == null || data.isEmpty()) {
                                view.showEmptyState();
                            } else {
                                view.showFavorites(data);
                            }
                        },
                        throwable -> {
                            view.hideLoading();
                            view.showErrorState(throwable.getMessage());
                        }
                ));
    }

    @Override
    public void onMealClicked(Meal meal) {
        if (view != null) view.navigateToDetails(meal);
    }

    @Override
    public void deleteMeal(Meal meal, int position) {
        // ToggleFavoriteUseCase now returns a Completable
        disposables.add(toggleFavoriteUseCase.execute(currentUserId, meal, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            if (view != null) view.showDeleteConfirmation(meal, position);
                        },
                        throwable -> {
                            if (view != null) view.showErrorState("Failed to remove: " + throwable.getMessage());
                        }
                ));
    }

    @Override
    public void undoDelete(Meal meal) {
        disposables.add(toggleFavoriteUseCase.execute(currentUserId, meal, false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::loadFavorites, // Reload list on success
                        throwable -> {
                            if (view != null) view.showErrorState("Failed to undo: " + throwable.getMessage());
                        }
                ));
    }
}