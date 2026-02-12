package com.rashed.mealify.ui.home.HomeFragment;

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

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomePresenter implements HomeContract.Presenter {

    private HomeContract.View view;

    private final GetRandomMealUseCase getRandomMealUseCase;
    private final GetCategoriesUseCase getCategoriesUseCase;
    private final FilterByCategoryUseCase filterMealsUseCase;
    private final GetMealDetailsUseCase getMealDetailsUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private static Meal cachedHeroMeal;
    private static List<Category> cachedCategories;
    private static final Map<String, List<Meal>> cachedCategoryMealsMap = new HashMap<>();
    private static String currentSelectedCategory = "";
    private static String cachedUserName = "Guest";
    private static boolean isInitLoadComplete = false;

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
    }

    @Override
    public void attach(HomeContract.View view) {
        this.view = view;
    }

    @Override
    public void detach() {
        this.view = null;
        disposables.clear();
    }

    @Override
    public void loadInitialData() {
        if (isInitLoadComplete) {
            restoreState();
            return;
        }

        if (view != null) view.showLoading();

        disposables.add(getCurrentUserUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        user -> {
                            cachedUserName = user.firstName;
                            if (view != null) view.showUserName(cachedUserName);
                        },
                        throwable -> {
                            cachedUserName = "Guest";
                            if (view != null) view.showUserName(cachedUserName);
                        }
                ));

        disposables.add(Single.zip(
                        getRandomMealUseCase.execute(),
                        getCategoriesUseCase.execute(),
                        (hero, categories) -> {
                            cachedHeroMeal = hero;
                            cachedCategories = categories;
                            return true;
                        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        success -> {
                            if (view == null) return;
                            view.displayHeroMeal(cachedHeroMeal);
                            view.displayCategories(cachedCategories);

                            if (!cachedCategories.isEmpty() && currentSelectedCategory.isEmpty()) {
                                selectCategory(cachedCategories.get(0));
                            }

                            isInitLoadComplete = true;
                            view.showContent();
                            view.hideLoading();
                        },
                        throwable -> {
                            if (view != null) {
                                view.hideLoading();
                                view.showErrorMessage(throwable.getMessage());
                            }
                        }
                ));
    }

    private void restoreState() {
        if (view == null) return;
        view.hideLoading();
        view.showContent();
        view.showUserName(cachedUserName);

        if (cachedHeroMeal != null) view.displayHeroMeal(cachedHeroMeal);
        if (cachedCategories != null) view.displayCategories(cachedCategories);

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

        disposables.add(filterMealsUseCase.execute(categoryName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        meals -> {
                            cachedCategoryMealsMap.put(categoryName, meals);
                            if (view != null && currentSelectedCategory.equals(categoryName)) {
                                view.hideGridLoading();
                                view.displayCategoryMeals(meals);
                            }
                        },
                        throwable -> {
                            if (view != null) {
                                view.hideGridLoading();
                                view.showErrorMessage("Failed to filter: " + throwable.getMessage());
                            }
                        }
                ));
    }

    @Override
    public void onMealClicked(String mealId) {
        if (view != null) view.showLoading();

        disposables.add(getMealDetailsUseCase.execute(mealId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        meal -> {
                            if (view != null) {
                                view.hideLoading();
                                view.navigateToDetails(meal);
                            }
                        },
                        throwable -> {
                            if (view != null) {
                                view.hideLoading();
                                view.showErrorMessage(throwable.getMessage());
                            }
                        }
                ));
    }

    @Override
    public void onHeroClicked() {
        if (cachedHeroMeal != null && view != null) {
            view.navigateToDetails(cachedHeroMeal);
        }
    }
}