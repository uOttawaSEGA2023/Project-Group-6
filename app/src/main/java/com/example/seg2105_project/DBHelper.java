package com.example.seg2105_project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "SEG2105DB.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // CREATING users TABLE
        String usersTableCreationQuery = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "firstname TEXT NOT NULL," +
                "lastname TEXT NOT NULL," +
                "email TEXT NOT NULL," +
                "password TEXT NOT NULL," +
                "telephone TEXT NOT NULL," +
                "address TEXT NOT NULL," +
                "health_card_number TEXT," +
                "employee_number INTEGER," +
                "specialties TEXT," +
                "user_type TEXT CHECK (user_type IN ('admin', 'patient', 'doctor'))" +
                ")";

        // CREATING registration_requests TABLE
        String registrationRequestsTableCreationQuery = "CREATE TABLE IF NOT EXISTS registration_requests (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "firstname TEXT NOT NULL," +
                "lastname TEXT NOT NULL," +
                "email TEXT NOT NULL," +
                "password TEXT NOT NULL," +
                "telephone TEXT NOT NULL," +
                "address TEXT NOT NULL," +
                "health_card_number TEXT," +
                "employee_number INTEGER," +
                "specialties TEXT," +
                "user_type TEXT CHECK (user_type IN ('patient', 'doctor'))," +
                "rejected BOOL NOT NULL DEFAULT 0" + // if rejected == false -> the request has not been reviewed. if rejected == true -> the request was rejected. if the row is gone -> the request was accepted
                ")";

        // CREATING patient_appointments TABLE
        String patientAppointmentsTableCreationQuery = "CREATE TABLE IF NOT EXISTS patient_appointments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "patient_id INTEGER NOT NULL," +
                "doctor_id INTEGER NOT NULL," +
                "shift_id INTEGER NOT NULL," + // shift with the start time and end time
                "rejected BOOL DEFAULT -1" + // if rejected == -1 --> it has not been reviewed yet
                ");";

        // CREATING shifts TABLE
        String shiftsTableCreationQuery = "CREATE TABLE IF NOT EXISTS shifts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "start_time INTEGER NOT NULL," + // UNIX timestamps
                "end_time INTEGER NOT NULL," + // UNIX timestamps
                "doctor_id INTEGER NOT NULL," +
                "patient_id Integer DEFAULT 0" +
                ");";

        sqLiteDatabase.execSQL(usersTableCreationQuery);
        sqLiteDatabase.execSQL(registrationRequestsTableCreationQuery);
        sqLiteDatabase.execSQL(patientAppointmentsTableCreationQuery);
        sqLiteDatabase.execSQL(shiftsTableCreationQuery);

        // inserting default administrator credentials into the DB
        String[] valuesToInsert = new String[]{"Admin", "Admin", "admin@admin.com", "Hello123", "1234567890", "123 Admin Dr.", UserType.ADMINISTRATOR.type};
        sqLiteDatabase.execSQL("INSERT INTO users (" +
                "firstname, " +
                "lastname, " +
                "email, " +
                "password, " +
                "telephone, " +
                "address, " +
                "user_type" +
                ") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)", valuesToInsert);

        // inserting default doctor credentials into the DB
        valuesToInsert = new String[]{"Doctor", "Doctor", "doctor@doctor.com", "Hello123", "1234567890", "123 Admin Dr.", UserType.DOCTOR.type, "Family Medicine"};
        sqLiteDatabase.execSQL("INSERT INTO users (" +
                "firstname, " +
                "lastname, " +
                "email, " +
                "password, " +
                "telephone, " +
                "address, " +
                "user_type," +
                "specialties" +
                ") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?,?)", valuesToInsert);

        // inserting dummy patient credentials into the DB
        valuesToInsert = new String[]{"John", "Doe", "patient@docuapp.com", "Hello123", "1234567890", "123 Hex Dr.", "314226975", UserType.PATIENT.type};
        sqLiteDatabase.execSQL("INSERT INTO users (" +
                "firstname, " +
                "lastname, " +
                "email, " +
                "password, " +
                "telephone, " +
                "address, " +
                "health_card_number," +
                "user_type" +
                ") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", valuesToInsert);

        //Patient appointment
        valuesToInsert = new String[]{"John", "Doe", "patient@docuapp.com", "Hello123", "1234567890", "123 Hex Dr.", "314226975", UserType.PATIENT.type};
        sqLiteDatabase.execSQL("INSERT INTO patient_appointments(patient_id, doctor_id, shift_id,rejected) values(?,?,?,?);",
                new String[]{Integer.toString(3), Integer.toString(1), Integer.toString(1), Integer.toString(-1)});
        //Doctor rating
        // CREATING shifts TABLE
        String createRatingTable = "CREATE TABLE IF NOT EXISTS ratings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "doctor_id INTEGER NOT NULL," +
                "patient_id INTEGER NOT NULL," +
                "rating INTEGER NOT NULL" +
                ");";

        sqLiteDatabase.execSQL(createRatingTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}