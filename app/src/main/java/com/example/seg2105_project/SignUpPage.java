package com.example.seg2105_project;
import android.widget.EditText;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class SignUpPage extends AppCompatActivity {
    Button StartSignUp;
    Button PatientTrue;
    Button DoctorTrue;
    private EditText Health;
    private EditText Employee;
    private EditText Specialties;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);


        StartSignUp = (Button)findViewById(R.id.back_input);
        PatientTrue = findViewById(R.id.patient);
        DoctorTrue = findViewById(R.id.doctor);

        Health = findViewById(R.id.healthcard_input);
        Employee = findViewById(R.id.employee_input);
        //Specialties = findViewById(R.id.specialties_input);
        StartSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpPage.this, MainActivity.class);
                startActivity(intent);
            }
        });

        PatientTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle visibility of input fields
                Health.setVisibility(View.VISIBLE);
                Employee.setVisibility(View.GONE);
                Specialties.setVisibility(View.GONE);
            }
        });

        DoctorTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle visibility of input fields
                Health.setVisibility(View.GONE);
                Employee.setVisibility(View.VISIBLE);
                Specialties.setVisibility(View.VISIBLE);
            }
        });

    }

    public boolean validEmail(String email) {
        //valid email format
        String emailRegex = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.+[a-zA-Z]+";
        return Pattern.matches(emailRegex, email);
    }

    public boolean validPhoneNumber(String phoneNumber) {
        //10 digits
        String phoneRegex = "\\d{10}";
        return Pattern.matches(phoneRegex, phoneNumber);
    }

    public boolean validPassword(String password) {
        //Minimum 8 characters, min one uppercase letter, min one number
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d).{8,}$";
        return Pattern.matches(passwordRegex, password);
    }

    public boolean validName(String name) {
        //only letters and spaces
        String nameRegex = "^[a-zA-Z ]+$";
        return Pattern.matches(nameRegex, name);
    }

    public boolean validHealthCard(String healthCard) {
        //only nums and letters and length of 10 to 12
        String healthCardRegex = "^[A-Za-z0-9]{10,12}$";
        return Pattern.matches(healthCardRegex, healthCard);
    }

    public boolean validAddress(String address) {
        //No special chars only nums and letters
        String addressRegex = "^[a-zA-Z0-9]+$";
        return Pattern.matches(addressRegex, address);
    }

    public boolean validEmployeeNumber(String employeeNumber) {
        //Only nums and not empty
        String employeeNumberRegex = "^[0-9]+$";
        return Pattern.matches(employeeNumberRegex, employeeNumber);
    }
}