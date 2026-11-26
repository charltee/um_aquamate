package com.example.um_aquamate;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class AquaMateViewModel extends ViewModel {

    private final MutableLiveData<List<Dispenser>> dispenserList = new MutableLiveData<>();
    private final MutableLiveData<Dispenser> selectedDispenser = new MutableLiveData<>();
    private final MutableLiveData<String> preselectedReportLocation = new MutableLiveData<>();
    private final MutableLiveData<Integer> reportCount = new MutableLiveData<>(14);
    private final MutableLiveData<Boolean> highContrastMode = new MutableLiveData<>(false);

    public AquaMateViewModel() {
        // Seed initial Universiti Malaya campus dispenser data
        List<Dispenser> initialData = new ArrayList<>();
        initialData.add(new Dispenser("1", "FSKTM - Block A (Ground Floor)", "FSKTM", "Working", true, false, 4.6, 28, 120, 3.1282, 101.6506));
        initialData.add(new Dispenser("2", "FSKTM - Block B (Level 1)", "FSKTM", "Working", true, true, 4.8, 19, 180, 3.1285, 101.6510));
        initialData.add(new Dispenser("3", "Main Library (Perpustakaan Utama)", "LIBRARY", "Warning", true, false, 3.9, 42, 350, 3.1209, 101.6538));
        initialData.add(new Dispenser("4", "KK8 (Kolej Kediaman Kinabalu) - Block B", "KK8", "Working", true, true, 4.5, 31, 600, 3.1245, 101.6482));
        initialData.add(new Dispenser("5", "KK12 (Kolej Kediaman Raja Dr. Nazrin Shah)", "KK12", "Working", true, true, 4.7, 54, 750, 3.1298, 101.6455));
        initialData.add(new Dispenser("6", "Faculty of Engineering - Block J", "FK", "Broken", false, false, 2.8, 15, 480, 3.1180, 101.6525));
        initialData.add(new Dispenser("7", "Faculty of Science - Dewan Kuliah 1", "FS", "Working", true, false, 4.4, 22, 410, 3.1221, 101.6552));

        dispenserList.setValue(initialData);
        selectedDispenser.setValue(initialData.get(0));
    }

    public LiveData<List<Dispenser>> getDispenserList() { return dispenserList; }
    public LiveData<Dispenser> getSelectedDispenser() { return selectedDispenser; }
    public void selectDispenser(Dispenser dispenser) { selectedDispenser.setValue(dispenser); }

    public LiveData<String> getPreselectedReportLocation() { return preselectedReportLocation; }
    public void setPreselectedReportLocation(String location) { preselectedReportLocation.setValue(location); }

    public LiveData<Integer> getReportCount() { return reportCount; }
    public LiveData<Boolean> getHighContrastMode() { return highContrastMode; }
    public void setHighContrastMode(boolean enabled) { highContrastMode.setValue(enabled); }

    // Updates dispenser status when a student submits a maintenance report
    public void submitIssueReport(String locationName, String issueCategory) {
        List<Dispenser> current = dispenserList.getValue();
        if (current != null) {
            for (Dispenser d : current) {
                if (d.getName().equalsIgnoreCase(locationName)) {
                    if (issueCategory.contains("No Water") || issueCategory.contains("Leak")) {
                        d.setStatus("Broken");
                    } else {
                        d.setStatus("Warning");
                    }
                    break;
                }
            }
            dispenserList.setValue(current); // Notify observers
        }
        Integer count = reportCount.getValue();
        reportCount.setValue((count != null ? count : 0) + 1);
    }
}