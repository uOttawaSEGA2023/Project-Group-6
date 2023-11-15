package com.example.seg2105_project.UI;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
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
            if(intent.getStringExtra("approved").equalsIgnoreCase("true")) {
                userTypeText.append(intent.getStringExtra("userType"));
            }
            else{
                if(intent.getStringExtra("rejected").equalsIgnoreCase("true")) {
                    userTypeText.setText("Your registration request has been rejected by the administrator. Please contact them via email: admin@admin.com or phone: +1 314 142 2953");
                }
                else {
                    userTypeText.setText(("Your account hasn't been approved yet"));
                }

                return;
            }
        }

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

        logoutBtn.setOnClickListener(view -> {
            finish();
        });


    }

    void showAppointments(){
        showSettings(false);
        mainView.removeAllViews();

        Button approvedAppointmentsBtn = new Button(this);
        Button pendingAppointmentsBtn = new Button (this);
        Button rejectedAppointmentsBtn = new Button(this);

        approvedAppointmentsBtn.setText("Approved");
        pendingAppointmentsBtn.setText("Pending");
        rejectedAppointmentsBtn.setText("Rejected");

        appointmentView = new LinearLayout(this);
        mainView.addView(approvedAppointmentsBtn);
        mainView.addView(pendingAppointmentsBtn);
        mainView.addView(rejectedAppointmentsBtn);
        mainView.addView(appointmentView);

        //get appointments from db

        ArrayList<HashMap<String, Object>> approvedAppointments = db.getApprovedAppointments();
        ArrayList<HashMap<String, Object>> pendingAppointments = db.getPendingAppointments();
        ArrayList<HashMap<String, Object>> rejectedAppointments = db.getRejectedAppointments();

        TextView message = new TextView(this);

        approvedAppointmentsBtn.setOnClickListener(view->{
            appointmentView.removeAllViews();

            if(approvedAppointments == null){
                message.setText("No approved appointments");
                appointmentView.addView(message);
                return;
            }

            displayAppointments(approvedAppointments, false, true);

        });

        pendingAppointmentsBtn.setOnClickListener(view->{
            appointmentView.removeAllViews();

            if(pendingAppointments == null){
                message.setText("No pending appointments");
                appointmentView.addView(message);
                return;
            }
            displayAppointments(pendingAppointments, true, true);

        });
        rejectedAppointmentsBtn.setOnClickListener(view->{
            appointmentView.removeAllViews();

            if(rejectedAppointments == null){
                message.setText("No rejected appointments");
                appointmentView.addView(message);
                return;
            }
            displayAppointments(rejectedAppointments, true, false);

        });
    }

    private void displayAppointments(ArrayList<HashMap<String, Object>> appointments, boolean showAcceptBtn, boolean showCancelBtn){
        for(int i=0; i<appointments.size(); i++){
            FrameLayout container = new FrameLayout(this);

            TextView appointmentInfo = new TextView(this);

            HashMap<String, Object> appointment = appointments.get(i);

            //append appoint info
            for(String col:appointment.keySet()){
                if (col.equalsIgnoreCase("id") || col.equalsIgnoreCase("doctor_id")) continue;
                appointmentInfo.append(col+": "+ appointment.get(col) +" ");
            }

            //add userInfo
            Object patient_id = appointment.get("patient_id");

            if(patient_id !=null){
                //get patientInfo
                HashMap<String, Object> patient = db.getUser((int)patient_id);

                if(patient.size() !=0){
                    //append patient info to textView
                    for(String col:patient.keySet()){
                        appointmentInfo.append("\n");
                        if (col.equalsIgnoreCase("id")
                                || col.equalsIgnoreCase("employee_number")
                                || col.equalsIgnoreCase("user_type")
                                || col.equalsIgnoreCase("specialties")) continue;

                        appointmentInfo.append(col+": "+ patient.get(col) +"\n");
                    }
                }

            }

            container.addView(appointmentInfo);

            Button acceptBtn = new Button(this);
            acceptBtn.setText("Accept");
            Button cancelBtn = new Button(this);
            cancelBtn.setText("Cancel");

            acceptBtn.setOnClickListener(view -> {
                db.approveAppointment((int)appointment.get("id"));
                appointmentView.removeView(container);
            });

            cancelBtn.setOnClickListener(view -> {
                db.cancelAppointment((int)appointment.get("id"));
                appointmentView.removeView(container);
            });

            appointmentInfo.setOnClickListener(v ->{
                if(appointmentInfo.getMaxLines()==1){
                    appointmentInfo.setMaxLines(10);
                }else{
                    appointmentInfo.setMaxLines(1);
                }
            });

            if(showAcceptBtn) container.addView(acceptBtn);
            if(showCancelBtn) container.addView(cancelBtn);

            appointmentView.addView(container);
        }
    }
    void showSettings(boolean visibility){
        if(visibility) {
            mainView.removeAllViews();
            userTypeText.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.VISIBLE);
        }
        else{
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
        mainView.addView(addShift);


        showShift.setOnClickListener(v -> {
            try {
                addShift();
            } catch (ParseException e) {
                e.printStackTrace(); // Handle the ParseException appropriately
            }
        });

        int statusTextViewId = 123;

        your_textview_id = View.generateViewId();

        TextView statusTextView = new TextView(this);
        statusTextView.setId(your_textview_id);
        statusTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        statusTextView.setText("Status");
        mainView.addView(statusTextView);




    }



    private void showAllShifts(){
        ArrayList<Map<String, Object>>  userRequests = db.getRegistrationRequests();
        int request_id = (int) userRequests.get(0).get("id");

        ArrayList<HashMap<String, Object>> approvedShifts = db.getShifts(request_id);

        for(int i=0; i<approvedShifts.size(); i++){
            FrameLayout container = new FrameLayout(this);

            TextView appointmentInfo = new TextView(this);



            //append appoint info
            for(String col:approvedShifts.keySet()){
                if (col.equalsIgnoreCase("id") || col.equalsIgnoreCase("doctor_id")) continue;
                appointmentInfo.append(col+": "+ approvedShifts.get(col) +" ");
            }

            //add userInfo
            Object patient_id = approvedShifts.get("patient_id");

            if(patient_id !=null){
                //get patientInfo
                HashMap<String, Object> patient = db.getUser((int)patient_id);

                if(patient.size() !=0){
                    //append patient info to textView
                    for(String col:patient.keySet()){
                        appointmentInfo.append("\n");
                        if (col.equalsIgnoreCase("id")
                                || col.equalsIgnoreCase("employee_number")
                                || col.equalsIgnoreCase("user_type")
                                || col.equalsIgnoreCase("specialties")) continue;

                        appointmentInfo.append(col+": "+ patient.get(col) +"\n");
                    }
                }

            }

            container.addView(appointmentInfo);


            Button cancelBtn = new Button(this);
            cancelBtn.setText("Cancel");

            acceptBtn.setOnClickListener(view -> {
                db.approveAppointment((int)approvedShifts.get("id"));
                appointmentView.removeView(container);
            });

            cancelBtn.setOnClickListener(view -> {
                db.cancelAppointment((int)approvedShifts.get("id"));
                appointmentView.removeView(container);
            });

            appointmentInfo.setOnClickListener(v ->{
                if(appointmentInfo.getMaxLines()==1){
                    appointmentInfo.setMaxLines(10);
                }else{
                    appointmentInfo.setMaxLines(1);
                }
            });

            if(showAcceptBtn) container.addView(acceptBtn);
            if(showCancelBtn) container.addView(cancelBtn);

            appointmentView.addView(container);
        }



    }
    private void addShift () throws ParseException {
        db = new DBManager(this).open();
        ArrayList<Map<String, Object>> userRequests = db.getRegistrationRequests();

        if (!userRequests.isEmpty()) {
            int request_id = (int) userRequests.get(0).get("id");

            String startTotal = selectedDate + " " + selectedTime;
            String endTotal = selectedDate + " " + selectedEndTime;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
            try {
                Date startDate = dateFormat.parse(startTotal);
                Date endDate = dateFormat.parse(endTotal);


                String startDate1 = dateFormat.format(startDate);
                String endDate1 = dateFormat.format(endDate);

                boolean check =  db.createShift(request_id, startDate1, endDate1);
                if(check != false){
                    //Error conflict
                    TextView foundTextView = findViewById(your_textview_id );
                    foundTextView.setText("error conflict");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }



    private void displayShifts(ArrayList<HashMap<String, Object>> shifts){
        for(int i=0; i<shifts.size(); i++){
            FrameLayout container = new FrameLayout(this);

            TextView appointmentInfo = new TextView(this);

            HashMap<String, Object> appointment = shifts.get(i);

            //append appoint info
            for(String col:appointment.keySet()){
                if (col.equalsIgnoreCase("id") || col.equalsIgnoreCase("doctor_id")) continue;
                appointmentInfo.append(col+": "+ appointment.get(col) +" ");
            }

            //add userInfo
            Object patient_id = appointment.get("patient_id");

            if(patient_id !=null){
                //get patientInfo
                HashMap<String, Object> patient = db.getUser((int)patient_id);

                if(patient.size() !=0){
                    //append patient info to textView
                    for(String col:patient.keySet()){
                        appointmentInfo.append("\n");
                        if (col.equalsIgnoreCase("id")
                                || col.equalsIgnoreCase("employee_number")
                                || col.equalsIgnoreCase("user_type")
                                || col.equalsIgnoreCase("specialties")) continue;

                        appointmentInfo.append(col+": "+ patient.get(col) +"\n");
                    }
                }

            }

            container.addView(appointmentInfo);


            Button cancelBtn = new Button(this);
            cancelBtn.setText("Cancel");



            cancelBtn.setOnClickListener(view -> {
                db.cancelAppointment((int)appointment.get("id"));
                appointmentView.removeView(container);
            });

            appointmentInfo.setOnClickListener(v ->{
                if(appointmentInfo.getMaxLines()==1){
                    appointmentInfo.setMaxLines(10);
                }else{
                    appointmentInfo.setMaxLines(1);
                }
            });


            if(showCancelBtn) container.addView(cancelBtn);

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

    void showAppointmentss(boolean visibility){}

}