package com.example.um_aquamate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class DispenserDetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dispenser_detail, container, false);

        Button btnReport = view.findViewById(R.id.btnGoToReport);
        btnReport.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_dispenserDetailFragment_to_reportIssueFragment)
        );

        return view;
    }
}