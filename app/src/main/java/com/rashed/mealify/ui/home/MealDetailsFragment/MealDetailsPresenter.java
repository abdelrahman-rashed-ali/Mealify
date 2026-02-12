package com.rashed.mealify.ui.home.MealDetailsFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser; // Added import
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.usecases.meal.CheckMealStatusUseCase;
import com.rashed.mealify.domain.usecases.meal.ManagePlanUseCase;
import com.rashed.mealify.domain.usecases.meal.ToggleFavoriteUseCase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MealDetailsPresenter implements MealDetailsContract.Presenter {

    private MealDetailsContract.View view;
    private final CheckMealStatusUseCase checkMealStatusUseCase;
    private final ToggleFavoriteUseCase toggleFavoriteUseCase;
    private final ManagePlanUseCase managePlanUseCase;

    private Meal currentMeal;
    private String userId;
    private boolean isFavorite = false;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public MealDetailsPresenter(CheckMealStatusUseCase checkMealStatusUseCase,
                                ToggleFavoriteUseCase toggleFavoriteUseCase,
                                ManagePlanUseCase managePlanUseCase) {
        this.checkMealStatusUseCase = checkMealStatusUseCase;
        this.toggleFavoriteUseCase = toggleFavoriteUseCase;
        this.managePlanUseCase = managePlanUseCase;

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            userId = "";
        }
    }

    private boolean isGuestUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null && user.isAnonymous();
    }

    @Override
    public void attach(MealDetailsContract.View view) {
        this.view = view;
    }

    @Override
    public void detach() {
        this.view = null;
        disposables.clear();
    }

    @Override
    public void initMealData(Meal meal) {
        if (view == null) return;

        view.showLoading();
        this.currentMeal = meal;
        view.displayMealDetails(meal);
        checkFavoriteStatus();
    }

    private void checkFavoriteStatus() {
        if (currentMeal == null) return;

        if (isGuestUser()) {
            if (view != null) view.hideLoading();
            return;
        }

        disposables.add(checkMealStatusUseCase.isFavorite(userId, currentMeal.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        status -> {
                            this.isFavorite = status;
                            if (view != null) {
                                view.updateFavoriteIcon(isFavorite);
                                view.hideLoading();
                            }
                        },
                        throwable -> {
                            if (view != null) view.hideLoading();
                        }
                ));
    }

    @Override
    public void toggleFavorite() {
        if (currentMeal == null) return;

        if (isGuestUser()) {
            if (view != null) view.showGuestModeDialog();
            return;
        }

        boolean previousState = isFavorite;
        isFavorite = !isFavorite;

        if (view != null) {
            view.updateFavoriteIcon(isFavorite);
            view.showMessage(isFavorite ? "Added to Favorites" : "Removed from Favorites");
        }

        disposables.add(toggleFavoriteUseCase.execute(userId, currentMeal, previousState)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {  },
                        throwable -> {
                            isFavorite = previousState;
                            if (view != null) {
                                view.updateFavoriteIcon(isFavorite);
                                view.showMessage("Error updating favorites");
                            }
                        }
                ));
    }

    @Override
    public void addToPlan(long dateSelection, String mealType) {
        if (view == null || currentMeal == null) return;

        if (isGuestUser()) {
            view.showGuestModeDialog();
            return;
        }

        view.showLoading();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String formattedDate = sdf.format(new Date(dateSelection));

        disposables.add(managePlanUseCase.addMeal(userId, formattedDate, mealType, currentMeal)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            if (view != null) {
                                view.hideLoading();
                                view.showMessage("Added to " + mealType + " plan");
                            }
                        },
                        throwable -> {
                            if (view != null) {
                                view.hideLoading();
                                view.showMessage("Error adding to plan: " + throwable.getMessage());
                            }
                        }
                ));
    }
}