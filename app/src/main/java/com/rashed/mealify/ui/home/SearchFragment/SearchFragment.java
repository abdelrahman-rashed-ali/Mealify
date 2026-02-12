package com.rashed.mealify.ui.home.SearchFragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.res.ColorStateList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.rashed.mealify.R;
import com.rashed.mealify.datasource.repository.MealRepositoryImpl;
import com.rashed.mealify.domain.model.Category;
import com.rashed.mealify.domain.model.Ingredient;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.repository.MealRepository;
import com.rashed.mealify.domain.usecases.meal.FilterByAreaUseCase;
import com.rashed.mealify.domain.usecases.meal.FilterByCategoryUseCase;
import com.rashed.mealify.domain.usecases.meal.FilterByIngredientUseCase;
import com.rashed.mealify.domain.usecases.meal.GetAreasUseCase;
import com.rashed.mealify.domain.usecases.meal.GetCategoriesUseCase;
import com.rashed.mealify.domain.usecases.meal.GetIngredientsUseCase;
import com.rashed.mealify.domain.usecases.meal.GetMealDetailsUseCase;
import com.rashed.mealify.domain.usecases.meal.SearchMealsByNameUseCase;
import com.rashed.mealify.domain.usecases.meal.SearchMealsUseCase;
import com.rashed.mealify.ui.home.SearchFragment.adapters.SearchAdapter;

import java.util.List;

public class SearchFragment extends Fragment implements SearchContract.View {

    private SearchContract.Presenter presenter;
    private SearchAdapter adapter;

    private EditText etSearch;
    private ImageButton btnClear;
    private TextView btnClearFilters;
    private ChipGroup cgCategories, cgAreas, cgIngredients;
    private RecyclerView rvResults;
    private ProgressBar progressBar;
    private View layoutEmpty, layoutError;
    private TextView tvError;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initPresenter();
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.et_search);
        btnClear = view.findViewById(R.id.btn_clear_search);
        btnClearFilters = view.findViewById(R.id.btn_clear_filters);

        cgCategories = view.findViewById(R.id.cg_categories);
        cgAreas = view.findViewById(R.id.cg_areas);
        cgIngredients = view.findViewById(R.id.cg_ingredients);

        rvResults = view.findViewById(R.id.rv_search_results);
        progressBar = view.findViewById(R.id.progress_bar);
        layoutEmpty = view.findViewById(R.id.layout_empty_state);
        layoutError = view.findViewById(R.id.layout_error_state);
        tvError = view.findViewById(R.id.tv_error);

        adapter = new SearchAdapter(meal -> presenter.onMealClicked(meal));
        rvResults.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvResults.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                presenter.search(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnClear.setOnClickListener(v -> etSearch.setText(""));

        btnClearFilters.setOnClickListener(v -> {
            cgCategories.clearCheck();
            cgAreas.clearCheck();
            cgIngredients.clearCheck();
            presenter.clearFilters();
        });
    }

    private void initPresenter() {
        MealRepository repository = new MealRepositoryImpl(requireContext());

        presenter = new SearchPresenter(
                new SearchMealsUseCase(repository),
                new GetCategoriesUseCase(repository),
                new GetAreasUseCase(repository),
                new GetIngredientsUseCase(repository)
        );

        presenter.attach(this);
    }


    @Override
    public void onDestroyView() {
        presenter.detach();
        super.onDestroyView();
    }


    private Chip createChip(String text) {
        Chip chip = new Chip(getContext());
        chip.setText(text);
        chip.setCheckable(true);
        chip.setClickable(true);

        chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.surface_dark_green)));
        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_white));

        int[][] states = new int[][] {
                new int[] { android.R.attr.state_checked},
                new int[] { -android.R.attr.state_checked}
        };
        int[] colors = new int[] {
                ContextCompat.getColor(requireContext(), R.color.primary_green),
                ContextCompat.getColor(requireContext(), R.color.surface_dark_green)
        };
        chip.setChipBackgroundColor(new ColorStateList(states, colors));

        return chip;
    }

    @Override
    public void populateCategories(List<Category> categories) {
        cgCategories.removeAllViews();
        for (Category cat : categories) {
            Chip chip = createChip(cat.getName());
            chip.setOnClickListener(v -> presenter.toggleCategoryFilter(cat.getName()));
            cgCategories.addView(chip);
        }
    }

    @Override
    public void populateAreas(List<String> areas) {
        cgAreas.removeAllViews();
        for (String area : areas) {
            Chip chip = createChip(area);
            chip.setOnClickListener(v -> presenter.toggleAreaFilter(area));
            cgAreas.addView(chip);
        }
    }

    @Override
    public void populateIngredients(List<Ingredient> ingredients) {
        cgIngredients.removeAllViews();
        for (Ingredient ing : ingredients) {
            Chip chip = createChip(ing.getName());
            chip.setOnClickListener(v -> presenter.toggleIngredientFilter(ing.getName()));
            cgIngredients.addView(chip);
        }
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        rvResults.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showResults(List<Meal> meals) {
        rvResults.setVisibility(View.VISIBLE);
        adapter.setList(meals);
    }

    @Override
    public void showEmptyState() {
        rvResults.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    public void showErrorState(String message) {
        rvResults.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        tvError.setText(message);
    }

    @Override
    public void navigateToDetails(Meal meal) {
        SearchFragmentDirections.ActionNavSearchToMealDetailsFragment action =
                SearchFragmentDirections.actionNavSearchToMealDetailsFragment(meal);
        Navigation.findNavController(requireView()).navigate(action);
    }
}