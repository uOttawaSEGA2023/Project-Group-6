package com.example.seg2105_project.UI;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seg2105_project.DBManager;
import com.example.seg2105_project.R;
import com.example.seg2105_project.UserType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.Dialog;

import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Date;

public class DoctorDashboard extends AppCompatActivity {
    private UserType userType;
    private DBManager db;
    private TextView statusText;
    TextView userTypeText;
    Button logoutBtn;
    LinearLayout mainView;
    LinearLayout appointmentView;
    int your_textview_id;
    String selectedDate;
    String selectedTime;
    String selectedEndTime;
    int doctor_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_dashboard);

        db = new DBManager(this).open();
        mainView = (LinearLayout) findViewById(R.id.mainView);

        userTypeText = findViewById(R.id.user_type_text);

        //nav bar items
        FloatingActionButton shifts = findViewById(R.id.shifts);
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

                return;
            }
        }

        doctor_id = Integer.parseInt(intent.getStringExtra("id"));

        shifts.setOnClickListener(view -> {
            showSettings(false);
            showShifts();
        });

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

    void showAppointments() {
        showSettings(false);
        mainView.removeAllViews();

        Button approvedAppointmentsBtn = new Button(this);
        Button pendingAppointmentsBtn = new Button(this);
        Button rejectedAppointmentsBtn = new Button(this);
        Button approveAll = new Button(this);

        approvedAppointmentsBtn.setText("Approved");
        pendingAppointmentsBtn.setText("Pending");
        rejectedAppointmentsBtn.setText("Rejected");
        approveAll.setText("Approve All appointments");

        appointmentView = new LinearLayout(this);
        mainView.addView(approvedAppointmentsBtn);
        mainView.addView(pendingAppointmentsBtn);
        mainView.addView(rejectedAppointmentsBtn);
        mainView.addView(approveAll);

        mainView.addView(appointmentView);

        TextView message = new TextView(this);

        approvedAppointmentsBtn.setOnClickListener(view -> {
            appointmentView.removeAllViews();
            ArrayList<HashMap<String, Object>> approvedAppointments = db.getAppointments(0, doctor_id);

            if (approvedAppointments.size() == 0) {
                message.setText("No approved appointments");
                appointmentView.addView(message);
                return;
            }

            displayAppointments(approvedAppointments, false, true);

        });

        pendingAppointmentsBtn.setOnClickListener(view -> {
            appointmentView.removeAllViews();
            ArrayList<HashMap<String, Object>> pendingAppointments = db.getAppointments(-1, doctor_id);

            if (pendingAppointments.size() == 0) {
                message.setText("No pending appointments");
                appointmentView.addView(message);
                return;
            }

            displayAppointments(pendingAppointments, true, true);

        });

        rejectedAppointmentsBtn.setOnClickListener(view -> {
            appointmentView.removeAllViews();
            ArrayList<HashMap<String, Object>> rejectedAppointments = db.getAppointments(0, doctor_id);

            if (rejectedAppointments.size() == 0) {
                message.setText("No rejected appointments");
                appointmentView.addView(message);
                return;
            }
            displayAppointments(rejectedAppointments, true, false);

        });

        approveAll.setOnClickListener(v -> {
            ArrayList<HashMap<String, Object>> pendingAppointments = db.getAppointments(-1, doctor_id);

            for (int i = 0; i < pendingAppointments.size(); i++) {
                HashMap<String, Object> appointment = pendingAppointments.get(i);
                db.approveAppointment(Integer.parseInt(appointment.get("id").toString()));
            }
            pendingAppointmentsBtn.performClick();
        });
    }

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
                if (col.equalsIgnoreCase("id") || col.equalsIgnoreCase("doctor_id")) continue;
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

    void showShifts() {
        mainView.removeAllViews();

        Button calendar = new Button(this);
        calendar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        calendar.setText("Calendar");
        mainView.addView(calendar);

        calendar.setOnClickListener(v -> showCalendarDialog());

        Button startTimer = new Button(this);
        startTimer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        startTimer.setText("Start Time");
        mainView.addView(startTimer);

        startTimer.setOnClickListener(v -> showTimePickerDialog());


        Button endTimer = new Button(this);
        endTimer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        endTimer.setText("End Time");
        mainView.addView(endTimer);

        endTimer.setOnClickListener(v -> showEndTimePickerDialog());

        Button addShift = new Button(this);
        addShift.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        addShift.setText("Add Shift");
        mainView.addView(addShift);

        endTimer.setOnClickListener(v -> showEndTimePickerDialog());

        Button showShift = new Button(this);
        showShift.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        showShift.setText("Show Shifts");
        mainView.addView(showShift);


        addShift.setOnClickListener(v -> {
            try {
                addShift();
            } catch (ParseException e) {
                e.printStackTrace(); // Handle the ParseException appropriately
            }
        });

        showShift.setOnClickListener(v -> {
            displayShifts();
        });

        your_textview_id = View.generateViewId();

        TextView statusTextView = new TextView(this);
        statusTextView.setId(your_textview_id);
        statusTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        statusTextView.setText("Status");
        mainView.addView(statusTextView);


    }

    private void addShift() throws ParseException {

        String startTotal = selectedDate + " " + selectedTime + ":00";
        ;
        String endTotal = selectedDate + " " + selectedEndTime + ":00";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            Date startDate = dateFormat.parse(startTotal);
            Date endDate = dateFormat.parse(endTotal);


            String startDate1 = dateFormat.format(startDate);
            String endDate1 = dateFormat.format(endDate);

            boolean check = db.createShift(doctor_id, startDate1, endDate1);
            if (check) {
                //Error conflict
                TextView foundTextView = findViewById(your_textview_id);
                foundTextView.setText("error conflict");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void displayShifts() {
        if (appointmentView != null) {
            mainView.removeView(appointmentView);
        }

        appointmentView = new LinearLayout(this);
        mainView.addView(appointmentView);

        ArrayList<HashMap<String, Object>> shifts = db.getShifts(doctor_id);

        for (int i = 0; i < shifts.size(); i++) {
            FrameLayout container = new FrameLayout(this);

            ImageView rectangle = new ImageView(this);
            rectangle.setImageResource(R.drawable.curve);

            FrameLayout.LayoutParams imageLayoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );

            container.addView(rectangle, imageLayoutParams);

            TextView shiftInfo = new TextView(this);

            HashMap<String, Object> shift = shifts.get(i);

            //append appoint info
            for (String col : shift.keySet()) {
                if (col.equalsIgnoreCase("id") || col.equalsIgnoreCase("doctor_id")) continue;
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
        }

    }


    private void showCalendarDialog() {
        final Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH);
        int currentDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        selectedDate = String.format("%02d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                        //2023-05-07
                    }
                },
                currentYear,
                currentMonth,
                currentDay
        );

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();
    }


    private void showTimePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int currentHour = c.get(Calendar.HOUR_OF_DAY);
        int currentMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    // Round down to the nearest whole hour or half-hour
                    int roundedMinute = (minute < 30) ? 0 : 30;

                    selectedTime = String.format("%02d:%02d", hourOfDay, roundedMinute);
                    //09:03
                    // Now 'selectedTime' represents the rounded time
                    // You can use it as needed
                },
                currentHour, currentMinute, false
        );

        timePickerDialog.show();
    }


    private void showEndTimePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int currentHour = c.get(Calendar.HOUR_OF_DAY);
        int currentMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    int roundedMinute = (minute < 30) ? 0 : 30;
                    selectedEndTime = String.format("%02d:%02d", hourOfDay, roundedMinute);

                },
                currentHour, currentMinute, false);

        timePickerDialog.show();
    }

}