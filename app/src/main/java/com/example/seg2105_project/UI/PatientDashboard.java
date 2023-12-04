package com.example.seg2105_project.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.seg2105_project.DBManager;
import com.example.seg2105_project.R;
import com.example.seg2105_project.UserType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.AdapterView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SearchView;


public class PatientDashboard extends AppCompatActivity {
    private UserType userType;
    private DBManager db;
    private TextView userTypeText;
    private Button logoutBtn;
    LinearLayout appointmentView;

    LinearLayout mainView;

    int patient_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_dashboard);


        mainView = (LinearLayout) findViewById(R.id.mainView);


        userTypeText = findViewById(R.id.user_type_text);


        FloatingActionButton home = findViewById(R.id.doctorHome);
        FloatingActionButton settings = findViewById(R.id.setting);

        logoutBtn = findViewById(R.id.logout_button);


        Intent intent = getIntent();
        if (intent.hasExtra("userType")) {
            if (intent.getStringExtra("approved").equalsIgnoreCase("true")) {
                userTypeText.append(intent.getStringExtra("userType"));
            } else {
                if (intent.getStringExtra("rejected").equalsIgnoreCase("true")) {
                    userTypeText.setText("Your registration request has been rejected by the administrator. Please contact them via email: admin@admin.com or phone: +1 314 142 2953");
                } else {
                    userTypeText.setText(("Your account hasn't been approved yet"));
                }
            }


        }
        patient_id = Integer.parseInt(intent.getStringExtra("id"));


        home.setOnClickListener(view -> {
            showSettings(false);
            showAppointments();
        });

        settings.setOnClickListener(view -> {
            showSettings(true);
        });

        logoutBtn.bringToFront();
        logoutBtn.setOnClickListener(view -> {
            finish();
        });

    }


    void showSettings(boolean visibility) {
        if (visibility) {
            mainView.removeAllViews();
            userTypeText.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.VISIBLE);
        } else {
            userTypeText.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.GONE);
        }
    }


    //showAcceptBtn --> booking
    //showCancelBtn --> opt out only if user cancels before 60 minutes
    private void displayAppointments(String specialties) {
        if (appointmentView != null) {
            mainView.removeView(appointmentView);
        }

        appointmentView = new LinearLayout(this);
        mainView.addView(appointmentView);




        ArrayList<HashMap<String, Object>> specialt = db.searchAppointment(specialties);



        for (int i = 0; i < specialt.size(); i++) {
            FrameLayout container = new FrameLayout(this);

            ImageView rectangle = new ImageView(this);
            rectangle.setImageResource(R.drawable.curve);

            FrameLayout.LayoutParams imageLayoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );

            container.addView(rectangle, imageLayoutParams);

            TextView shiftInfo = new TextView(this);

            HashMap<String, Object> shift = specialt.get(i);

            //append appoint info
            for (String col : shift.keySet()) {
                if (col.equalsIgnoreCase("id") || col.equalsIgnoreCase("patient_id")) continue;
                shiftInfo.append(col + ": " + shift.get(col) + "\n");
            }

            FrameLayout.LayoutParams infoLayoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );


            infoLayoutParams.gravity = Gravity.CENTER;
            container.addView(shiftInfo, infoLayoutParams);

//
            Button cancelBtn = new Button(this);
            cancelBtn.setText("Cancel");

            cancelBtn.setOnClickListener(view -> {
                db.deleteShift(Integer.parseInt(shift.get("id").toString()));
                appointmentView.removeView(container);
            });

            FrameLayout.LayoutParams cancelLayoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );

            cancelLayoutParams.gravity = Gravity.TOP | Gravity.END;

            container.addView(cancelBtn, cancelLayoutParams);

            appointmentView.addView(container);
            Button approvedAppointmentsBtn = new Button(this);
            approvedAppointmentsBtn.setText("Approve");
            mainView.addView(approvedAppointmentsBtn);


            Button rejectedAppointmentsBtn = new Button(this);
            rejectedAppointmentsBtn.setText("Reject");
            mainView.addView(rejectedAppointmentsBtn);

            approvedAppointmentsBtn.setOnClickListener(view -> {
                appointmentView.removeAllViews();
                db.createAppointments(patient_id, doctorid, starttime, endtime);

            });



            rejectedAppointmentsBtn.setOnClickListener(view -> {
                appointmentView.removeAllViews();

                db.cancelapointment(patient_id, doctorid, starttime, endtime);

            });
        }

    }
    String selectedValue;

    void showAppointments() {
        showSettings(false);
        mainView.removeAllViews();

        Button upcomingAppointmentsBtn = new Button(this);
        SearchView searchView = new SearchView(this);

        Button pastAppointments = new Button(this);


        Spinner dropdown = new Spinner(this);
        String[] items = new String[]{"Family Medicine", "Internal Medicine", "Pediatrics", "Obstetrics", "Gynecology"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        SearchView find = new SearchView(this);

        upcomingAppointmentsBtn.setText("Upcoming Appointments");
        pastAppointments.setText("Past Appointments");

        appointmentView = new LinearLayout(this);
        mainView.addView(upcomingAppointmentsBtn);
        mainView.addView(pastAppointments);
        mainView.addView(dropdown);


        mainView.addView(find);


        mainView.addView(appointmentView);

        TextView message = new TextView(this);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                selectedValue = (String) parentView.getItemAtPosition(position);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });



        upcomingAppointmentsBtn.setOnClickListener(view -> {


            displayAppointments(selectedValue);

        });


        pastAppointments.setOnClickListener(v -> {
            ArrayList<HashMap<String, Object>> pendingAppointments = db.getPastAppointments(-1, patient_id);

            for (int i = 0; i < pendingAppointments.size(); i++) {
                HashMap<String, Object> appointment = pendingAppointments.get(i);


                //NotSure what this do
                db.approveAppointment(Integer.parseInt(appointment.get("id").toString()));
            }

        });
    }


}