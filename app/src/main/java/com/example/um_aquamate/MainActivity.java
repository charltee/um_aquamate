package com.example.um_aquamate;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navHostFragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Define top-level destinations so the Up arrow only shows on sub-screens
            AppBarConfiguration appBarConfig = new AppBarConfiguration.Builder(
                    R.id.mapFragment,
                    R.id.dispenserListFragment,
                    R.id.reportIssueFragment,
                    R.id.profileFragment
            ).build();

            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig);

            BottomNavigationView bottomNav = findViewById(R.id.bottomNavView);
            NavigationUI.setupWithNavController(bottomNav, navController);

            // Ensure tapping any bottom navigation tab pops sub-screens cleanly back to that tab
            bottomNav.setOnItemSelectedListener(item -> {
                NavOptions options = new NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(navController.getGraph().getStartDestinationId(), false)
                        .build();
                try {
                    navController.navigate(item.getItemId(), null, options);
                    return true;
                } catch (IllegalArgumentException e) {
                    return false;
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}