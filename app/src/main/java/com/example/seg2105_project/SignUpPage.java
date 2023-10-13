package com.example.seg2105_project;
import android.widget.EditText;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
        Specialties = findViewById(R.id.specialties_input);
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
}