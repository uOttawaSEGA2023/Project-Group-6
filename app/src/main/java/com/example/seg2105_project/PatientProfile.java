package com.example.seg2105_project;
public class PatientProfile {
    private String name;
    private boolean isDeleted;

    public PatientProfile(String name) {
        this.name = name;
        this.isDeleted = false; // Initialize as not deleted
    }

    public String getName() {
        return name;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void deleteProfile() {
        isDeleted = true; // Mark the profile as deleted
        // Optionally, you can perform other cleanup or removal tasks here
    }
}