package com.rashed.mealify.ui.home.HomeFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.rashed.mealify.datasource.auth.FirebaseAuthDataSource;
import com.rashed.mealify.datasource.repository.AuthRepositoryImpl;
import com.rashed.mealify.datasource.repository.MealRepositoryImpl;
import com.rashed.mealify.domain.model.Category;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.repository.AuthRepository;
import com.rashed.mealify.domain.repository.MealRepository;
import com.rashed.mealify.domain.usecases.auth.GetCurrentUserUseCase;
import com.rashed.mealify.domain.usecases.meal.FilterByCategoryUseCase;
import com.rashed.mealify.domain.usecases.meal.GetCategoriesUseCase;
import com.rashed.mealify.domain.usecases.meal.GetMealDetailsUseCase;
import com.rashed.mealify.domain.usecases.meal.GetRandomMealUseCase;
import com.rashed.mealify.ui.home.HomeFragment.adapter.CategoriesAdapter;
import com.rashed.mealify.ui.home.HomeFragment.adapter.RecommendedAdapter;

import java.util.List;

public class HomeFragment extends Fragment implements HomeContract.View {

    private HomeContract.Presenter presenter;

    // UI Components
    private ProgressBar loadingIndicator;
    private ProgressBar gridLoadingIndicator;
    private NestedScrollView contentScrollView;
    private ImageView imgHeroMeal;
    private TextView tvHeroName, tvCategoryTitle, tvUserName;
    private RecyclerView rvCategories, rvMealsGrid;
    private Button btnViewRecipe;

    private CategoriesAdapter categoriesAdapter;
    private RecommendedAdapter mealsGridAdapter;
    private View searchBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initPresenter();
        presenter.loadInitialData();
    }

    private void initViews(View view) {
        loadingIndicator = view.findViewById(R.id.loading_indicator);
        gridLoadingIndicator = view.findViewById(R.id.pb_grid_loading);
        contentScrollView = view.findViewById(R.id.content_scroll_view);

        imgHeroMeal = view.findViewById(R.id.img_hero_meal);
        tvHeroName = view.findViewById(R.id.tv_hero_name);
        tvCategoryTitle = view.findViewById(R.id.tv_category_title_dynamic);
        tvUserName = view.findViewById(R.id.tv_user_name);
        btnViewRecipe = view.findViewById(R.id.btn_view_recipe);
        searchBar = view.findViewById(R.id.search_bar);
        rvCategories = view.findViewById(R.id.rv_categories);
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoriesAdapter = new CategoriesAdapter();
        rvCategories.setAdapter(categoriesAdapter);

        rvMealsGrid = view.findViewById(R.id.rv_recommended);
        rvMealsGrid.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvMealsGrid.setNestedScrollingEnabled(false);
        mealsGridAdapter = new RecommendedAdapter();
        rvMealsGrid.setAdapter(mealsGridAdapter);

        categoriesAdapter.setListener(category -> presenter.selectCategory(category));
        mealsGridAdapter.setListener(meal -> presenter.onMealClicked(meal.getId()));
        btnViewRecipe.setOnClickListener(v -> presenter.onHeroClicked());
        searchBar.setOnClickListener(v -> navigateToSearch());
    }

    private void initPresenter() {
        MealRepository mealRepo = new MealRepositoryImpl(requireContext());
        AuthRepository authRepo = new AuthRepositoryImpl(new FirebaseAuthDataSource());

        presenter = new HomePresenter(
                new GetRandomMealUseCase(mealRepo),
                new GetCategoriesUseCase(mealRepo),
                new FilterByCategoryUseCase(mealRepo),
                new GetMealDetailsUseCase(mealRepo),
                new GetCurrentUserUseCase(authRepo)
        );
        presenter.attach(this);
    }

    @Override
    public void showLoading() {
        loadingIndicator.setVisibility(View.VISIBLE);
        contentScrollView.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        loadingIndicator.setVisibility(View.GONE);
    }

    @Override
    public void showContent() {
        contentScrollView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showGridLoading() {
        gridLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideGridLoading() {
        gridLoadingIndicator.setVisibility(View.GONE);
    }

    @Override
    public void displayHeroMeal(Meal meal) {
        tvHeroName.setText(meal.getName());
        Glide.with(this).load(meal.getThumbUrl()).centerCrop().into(imgHeroMeal);
    }

    @Override
    public void displayCategories(List<Category> categories) {
        categoriesAdapter.setList(categories);
    }

    @Override
    public void displayCategoryMeals(List<Meal> meals) {
        mealsGridAdapter.setList(meals);
    }

    @Override
    public void updateCategoryTitle(String title) {
        tvCategoryTitle.setText(title);
    }

    @Override
    public void showUserName(String userName) {
        if (tvUserName != null) {
            tvUserName.setText("Hello, " + userName);
        }
    }

    @Override
    public void navigateToDetails(Meal meal) {
        HomeFragmentDirections.ActionNavHomeToMealDetailsFragment action =
                HomeFragmentDirections.actionNavHomeToMealDetailsFragment(meal);
        Navigation.findNavController(requireView()).navigate(action);
    }

    public void navigateToSearch() {
        Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.actionNavHomeToNavSearch());
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        presenter.detach();
        super.onDestroyView();
    }
}