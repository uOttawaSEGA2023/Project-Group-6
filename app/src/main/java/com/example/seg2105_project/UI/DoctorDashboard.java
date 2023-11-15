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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class DoctorDashboard extends AppCompatActivity {
    private UserType userType;
    private DBManager db;
    private TextView statusText;
    TextView userTypeText;
    Button logoutBtn;
    LinearLayout mainView;
    LinearLayout appointmentView;
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

    void showShifts(){
        mainView.removeAllViews();

        TimePicker timePicker = new TimePicker(this);
        timePicker.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mainView.addView(timePicker);

        Button button = new Button(this);
        button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        button.setText("Select shift time");
        mainView.addView(button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(DoctorDashboard.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Do something with the selected time
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

    }
    void showAppointmentss(boolean visibility){}

}