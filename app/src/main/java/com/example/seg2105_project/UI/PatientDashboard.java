package com.example.seg2105_project.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
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
import com.google.android.material.snackbar.Snackbar;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SearchView;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

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
        db = new DBManager(this).open();

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


        ArrayList<HashMap<String, Object>> appointmentSlots = db.searchAppointment(specialties);

        if (appointmentSlots == null) {
            Snackbar.make(appointmentView, "No available appointment with a " + selectedValue + " doctor!", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (appointmentSlots.size() == 0) {
            Snackbar.make(appointmentView, "No available appointment with " + selectedValue + " doctor!", Snackbar.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < appointmentSlots.size(); i++) {
            if (appointmentSlots.get(i) == null) {
                Snackbar.make(appointmentView, "No appointments found", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (appointmentSlots.get(i).size() == 0) {
                Snackbar.make(appointmentView, "No appointments found", Snackbar.LENGTH_SHORT).show();
                return;
            }

            int doctor_id = Integer.parseInt(appointmentSlots.get(i).get("doctor_id").toString());
            int shift_id = Integer.parseInt(appointmentSlots.get(i).get("shift_id").toString());

            FrameLayout container = new FrameLayout(this);

            ImageView rectangle = new ImageView(this);
            rectangle.setImageResource(R.drawable.curve);

            FrameLayout.LayoutParams imageLayoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );

            container.addView(rectangle, imageLayoutParams);

            TextView shiftInfo = new TextView(this);

            HashMap<String, Object> shift = appointmentSlots.get(i);

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

            Button approvedAppointmentsBtn = new Button(this);
            approvedAppointmentsBtn.setText("Take appointment");

            approvedAppointmentsBtn.setOnClickListener(view -> {
                appointmentView.removeAllViews();
                db.createAppointments(patient_id, doctor_id, shift_id);

            });

            FrameLayout.LayoutParams acceptLayoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            acceptLayoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER;

            container.addView(approvedAppointmentsBtn, acceptLayoutParams);

            appointmentView.addView(container);
        }

    }

    private static long calculateDifferenceInMinutes(Date startDate, Date endDate) {
        long startMillis = startDate.getTime();
        long endMillis = endDate.getTime();
        long millisDifference = endMillis - startMillis;

        // Convert milliseconds to minutes
        return millisDifference / (60 * 1000);
    }


    private void displayUpcomingAppointments(ArrayList<HashMap<String, Object>> allUpcomingApponmtents) {
        if (appointmentView != null) {
            mainView.removeView(appointmentView);
        }

        appointmentView = new LinearLayout(this);
        mainView.addView(appointmentView);


        for (int i = 0; i < allUpcomingApponmtents.size(); i++) {
            FrameLayout container = new FrameLayout(this);

            ImageView rectangle = new ImageView(this);
            rectangle.setImageResource(R.drawable.curve);

            FrameLayout.LayoutParams imageLayoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );

            container.addView(rectangle, imageLayoutParams);

            TextView shiftInfo = new TextView(this);

            HashMap<String, Object> shift = allUpcomingApponmtents.get(i);

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

            Button cancelBtn = new Button(this);
            cancelBtn.setText("Cancel");


            cancelBtn.setOnClickListener(view -> {
                db.cancelAppointment(Integer.parseInt(shift.get("id").toString()));
                appointmentView.removeView(container);
            });


            FrameLayout.LayoutParams cancelLayoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );

            cancelLayoutParams.gravity = Gravity.TOP | Gravity.END;
            container.addView(cancelBtn, cancelLayoutParams);
            appointmentView.addView(container);

        }

    }

    private void getPastAppointments(ArrayList<HashMap<String, Object>> appointments) {
        if (appointmentView != null) {
            mainView.removeView(appointmentView);
        }

        appointmentView = new LinearLayout(this);
        mainView.addView(appointmentView);

        for (int i = 0; i < appointments.size(); i++) {
            FrameLayout container = new FrameLayout(this);

            ImageView rectangle = new ImageView(this);
            rectangle.setImageResource(R.drawable.curve);

            FrameLayout.LayoutParams imageLayoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );

            container.addView(rectangle, imageLayoutParams);

            TextView shiftInfo = new TextView(this);

            HashMap<String, Object> shift = appointments.get(i);

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

            Button rateDoctor = new Button(this);
            rateDoctor.setText("Rate Doctor");

            int doctor_id = Integer.parseInt(appointments.get(i).get("doctor_id").toString());
            rateDoctor.setOnClickListener(view -> {
                appointmentView.removeAllViews();
                db.rateDoctor(patient_id, doctor_id, 4);

            });

            FrameLayout.LayoutParams rateLayout = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            rateLayout.gravity = Gravity.BOTTOM | Gravity.CENTER;
            container.addView(rateDoctor, rateLayout);
            appointmentView.addView(container);
        }
    }

    String selectedValue;

    void showAppointments() {
        showSettings(false);
        mainView.removeAllViews();

        Button upcomingAppointmentsBtn = new Button(this);

        Button pastAppointments = new Button(this);

        Spinner dropdown = new Spinner(this);
        String[] items = new String[]{"Family Medicine", "Internal Medicine", "Pediatrics", "Obstetrics", "Gynecology"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        upcomingAppointmentsBtn.setText("Upcoming Appointments");
        pastAppointments.setText("Past Appointments");

        appointmentView = new LinearLayout(this);
        mainView.addView(upcomingAppointmentsBtn);
        mainView.addView(pastAppointments);
        mainView.addView(dropdown);

        mainView.addView(appointmentView);

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

        Button searchBtn = new Button(this);
        searchBtn.setText("Search");

        searchBtn.setOnClickListener(v -> {
            Log.e("selected_val", selectedValue);
            displayAppointments(selectedValue);
        });

        mainView.addView(searchBtn);

        upcomingAppointmentsBtn.setOnClickListener(view -> {

            ArrayList<HashMap<String, Object>> allUpcomingApponmtents = db.getUpcomingAppointments(patient_id);
            if (allUpcomingApponmtents == null) {
                Snackbar.make(view, "No upcoming appointments found", Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (allUpcomingApponmtents.size() == 0) {
                Snackbar.make(view, "No upcoming appointments found", Snackbar.LENGTH_SHORT).show();
                return;
            }
            displayUpcomingAppointments(allUpcomingApponmtents);

        });

        pastAppointments.setOnClickListener(v -> {
            ArrayList<HashMap<String, Object>> allPastAppointments = db.getPastAppointments(patient_id);

            if (allPastAppointments == null) {
                Snackbar.make(v, "No past appointments found", Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (allPastAppointments.size() == 0) {
                Snackbar.make(v, "No past appointments found", Snackbar.LENGTH_SHORT).show();
                return;
            }

            getPastAppointments(allPastAppointments);

        });
    }


}