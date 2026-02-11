package com.rashed.mealify.ui.home.HomeFragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rashed.mealify.R;
import com.rashed.mealify.common.Result;
import com.rashed.mealify.datasource.repository.MealRepositoryImpl;
import com.rashed.mealify.domain.model.Category;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.repository.MealRepository;
import com.rashed.mealify.domain.usecases.meal.FilterByCategoryUseCase;
import com.rashed.mealify.domain.usecases.meal.GetCategoriesUseCase;
import com.rashed.mealify.domain.usecases.meal.GetMealDetailsUseCase;
import com.rashed.mealify.domain.usecases.meal.GetRandomMealUseCase;
import com.rashed.mealify.ui.home.HomeFragment.adapter.CategoriesAdapter; // Updated import
import com.rashed.mealify.ui.home.HomeFragment.adapter.RecommendedAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private static Meal cachedHeroMeal;
    private ProgressBar gridLoadingIndicator;
    private static List<Category> cachedCategories;
    private static Map<String, List<Meal>> cachedCategoryMealsMap = new HashMap<>();
    private static String currentSelectedCategory = "";
    private static boolean isInitLoadComplete = false;

    // Dependencies
    private MealRepository repository;
    private ExecutorService executorService;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Use Cases
    private GetRandomMealUseCase getRandomMealUseCase;
    private GetCategoriesUseCase getCategoriesUseCase;
    private FilterByCategoryUseCase filterMealsUseCase;
    private GetMealDetailsUseCase getMealDetailsUseCase;
    // UI
    private ProgressBar loadingIndicator;
    private NestedScrollView contentScrollView;
    private LinearLayout errorLayout;
    private ImageView imgHeroMeal;
    private TextView tvHeroName, tvCategoryTitle;
    private RecyclerView rvCategories, rvMealsGrid;

    private CategoriesAdapter categoriesAdapter;
    private RecommendedAdapter mealsGridAdapter;
    private Button btnViewRecipe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new MealRepositoryImpl(requireContext());
        executorService = Executors.newFixedThreadPool(3);

        getRandomMealUseCase = new GetRandomMealUseCase(repository);
        getCategoriesUseCase = new GetCategoriesUseCase(repository);
        filterMealsUseCase = new FilterByCategoryUseCase(repository);
        getMealDetailsUseCase = new GetMealDetailsUseCase(repository);
        loadingIndicator = view.findViewById(R.id.loading_indicator);
        contentScrollView = view.findViewById(R.id.content_scroll_view);
        btnViewRecipe = view.findViewById(R.id.btn_view_recipe);

        imgHeroMeal = view.findViewById(R.id.img_hero_meal);
        tvHeroName = view.findViewById(R.id.tv_hero_name);
        tvCategoryTitle = view.findViewById(R.id.tv_category_title_dynamic);
        gridLoadingIndicator = view.findViewById(R.id.pb_grid_loading);
        rvCategories = view.findViewById(R.id.rv_categories);
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoriesAdapter = new CategoriesAdapter();
        rvCategories.setAdapter(categoriesAdapter);

        rvMealsGrid = view.findViewById(R.id.rv_recommended); // Reusing existing ID
        rvMealsGrid.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvMealsGrid.setNestedScrollingEnabled(false);
        mealsGridAdapter = new RecommendedAdapter();
        rvMealsGrid.setAdapter(mealsGridAdapter);

        mealsGridAdapter.setListener(partialMeal -> {

            loadingIndicator.setVisibility(View.VISIBLE);

            executorService.execute(() -> {
                Result<Meal> result = getMealDetailsUseCase.execute(partialMeal.getId());

                mainHandler.post(() -> {
                    if (!isAdded()) return;

                    loadingIndicator.setVisibility(View.GONE);

                    if (result instanceof Result.Success) {
                        Meal fullMeal = ((Result.Success<Meal>) result).data;

                        HomeFragmentDirections.ActionNavHomeToMealDetailsFragment action =
                                HomeFragmentDirections.actionNavHomeToMealDetailsFragment(fullMeal);

                        Navigation.findNavController(view).navigate(action);

                    } else {
                        String errorMsg = result instanceof Result.Error ?
                                ((Result.Error) result).message : "Failed to load details";
                        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
        categoriesAdapter.setListener(category -> {
            currentSelectedCategory = category.getName();
            tvCategoryTitle.setText("Explore " + currentSelectedCategory);
            loadMealsForCategory(currentSelectedCategory);
        });

        if (isInitLoadComplete) {
            showContent();
            restoreState();
        } else {
            loadInitialData();
        }
    }

    private void loadInitialData() {
        showLoading();

        executorService.execute(() -> {
            Result<Meal> heroResult = getRandomMealUseCase.execute();
            Result<List<Category>> catResult = getCategoriesUseCase.execute();

            mainHandler.post(() -> {
                if (!isAdded()) return;

                if (heroResult instanceof Result.Success) {
                    cachedHeroMeal = ((Result.Success<Meal>) heroResult).data;
                    bindHero(cachedHeroMeal);
                }

                if (catResult instanceof Result.Success) {
                    cachedCategories = ((Result.Success<List<Category>>) catResult).data;
                    categoriesAdapter.setList(cachedCategories);

                    if (!cachedCategories.isEmpty()) {
                        Category first = cachedCategories.get(0);
                        currentSelectedCategory = first.getName();
                        tvCategoryTitle.setText("Explore " + currentSelectedCategory);
                        loadMealsForCategory(currentSelectedCategory);
                    }
                }

                isInitLoadComplete = true;
                showContent();
            });
        });
    }

    private void loadMealsForCategory(String category) {
        if (cachedCategoryMealsMap.containsKey(category)) {
            mealsGridAdapter.setList(cachedCategoryMealsMap.get(category));
            return;
        }

        gridLoadingIndicator.setVisibility(View.VISIBLE);



        executorService.execute(() -> {
            Result<List<Meal>> result = filterMealsUseCase.execute(category);

            mainHandler.post(() -> {
                if (!isAdded()) return;

                gridLoadingIndicator.setVisibility(View.GONE);

                if (result instanceof Result.Success) {
                    List<Meal> meals = ((Result.Success<List<Meal>>) result).data;

                    cachedCategoryMealsMap.put(category, meals);

                    if (currentSelectedCategory.equals(category)) {
                        mealsGridAdapter.setList(meals);
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load meals", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void restoreState() {
        if (cachedHeroMeal != null) bindHero(cachedHeroMeal);
        if (cachedCategories != null) categoriesAdapter.setList(cachedCategories);

        if (!currentSelectedCategory.isEmpty()) {
            tvCategoryTitle.setText("Explore " + currentSelectedCategory);
            if (cachedCategoryMealsMap.containsKey(currentSelectedCategory)) {
                mealsGridAdapter.setList(cachedCategoryMealsMap.get(currentSelectedCategory));
            } else {
                loadMealsForCategory(currentSelectedCategory);
            }
        }
    }

    private void bindHero(Meal meal) {
        tvHeroName.setText(meal.getName());
        Glide.with(this).load(meal.getThumbUrl()).centerCrop().into(imgHeroMeal);
        btnViewRecipe.setOnClickListener(e->{
                    HomeFragmentDirections.ActionNavHomeToMealDetailsFragment action =
                            HomeFragmentDirections.actionNavHomeToMealDetailsFragment(meal);
                    Navigation.findNavController(e).navigate(action);
                }
        );
    }

    private void showLoading() {
        loadingIndicator.setVisibility(View.VISIBLE);
        contentScrollView.setVisibility(View.GONE);
    }

    private void showContent() {
        loadingIndicator.setVisibility(View.GONE);
        contentScrollView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (executorService != null) executorService.shutdown();
    }
}