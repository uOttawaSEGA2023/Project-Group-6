package com.example.seg2105_project;

public class Patient extends User {
    private String healthCardNumber;

    public Patient() {
        setUserType(DBOperations.UserType.PATIENT);
    }

    public void setHealthCardNumber(String healthCardNumber) {
        this.healthCardNumber = healthCardNumber;
    }

    public String getHealthCardNumber() {
        return this.healthCardNumber;
    }
}
