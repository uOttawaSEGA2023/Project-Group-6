package com.example.seg2105_project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBConnector extends SQLiteOpenHelper {
    private static final String DB_NAME = "SEG2105DB";
    private static final int DB_VERSION = 1;

    public DBConnector(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String tableCreationQuery = "CREATE TABLE IF NOT EXISTS users (" +
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
        sqLiteDatabase.execSQL(tableCreationQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}