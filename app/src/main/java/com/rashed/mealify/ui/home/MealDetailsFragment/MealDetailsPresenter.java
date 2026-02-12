package com.rashed.mealify.ui.home.MealDetailsFragment;

import android.os.Handler;
import android.os.Looper;

import com.google.firebase.auth.FirebaseAuth;
import com.rashed.mealify.common.Result;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.usecases.meal.CheckMealStatusUseCase;
import com.rashed.mealify.domain.usecases.meal.ManagePlanUseCase;
import com.rashed.mealify.domain.usecases.meal.ToggleFavoriteUseCase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MealDetailsPresenter implements MealDetailsContract.Presenter {

    private MealDetailsContract.View view;
    private final CheckMealStatusUseCase checkMealStatusUseCase;
    private final ToggleFavoriteUseCase toggleFavoriteUseCase;
    private final ManagePlanUseCase managePlanUseCase;

    private Meal currentMeal;
    private String userId;
    private boolean isFavorite = false;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public MealDetailsPresenter(CheckMealStatusUseCase checkMealStatusUseCase,
                                ToggleFavoriteUseCase toggleFavoriteUseCase,
                                ManagePlanUseCase managePlanUseCase) {
        this.checkMealStatusUseCase = checkMealStatusUseCase;
        this.toggleFavoriteUseCase = toggleFavoriteUseCase;
        this.managePlanUseCase = managePlanUseCase;

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            userId = "guest_user";
        }
    }

    @Override
    public void attach(MealDetailsContract.View view) {
        this.view = view;
    }

    @Override
    public void detach() {
        this.view = null;
    }

    @Override
    public void initMealData(Meal meal) {
        if (view != null) view.showLoading();

        this.currentMeal = meal;

        if (view != null) {
            view.displayMealDetails(meal);
            checkFavoriteStatus();
        }
    }

    private void checkFavoriteStatus() {
        if (currentMeal == null) return;

        executor.execute(() -> {
            Result<Boolean> result = checkMealStatusUseCase.isFavorite(userId, currentMeal.getId());

            mainHandler.post(() -> {
                if (result instanceof Result.Success) {
                    isFavorite = ((Result.Success<Boolean>) result).data;
                    if (view != null) view.updateFavoriteIcon(isFavorite);
                }
                if (view != null) view.hideLoading();
            });
        });
    }

    @Override
    public void toggleFavorite() {
        if (currentMeal == null) return;

        isFavorite = !isFavorite;
        if (view != null) {
            view.updateFavoriteIcon(isFavorite);
            view.showMessage(isFavorite ? "Added to Favorites" : "Removed from Favorites");
        }

        executor.execute(() -> {
            toggleFavoriteUseCase.execute(userId, currentMeal, !isFavorite);
        });
    }

    @Override
    public void addToPlan(long dateSelection, String mealType) {
        if (view != null) view.showLoading();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String formattedDate = sdf.format(new Date(dateSelection));

        managePlanUseCase.addMeal(userId, formattedDate, mealType, currentMeal, new ManagePlanUseCase.PlanCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                mainHandler.post(() -> {
                    if (view != null) {
                        view.hideLoading();
                        view.showMessage("Added to " + mealType + " plan");
                    }
                });
            }

            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    if (view != null) {
                        view.hideLoading();
                        view.showMessage("Error adding to plan: " + error);
                    }
                });
            }
        });
    }
}