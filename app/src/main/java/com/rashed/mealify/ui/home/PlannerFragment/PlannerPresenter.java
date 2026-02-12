package com.rashed.mealify.ui.home.PlannerFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.rashed.mealify.domain.usecases.meal.ManagePlanUseCase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlannerPresenter implements PlannerContract.Presenter {

    private PlannerContract.View view;
    private final ManagePlanUseCase managePlanUseCase;
    private final String userId;
    private String selectedDateFormatted;
    private final CompositeDisposable disposables = new CompositeDisposable();

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
        disposables.clear();
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
        if (selectedDateFormatted == null || userId.equals("guest_user")) {
            if (view != null) view.showEmptyState();
            return;
        }

        disposables.add(managePlanUseCase.getPlanForDay(userId, selectedDateFormatted)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        data -> {
                            if (view == null) return;
                            if (data.isEmpty()) {
                                view.showEmptyState();
                            } else {
                                view.showContent();
                                view.showPlanList(data);
                            }
                        },
                        throwable -> {
                            if (view != null) view.showError(throwable.getMessage());
                        }
                ));
    }

    @Override
    public void deleteMeal(ManagePlanUseCase.PlannedMealDomain item, int position) {
        if (view != null) {
            view.showMealRemovedMessage(item, position);
        }

        disposables.add(managePlanUseCase.removeMeal(userId, selectedDateFormatted, item.meal.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {},
                        throwable -> {
                            if (view != null) view.showError("Failed to remove: " + throwable.getMessage());
                        }
                ));
    }

    @Override
    public void undoDelete(ManagePlanUseCase.PlannedMealDomain item, int position) {
        if (view != null) {
            view.restoreItemToAdapter(position, item);
            view.showContent();
        }

        disposables.add(managePlanUseCase.addMeal(userId, selectedDateFormatted, item.type, item.meal)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            if (view != null) view.showRestoreSuccessMessage();
                        },
                        throwable -> {
                            if (view != null) view.showError("Failed to restore: " + throwable.getMessage());
                        }
                ));
    }
}