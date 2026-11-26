package com.example.um_aquamate;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.util.Random;

public class ReportIssueFragment extends Fragment {

    private AquaMateViewModel viewModel;
    private ImageView ivPreview;
    private RadioGroup rgIssueType;
    private boolean hasCapturedPhoto = false;

    // Camera capture launcher
    private final ActivityResultLauncher<Void> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bitmap -> {
                if (bitmap != null) {
                    displayCapturedPhoto(bitmap);
                }
            });

    // Camera permission launcher
    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    takePictureLauncher.launch(null);
                } else {
                    Toast.makeText(requireContext(),
                            "Camera permission is required to capture dispenser photos.",
                            Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report_issue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AquaMateViewModel.class);

        ivPreview = view.findViewById(R.id.ivReportPreview);
        Button btnCapture = view.findViewById(R.id.btnCapturePhoto);
        Spinner spinnerLocation = view.findViewById(R.id.spinnerUmLocation);
        rgIssueType = view.findViewById(R.id.rgIssueType);
        EditText etNotes = view.findViewById(R.id.etReportNotes);
        Button btnSubmit = view.findViewById(R.id.btnSubmitReport);

        // Pre-select location if navigated from DispenserDetailFragment
        viewModel.getPreselectedReportLocation().observe(getViewLifecycleOwner(), targetLocation -> {
            if (targetLocation != null) {
                String[] locations = getResources().getStringArray(R.array.um_locations);
                for (int i = 0; i < locations.length; i++) {
                    if (locations[i].equalsIgnoreCase(targetLocation)) {
                        spinnerLocation.setSelection(i);
                        break;
                    }
                }
            }
        });

        // Capture Photo button
        btnCapture.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                takePictureLauncher.launch(null);
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        // Submit Report button
        btnSubmit.setOnClickListener(v -> {
            int selectedRadioId = rgIssueType.getCheckedRadioButtonId();
            if (selectedRadioId == -1) {
                Toast.makeText(requireContext(),
                        "Please select an issue category before submitting.", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedLocation = spinnerLocation.getSelectedItem().toString();
            RadioButton selectedRadio = view.findViewById(selectedRadioId);
            String issueCategory = selectedRadio.getText().toString();
            String notes = etNotes.getText().toString().trim();

            // Update backend state in ViewModel
            viewModel.submitIssueReport(selectedLocation, issueCategory);

            Toast.makeText(requireContext(),
                    "Report sent to UM JPPHB for " + selectedLocation + "!",
                    Toast.LENGTH_LONG).show();

            // Reset form
            rgIssueType.clearCheck();
            etNotes.setText("");
            ivPreview.setPadding(100, 100, 100, 100);
            ivPreview.setImageResource(R.drawable.ic_camera);
            ivPreview.setColorFilter(ContextCompat.getColor(requireContext(), R.color.um_blue_primary));
            hasCapturedPhoto = false;

            // Return cleanly to the Campus Map tab
            androidx.navigation.NavOptions navOptions = new androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.mapFragment, true)
                    .build();
            androidx.navigation.fragment.NavHostFragment.findNavController(ReportIssueFragment.this)
                    .navigate(R.id.mapFragment, null, navOptions);
        });
    }

    private void displayCapturedPhoto(Bitmap bitmap) {
        hasCapturedPhoto = true;
        ivPreview.clearColorFilter();
        ivPreview.setPadding(0, 0, 0, 0);
        ivPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivPreview.setImageBitmap(bitmap);

        // Simulate AI Auto-Categorization when a photo is snapped
        if (rgIssueType.getCheckedRadioButtonId() == -1) {
            int[] categories = {R.id.rbLeak, R.id.rbDirty, R.id.rbEmpty};
            int aiPick = categories[new Random().nextInt(categories.length)];
            rgIssueType.check(aiPick);
            Toast.makeText(requireContext(),
                    "AI Vision detected issue category automatically!", Toast.LENGTH_SHORT).show();
        }
    }
}