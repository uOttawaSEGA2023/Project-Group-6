package com.example.seg2105_project;

import android.widget.EditText;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpPage extends AppCompatActivity {
    private DBManager.UserType userType = DBManager.UserType.PATIENT;
    private Button backBtn;
    private Button isPatientBtn;
    private Button isDoctorBtn;
    private Button submitBtn;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText phoneNumber;
    private EditText address;
    private EditText healthCardNumber;
    private EditText employeeNumber;
    private Spinner specialties;

    private DBManager db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        db = new DBManager(this).open();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        backBtn = findViewById(R.id.back_input);
        isPatientBtn = findViewById(R.id.is_patient);
        isDoctorBtn = findViewById(R.id.is_doctor);
        submitBtn = findViewById(R.id.submit);

        firstName = findViewById(R.id.firstname_input);
        lastName = findViewById(R.id.lastname_input);
        email = findViewById(R.id.email_input);
        password = findViewById(R.id.password_input);
        address = findViewById(R.id.address_input);
        phoneNumber = findViewById(R.id.phonenumber_input);
        healthCardNumber = findViewById(R.id.healthcard_input);
        employeeNumber = findViewById(R.id.employee_input);
        specialties = findViewById(R.id.specialties_input);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userType.equals(DBManager.UserType.DOCTOR)) {
                    Doctor doctor = new Doctor(
                            specialties.getSelectedItem().toString(),
                            firstName.getText().toString(),
                            lastName.getText().toString(),
                            email.getText().toString(),
                            password.getText().toString(),
                            phoneNumber.getText().toString(),
                            address.getText().toString()
                    );
                    db.addDoctor(doctor);
                }
                else if (userType.equals(DBManager.UserType.PATIENT)) {
                    Patient patient = new Patient(
                            healthCardNumber.getText().toString(),
                            firstName.getText().toString(),
                            lastName.getText().toString(),
                            email.getText().toString(),
                            password.getText().toString(),
                            phoneNumber.getText().toString(),
                            address.getText().toString()
                    );
                    db.addPatient(patient);
                }

                finish(); // go back to MainActivity
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        isPatientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle visibility of input fields
                healthCardNumber.setVisibility(View.VISIBLE);
                employeeNumber.setVisibility(View.GONE);
                specialties.setVisibility(View.GONE);
                userType = DBManager.UserType.PATIENT;
            }
        });

        isDoctorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle visibility of input fields
                healthCardNumber.setVisibility(View.GONE);
                employeeNumber.setVisibility(View.VISIBLE);
                specialties.setVisibility(View.VISIBLE);
                userType = DBManager.UserType.DOCTOR;
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