package com.example.um_aquamate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class ProfileFragment extends Fragment {

    private AquaMateViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AquaMateViewModel.class);

        // Note: Add android:id="@+id/tvProfileStats" to the stats TextView in fragment_profile.xml
        TextView tvStats = view.findViewById(R.id.tvProfileStats);
        SwitchCompat switchAccessibility = view.findViewById(R.id.switchAccessibility);

        // Observe live report count & plastic bottles saved
        viewModel.getReportCount().observe(getViewLifecycleOwner(), count -> {
            if (tvStats != null && count != null) {
                int bottlesSaved = count * 3;
                tvStats.setText(count + " Reports Verified • ~" + bottlesSaved + " Plastic Bottles Saved");
            }
        });

        // Observe & toggle High-Contrast Mode
        Boolean isHighContrast = viewModel.getHighContrastMode().getValue();
        if (isHighContrast != null) {
            switchAccessibility.setChecked(isHighContrast);
        }

        switchAccessibility.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setHighContrastMode(isChecked);
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Toast.makeText(requireContext(), "High-Contrast Dark Mode Enabled", Toast.LENGTH_SHORT).show();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(requireContext(), "Standard Light Mode Enabled", Toast.LENGTH_SHORT).show();
            }
        });
    }
}