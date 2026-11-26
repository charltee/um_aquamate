package com.example.um_aquamate;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

    private AquaMateViewModel viewModel;
    private String currentTempFilter = "ALL";
    private String currentSearchQuery = "";
    private Dispenser highlightedDispenser;

    private Button btnAll, btnCold, btnHot, btnOpenPin;
    private AutoCompleteTextView actvSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AquaMateViewModel.class);

        actvSearch = view.findViewById(R.id.actvCampusSearch);
        btnAll = view.findViewById(R.id.btnFilterAll);
        btnCold = view.findViewById(R.id.btnFilterCold);
        btnHot = view.findViewById(R.id.btnFilterHot);
        btnOpenPin = view.findViewById(R.id.btnOpenPinDetail);

        // Setup Search Autocomplete using string-array
        String[] locations = getResources().getStringArray(R.array.um_locations);
        ArrayAdapter<String> searchAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, locations);
        actvSearch.setAdapter(searchAdapter);

        actvSearch.setOnItemClickListener((parent, v, position, id) -> {
            currentSearchQuery = (String) parent.getItemAtPosition(position);
            applyMapFilters();
        });

        actvSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().trim();
                applyMapFilters();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Filter Button Listeners
        btnAll.setOnClickListener(v -> setFilter("ALL"));
        btnCold.setOnClickListener(v -> setFilter("COLD"));
        btnHot.setOnClickListener(v -> setFilter("HOT"));

        // Observe dispenser updates
        viewModel.getDispenserList().observe(getViewLifecycleOwner(), dispensers -> applyMapFilters());

        // Inspect Pin Button
        btnOpenPin.setOnClickListener(v -> {
            if (highlightedDispenser != null) {
                viewModel.selectDispenser(highlightedDispenser);
                Navigation.findNavController(v).navigate(R.id.action_mapFragment_to_dispenserDetailFragment);
            } else {
                Toast.makeText(requireContext(), "No matching dispenser found.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setFilter(String filter) {
        currentTempFilter = filter;
        int primary = ContextCompat.getColor(requireContext(), R.color.um_blue_primary);
        int accent = ContextCompat.getColor(requireContext(), R.color.um_teal_accent);

        btnAll.setBackgroundTintList(ColorStateList.valueOf(filter.equals("ALL") ? primary : accent));
        btnCold.setBackgroundTintList(ColorStateList.valueOf(filter.equals("COLD") ? primary : accent));
        btnHot.setBackgroundTintList(ColorStateList.valueOf(filter.equals("HOT") ? primary : accent));

        applyMapFilters();
    }

    private void applyMapFilters() {
        List<Dispenser> all = viewModel.getDispenserList().getValue();
        if (all == null) return;

        List<Dispenser> filtered = new ArrayList<>();
        for (Dispenser d : all) {
            boolean matchesTemp = currentTempFilter.equals("ALL")
                    || (currentTempFilter.equals("COLD") && d.hasCold())
                    || (currentTempFilter.equals("HOT") && d.hasHot());

            boolean matchesQuery = currentSearchQuery.isEmpty()
                    || d.getName().toLowerCase().contains(currentSearchQuery.toLowerCase())
                    || d.getFacultyCode().toLowerCase().contains(currentSearchQuery.toLowerCase());

            if (matchesTemp && matchesQuery) {
                filtered.add(d);
            }
        }

        if (!filtered.isEmpty()) {
            highlightedDispenser = filtered.get(0);
            btnOpenPin.setEnabled(true);
            btnOpenPin.setText("Inspect Pin: " + highlightedDispenser.getName()
                    + " (" + filtered.size() + " found)");
        } else {
            highlightedDispenser = null;
            btnOpenPin.setEnabled(false);
            btnOpenPin.setText("No Dispensers Match Filter");
        }
    }
}