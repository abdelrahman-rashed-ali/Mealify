package com.rashed.mealify.ui.home.PlannerFragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.rashed.mealify.R;
import com.rashed.mealify.datasource.repository.MealRepositoryImpl;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.repository.MealRepository;
import com.rashed.mealify.domain.usecases.meal.ManagePlanUseCase;
import com.rashed.mealify.ui.home.FavouritesFragment.FavouritesFragmentDirections;
import com.rashed.mealify.ui.home.PlannerFragment.adapters.PlannerAdapter;

import java.util.Date;
import java.util.List;

public class PlannerFragment extends Fragment implements PlannerContract.View {

    private PlannerContract.Presenter presenter;

    private TextView tvDateDisplay;
    private RecyclerView rvPlanner;
    private LinearLayout layoutEmptyState;
    private CardView cardDatePicker;

    private PlannerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_planner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initPresenter();

        presenter.setDate(new Date());
    }

    private void initViews(View view) {
        tvDateDisplay = view.findViewById(R.id.tv_date_display);
        rvPlanner = view.findViewById(R.id.rv_planner);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        cardDatePicker = view.findViewById(R.id.card_date_picker);

        adapter = new PlannerAdapter(requireContext(), item ->  presenter.onMealClicked(item.meal) );
        rvPlanner.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPlanner.setAdapter(adapter);

        cardDatePicker.setOnClickListener(v -> showDatePicker());

        setupSwipeToDelete();
    }

    private void initPresenter() {
        MealRepository repo = new MealRepositoryImpl(requireContext());
        ManagePlanUseCase useCase = new ManagePlanUseCase(repo);
        presenter = new PlannerPresenter(useCase);
        presenter.attach(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.loadPlan();
    }


    @Override
    public void showDate(String dateString) {
        tvDateDisplay.setText(dateString);
    }

    @Override
    public void showPlanList(List<ManagePlanUseCase.PlannedMealDomain> planList) {
        adapter.setData(planList);
    }

    @Override
    public void showEmptyState() {
        rvPlanner.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.VISIBLE);
    }

    @Override
    public void showContent() {
        rvPlanner.setVisibility(View.VISIBLE);
        layoutEmptyState.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMealRemovedMessage(ManagePlanUseCase.PlannedMealDomain item, int position) {
        Snackbar snackbar = Snackbar.make(rvPlanner, "Meal removed from plan", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", v -> {
            presenter.undoDelete(item, position);
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    @Override
    public void restoreItemToAdapter(int position, ManagePlanUseCase.PlannedMealDomain item) {
        adapter.addItem(position, item);
        showContent();
    }

    @Override
    public void navigateToDetails(Meal meal) {
        Navigation.findNavController(requireView()).navigate(PlannerFragmentDirections.actionNavPlannerToMealDetailsFragment(meal));
    }

    @Override
    public void showRestoreSuccessMessage() {
    }


    private void showDatePicker() {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Plan Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        picker.addOnPositiveButtonClickListener(selection -> {
            presenter.setDate(new Date(selection));
        });

        picker.show(getParentFragmentManager(), "PLAN_DATE");
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();

                ManagePlanUseCase.PlannedMealDomain item = adapter.getData().get(position);

                adapter.removeItem(position);

                if (adapter.getItemCount() == 0) {
                    showEmptyState();
                }

                presenter.deleteMeal(item, position);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rvPlanner);
    }

    @Override
    public void onDestroyView() {
        presenter.detach();
        super.onDestroyView();
    }
}