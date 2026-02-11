package com.rashed.mealify;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        View mainLayout = findViewById(R.id.main);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        FloatingActionButton fabHome = findViewById(R.id.fab_home);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }


        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (item.isChecked()) {
                return false;
            }

            NavOptions navOptions = new NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setRestoreState(true)
                    .setEnterAnim(R.anim.nav_enter)
                    .setExitAnim(R.anim.nav_exit)
                    .setPopEnterAnim(R.anim.nav_enter)
                    .setPopExitAnim(R.anim.nav_exit)
                    .build();


            navController.navigate(itemId, null, navOptions);

            return true;
        });


        fabHome.setOnClickListener(v -> {
            if (bottomNav.getSelectedItemId() != R.id.nav_home) {
                bottomNav.setSelectedItemId(R.id.nav_home);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            bottomNav.setPadding(0, 0, 0, systemBars.bottom);

            ViewGroup.MarginLayoutParams fabParams = (ViewGroup.MarginLayoutParams) fabHome.getLayoutParams();
            int originalMargin = (int) (30 * getResources().getDisplayMetrics().density);
            fabParams.bottomMargin = originalMargin + systemBars.bottom;
            fabHome.setLayoutParams(fabParams);

            return insets;
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            MenuItem item = bottomNav.getMenu().findItem(destination.getId());
            if (item != null) {
                item.setChecked(true);
            }
        });
    }
}