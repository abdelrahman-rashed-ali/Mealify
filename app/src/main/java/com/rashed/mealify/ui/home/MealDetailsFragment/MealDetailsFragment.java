package com.rashed.mealify.ui.home.MealDetailsFragment;

import android.content.Intent; // Added Import
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder; // Added Import
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth; // Added Import
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import com.rashed.mealify.R;
import com.rashed.mealify.datasource.repository.MealRepositoryImpl;
import com.rashed.mealify.domain.model.Meal;
import com.rashed.mealify.domain.usecases.meal.CheckMealStatusUseCase;
import com.rashed.mealify.domain.usecases.meal.ManagePlanUseCase;
import com.rashed.mealify.domain.usecases.meal.ToggleFavoriteUseCase;
import com.rashed.mealify.ui.authentication.AuthActivity;
import com.rashed.mealify.ui.home.MealDetailsFragment.adapter.IngredientsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MealDetailsFragment extends Fragment implements MealDetailsContract.View {

    private MealDetailsContract.Presenter presenter;
    private ImageView imgHeader;
    private Toolbar toolbar;
    private TextView tvTitleHeader, tvArea, tvCategory, tvStepCounter, tvInstructionText, tvVideoTitle;
    private RecyclerView rvIngredients;
    private Button btnPrevStep, btnNextStep;
    private MaterialButton btnFavorite, btnPlan;
    private CardView cardVideo;
    private YouTubePlayerView youtubePlayerView;
    private ProgressBar progressBar;
    private View contentView;
    private IngredientsAdapter ingredientsAdapter;
    private YouTubePlayer mYouTubePlayer;
    private String pendingVideoId;
    private int currentStepIndex = 0;
    private List<String> instructionSteps = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meal_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initPresenter();

        if (getArguments() != null) {
            MealDetailsFragmentArgs args = MealDetailsFragmentArgs.fromBundle(getArguments());
            Meal meal = args.getMeal();
            if (meal != null) {
                presenter.initMealData(meal);
            }
        }
    }

    private void initViews(View view) {
        imgHeader = view.findViewById(R.id.img_meal_detail);
        toolbar = view.findViewById(R.id.toolbar);
        tvTitleHeader = view.findViewById(R.id.tv_meal_title_header);
        tvArea = view.findViewById(R.id.tv_area);
        tvCategory = view.findViewById(R.id.tv_category);
        rvIngredients = view.findViewById(R.id.rv_ingredients);
        tvStepCounter = view.findViewById(R.id.tv_step_counter);
        tvInstructionText = view.findViewById(R.id.tv_instruction_step);
        btnPrevStep = view.findViewById(R.id.btn_prev_step);
        btnNextStep = view.findViewById(R.id.btn_next_step);
        btnFavorite = view.findViewById(R.id.btn_action_favorite);
        btnPlan = view.findViewById(R.id.btn_action_plan);
        cardVideo = view.findViewById(R.id.card_video);
        tvVideoTitle = view.findViewById(R.id.tv_video_title);
        youtubePlayerView = view.findViewById(R.id.youtube_player_view);
        progressBar = view.findViewById(R.id.progress_bar);
        contentView = view.findViewById(R.id.nested_scroll_view);

        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        }

        rvIngredients.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        ingredientsAdapter = new IngredientsAdapter();
        rvIngredients.setAdapter(ingredientsAdapter);

        btnFavorite.setOnClickListener(v -> presenter.toggleFavorite());
        btnPlan.setOnClickListener(v -> presenter.addToPlan(MaterialDatePicker.todayInUtcMilliseconds(), "Check")); // Modified to check permissions first


        btnPlan.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null && FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
                showGuestModeDialog();
            } else {
                showPlanDatePicker();
            }
        });

        btnNextStep.setOnClickListener(v -> changeStep(1));
        btnPrevStep.setOnClickListener(v -> changeStep(-1));

        getLifecycle().addObserver(youtubePlayerView);
        setupYoutubePlayer();
    }

    private void initPresenter() {
        MealRepositoryImpl repo = new MealRepositoryImpl(requireContext());
        presenter = new MealDetailsPresenter(
                new CheckMealStatusUseCase(repo),
                new ToggleFavoriteUseCase(repo),
                new ManagePlanUseCase(repo)
        );
        presenter.attach(this);
    }



    @Override
    public void showGuestModeDialog() {
        if (getContext() == null) return;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sign in Required")
                .setMessage("To save your favorite meals and create weekly plans, you need to sign in with an account.")
                .setIcon(R.drawable.ic_favorite_filled) // Optional: Add an icon if you have one
                .setPositiveButton("Sign In", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();

                    Intent intent = new Intent(requireContext(), AuthActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }


    @Override
    public void displayMealDetails(Meal meal) {
        tvTitleHeader.setText(meal.getName());
        Glide.with(this).load(meal.getThumbUrl()).centerCrop().into(imgHeader);
        tvArea.setText(meal.getArea() != null ? meal.getArea() : "Unknown");
        tvCategory.setText(meal.getCategory() != null ? meal.getCategory() : "Unknown");
        ingredientsAdapter.setList(meal.getIngredients());
        parseInstructions(meal.getInstructions());
        loadVideo(meal.getYoutubeUrl());
    }

    @Override
    public void updateFavoriteIcon(boolean isFavorite) {
        if (isFavorite) {
            btnFavorite.setIconResource(R.drawable.ic_favorite_filled);
            btnFavorite.setIconTintResource(R.color.error_red);
            btnFavorite.setText("Saved");
        } else {
            btnFavorite.setIconResource(R.drawable.ic_favorite_border);
            btnFavorite.setIconTintResource(R.color.white);
            btnFavorite.setText("Favorite");
        }
    }

    @Override
    public void showMessage(String message) {
        if (contentView != null) {
            Snackbar.make(contentView, message, Snackbar.LENGTH_SHORT)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                    .show();
        }
    }

    @Override
    public void showPlanDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date for Meal")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(this::showMealTypeDialog);
        datePicker.show(getChildFragmentManager(), "DATE_PICKER");
    }

    @Override
    public void showMealTypeDialog(long dateSelection) {
        String[] types = {"Breakfast", "Lunch", "Dinner"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Select Meal Type")
                .setItems(types, (dialog, which) -> {
                    presenter.addToPlan(dateSelection, types[which]);
                }).show();
    }

    @Override
    public void showLoading() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (contentView != null) contentView.setAlpha(0.3f);
    }

    @Override
    public void hideLoading() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
        if (contentView != null) contentView.setAlpha(1.0f);
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
        tvStepCounter.setText(String.format(Locale.US, "STEP %d OF %d", currentStepIndex + 1, instructionSteps.size()));
        btnPrevStep.setVisibility(currentStepIndex > 0 ? View.VISIBLE : View.INVISIBLE);
        btnNextStep.setText(currentStepIndex == instructionSteps.size() - 1 ? "Finished" : "Next Step");

        String stepText = instructionSteps.get(currentStepIndex);

        if (animate) {
            float translationOut = (direction > 0) ? -50f : 50f;
            float translationIn = (direction > 0) ? 50f : -50f;
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
        toggleNavigation(true);
        presenter.detach();
        super.onDestroyView();
        youtubePlayerView.release();
        mYouTubePlayer = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        toggleNavigation(false);
    }

    private void toggleNavigation(boolean show) {
        if (getActivity() == null) return;

        View bottomNav = getActivity().findViewById(R.id.bottom_nav);
        View fabHome = getActivity().findViewById(R.id.fab_home);
        View fabGlow = getActivity().findViewById(R.id.fab_glow);

        int duration = 300;
        float translationY = show ? 0f : (bottomNav != null ? bottomNav.getHeight() : 300f);
        float alpha = show ? 1f : 0f;

        if (show) {
            if (bottomNav != null) bottomNav.setVisibility(View.VISIBLE);
            if (fabHome != null) fabHome.setVisibility(View.VISIBLE);
            if (fabGlow != null) fabGlow.setVisibility(View.VISIBLE);
        }

        if (bottomNav != null) {
            bottomNav.animate()
                    .translationY(translationY)
                    .alpha(alpha)
                    .setDuration(duration)
                    .withEndAction(() -> {
                        // If HIDING: Set GONE after animation ends to reclaim space
                        if (!show) bottomNav.setVisibility(View.GONE);
                    })
                    .start();
        }

        if (fabHome != null) {
            fabHome.animate()
                    .translationY(translationY)
                    .alpha(alpha)
                    .setDuration(duration)
                    .withEndAction(() -> {
                        if (!show) fabHome.setVisibility(View.GONE);
                    })
                    .start();
        }

        if (fabGlow != null) {
            fabGlow.animate()
                    .translationY(translationY)
                    .alpha(alpha)
                    .setDuration(duration)
                    .withEndAction(() -> {
                        if (!show) fabGlow.setVisibility(View.GONE);
                    })
                    .start();
        }
    }
}