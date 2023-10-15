package com.example.seg2105_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private View view;
    private Intent homeIntent;
    private Intent signUpIntent;
    private Button signUpBtn;
    private Button loginBtn;
    private EditText email;
    private EditText password;
    private Snackbar invalidCredentialsPopup;
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
        invalidCredentialsPopup = Snackbar.make(view, "Invalid email or password.", Snackbar.LENGTH_SHORT);
        homeIntent = new Intent(this, Home.class);
        signUpIntent = new Intent(this, SignUpPage.class);

        signUpBtn.setOnClickListener(view -> {
            startActivity(signUpIntent);
        });

        loginBtn.setOnClickListener(view -> {
            DBManager.UserType userType = db.userExists(email.getText().toString(), password.getText().toString());
            if (userType != null) { // user exists
                homeIntent.putExtra("userType", userType.type);
                startActivity(homeIntent);
            } else { // user does not exist -> invalid credentials
               invalidCredentialsPopup.show();
            }
        });
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