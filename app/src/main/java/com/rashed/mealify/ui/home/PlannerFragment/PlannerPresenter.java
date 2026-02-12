package com.rashed.mealify.ui.home.PlannerFragment;

import android.os.Handler;
import android.os.Looper;

import com.google.firebase.auth.FirebaseAuth;
import com.rashed.mealify.domain.usecases.meal.ManagePlanUseCase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlannerPresenter implements PlannerContract.Presenter {

    private PlannerContract.View view;
    private final ManagePlanUseCase managePlanUseCase;
    private String userId;
    private String selectedDateFormatted;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public PlannerPresenter(ManagePlanUseCase managePlanUseCase) {
        this.managePlanUseCase = managePlanUseCase;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            userId = "guest_user";
        }
    }

    @Override
    public void attach(PlannerContract.View view) {
        this.view = view;
    }

    @Override
    public void detach() {
        this.view = null;
    }

    @Override
    public void setDate(Date date) {
        SimpleDateFormat displayFormat = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.US);
        if (view != null) {
            view.showDate(displayFormat.format(date));
        }

        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        selectedDateFormatted = dbFormat.format(date);

        loadPlan();
    }

    @Override
    public void loadPlan() {
        if (selectedDateFormatted == null) return;

        managePlanUseCase.getPlanForDay(userId, selectedDateFormatted, new ManagePlanUseCase.PlanCallback<List<ManagePlanUseCase.PlannedMealDomain>>() {
            @Override
            public void onSuccess(List<ManagePlanUseCase.PlannedMealDomain> data) {
                mainHandler.post(() -> {
                    if (view == null) return;

                    if (data.isEmpty()) {
                        view.showEmptyState();
                    } else {
                        view.showContent();
                        view.showPlanList(data);
                    }
                });
            }

            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    if (view != null) view.showError(error);
                });
            }
        });
    }

    @Override
    public void deleteMeal(ManagePlanUseCase.PlannedMealDomain item, int position) {
        if (view != null) {
            view.showMealRemovedMessage(item, position);
        }

        managePlanUseCase.removeMeal(userId, selectedDateFormatted, item.meal.getId(), new ManagePlanUseCase.PlanCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                // Deletion Confirmed
            }

            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    if (view != null) view.showError("Failed to remove: " + error);
                });
            }
        });
    }

    @Override
    public void undoDelete(ManagePlanUseCase.PlannedMealDomain item, int position) {
        if (view != null) {
            view.restoreItemToAdapter(position, item);
            view.showContent();
        }

        managePlanUseCase.addMeal(userId, selectedDateFormatted, item.type, item.meal, new ManagePlanUseCase.PlanCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                mainHandler.post(() -> {
                    if (view != null) view.showRestoreSuccessMessage();
                });
            }

            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    if (view != null) view.showError("Failed to restore meal: " + error);
                });
            }
        });
    }
}