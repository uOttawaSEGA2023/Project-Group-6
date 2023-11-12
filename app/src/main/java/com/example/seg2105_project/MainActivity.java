package com.example.seg2105_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.view.View;

import com.example.seg2105_project.UI.AdminDashboard;
import com.example.seg2105_project.UI.SignUpPage;
import com.example.seg2105_project.UI.UserDashboard;
import com.google.android.material.snackbar.Snackbar;

import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private View view;
    private Intent adminDashboard;
    private Intent userDashboard;

    private Intent signUpIntent;
    private Button signUpBtn;
    private Button loginBtn;
    private EditText email;
    private EditText password;
    private DBManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = new DBManager(this).open();
        view = getWindow().getDecorView().getRootView();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtn = findViewById(R.id.login);
        signUpBtn = findViewById(R.id.signUp);
        email = findViewById(R.id.email_input);
        password = findViewById(R.id.password_input);

        adminDashboard = new Intent(this, AdminDashboard.class);
        userDashboard = new Intent(this, UserDashboard.class);

        signUpIntent = new Intent(this, SignUpPage.class);

        signUpBtn.setOnClickListener(view -> {
            startActivity(signUpIntent);
        });

        loginBtn.setOnClickListener(view -> {


            startActivity(userDashboard);
        });
    }
    public boolean validEmail(String email) {
        //valid email format
        String emailRegex = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.+[a-zA-Z]+";
        return Pattern.matches(emailRegex, email);
    }

    public boolean validPassword(String password) {
        //Minimum 8 characters, min one uppercase letter, min one number
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d).{8,}$";
        return Pattern.matches(passwordRegex, password);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // close the database connection
        if (db != null) {
            db.close();
        }
    }
}