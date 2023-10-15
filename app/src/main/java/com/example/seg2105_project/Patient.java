package com.example.seg2105_project;

public class Patient extends User {
    private String healthCardNumber;

    public Patient(String healthCardNumber, String firstName, String lastName, String email, String password, String telephone, String address) {
        super(firstName, lastName, email, password, telephone, address);
        this.healthCardNumber = healthCardNumber;
        setUserType(DBManager.UserType.PATIENT);
    }

    public void setHealthCardNumber(String healthCardNumber) {
        this.healthCardNumber = healthCardNumber;
    }

    public String getHealthCardNumber() {
        return this.healthCardNumber;
    }
}
