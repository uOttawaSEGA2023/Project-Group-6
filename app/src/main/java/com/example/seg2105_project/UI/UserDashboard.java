package com.example.seg2105_project.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.seg2105_project.DBManager;
import com.example.seg2105_project.R;
import com.example.seg2105_project.UserType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Calendar;
import android.widget.TimePicker;
import java.util.Locale;
import android.os.Build;


public class UserDashboard extends AppCompatActivity {
    private UserType userType;
    private DBManager db;
    private TextView userTypeText;
    private Button logoutBtn;
    private FloatingActionButton AddAppointments;

    private FloatingActionButton AllAppointments;
    private TextView statusText;

    private TextView startime;

    private TextView Date;
    private Button openCalendar;

    private TextView user_type_text;

    private Button logout_button;

    private TimePicker timePicker;

    private TextView startTime2;

    private TimePicker  timePicker2;

    private FloatingActionButton confirmA;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_dashboard);

        startime = findViewById(R.id.startTime);
        AllAppointments = findViewById(R.id.AllAppointments);
        userTypeText = findViewById(R.id.user_type_text);
        logoutBtn = findViewById(R.id.logout_button);
        timePicker = findViewById(R.id.timePicker);
        startTime2 = findViewById(R.id.startTime2);
        confirmA = findViewById(R.id.confirmA);
        timePicker2 = findViewById(R.id.timePicker2);
        openCalendar = findViewById(R.id.openCalendar);
        statusText = findViewById(R.id.statusText);
        AddAppointments = findViewById(R.id.AddAppointments);
        user_type_text = findViewById(R.id.user_type_text);
        logout_button = findViewById(R.id.logout_button);
        Date = findViewById(R.id.Date);


        //Appointments
        startime.setVisibility(View.GONE);
        openCalendar.setVisibility(View.GONE);
        statusText.setVisibility(View.GONE);
        timePicker.setVisibility(View.GONE);
        startTime2.setVisibility(View.GONE);
        timePicker2.setVisibility(View.GONE);
        confirmA.setVisibility(View.GONE);
        ///


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
            }


        }

        AddAppointments.setOnClickListener(view -> {
            user_type_text.setVisibility(View.GONE);
            logout_button.setVisibility(View.GONE);


            startime.setVisibility(View.VISIBLE);
            openCalendar.setVisibility(View.VISIBLE);
            statusText.setVisibility(View.VISIBLE);
            timePicker.setVisibility(View.VISIBLE);
            startTime2.setVisibility(View.VISIBLE);
            timePicker2.setVisibility(View.VISIBLE);
            confirmA.setVisibility(View.VISIBLE);




        });

        AllAppointments.setOnClickListener(view -> {
            user_type_text.setVisibility(View.VISIBLE);
            logout_button.setVisibility(View.VISIBLE);


            ///Appointments
            startime.setVisibility(View.GONE);
            openCalendar.setVisibility(View.GONE);
            statusText.setVisibility(View.GONE);
            timePicker.setVisibility(View.GONE);
            startTime2.setVisibility(View.GONE);
            timePicker2.setVisibility(View.GONE);
            confirmA.setVisibility(View.GONE);

        });

        openCalendar.setOnClickListener(view -> {
            openDate();


        });

        confirmA.setOnClickListener(view -> {
            startTime();
            EndTime();


        });




        logoutBtn.setOnClickListener(view -> {
            finish();
        });

    }



    public void openDate() {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {

                //Data retrieval for the day
                statusText.setText(String.valueOf(datePicker));
            }
        }, year, month, day);


        datePickerDialog.show();
    }

    public void startTime() {

        //First timer
        TimePicker timePicker = findViewById(R.id.timePicker2);
        TextView selectedTimeTextView = findViewById(R.id.Date);

        int selectedHour;
        int selectedMinute;


        if (Build.VERSION.SDK_INT >= 23) {
            selectedHour = timePicker.getHour();
            selectedMinute = timePicker.getMinute();
        } else {
            selectedHour = timePicker.getCurrentHour();
            selectedMinute = timePicker.getCurrentMinute();
        }


        String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);

        //Date for start time
        selectedTimeTextView.setText("Selected Time: " + selectedTime);
    }


    public void EndTime () {

        //First timer
        TimePicker timePicker = findViewById(R.id.timePicker2);
        TextView selectedTimeTextView = findViewById(R.id.Date);

        int selectedHour;
        int selectedMinute;


        if (Build.VERSION.SDK_INT >= 23) {
            selectedHour = timePicker.getHour();
            selectedMinute = timePicker.getMinute();
        } else {
            selectedHour = timePicker.getCurrentHour();
            selectedMinute = timePicker.getCurrentMinute();
        }


        String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);

             //Date for end time
        selectedTimeTextView.setText("Selected Time: " + selectedTime);
    }












}