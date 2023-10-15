package com.example.seg2105_project;

public class Doctor extends User {
    private String specialties;
    private int employeeNumber;

    public Doctor(String specialties, String firstName, String lastName, String email, String password, String telephone, String address, int employeeNumber) {
        super(firstName, lastName, email, password, telephone, address);
        this.specialties = specialties;
        this.employeeNumber = employeeNumber;
        setUserType(DBManager.UserType.DOCTOR);
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
