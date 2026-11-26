package com.example.um_aquamate;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import java.util.ArrayList;
import java.util.List;

public class DispenserListFragment extends Fragment {

    private AquaMateViewModel viewModel;
    private final List<Dispenser> currentItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dispenser_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView lvDispensers = view.findViewById(R.id.lvDispensers);
        if (lvDispensers == null) return;

        viewModel = new ViewModelProvider(requireActivity()).get(AquaMateViewModel.class);

        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return currentItems.size();
            }

            @Override
            public Dispenser getItem(int position) {
                return currentItems.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LinearLayout rowLayout;
                TextView tvTitle;
                TextView tvSubtitle;

                if (convertView instanceof LinearLayout) {
                    rowLayout = (LinearLayout) convertView;
                    tvTitle = (TextView) rowLayout.getChildAt(0);
                    tvSubtitle = (TextView) rowLayout.getChildAt(1);
                } else {
                    rowLayout = new LinearLayout(requireContext());
                    rowLayout.setOrientation(LinearLayout.VERTICAL);
                    rowLayout.setPadding(36, 28, 36, 28);
                    rowLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.card_white));

                    tvTitle = new TextView(requireContext());
                    tvTitle.setTextSize(16f);
                    tvTitle.setTypeface(null, Typeface.BOLD);
                    tvTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));

                    tvSubtitle = new TextView(requireContext());
                    tvSubtitle.setTextSize(14f);
                    tvSubtitle.setPadding(0, 8, 0, 0);

                    rowLayout.addView(tvTitle);
                    rowLayout.addView(tvSubtitle);
                }

                Dispenser d = getItem(position);
                tvTitle.setText(d.getName());

                String status = d.getStatus() != null ? d.getStatus() : "Working";
                String statusIcon;
                int colorRes;

                if ("Working".equalsIgnoreCase(status)) {
                    statusIcon = "● Working";
                    colorRes = R.color.status_working;
                } else if ("Warning".equalsIgnoreCase(status)) {
                    statusIcon = "▲ Needs Attention";
                    colorRes = R.color.status_warning;
                } else {
                    statusIcon = "■ Broken";
                    colorRes = R.color.status_broken;
                }

                tvSubtitle.setText(String.format("%s  •  %dm away  •  %s",
                        statusIcon, d.getDistanceMeters(), d.getTempSummary()));
                tvSubtitle.setTextColor(ContextCompat.getColor(requireContext(), colorRes));

                return rowLayout;
            }
        };

        lvDispensers.setAdapter(adapter);

        viewModel.getDispenserList().observe(getViewLifecycleOwner(), dispensers -> {
            currentItems.clear();
            if (dispensers != null) {
                currentItems.addAll(dispensers);
            }
            adapter.notifyDataSetChanged();
        });

        lvDispensers.setOnItemClickListener((parent, itemView, position, id) -> {
            if (position >= 0 && position < currentItems.size()) {
                Dispenser selected = currentItems.get(position);
                viewModel.selectDispenser(selected);
                try {
                    NavHostFragment.findNavController(DispenserListFragment.this)
                            .navigate(R.id.action_dispenserListFragment_to_dispenserDetailFragment);
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "无法打开详情页: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}