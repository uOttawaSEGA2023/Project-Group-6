package com.example.seg2105_project;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteTransactionListener;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase connectToDatabase() {
        DBConnector dbConnection = new DBConnector(this);
        SQLiteDatabase db = dbConnection.getWritableDatabase();
        return db;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        SQLiteDatabase db = connectToDatabase();

    }
}