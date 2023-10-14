package com.example.seg2105_project;

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

public class DBOperations {
    private SQLiteDatabase db;

    public enum UserType {
        ADMINISTRATOR("admin"),
        DOCTOR("doctor"),
        PATIENT("patient");

        public final String type;

        private UserType(String type) {
            this.type = type;
        }
    }

    public DBOperations(SQLiteDatabase dbConnection) {
        this.db = dbConnection;
    }

    public void addPatient(Patient patient) {
        String firstName = patient.getFirstName();
        String lastName = patient.getLastName();
        String email = patient.getEmail();
        String password = patient.getPassword();
        String telephone = patient.getTelephone();
        String address = patient.getAddress();
        String healthCardNumber = patient.getHealthCardNumber();
        UserType userType = patient.getUserType();

        Object[] valuesToInsert = new Object[]{firstName, lastName, email, password, telephone, address, healthCardNumber, userType};

        db.execSQL("INSERT INTO users (" +
                    "firstname, " +
                    "lastname, " +
                    "email, " +
                    "password, " +
                    "telephone, " +
                    "address, " +
                    "health_card_number, " +
                    "user_type" +
                ") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", valuesToInsert);
    }

    public void addDoctor(Doctor doctor) {
        String firstName = doctor.getFirstName();
        String lastName = doctor.getLastName();
        String email = doctor.getEmail();
        String password = doctor.getPassword();
        String telephone = doctor.getTelephone();
        String address = doctor.getAddress();
        int employeeNumber = doctor.getEmployeeNumber();
        String specialties = doctor.getSpecialties();
        UserType userType = doctor.getUserType();

        Object[] valuesToInsert = new Object[]{firstName, lastName, email, password, telephone, address, employeeNumber, specialties, userType};

        db.execSQL("INSERT INTO users (" +
                "firstname, " +
                "lastname, " +
                "email, " +
                "password, " +
                "telephone, " +
                "address, " +
                "employee_number, " +
                "specialties, " +
                "user_type" +
                ") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", valuesToInsert);
    }

    public boolean userExists(String email, String password) {
        String[] columnsToRetrieve = new String[]{"email"};
        String[] valuesToSearch = new String[]{email, password}; // values to search for in the DB
        Cursor cursor = db.query("users", columnsToRetrieve, "email = ? AND password = ?", valuesToSearch, null, null ,null);
        boolean userExists = cursor.moveToFirst();
        cursor.close();

        return userExists;
    }
}
