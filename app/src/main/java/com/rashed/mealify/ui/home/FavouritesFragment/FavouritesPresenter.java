package com.rashed.mealify.ui.home.FavouritesFragment;

import android.os.Handler;
import android.os.Looper;

import com.google.firebase.auth.FirebaseAuth;
import com.rashed.mealify.common.Result;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.usecases.meal.GetFavoritesUseCase;
import com.rashed.mealify.domain.usecases.meal.ToggleFavoriteUseCase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavouritesPresenter implements FavouritesContract.Presenter {

    private FavouritesContract.View view;
    private final GetFavoritesUseCase getFavoritesUseCase;
    private final ToggleFavoriteUseCase toggleFavoriteUseCase;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private String currentUserId;

    public FavouritesPresenter(GetFavoritesUseCase getFavoritesUseCase, ToggleFavoriteUseCase toggleFavoriteUseCase) {
        this.getFavoritesUseCase = getFavoritesUseCase;
        this.toggleFavoriteUseCase = toggleFavoriteUseCase;

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            currentUserId = "current_user_id";
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
    }

    @Override
    public void loadFavorites() {
        if (view != null) view.showLoading();

        executor.execute(() -> {
            if (currentUserId.isEmpty()) {
                mainHandler.post(() -> {
                    if (view != null) view.showErrorState("User not logged in");
                });
                return;
            }

            Result<List<Meal>> result = getFavoritesUseCase.execute(currentUserId);

            mainHandler.post(() -> {
                if (view == null) return;
                view.hideLoading();

                if (result instanceof Result.Success) {
                    List<Meal> data = ((Result.Success<List<Meal>>) result).data;
                    if (data == null || data.isEmpty()) {
                        view.showEmptyState();
                    } else {
                        view.showFavorites(data);
                    }
                } else {
                    view.showErrorState(((Result.Error) result).message);
                }
            });
        });
    }

    @Override
    public void onMealClicked(Meal meal) {
        if (view != null) view.navigateToDetails(meal);
    }

    @Override
    public void deleteMeal(Meal meal, int position) {
        toggleFavoriteUseCase.execute(currentUserId, meal, true);
        if (view != null) {
            view.showDeleteConfirmation(meal, position);
        }
    }

    @Override
    public void undoDelete(Meal meal) {
        toggleFavoriteUseCase.execute(currentUserId, meal, false);
        loadFavorites();
    }
}