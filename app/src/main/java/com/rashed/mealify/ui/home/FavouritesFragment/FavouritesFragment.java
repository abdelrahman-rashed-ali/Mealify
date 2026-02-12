package com.rashed.mealify.ui.home.FavouritesFragment;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.rashed.mealify.R;
import com.rashed.mealify.datasource.repository.MealRepositoryImpl;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.repository.MealRepository;
import com.rashed.mealify.domain.usecases.meal.GetFavoritesUseCase;
import com.rashed.mealify.domain.usecases.meal.ToggleFavoriteUseCase;
import com.rashed.mealify.ui.home.FavouritesFragment.adapters.FavouritesAdapter;

import java.util.List;

public class FavouritesFragment extends Fragment implements FavouritesContract.View {

    private FavouritesContract.Presenter presenter;
    private FavouritesAdapter adapter;

    private RecyclerView rvFavorites;
    private ProgressBar progressBar;
    private View layoutEmpty, layoutError;
    private TextView tvError;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initPresenter();
    }

    private void initViews(View view) {
        rvFavorites = view.findViewById(R.id.rv_favorites);
        progressBar = view.findViewById(R.id.progress_bar);
        layoutEmpty = view.findViewById(R.id.layout_empty_state);
        layoutError = view.findViewById(R.id.layout_error_state);
        tvError = view.findViewById(R.id.tv_error_message);

        adapter = new FavouritesAdapter(meal -> presenter.onMealClicked(meal));
        rvFavorites.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvFavorites.setAdapter(adapter);

        setupSwipeHandler();
    }

    private void initPresenter() {
        MealRepository repository = new MealRepositoryImpl(requireContext());
        presenter = new FavouritesPresenter(
                new GetFavoritesUseCase(repository),
                new ToggleFavoriteUseCase(repository)
        );
        presenter.attach(this);
    }

    private void setupSwipeHandler() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            private final ColorDrawable background = new ColorDrawable(Color.parseColor("#FF6B6B")); // Error Red
            private final Drawable deleteIcon = ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_delete);

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Meal meal = adapter.getItem(position);

                adapter.removeItem(position);

                presenter.deleteMeal(meal, position);

                if (adapter.getItemCount() == 0) {
                    showEmptyState();
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;

                if (dX > 0) {
                    background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), itemView.getBottom());
                } else if (dX < 0) {
                    background.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else {
                    background.setBounds(0, 0, 0, 0);
                }
                background.draw(c);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rvFavorites);
    }

    @Override
    public void onDestroyView() {
        presenter.detach();
        super.onDestroyView();
    }


    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        rvFavorites.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showFavorites(List<Meal> meals) {
        rvFavorites.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        adapter.setList(meals);
    }

    @Override
    public void showEmptyState() {
        rvFavorites.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
    }

    @Override
    public void showErrorState(String message) {
        rvFavorites.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        tvError.setText(message);
    }

    @Override
    public void navigateToDetails(Meal meal) {
        FavouritesFragmentDirections.ActionNavFavouritesToMealDetailsFragment action =
                FavouritesFragmentDirections.actionNavFavouritesToMealDetailsFragment(meal);
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void showDeleteConfirmation(Meal meal, int position) {
        Snackbar snackbar = Snackbar.make(rvFavorites, meal.getName() + " removed from favorites", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", v -> {
            presenter.undoDelete(meal);
        });
        snackbar.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.primary_green));
        snackbar.show();
    }
}