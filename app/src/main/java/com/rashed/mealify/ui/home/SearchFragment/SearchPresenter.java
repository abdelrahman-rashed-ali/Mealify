package com.rashed.mealify.ui.home.SearchFragment;

import com.rashed.mealify.domain.model.Area;
import com.rashed.mealify.domain.model.Category;
import com.rashed.mealify.domain.model.Ingredient;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.usecases.meal.GetAreasUseCase;
import com.rashed.mealify.domain.usecases.meal.GetCategoriesUseCase;
import com.rashed.mealify.domain.usecases.meal.GetIngredientsUseCase;
import com.rashed.mealify.domain.usecases.meal.SearchMealsUseCase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class SearchPresenter implements SearchContract.Presenter {

    private SearchContract.View view;

    private final SearchMealsUseCase searchMealsUseCase;
    private final GetCategoriesUseCase getCategoriesUseCase;
    private final GetAreasUseCase getAreasUseCase;
    private final GetIngredientsUseCase getIngredientsUseCase;

    private final CompositeDisposable disposables = new CompositeDisposable();
    private final PublishSubject<String> searchSubject = PublishSubject.create();

    private String selectedCategory = null;
    private String selectedArea = null;
    private String selectedIngredient = null;

    private String lastQuery = "";
    private List<Meal> lastResults = new ArrayList<>();

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

        setupSearchPipeline();
    }

    @Override
    public void attach(SearchContract.View view) {
        this.view = view;
        loadFiltersLists();
    }

    @Override
    public void detach() {
        view = null;
        disposables.clear();
    }


    private void loadFiltersLists() {
        disposables.add(
                Single.zip(
                                getCategoriesUseCase.execute(),     // Single<List<Category>>
                                getAreasUseCase.execute(),          // Single<List<Area>>
                                getIngredientsUseCase.execute(),    // Single<List<Ingredient>>
                                FiltersBundle::new
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(d -> { if (view != null) view.showLoading(); })
                        .doFinally(() -> { if (view != null) view.hideLoading(); })
                        .subscribe(
                                bundle -> {
                                    if (view == null) return;

                                    view.populateCategories(bundle.categories);

                                    List<String> areaNames = new ArrayList<>();
                                    for (Area a : bundle.areas) {
                                        if (a != null && a.getName() != null) areaNames.add(a.getName());
                                    }
                                    view.populateAreas(areaNames);

                                    view.populateIngredients(bundle.ingredients);
                                },
                                throwable -> {
                                    throwable.printStackTrace();
                                    if (view != null) view.showErrorState(throwable.getMessage());
                                }
                        )
        );
    }

    private void setupSearchPipeline() {
        disposables.add(
                searchSubject
                        .map(q -> q == null ? "" : q.trim())
                        .debounce(350, TimeUnit.MILLISECONDS)
                        .distinctUntilChanged()
                        .switchMapSingle(query -> {
                            if (query.isEmpty()) return Single.just(new ArrayList<>());

                            return Single.just(query)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnSuccess(q -> { if (view != null) view.showLoading(); })
                                    .observeOn(Schedulers.io())
                                    .flatMap(searchMealsUseCase::execute);
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                results -> {
                                    if (view == null) return;

                                    view.hideLoading();

                                    lastResults = (results == null) ? new ArrayList<>() : (List<Meal>) results;

                                    renderFilteredResults();
                                },
                                throwable -> {
                                    throwable.printStackTrace();
                                    if (view != null) {
                                        view.hideLoading();
                                        view.showErrorState(throwable.getMessage());
                                    }
                                }
                        )
        );
    }

    @Override
    public void search(String query) {
        lastQuery = query == null ? "" : query.trim();

        if (lastQuery.isEmpty()) {
            lastResults = new ArrayList<>();
            if (view != null) view.showEmptyState();
            return;
        }

        searchSubject.onNext(lastQuery);
    }

    @Override
    public void toggleCategoryFilter(String category) {
        selectedCategory = (selectedCategory != null && selectedCategory.equals(category)) ? null : category;
        renderFilteredResults();
    }

    @Override
    public void toggleAreaFilter(String area) {
        selectedArea = (selectedArea != null && selectedArea.equals(area)) ? null : area;
        renderFilteredResults();
    }

    @Override
    public void toggleIngredientFilter(String ingredient) {
        selectedIngredient = (selectedIngredient != null && selectedIngredient.equals(ingredient)) ? null : ingredient;
        renderFilteredResults();
    }

    @Override
    public void clearFilters() {
        selectedCategory = null;
        selectedArea = null;
        selectedIngredient = null;
        renderFilteredResults();
    }

    private void renderFilteredResults() {
        if (view == null) return;

        if (lastQuery == null || lastQuery.trim().isEmpty()) {
            view.showEmptyState();
            return;
        }

        List<Meal> filtered = applyFilters(lastResults);
        if (filtered.isEmpty()) view.showEmptyState();
        else view.showResults(filtered);
    }

    private List<Meal> applyFilters(List<Meal> origin) {
        if (origin == null) return new ArrayList<>();

        List<Meal> out = new ArrayList<>();
        for (Meal m : origin) {
            if (m == null) continue;

            if (selectedCategory != null && !safe(m.getCategory()).equalsIgnoreCase(selectedCategory)) continue;
            if (selectedArea != null && !safe(m.getArea()).equalsIgnoreCase(selectedArea)) continue;

            if (selectedIngredient != null) {
                if (m.getIngredients() != null && !mealHasIngredient(m, selectedIngredient)) continue;
            }

            out.add(m);
        }
        return out;
    }

    private boolean mealHasIngredient(Meal m, String ing) {
        try {
            for (Object obj : m.getIngredients()) {
                String name = (String) obj.getClass().getField("name").get(obj);
                if (name != null && name.equalsIgnoreCase(ing)) return true;
            }
        } catch (Exception ignore) {}
        return false;
    }

    private String safe(String s) { return s == null ? "" : s; }

    @Override
    public void onMealClicked(Meal meal) {
        if (view != null) view.navigateToDetails(meal);
    }

    static class FiltersBundle {
        final List<Category> categories;
        final List<Area> areas;
        final List<Ingredient> ingredients;

        FiltersBundle(List<Category> categories, List<Area> areas, List<Ingredient> ingredients) {
            this.categories = categories != null ? categories : new ArrayList<>();
            this.areas = areas != null ? areas : new ArrayList<>();
            this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
        }
    }
}
