package com.rashed.mealify.ui.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ramotion.paperonboarding.PaperOnboardingFragment;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.rashed.mealify.R;
import com.rashed.mealify.ui.authentication.AuthActivity;

import java.util.ArrayList;

public class onBoardingActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isFirstTime()) {
            navigateToAuth();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_on_boarding);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fragmentManager = getSupportFragmentManager();

        setupOnboardingFragment();
    }

    private void setupOnboardingFragment() {
        int colorTransparent = Color.TRANSPARENT;

        PaperOnboardingPage scr1 = new PaperOnboardingPage(
                "Welcome to Mealify",
                "Your personal meal planning assistant.",
                colorTransparent,
                R.drawable.ic_food,
                R.drawable.ic_food
        );

        PaperOnboardingPage scr2 = new PaperOnboardingPage(
                "Plan Your Week",
                "Easily schedule breakfast, lunch, and dinner.",
                colorTransparent,
                R.drawable.ic_calendar,
                R.drawable.ic_calendar
        );

        PaperOnboardingPage scr3 = new PaperOnboardingPage(
                "Sync Anywhere",
                "Access your meals from any device, anytime.",
                colorTransparent,
                R.drawable.ic_cloud_sync,
                R.drawable.ic_cloud_sync
        );

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);

        PaperOnboardingFragment onBoardingFragment = PaperOnboardingFragment.newInstance(elements);

        onBoardingFragment.setOnRightOutListener(() -> {
            setFirstTimeFinished();
            navigateToAuth();
        });

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, onBoardingFragment);
        fragmentTransaction.commit();
    }

    private boolean isFirstTime() {
        SharedPreferences preferences = getSharedPreferences("MealifyPrefs", MODE_PRIVATE);
        return preferences.getBoolean("isFirstTime", true);
    }

    private void setFirstTimeFinished() {
        SharedPreferences preferences = getSharedPreferences("MealifyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isFirstTime", false);
        editor.apply();
    }

    private void navigateToAuth() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }
}