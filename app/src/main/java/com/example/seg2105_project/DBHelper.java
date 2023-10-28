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

        sqLiteDatabase.execSQL(usersTableCreationQuery);
        sqLiteDatabase.execSQL(registrationRequestsTableCreationQuery);

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}