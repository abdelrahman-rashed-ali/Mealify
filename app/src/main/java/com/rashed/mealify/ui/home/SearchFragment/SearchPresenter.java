package com.rashed.mealify.ui.home.SearchFragment;

import android.os.Handler;
import android.os.Looper;

import com.rashed.mealify.common.Result;
import com.rashed.mealify.domain.model.Area;
import com.rashed.mealify.domain.model.Category;
import com.rashed.mealify.domain.model.Ingredient;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.usecases.meal.FilterByAreaUseCase;
import com.rashed.mealify.domain.usecases.meal.FilterByCategoryUseCase;
import com.rashed.mealify.domain.usecases.meal.FilterByIngredientUseCase;
import com.rashed.mealify.domain.usecases.meal.GetAreasUseCase;
import com.rashed.mealify.domain.usecases.meal.GetCategoriesUseCase;
import com.rashed.mealify.domain.usecases.meal.GetIngredientsUseCase;
import com.rashed.mealify.domain.usecases.meal.GetMealDetailsUseCase;
import com.rashed.mealify.domain.usecases.meal.SearchMealsByNameUseCase;
import com.rashed.mealify.domain.usecases.meal.SearchMealsUseCase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class SearchPresenter implements SearchContract.Presenter {

    private SearchContract.View view;

    private final SearchMealsUseCase searchMealsUseCase;
    private final GetCategoriesUseCase getCategoriesUseCase;
    private final GetAreasUseCase getAreasUseCase;
    private final GetIngredientsUseCase getIngredientsUseCase;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private String currentQuery = "";
    private String selectedCategory = null;
    private String selectedArea = null;
    private String selectedIngredient = null;

    private Runnable searchRunnable;
    private static final long DEBOUNCE_DELAY_MS = 350;

    public SearchPresenter(
            SearchMealsUseCase searchMealsUseCase,
            GetCategoriesUseCase getCategoriesUseCase,
            GetAreasUseCase getAreasUseCase,
            GetIngredientsUseCase getIngredientsUseCase
    ) {
        this.searchMealsUseCase = searchMealsUseCase;
        this.getCategoriesUseCase = getCategoriesUseCase;
        this.getAreasUseCase = getAreasUseCase;
        this.getIngredientsUseCase = getIngredientsUseCase;
    }

    @Override
    public void attach(SearchContract.View view) {
        this.view = view;
        loadFiltersLists();
    }

    private void loadFiltersLists() {
        executor.execute(() -> {
            Result<List<Category>> cats = getCategoriesUseCase.execute();
            if (cats instanceof Result.Success && view != null) {
                mainHandler.post(() -> view.populateCategories(((Result.Success<List<Category>>) cats).data));
            }
        });

        executor.execute(() -> {
            Result<List<Area>> areas = getAreasUseCase.execute();
            if (areas instanceof Result.Success && view != null) {
                List<Area> areaList = ((Result.Success<List<Area>>) areas).data;
                List<String> names = new ArrayList<>();
                for (Area a : areaList) names.add(a.getName()); // عدّلي getName حسب Area model عندك
                mainHandler.post(() -> view.populateAreas(names));
            }
        });

        executor.execute(() -> {
            Result<List<Ingredient>> ings = getIngredientsUseCase.execute();
            if (ings instanceof Result.Success && view != null) {
                mainHandler.post(() -> view.populateIngredients(((Result.Success<List<Ingredient>>) ings).data));
            }
        });
    }

    @Override
    public void detach() {
        view = null;
        if (searchRunnable != null) mainHandler.removeCallbacks(searchRunnable);
    }

    @Override
    public void search(String query) {
        currentQuery = (query == null) ? "" : query;
        postSearch();
    }

    @Override
    public void toggleCategoryFilter(String category) {
        selectedCategory = (selectedCategory != null && selectedCategory.equals(category)) ? null : category;
        postSearch();
    }

    @Override
    public void toggleAreaFilter(String area) {
        selectedArea = (selectedArea != null && selectedArea.equals(area)) ? null : area;
        postSearch();
    }

    @Override
    public void toggleIngredientFilter(String ingredient) {
        selectedIngredient = (selectedIngredient != null && selectedIngredient.equals(ingredient)) ? null : ingredient;
        postSearch();
    }

    @Override
    public void clearFilters() {
        selectedCategory = null;
        selectedArea = null;
        selectedIngredient = null;
        postSearch();
    }

    private void postSearch() {
        if (view == null) return;

        if (searchRunnable != null) mainHandler.removeCallbacks(searchRunnable);

        String q = currentQuery.trim();
        if (q.isEmpty()) {
            // UX: اطلب من المستخدم يكتب حاجة بدل ما نعمل empty
            view.showEmptyState();
            return;
        }

        view.showLoading();
        searchRunnable = this::executeSearch;
        mainHandler.postDelayed(searchRunnable, DEBOUNCE_DELAY_MS);
    }

    private void executeSearch() {
        executor.execute(() -> {
            Result<List<Meal>> result = searchMealsUseCase.execute(currentQuery.trim());

            mainHandler.post(() -> {
                if (view == null) return;
                view.hideLoading();

                if (result instanceof Result.Success) {
                    List<Meal> base = ((Result.Success<List<Meal>>) result).data;
                    List<Meal> filtered = applyFilters(base);

                    if (filtered.isEmpty()) view.showEmptyState();
                    else view.showResults(filtered);

                } else {
                    view.showErrorState(((Result.Error) result).message);
                }
            });
        });
    }

    private List<Meal> applyFilters(List<Meal> origin) {
        List<Meal> out = new ArrayList<>();
        if (origin == null) return out;

        for (Meal m : origin) {
            if (selectedCategory != null && !safe(m.getCategory()).equalsIgnoreCase(selectedCategory)) continue;
            if (selectedArea != null && !safe(m.getArea()).equalsIgnoreCase(selectedArea)) continue;

            // Ingredient filter: لازم Meal model يكون فيه ingredients (List<String>) أو أي طريقة تتحقق منها
            if (selectedIngredient != null && !mealHasIngredient(m, selectedIngredient)) continue;

            out.add(m);
        }
        return out;
    }

    private boolean mealHasIngredient(Meal m, String ing) {
        if (m.getIngredients() == null) return false;
        for (Meal.Ingredient x : m.getIngredients()) {
            if (x.name != null && x.name.equalsIgnoreCase(ing)) return true;
        }
        return false;
    }

    private String safe(String s) { return s == null ? "" : s; }

    @Override
    public void onMealClicked(Meal meal) {
        if (view == null) return;
        view.navigateToDetails(meal);
    }
}

