package com.example.seg2105_project;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.Context;

public class DBManager {
    private static DBHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;

    public enum UserType {
        ADMINISTRATOR("admin"),
        DOCTOR("doctor"),
        PATIENT("patient");

        public final String type;

        UserType(String type) {
            this.type = type;
        }

        public static UserType fromString(String type) throws IllegalArgumentException {
            for (UserType userType : UserType.values()) {
                if (userType.type.equalsIgnoreCase(type)) {
                    return userType;
                }
            }
            throw new IllegalArgumentException("No UserType for string: " + type);
        }
    }

    public DBManager(Context context) {
        this.context = context;
    }

    public DBManager open() throws SQLException { // opens connection with the DB
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() { // closes connection with the DB
        db.close();
    }

    public void addPatient(Patient patient) {
        String firstName = patient.getFirstName();
        String lastName = patient.getLastName();
        String email = patient.getEmail();
        String password = patient.getPassword();
        String telephone = patient.getTelephone();
        String address = patient.getAddress();
        String healthCardNumber = patient.getHealthCardNumber();
        String userType = patient.getUserType().type;

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
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", valuesToInsert); // prevents SQL injections
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
        String userType = doctor.getUserType().type;

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
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", valuesToInsert); // prevents SQL injections
    }

    public UserType userExists(String email, String password) throws IllegalArgumentException {
        String[] columnsToRetrieve = new String[]{"user_type"};
        String[] valuesToSearch = new String[]{email, password}; // values to search for in the DB
        Cursor cursor = db.query("users", columnsToRetrieve, "email = ? AND password = ?", valuesToSearch, null, null ,null);
        boolean userExists = cursor.moveToFirst();
        if (userExists) {
            int columnIndex = cursor.getColumnIndex("user_type");
            return UserType.fromString(cursor.getString(columnIndex));
        }
        cursor.close();
        return null;
    }
}
