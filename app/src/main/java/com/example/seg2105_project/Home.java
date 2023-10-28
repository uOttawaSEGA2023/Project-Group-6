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
import java.util.ArrayList;
import java.util.List;

import android.view.ViewGroup;

public class Home extends AppCompatActivity {
    private UserType userType;

    private FrameLayout container;
    private TextView userTypeText;
    private Button logoutBtn;
    private FloatingActionButton homebutton;
    private LinearLayout rectangleContainer;

    private LinearLayout rectangleContainer1;
    private FloatingActionButton settingbutton;

    private FloatingActionButton cancelbutton;
    private List<PatientProfile> deletedPatients = new ArrayList<>();

    private List<PatientProfile> patientList = new ArrayList<>();

    private int rectangleCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        userTypeText = findViewById(R.id.user_type_text);
        logoutBtn = findViewById(R.id.logout_button);
        homebutton = findViewById(R.id.homebutton);
        settingbutton = findViewById(R.id.settingbutton);
        cancelbutton = findViewById(R.id.cancelbutton);
        // Initialize the rectangleContainer


        //main request
        rectangleContainer = findViewById(R.id.rectangleContainer);

        //rejected request
        rectangleContainer1 = findViewById(R.id.rectangleContainer1);



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

        cancelbutton.setOnClickListener(view -> {
            logoutBtn.setVisibility(View.VISIBLE);
            userTypeText.setVisibility(View.VISIBLE);
            rectangleContainer.setVisibility(View.GONE);

        });


    }

    private PatientProfile findPatientByName(String name) {
        for (PatientProfile patient : patientList) {
            if (name.equals("Patient Name:" + patient.getName())) {
                return patient;
            }
        }
        return null;
    }

    public void DeleteTask(String targetTag){

        ViewGroup yourParentLayout = findViewById(R.id.rectangleContainer); // Assuming you have a parent layout

        for (int i = 0; i < yourParentLayout.getChildCount(); i++) {
            View child = yourParentLayout.getChildAt(i);
            Object tag = child.getTag();

            if (tag != null && tag.equals(targetTag)) {
                // Found the view with the desired tag, you can remove it from the parent
                RejectionLoad(targetTag);
                yourParentLayout.removeView(child);
                break; // You can break if you know there's only one view with this tag
            }
        }
    }

    public void DeleteForever(String targetTag){

        ViewGroup yourParentLayout = findViewById(R.id.rectangleContainer1); // Assuming you have a parent layout

        for (int i = 0; i < yourParentLayout.getChildCount(); i++) {
            View child = yourParentLayout.getChildAt(i);
            Object tag = child.getTag();


            if (tag != null && tag.equals(targetTag)) {
                // Found the view with the desired tag, you can remove it from the parent

                yourParentLayout.removeView(child);

                break; // You can break if you know there's only one view with this tag
            }
        }
    }





    private void RequestsLoad(int visible) {
        FrameLayout container = new FrameLayout(this); // Declare container within the method


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

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");

        FrameLayout.LayoutParams deleteButtonLayoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        deleteButtonLayoutParams.gravity = Gravity.BOTTOM;

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
        container.setTag(String.valueOf(rectangleCounter));

        deleteButton.setOnClickListener(view -> {
            // Get the tag associated with the container
            Object tag = container.getTag();

            // Call DeleteTask to remove the container
            if (tag != null) {
                DeleteTask(tag.toString());
            }

            // Other actions to be taken on delete, e.g., adding the patient to the deletedPatients list
        });

        // Ensure that rectangleContainer is initialized and accessible at this point
        if (rectangleContainer != null) {
            rectangleContainer.addView(container);
        }

        rectangleCounter++;
    }

    public void RejectionLoad(String name){


        FrameLayout container = new FrameLayout(this); // Declare container within the method


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

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");

        FrameLayout.LayoutParams deleteButtonLayoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        deleteButtonLayoutParams.gravity = Gravity.BOTTOM;

        Button declineButton = new Button(this);
        declineButton.setText("Decline");

        FrameLayout.LayoutParams declineButtonLayoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        declineButtonLayoutParams.gravity = Gravity.TOP | Gravity.END;

        TextView patientName = new TextView(this);
        patientName.setText("Patient Name:" + name);

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
        container.setTag(name);

        deleteButton.setOnClickListener(view -> {
            // Get the tag associated with the container
            Object tag = container.getTag();

            // Call DeleteTask to remove the container
            if (tag != null) {
                DeleteForever(tag.toString());
            }

            // Other actions to be taken on delete, e.g., adding the patient to the deletedPatients list
        });

        // Ensure that rectangleContainer is initialized and accessible at this point
        if (rectangleContainer1 != null) {
            rectangleContainer1.addView(container);
        }



    }

}