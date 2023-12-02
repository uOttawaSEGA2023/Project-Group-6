package com.example.seg2105_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.view.View;

import com.example.seg2105_project.UI.AdminDashboard;
import com.example.seg2105_project.UI.DoctorDashboard;
import com.example.seg2105_project.UI.SignUpPage;
import com.example.seg2105_project.UI.PatientDashboard;
import com.google.android.material.snackbar.Snackbar;

import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private View view;
    private Intent adminDashboard, doctorDashboard, patientDashboard;

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
        doctorDashboard = new Intent(this, DoctorDashboard.class);
        patientDashboard = new Intent(this, DoctorDashboard.class);

        patientDashboard = new Intent(this, PatientDashboard.class);

        signUpIntent = new Intent(this, SignUpPage.class);

        signUpBtn.setOnClickListener(view -> {
            startActivity(signUpIntent);
        });

        loginBtn.setOnClickListener(view -> {

            if(!validEmail( email.getText().toString())){ Snackbar.make(view,"Enter a valid email e.g user@domain.com",Snackbar.LENGTH_SHORT).show(); return;}
            if(!validPassword( password.getText().toString())){ Snackbar.make(view,"Password should be 8 characters, One capital letter and one number",Snackbar.LENGTH_SHORT).show(); return;}

            Map<String, String> userType = db.userExists(email.getText().toString().toLowerCase(), password.getText().toString());

            if (!userType.isEmpty()) { // user exists
                if(userType.get("user_type").equalsIgnoreCase("admin")){
                    adminDashboard.putExtra("userType", userType.get("user_type"));
                    startActivity(adminDashboard);
                    return;
                }

                doctorDashboard.putExtra("id", userType.get("id"));
                doctorDashboard.putExtra("userType", userType.get("user_type"));
                doctorDashboard.putExtra("approved", userType.get("approved"));
                doctorDashboard.putExtra("rejected", userType.get("rejected"));

                startActivity(doctorDashboard);
                startActivity(patientDashboard);

            } else { // user does not exist -> invalid credentials
                Snackbar.make(view, "Invalid email or password.", Snackbar.LENGTH_SHORT).show();
            }



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