package com.example.seg2105_project;

public class User {
    public User(String firstName, String lastName, String email, String password, String telephone, String address) {
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setPassword(password);
        setTelephone(telephone);
        setAddress(address);
    }

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String telephone;
    private String address;
    private DBManager.UserType userType;

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public String getAddress() {
        return this.address;
    }


    public DBManager.UserType getUserType() {
        return this.userType;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setUserType(DBManager.UserType userType) {
        this.userType = userType;
    }
}
