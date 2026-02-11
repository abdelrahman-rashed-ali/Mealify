package com.rashed.mealify.ui.home.MealDetailsFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.rashed.mealify.R;
import com.rashed.mealify.common.Result;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.usecases.meal.CheckMealStatusUseCase;
import com.rashed.mealify.domain.usecases.meal.ManagePlanUseCase;
import com.rashed.mealify.domain.usecases.meal.ToggleFavoriteUseCase;
import com.rashed.mealify.ui.home.MealDetailsFragment.adapter.IngredientsAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MealDetailsFragment extends Fragment {

    private Meal currentMeal;
    private int currentStepIndex = 0;
    private List<String> instructionSteps = new ArrayList<>();

    private YouTubePlayerView youtubePlayerView;
    private ImageView imgHeader;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private TextView tvArea, tvCategory, tvStepCounter, tvInstructionText, tvVideoTitle;
    private RecyclerView rvIngredients;
    private Button btnPrevStep, btnNextStep;
    private CardView cardVideo;

    private FloatingActionButton fabFavorite;
    private ExtendedFloatingActionButton fabPlan;

    private boolean isFavorite = false;
    private final String userId = "current_user_id";
    private ToggleFavoriteUseCase toggleFavoriteUseCase;
    private CheckMealStatusUseCase checkMealStatusUseCase;
    private ManagePlanUseCase managePlanUseCase;
    private IngredientsAdapter ingredientsAdapter;

    private YouTubePlayer mYouTubePlayer;
    private String pendingVideoId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meal_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDependencies();
        initViews(view);

        if (getArguments() != null) {
            MealDetailsFragmentArgs args = MealDetailsFragmentArgs.fromBundle(getArguments());
            currentMeal = args.getMeal();
            if (currentMeal != null) {
                bindMealData(currentMeal);
                checkInitialState();
            }
        }
    }

    private void initDependencies() {
        com.rashed.mealify.domain.repository.MealRepository repo =
                new com.rashed.mealify.datasource.repository.MealRepositoryImpl(requireContext());
        toggleFavoriteUseCase = new ToggleFavoriteUseCase(repo);
        checkMealStatusUseCase = new CheckMealStatusUseCase(repo);
        managePlanUseCase = new ManagePlanUseCase(repo);
    }

    private void initViews(View view) {
        imgHeader = view.findViewById(R.id.img_meal_detail);
        toolbar = view.findViewById(R.id.toolbar);
        collapsingToolbar = view.findViewById(R.id.collapsing_toolbar);
        tvArea = view.findViewById(R.id.tv_area);
        tvCategory = view.findViewById(R.id.tv_category);
        rvIngredients = view.findViewById(R.id.rv_ingredients);
        tvStepCounter = view.findViewById(R.id.tv_step_counter);
        tvInstructionText = view.findViewById(R.id.tv_instruction_step);
        btnPrevStep = view.findViewById(R.id.btn_prev_step);
        btnNextStep = view.findViewById(R.id.btn_next_step);
        cardVideo = view.findViewById(R.id.card_video);
        tvVideoTitle = view.findViewById(R.id.tv_video_title);
        youtubePlayerView = view.findViewById(R.id.youtube_player_view);
        fabFavorite = view.findViewById(R.id.fab_favorite);
        fabPlan = view.findViewById(R.id.fab_plan);

        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        }

        getLifecycle().addObserver(youtubePlayerView);
        setupYoutubePlayer();
        setupActions();

        rvIngredients.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        ingredientsAdapter = new IngredientsAdapter();
        rvIngredients.setAdapter(ingredientsAdapter);

        btnNextStep.setOnClickListener(v -> changeStep(1));
        btnPrevStep.setOnClickListener(v -> changeStep(-1));
    }

    private void checkInitialState() {
        if (currentMeal == null) return;
        new Thread(() -> {
            Result<Boolean> favResult = checkMealStatusUseCase.isFavorite(userId, currentMeal.getId());
            if (favResult instanceof Result.Success) {
                isFavorite = ((Result.Success<Boolean>) favResult).data;
                if (getActivity() != null) {
                    getActivity().runOnUiThread(this::updateFavoriteIcon);
                }
            }
        }).start();
    }

    private void updateFavoriteIcon() {
        if (fabFavorite == null) return;
        fabFavorite.setImageResource(isFavorite ?
                R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
    }

    private void setupActions() {
        fabFavorite.setOnClickListener(v -> {
            if (currentMeal == null) return;
            new Thread(() -> {
                toggleFavoriteUseCase.execute(userId, currentMeal, isFavorite);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        isFavorite = !isFavorite;
                        updateFavoriteIcon();
                        Toast.makeText(getContext(), isFavorite ? "Added to Favorites" : "Removed", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        });

        fabPlan.setOnClickListener(v -> {
            if (currentMeal != null) showDatePickerAndAddMeal();
        });
    }

    private void showDatePickerAndAddMeal() {
        com.google.android.material.datepicker.MaterialDatePicker<Long> datePicker =
                com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select Date for Meal")
                        .setSelection(com.google.android.material.datepicker.MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String formattedDate = sdf.format(new Date(selection));

            String[] types = {"Breakfast", "Lunch", "Dinner"};
            new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Select Meal Type")
                    .setItems(types, (dialog, which) -> {
                        String selectedType = types[which];

                        managePlanUseCase.addMeal(userId, formattedDate, selectedType, currentMeal, new ManagePlanUseCase.PlanCallback<Void>() {
                            @Override
                            public void onSuccess(Void data) {
                                Toast.makeText(getContext(), "Added to " + selectedType + " plan", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(getContext(), "Error adding to plan: " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).show();
        });
        datePicker.show(getChildFragmentManager(), "DATE_PICKER");
    }

    private void bindMealData(Meal meal) {
        collapsingToolbar.setTitle(meal.getName());
        toolbar.setTitle("");
        Glide.with(this).load(meal.getThumbUrl()).centerCrop().into(imgHeader);
        tvArea.setText(meal.getArea() != null ? meal.getArea() : "");
        tvCategory.setText(meal.getCategory() != null ? meal.getCategory() : "");
        ingredientsAdapter.setList(meal.getIngredients());
        parseInstructions(meal.getInstructions());
        loadVideo(meal.getYoutubeUrl());
    }

    private void parseInstructions(String rawInstructions) {
        instructionSteps.clear();
        if (rawInstructions != null && !rawInstructions.isEmpty()) {
            String normalized = rawInstructions.replace("\r\n", "\n").replace("\r", "\n");
            String[] split = normalized.split("\n");
            for (String s : split) {
                String step = s.trim();
                if (step.length() > 3) {
                    step = step.replaceAll("^\\d+[.)]\\s*", "");
                    instructionSteps.add(step);
                }
            }
        }
        if (instructionSteps.isEmpty()) instructionSteps.add("No instructions available.");
        currentStepIndex = 0;
        updateStepUI(false, 0);
    }

    private void changeStep(int direction) {
        int nextIndex = currentStepIndex + direction;
        if (nextIndex >= 0 && nextIndex < instructionSteps.size()) {
            currentStepIndex = nextIndex;
            updateStepUI(true, direction);
        }
    }

    private void updateStepUI(boolean animate, int direction) {
        tvStepCounter.setText(String.format(Locale.US, "Step %d of %d", currentStepIndex + 1, instructionSteps.size()));
        btnPrevStep.setEnabled(currentStepIndex > 0);
        btnNextStep.setEnabled(currentStepIndex < instructionSteps.size() - 1);
        String stepText = instructionSteps.get(currentStepIndex);

        if (animate) {
            float translationOut = (direction > 0) ? -100f : 100f;
            float translationIn = (direction > 0) ? 100f : -100f;
            tvInstructionText.animate()
                    .alpha(0f).translationX(translationOut)
                    .setDuration(150).setInterpolator(new AccelerateDecelerateInterpolator())
                    .withEndAction(() -> {
                        tvInstructionText.setText(stepText);
                        tvInstructionText.setTranslationX(translationIn);
                        tvInstructionText.animate().alpha(1f).translationX(0f)
                                .setDuration(150).setInterpolator(new AccelerateDecelerateInterpolator())
                                .start();
                    }).start();
        } else {
            tvInstructionText.setText(stepText);
        }
    }

    private void setupYoutubePlayer() {
        IFramePlayerOptions options = new IFramePlayerOptions.Builder(getContext()).controls(1).fullscreen(0).build();
        youtubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                mYouTubePlayer = youTubePlayer;
                if (pendingVideoId != null) {
                    mYouTubePlayer.cueVideo(pendingVideoId, 0);
                    pendingVideoId = null;
                }
            }
        }, options);
    }

    private void loadVideo(String youtubeUrl) {
        String videoId = extractYoutubeId(youtubeUrl);
        if (videoId == null) {
            cardVideo.setVisibility(View.GONE);
            tvVideoTitle.setVisibility(View.GONE);
            return;
        }
        cardVideo.setVisibility(View.VISIBLE);
        tvVideoTitle.setVisibility(View.VISIBLE);
        if (mYouTubePlayer != null) mYouTubePlayer.cueVideo(videoId, 0);
        else pendingVideoId = videoId;
    }

    private String extractYoutubeId(String url) {
        if (url == null || url.trim().isEmpty()) return null;
        try {
            android.net.Uri uri = android.net.Uri.parse(url);
            if (uri.getHost() != null && uri.getHost().contains("youtu.be")) return uri.getLastPathSegment();
            String v = uri.getQueryParameter("v");
            if (v != null && !v.isEmpty()) return v;
            List<String> segments = uri.getPathSegments();
            if (segments != null) {
                int embedIndex = segments.indexOf("embed");
                if (embedIndex != -1 && segments.size() > embedIndex + 1) return segments.get(embedIndex + 1);
            }
        } catch (Exception ignored) {}
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        youtubePlayerView.release();
        mYouTubePlayer = null;
    }
}