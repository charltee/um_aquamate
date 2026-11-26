package com.example.um_aquamate;

public class Dispenser {
    private final String id;
    private final String name;
    private final String facultyCode;
    private String status; // "Working", "Warning", "Broken"
    private final boolean hasCold;
    private final boolean hasHot;
    private final double rating;
    private final int reviewCount;
    private final int distanceMeters;
    private final double latitude;
    private final double longitude;

    public Dispenser(String id, String name, String facultyCode, String status,
                     boolean hasCold, boolean hasHot, double rating, int reviewCount,
                     int distanceMeters, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.facultyCode = facultyCode;
        this.status = status;
        this.hasCold = hasCold;
        this.hasHot = hasHot;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.distanceMeters = distanceMeters;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getFacultyCode() { return facultyCode; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean hasCold() { return hasCold; }
    public boolean hasHot() { return hasHot; }
    public double getRating() { return rating; }
    public int getReviewCount() { return reviewCount; }
    public int getDistanceMeters() { return distanceMeters; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    public String getTempSummary() {
        if (hasCold && hasHot) return "Available: Cold, Hot & Room Temp";
        if (hasCold) return "Available: Cold & Room Temp";
        if (hasHot) return "Available: Hot & Room Temp";
        return "Available: Room Temp Only";
    }
}