package com.example.seg2105_project;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.ImageButton;
import android.widget.FrameLayout;
import android.view.Gravity;

public class Home extends AppCompatActivity {
    private UserType userType;

    private FrameLayout container;
    private TextView userTypeText;
    private Button logoutBtn;
    private FloatingActionButton homebutton;
    private LinearLayout rectangleContainer;
    private FloatingActionButton settingbutton;

    private int rectangleCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        userTypeText = findViewById(R.id.user_type_text);
        logoutBtn = findViewById(R.id.logout_button);
        homebutton = findViewById(R.id.homebutton);
        settingbutton = findViewById(R.id.settingbutton);
        // Initialize the rectangleContainer

        rectangleContainer = findViewById(R.id.rectangleContainer);



        Intent intent = getIntent();
        if (intent.hasExtra("userType")) {
            userType = UserType.fromString(intent.getStringExtra("userType"));
            userTypeText.append(userType.type);
        }

        logoutBtn.setOnClickListener(view -> {
            finish();
        });

        homebutton.setOnClickListener(view -> {
            logoutBtn.setVisibility(View.INVISIBLE);
            userTypeText.setVisibility(View.INVISIBLE);
            RequestsLoad(1); // Call the method to add a rectangle
        });

        settingbutton.setOnClickListener(view -> {
            logoutBtn.setVisibility(View.VISIBLE);
            userTypeText.setVisibility(View.VISIBLE);
            RequestsLoad(0);

        });


    }



    private void RequestsLoad(int visible) {

        container = new FrameLayout(this);

        if (visible == View.VISIBLE) {
            container.setVisibility(View.VISIBLE);
        } else if (visible == View.INVISIBLE) {
            container.setVisibility(View.INVISIBLE);
        }

        ImageView rectangle = new ImageView(this);
        rectangle.setImageResource(R.drawable.curve);

        FrameLayout.LayoutParams imageLayoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        imageLayoutParams.gravity = Gravity.TOP | Gravity.START;

        Button acceptButton = new Button(this);
        acceptButton.setText("Accept");

        FrameLayout.LayoutParams acceptButtonLayoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        acceptButtonLayoutParams.gravity = Gravity.TOP | Gravity.START;
           //////////////////////////////OPTIONAL JUST TO DELETE PATIENT
        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");

        FrameLayout.LayoutParams deleteButtonLayoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        deleteButtonLayoutParams.gravity = Gravity.BOTTOM;
       ////////////////////////////////////OPTIONAL
        Button declineButton = new Button(this);
        declineButton.setText("Decline");

        FrameLayout.LayoutParams declineButtonLayoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        declineButtonLayoutParams.gravity = Gravity.TOP | Gravity.END;

        TextView patientName = new TextView(this);
        patientName.setText("Patient Name:" + String.valueOf(rectangleCounter));

        FrameLayout.LayoutParams nameLayoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        nameLayoutParams.gravity = Gravity.CENTER;

        container.addView(rectangle, imageLayoutParams);
        container.addView(acceptButton, acceptButtonLayoutParams);
        container.addView(deleteButton, deleteButtonLayoutParams);
        container.addView(declineButton, declineButtonLayoutParams);
        container.addView(patientName, nameLayoutParams);


        deleteButton.setOnClickListener(view -> {
            container.removeView(deleteButton);
            container.removeView(declineButton);
            container.removeView(patientName);
            rectangleContainer.removeView(container);
            rectangleCounter--;

        });

        rectangleContainer.addView(container);

        rectangleCounter++;

    }

}