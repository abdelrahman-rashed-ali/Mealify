package com.rashed.mealify.domain.usecases.meal;

import com.rashed.mealify.domain.mapper.MealMapper;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.repository.MealRepository;
import java.util.List;
import java.util.stream.Collectors;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class ManagePlanUseCase {
    private final MealRepository repository;

    public ManagePlanUseCase(MealRepository repository) {
        this.repository = repository;
    }

    public Completable addMeal(String uid, String date, String type, Meal meal) {
        return repository.addMealToPlan(uid, date, type, MealMapper.mapDomainToEntity(meal));
    }

    public Completable removeMeal(String uid, String date, String mealId) {
        return repository.removeMealFromPlan(uid, date, mealId);
    }

    public Single<List<PlannedMealDomain>> getPlanForDay(String uid, String date) {
        return repository.getPlanForDayDetails(uid, date)
                .map(details -> details.stream()
                        .map(d -> new PlannedMealDomain(
                                MealMapper.mapEntityToDomain(d.meal),
                                d.mealType,
                                d.date))
                        .collect(Collectors.toList()));
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