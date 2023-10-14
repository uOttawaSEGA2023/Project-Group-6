package com.example.seg2105_project;

public class Doctor extends User {
    private String specialties;
    private int employeeNumber;

    public Doctor(String specialties) {
        setUserType(DBOperations.UserType.DOCTOR);
        this.specialties = specialties;
    }

    public String getSpecialties() {
        return this.specialties;
    }

    public int getEmployeeNumber() {
        return this.employeeNumber;
    }

    public void setSpecialties(String specialties) {
        this.specialties = specialties;
    }

    public void setEmployeeNumber(int employeeNumber) {
        this.employeeNumber = employeeNumber;
    }
}
