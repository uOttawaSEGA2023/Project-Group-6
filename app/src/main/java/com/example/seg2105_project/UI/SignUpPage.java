package com.example.seg2105_project.UI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.seg2105_project.DBManager;
import com.example.seg2105_project.Doctor;
import com.example.seg2105_project.Patient;
import com.example.seg2105_project.R;
import com.example.seg2105_project.UserType;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

public class SignUpPage extends AppCompatActivity {
    private UserType userType = UserType.PATIENT;
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
    private TextView specialties;

    private DBManager db;

    //Specialty dropdown variables
    ArrayList<Integer> selectedSpecialties = new ArrayList<>();
    String specialtiesArray[] = {"Family Medicine", "Internal Medicine", "Pediatrics", "Obstetrics", "Gynecology"};
    boolean[] bselectedSpecialties;

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

        specialties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bselectedSpecialties = new boolean[specialtiesArray.length];
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpPage.this);
                builder.setTitle("Select Specialties");
                builder.setCancelable(false);

                /** Specialty select select box */

                builder.setMultiChoiceItems(specialtiesArray, bselectedSpecialties, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {
                            selectedSpecialties.add(i);
                            // Sort specialties
                            Collections.sort(selectedSpecialties);
                        } else {
                            // when checkbox unselected
                            selectedSpecialties.remove(Integer.valueOf(i));
                        }
                    }
                });

                /** Ok select specialties */
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Initialize string builder
                        StringBuilder stringBuilder = new StringBuilder();
                        // use for loop
                        for (int j = 0; j < selectedSpecialties.size(); j++) {
                            // concat array value
                            stringBuilder.append(specialtiesArray[selectedSpecialties.get(j)]);
                            // check condition
                            if (j != selectedSpecialties.size() - 1) {
                                // When j value  not equal
                                // to lang list size - 1
                                // add comma
                                stringBuilder.append(", ");
                            }
                        }
                        // set text on textView
                        specialties.setText(stringBuilder.toString());
                    }
                });

                /** cancel */
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // dismiss dialog
                        dialogInterface.dismiss();
                    }
                });

                /** Clear specialties */
                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // use for loop
                        for (int j = 0; j < bselectedSpecialties.length; j++) {
                            // remove all selection
                            bselectedSpecialties[j] = false;
                            // clear language list
                            selectedSpecialties.clear();
                            // clear text view value
                            specialties.setText("");
                        }
                    }
                });

                // show dialog
                builder.show();
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validName(firstName.getText().toString())) {
                    Snackbar.make(view, "Enter a valid first name", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (!validName(lastName.getText().toString())) {
                    Snackbar.make(view, "Enter a valid last name", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (!validEmail(email.getText().toString())) {
                    Snackbar.make(view, "Enter a valid email e.g user@domain.com", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (!validPassword(password.getText().toString())) {
                    Snackbar.make(view, "Password should be 8 characters, One capital letter and one number", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (!validAddress(address.getText().toString())) {
                    Snackbar.make(view, "Enter valid address (no special characters)", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (!validPhoneNumber(phoneNumber.getText().toString())) {
                    Snackbar.make(view, "Enter a valid phone number (10 digits)", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (userType.equals(UserType.DOCTOR)) {
                    if (!validEmployeeNumber(employeeNumber.getText().toString())) {
                        Snackbar.make(view, "Enter a valid Employee number (4-10 digit ID)", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    if (!validSpecialties(specialties.getText().toString())) {
                        Snackbar.make(view, "Please select specialties", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    Doctor doctor = new Doctor(
                            specialties.getText().toString(),
                            firstName.getText().toString(),
                            lastName.getText().toString(),
                            email.getText().toString().toLowerCase(),
                            password.getText().toString(),
                            phoneNumber.getText().toString(),
                            address.getText().toString(),
                            Integer.parseInt(employeeNumber.getText().toString())
                    );
                    db.sendDoctorRegistrationRequest(doctor);
                } else if (userType.equals(UserType.PATIENT)) {
                    if (!validHealthCard(healthCardNumber.getText().toString())) {
                        Snackbar.make(view, "Enter a valid health card number (10-12 numbers)", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    Patient patient = new Patient(
                            healthCardNumber.getText().toString(),
                            firstName.getText().toString(),
                            lastName.getText().toString(),
                            email.getText().toString().toLowerCase(),
                            password.getText().toString(),
                            phoneNumber.getText().toString(),
                            address.getText().toString()
                    );
                    db.sendPatientRegistrationRequest(patient);
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
                userType = UserType.PATIENT;
            }
        });

        isDoctorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle visibility of input fields
                healthCardNumber.setVisibility(View.GONE);
                employeeNumber.setVisibility(View.VISIBLE);
                specialties.setVisibility(View.VISIBLE);
                userType = UserType.DOCTOR;
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
        String addressRegex = "^[0-9A-Za-z\\s\\-\\',.#]+$";
        return Pattern.matches(addressRegex, address);
    }

    public boolean validEmployeeNumber(String employeeNumber) {
        //Only nums and not empty
        String employeeNumberRegex = "^\\d{4,10}$";
        return Pattern.matches(employeeNumberRegex, employeeNumber);
    }

    public boolean validSpecialties(String specialties) {
        return !specialties.isEmpty();
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