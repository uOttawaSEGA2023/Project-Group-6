package com.example.seg2105_project.UI;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seg2105_project.DBManager;
import com.example.seg2105_project.R;
import com.example.seg2105_project.UserType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;


public class DoctorDashboard extends AppCompatActivity {
    private UserType userType;
    private DBManager db;
    private TextView statusText;
    TextView userTypeText;
    Button logoutBtn;
    LinearLayout mainView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_dashboard);

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

        });

        settings.setOnClickListener(view -> {
            showSettings(true);
        });

        logoutBtn.setOnClickListener(view -> {
            finish();
        });


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