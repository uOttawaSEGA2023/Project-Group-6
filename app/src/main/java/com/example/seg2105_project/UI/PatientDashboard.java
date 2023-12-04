package com.example.seg2105_project.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.seg2105_project.DBManager;
import com.example.seg2105_project.R;
import com.example.seg2105_project.UserType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    private void displayAppointments(ArrayList<HashMap<String, Object>> appointments, boolean showAcceptBtn, boolean showCancelBtn) {
        for (int i = 0; i < appointments.size(); i++) {
            FrameLayout container = new FrameLayout(this);

            ImageView rectangle = new ImageView(this);
            rectangle.setImageResource(R.drawable.curve);

            FrameLayout.LayoutParams imageLayoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );

            container.addView(rectangle, imageLayoutParams);

            TextView appointmentInfo = new TextView(this);

            HashMap<String, Object> appointment = appointments.get(i);

            //append appoint info
            for (String col : appointment.keySet()) {
                if (col.equalsIgnoreCase("id") || col.equalsIgnoreCase("patient_id")) continue;
                appointmentInfo.append(col + ": " + appointment.get(col) + "\n");
            }

            //add userInfo
            Object patient_id = appointment.get("patient_id");

            if (patient_id != null) {
                //get patientInfo
                HashMap<String, Object> patient = db.getUser((int) patient_id);

                if (patient.size() != 0) {
                    //append patient info to textView
                    for (String col : patient.keySet()) {
                        appointmentInfo.append("\n");
                        if (col.equalsIgnoreCase("id")
                                || col.equalsIgnoreCase("employee_number")
                                || col.equalsIgnoreCase("user_type")
                                || col.equalsIgnoreCase("specialties")) continue;

                        appointmentInfo.append(col + ": " + patient.get(col) + "\n");
                    }
                }

            }

            FrameLayout.LayoutParams infoLayoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );


            infoLayoutParams.gravity = Gravity.CENTER;
            appointmentInfo.setMaxLines(3);
            container.addView(appointmentInfo, infoLayoutParams);

            Button acceptBtn = new Button(this);
            acceptBtn.setText("Accept");

            Button cancelBtn = new Button(this);
            cancelBtn.setText("Cancel");

            acceptBtn.setOnClickListener(view -> {
                db.approveAppointment(Integer.parseInt(appointment.get("id").toString()));
                appointmentView.removeView(container);
            });


            cancelBtn.setOnClickListener(view -> {
                db.cancelAppointment(Integer.parseInt(appointment.get("id").toString()));
                appointmentView.removeView(container);
            });

            appointmentInfo.setOnClickListener(v -> {
                if (appointmentInfo.getMaxLines() == 3) {
                    appointmentInfo.setMaxLines(6);
                } else {
                    appointmentInfo.setMaxLines(3);
                }
            });

            FrameLayout.LayoutParams acceptLayoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            acceptLayoutParams.gravity = Gravity.TOP | Gravity.START;

            if (showAcceptBtn) container.addView(acceptBtn, acceptLayoutParams);

            FrameLayout.LayoutParams cancelLayoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );

            cancelLayoutParams.gravity = Gravity.TOP | Gravity.END;
            if (showCancelBtn) container.addView(cancelBtn, cancelLayoutParams);

            appointmentView.addView(container);
        }
    }

    void showAppointments() {
        showSettings(false);
        mainView.removeAllViews();

        Button upcomingAppointmentsBtn = new Button(this);
        SearchView searchView = new SearchView(this);

        Button pastAppointments = new Button(this);


        Spinner dropdown = new Spinner(this);
        String[] items = new String[]{"Specialty 1", "Specialty 2", "Specialty 3"};
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

        upcomingAppointmentsBtn.setOnClickListener(view -> {
            appointmentView.removeAllViews();
            ArrayList<HashMap<String, Object>> approvedAppointments = db.getAppointments(0, patient_id);

            if (approvedAppointments.size() == 0) {
                message.setText("No approved appointments");
                appointmentView.addView(message);
                return;
            }

            displayAppointments(approvedAppointments, false, true);

        });


        pastAppointments.setOnClickListener(v -> {
            ArrayList<HashMap<String, Object>> pendingAppointments = db.getAppointments(-1, patient_id);

            for (int i = 0; i < pendingAppointments.size(); i++) {
                HashMap<String, Object> appointment = pendingAppointments.get(i);
                db.approveAppointment(Integer.parseInt(appointment.get("id").toString()));
            }

        });
    }


}