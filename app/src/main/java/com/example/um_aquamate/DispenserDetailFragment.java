package com.example.um_aquamate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

public class DispenserDetailFragment extends Fragment {

    private AquaMateViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dispenser_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AquaMateViewModel.class);

        TextView tvTitle = view.findViewById(R.id.tvDetailTitle);
        TextView tvStatus = view.findViewById(R.id.tvDetailStatus);
        TextView tvTemp = view.findViewById(R.id.tvDetailTemp);
        TextView tvCleanliness = view.findViewById(R.id.tvDetailCleanliness);
        Button btnDirections = view.findViewById(R.id.btnDirections);
        Button btnReport = view.findViewById(R.id.btnGoToReport);

        viewModel.getSelectedDispenser().observe(getViewLifecycleOwner(), dispenser -> {
            if (dispenser == null) return;

            tvTitle.setText(dispenser.getName());
            tvTemp.setText(dispenser.getTempSummary());
            tvCleanliness.setText(String.format("Cleanliness Rating: %.1f / 5.0 (%d reviews)",
                    dispenser.getRating(), dispenser.getReviewCount()));
            btnDirections.setText(String.format("Walk Here (%dm)", dispenser.getDistanceMeters()));

            // Update status text & accessible color
            int colorRes;
            if ("Working".equalsIgnoreCase(dispenser.getStatus())) {
                tvStatus.setText("Status: Working Properly");
                colorRes = R.color.status_working;
            } else if ("Warning".equalsIgnoreCase(dispenser.getStatus())) {
                tvStatus.setText("Status: Needs Attention");
                colorRes = R.color.status_warning;
            } else {
                tvStatus.setText("Status: Out of Service");
                colorRes = R.color.status_broken;
            }
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), colorRes));

            // Launch Google Maps Walking Navigation
            btnDirections.setOnClickListener(v -> {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="
                        + dispenser.getLatitude() + "," + dispenser.getLongitude() + "&mode=w");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    // Fallback to browser map if Google Maps app isn't installed
                    Uri webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination="
                            + dispenser.getLatitude() + "," + dispenser.getLongitude() + "&travelmode=walking");
                    startActivity(new Intent(Intent.ACTION_VIEW, webUri));
                }
            });

            // Navigate to Report screen and pre-fill this dispenser's location
            btnReport.setOnClickListener(v -> {
                viewModel.setPreselectedReportLocation(dispenser.getName());
                Navigation.findNavController(v).navigate(R.id.action_dispenserDetailFragment_to_reportIssueFragment);
            });
        });
    }
}