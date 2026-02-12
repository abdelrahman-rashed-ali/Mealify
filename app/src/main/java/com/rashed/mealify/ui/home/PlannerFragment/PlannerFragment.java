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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.rashed.mealify.R;
import com.rashed.mealify.datasource.repository.MealRepositoryImpl;
import com.rashed.mealify.domain.repository.MealRepository;
import com.rashed.mealify.domain.usecases.meal.ManagePlanUseCase;
import com.rashed.mealify.ui.home.PlannerFragment.adapters.PlannerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlannerFragment extends Fragment {

    private TextView tvDateDisplay;
    private RecyclerView rvPlanner;
    private LinearLayout layoutEmptyState;
    private FloatingActionButton fabAdd;

    private PlannerAdapter adapter;
    private ManagePlanUseCase managePlanUseCase;

    private String selectedDate;
    private final String userId = "current_user_id";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_planner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDependencies();
        initViews(view);

        updateDate(new Date());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPlan();
    }

    private void initDependencies() {
        MealRepository repo = new MealRepositoryImpl(requireContext());
        managePlanUseCase = new ManagePlanUseCase(repo);
    }

    private void initViews(View view) {
        tvDateDisplay = view.findViewById(R.id.tv_date_display);
        rvPlanner = view.findViewById(R.id.rv_planner);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);

        adapter = new PlannerAdapter(requireContext(), item -> {

        });
        rvPlanner.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPlanner.setAdapter(adapter);

        view.findViewById(R.id.card_date_picker).setOnClickListener(v -> showDatePicker());

        setupSwipeToDelete();

    }

    private void updateDate(Date date) {
        SimpleDateFormat displayFormat = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.US);
        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        tvDateDisplay.setText(displayFormat.format(date));
        selectedDate = dbFormat.format(date);

        loadPlan();
    }

    private void loadPlan() {
        managePlanUseCase.getPlanForDay(userId, selectedDate, new ManagePlanUseCase.PlanCallback<List<ManagePlanUseCase.PlannedMealDomain>>() {
            @Override
            public void onSuccess(List<ManagePlanUseCase.PlannedMealDomain> data) {
                if (data.isEmpty()) {
                    rvPlanner.setVisibility(View.GONE);
                    layoutEmptyState.setVisibility(View.VISIBLE);
                } else {
                    rvPlanner.setVisibility(View.VISIBLE);
                    layoutEmptyState.setVisibility(View.GONE);
                    adapter.setData(data);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Plan Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        picker.addOnPositiveButtonClickListener(selection -> {
            updateDate(new Date(selection));
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
                    rvPlanner.setVisibility(View.GONE);
                    layoutEmptyState.setVisibility(View.VISIBLE);
                }

                managePlanUseCase.removeMeal(userId, selectedDate, item.meal.getId(), new ManagePlanUseCase.PlanCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) { /* Log success */ }
                    @Override
                    public void onError(String error) { /* Log error */ }
                });

                Snackbar snackbar = Snackbar.make(rvPlanner, "Meal removed from plan", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", v -> {
                    adapter.addItem(position, item);
                    rvPlanner.setVisibility(View.VISIBLE);
                    layoutEmptyState.setVisibility(View.GONE);

                    managePlanUseCase.addMeal(userId, selectedDate, item.type, item.meal, new ManagePlanUseCase.PlanCallback<Void>() {
                        @Override
                        public void onSuccess(Void data) { /* Restored */ }
                        @Override
                        public void onError(String error) {
                            Toast.makeText(getContext(), "Failed to restore", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rvPlanner);
    }
}