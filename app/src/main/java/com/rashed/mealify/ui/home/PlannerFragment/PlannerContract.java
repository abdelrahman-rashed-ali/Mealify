package com.rashed.mealify.ui.home.PlannerFragment;

import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.usecases.meal.ManagePlanUseCase;
import java.util.Date;
import java.util.List;

public interface PlannerContract {

    interface View {
        void showDate(String dateString);
        void showPlanList(List<ManagePlanUseCase.PlannedMealDomain> planList);
        void showEmptyState();
        void showContent();
        void showError(String message);
        void showMealRemovedMessage(ManagePlanUseCase.PlannedMealDomain item, int position);
        void showRestoreSuccessMessage();
        void restoreItemToAdapter(int position, ManagePlanUseCase.PlannedMealDomain item);
        void navigateToDetails(Meal meal);
    }

    interface Presenter {
        void attach(View view);
        void detach();
        void setDate(Date date);
        void loadPlan();
        void deleteMeal(ManagePlanUseCase.PlannedMealDomain item, int position);
        void undoDelete(ManagePlanUseCase.PlannedMealDomain item, int position);
        void onMealClicked(Meal meal);
    }
}