package com.rashed.mealify.ui.home.HomeFragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.rashed.mealify.common.Result;
import com.rashed.mealify.domain.model.AuthUser;
import com.rashed.mealify.domain.model.Category;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.usecases.auth.GetCurrentUserUseCase;
import com.rashed.mealify.domain.usecases.meal.FilterByCategoryUseCase;
import com.rashed.mealify.domain.usecases.meal.GetCategoriesUseCase;
import com.rashed.mealify.domain.usecases.meal.GetMealDetailsUseCase;
import com.rashed.mealify.domain.usecases.meal.GetRandomMealUseCase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomePresenter implements HomeContract.Presenter {

    private HomeContract.View view;

    private final GetRandomMealUseCase getRandomMealUseCase;
    private final GetCategoriesUseCase getCategoriesUseCase;
    private final FilterByCategoryUseCase filterMealsUseCase;
    private final GetMealDetailsUseCase getMealDetailsUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    // Static Cache
    private static Meal cachedHeroMeal;
    private static List<Category> cachedCategories;
    private static Map<String, List<Meal>> cachedCategoryMealsMap = new HashMap<>();
    private static String currentSelectedCategory = "";
    private static String cachedUserName = "";
    private static boolean isInitLoadComplete = false;

    private final ExecutorService executorService;
    private final Handler mainHandler;

    public HomePresenter(GetRandomMealUseCase getRandomMealUseCase,
                         GetCategoriesUseCase getCategoriesUseCase,
                         FilterByCategoryUseCase filterMealsUseCase,
                         GetMealDetailsUseCase getMealDetailsUseCase,
                         GetCurrentUserUseCase getCurrentUserUseCase) {
        this.getRandomMealUseCase = getRandomMealUseCase;
        this.getCategoriesUseCase = getCategoriesUseCase;
        this.filterMealsUseCase = filterMealsUseCase;
        this.getMealDetailsUseCase = getMealDetailsUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;

        this.executorService = Executors.newFixedThreadPool(4);
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void attach(HomeContract.View view) {
        this.view = view;
    }

    @Override
    public void detach() {
        this.view = null;
    }

    @Override
    public void loadInitialData() {
        if (isInitLoadComplete) {
            restoreState();
            return;
        }

        if (view != null) view.showLoading();

        executorService.execute(() -> {
            try {
                Result<Meal> heroResult = getRandomMealUseCase.execute();
                Result<List<Category>> catResult = getCategoriesUseCase.execute();

                getCurrentUserUseCase.execute(userResult -> {
                    if (userResult instanceof Result.Success) {
                        AuthUser user = ((Result.Success<AuthUser>) userResult).data;
                        cachedUserName = user.firstName;
                    } else {
                        cachedUserName = "Guest";
                    }
                });

                mainHandler.post(() -> {
                    if (view == null) return;

                    view.showUserName(cachedUserName);

                    if (heroResult instanceof Result.Success) {
                        cachedHeroMeal = ((Result.Success<Meal>) heroResult).data;
                        view.displayHeroMeal(cachedHeroMeal);
                    }

                    if (catResult instanceof Result.Success) {
                        cachedCategories = ((Result.Success<List<Category>>) catResult).data;
                        view.displayCategories(cachedCategories);

                        if (!cachedCategories.isEmpty()) {
                            Category first = cachedCategories.get(0);
                            selectCategory(first);
                        }
                    } else if (catResult instanceof Result.Error) {
                        String msg = ((Result.Error<?>) catResult).message;
                        view.showErrorMessage(msg);
                    }

                    isInitLoadComplete = true;
                    view.showContent();
                    view.hideLoading();
                });

            } catch (Exception e) {
                Log.e("HomePresenter", "Error loading initial data", e);
                mainHandler.post(() -> {
                    if (view != null) {
                        view.hideLoading();
                        view.showErrorMessage("An unexpected error occurred.");
                    }
                });
            }
        });
    }

    private void restoreState() {
        if (view == null) return;

        view.hideLoading();
        view.showContent();

        view.showUserName(cachedUserName.isEmpty() ? "Guest" : cachedUserName);

        if (cachedHeroMeal != null) {
            view.displayHeroMeal(cachedHeroMeal);
        }

        if (cachedCategories != null) {
            view.displayCategories(cachedCategories);
        }

        if (!currentSelectedCategory.isEmpty()) {
            view.updateCategoryTitle("Explore " + currentSelectedCategory);

            if (cachedCategoryMealsMap.containsKey(currentSelectedCategory)) {
                view.displayCategoryMeals(cachedCategoryMealsMap.get(currentSelectedCategory));
            } else {
                loadMealsForCategory(currentSelectedCategory);
            }
        }
    }

    @Override
    public void selectCategory(Category category) {
        currentSelectedCategory = category.getName();
        if (view != null) view.updateCategoryTitle("Explore " + currentSelectedCategory);
        loadMealsForCategory(currentSelectedCategory);
    }

    private void loadMealsForCategory(String categoryName) {
        if (cachedCategoryMealsMap.containsKey(categoryName)) {
            if (view != null) view.displayCategoryMeals(cachedCategoryMealsMap.get(categoryName));
            return;
        }

        if (view != null) view.showGridLoading();

        executorService.execute(() -> {
            try {
                Result<List<Meal>> result = filterMealsUseCase.execute(categoryName);

                mainHandler.post(() -> {
                    if (view == null) return;
                    view.hideGridLoading();

                    if (result instanceof Result.Success) {
                        List<Meal> meals = ((Result.Success<List<Meal>>) result).data;
                        cachedCategoryMealsMap.put(categoryName, meals);

                        if (currentSelectedCategory.equals(categoryName)) {
                            view.displayCategoryMeals(meals);
                        }
                    } else {
                        view.showErrorMessage("Failed to load meals");
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (view != null) view.hideGridLoading();
                });
            }
        });
    }

    @Override
    public void onMealClicked(String mealId) {
        if (view != null) view.showLoading();

        executorService.execute(() -> {
            try {
                Result<Meal> result = getMealDetailsUseCase.execute(mealId);

                mainHandler.post(() -> {
                    if (view == null) return;
                    view.hideLoading();

                    if (result instanceof Result.Success) {
                        view.navigateToDetails(((Result.Success<Meal>) result).data);
                    } else {
                        String msg = result instanceof Result.Error ? ((Result.Error<?>) result).message : "Failed to load details";
                        view.showErrorMessage(msg);
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (view != null) {
                        view.hideLoading();
                        view.showErrorMessage("Error opening meal details");
                    }
                });
            }
        });
    }

    @Override
    public void onHeroClicked() {
        if (cachedHeroMeal != null && view != null) {
            view.navigateToDetails(cachedHeroMeal);
        }
    }
}