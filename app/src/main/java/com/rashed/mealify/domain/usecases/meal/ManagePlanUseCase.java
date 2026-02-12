package com.rashed.mealify.domain.usecases.meal;

import android.os.Handler;
import android.os.Looper;

import com.rashed.mealify.common.Result;
import com.rashed.mealify.datasource.meals.local.entities.PlannedMealDetails;
import com.rashed.mealify.datasource.repository.MealRepositoryImpl;
import com.rashed.mealify.domain.mapper.MealMapper;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.repository.MealRepository;

import java.util.ArrayList;
import java.util.List;

public class ManagePlanUseCase {
    private final MealRepository repository;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface PlanCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }

    public ManagePlanUseCase(MealRepository repository) {
        this.repository = repository;
    }

    public void addMeal(String uid, String date, String type, Meal meal, PlanCallback<Void> callback) {
        new Thread(() -> {
            Result<Void> result = repository.addMealToPlan(uid, date, type, MealMapper.mapDomainToEntity(meal));
            mainHandler.post(() -> {
                if (result instanceof Result.Success) callback.onSuccess(null);
                else callback.onError(((Result.Error<?>) result).message);
            });
        }).start();
    }

    public void removeMeal(String uid, String date, String mealId, PlanCallback<Void> callback) {
        new Thread(() -> {
            Result<Void> result = repository.removeMealFromPlan(uid, date, mealId);
            mainHandler.post(() -> {
                if (result instanceof Result.Success) callback.onSuccess(null);
                else callback.onError(((Result.Error<?>) result).message);
            });
        }).start();
    }

    public void getPlanForDay(String uid, String date, PlanCallback<List<PlannedMealDomain>> callback) {
        new Thread(() -> {
            Result<List<PlannedMealDetails>> result = ((MealRepositoryImpl)repository).getPlanForDayDetails(uid, date);

            mainHandler.post(() -> {
                if (result instanceof Result.Success) {
                    List<PlannedMealDetails> details = ((Result.Success<List<PlannedMealDetails>>) result).data;
                    List<PlannedMealDomain> domainList = new ArrayList<>();
                    if (details != null) {
                        for (PlannedMealDetails d : details) {
                            Meal m = MealMapper.mapEntityToDomain(d.meal);
                            domainList.add(new PlannedMealDomain(m, d.mealType, d.date));
                        }
                    }
                    callback.onSuccess(domainList);
                } else {
                    callback.onError(((Result.Error<?>) result).message);
                }
            });
        }).start();
    }

    public static class PlannedMealDomain {
        public Meal meal;
        public String type;
        public String date;

        public PlannedMealDomain(Meal meal, String type, String date) {
            this.meal = meal;
            this.type = type;
            this.date = date;
        }
    }
}