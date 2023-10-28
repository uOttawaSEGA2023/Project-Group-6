package com.example.seg2105_project;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
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
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import android.view.ViewGroup;

public class Home extends AppCompatActivity {
    private UserType userType;
    private DBManager db;

    private TextView userTypeText;
    private Button logoutBtn;
    private FloatingActionButton homebutton;
    private FloatingActionButton cancelbutton;

    private LinearLayout rectangleContainer;

    private LinearLayout rectangleContainer1;
    private FloatingActionButton settingbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        userTypeText = findViewById(R.id.user_type_text);
        logoutBtn = findViewById(R.id.logout_button);
        homebutton = findViewById(R.id.homebutton);
        settingbutton = findViewById(R.id.settingbutton);
        cancelbutton = findViewById(R.id.cancelbutton);

        // Requests container
        rectangleContainer = findViewById(R.id.rectangleContainer);

        //fetch user requests and append them to view container
        db = new DBManager(this).open();
        ArrayList<Map<String, Object>>  userRequests = db.getRegistrationRequests();
        if(!userRequests.isEmpty()) addRequestsToView(userRequests);

        //rejected requests container
        rectangleContainer1 = findViewById(R.id.rectangleContainer1);
        ArrayList<Map<String, Object>>  rejectedRequests = db.getRejectedRegistrationRequests();
        if(!rejectedRequests.isEmpty()) addToRejectionList(rejectedRequests);

        Intent intent = getIntent();
        if (intent.hasExtra("userType")) {
            userType = UserType.fromString(intent.getStringExtra("userType"));
            userTypeText.append(userType.type);
        }

        //hide logout button, text and rejection list by default
        logoutBtn.setVisibility(View.GONE);
        userTypeText.setVisibility(View.GONE);
        rectangleContainer1.setVisibility(View.GONE);

        cancelbutton.setOnClickListener(view -> {
            logoutBtn.setVisibility(View.GONE);
            userTypeText.setVisibility(View.GONE);
            rectangleContainer.setVisibility(View.GONE);

            rectangleContainer1.setVisibility(view.VISIBLE);
        });

        logoutBtn.bringToFront();
        logoutBtn.setOnClickListener(view -> {
            finish();
        });

        homebutton.setOnClickListener(view -> {
            logoutBtn.setVisibility(View.GONE);
            userTypeText.setVisibility(View.GONE);
            rectangleContainer1.setVisibility(View.GONE);

            //show requests
            rectangleContainer.setVisibility(view.VISIBLE);
        });

        settingbutton.setOnClickListener(view -> {
            logoutBtn.setVisibility(View.VISIBLE);
            userTypeText.setVisibility(View.VISIBLE);

            //hide requests
            rectangleContainer.setVisibility(View.GONE);
            rectangleContainer1.setVisibility(View.GONE);
        });


    }

    private void addRequestsToView(ArrayList<Map<String, Object>> requests) {
        for(int i =0; i <requests.size(); i++){
            FrameLayout container = new FrameLayout(this);

            ImageView rectangle = new ImageView(this);
            rectangle.setImageResource(R.drawable.curve);

            FrameLayout.LayoutParams imageLayoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            imageLayoutParams.gravity = Gravity.TOP | Gravity.START;

            /*************** Accept *********/
            Button acceptButton = new Button(this);
            acceptButton.setText("Accept");

            FrameLayout.LayoutParams acceptButtonLayoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            acceptButtonLayoutParams.gravity = Gravity.TOP | Gravity.START;

            /*************** Decline *********/
            Button declineButton = new Button(this);
            declineButton.setText("Decline");

            FrameLayout.LayoutParams declineButtonLayoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            declineButtonLayoutParams.gravity = Gravity.TOP | Gravity.END;

            /*************** Add user info to view *********/
            Map<String, Object> user = requests.get(i);
            TextView user_info = new TextView(this);
            StringBuilder info = new StringBuilder();

            for(String col:user.keySet()){
                info.append(col+": "+ user.get(col) +"\n");
            }

            user_info.setText(info);

            FrameLayout.LayoutParams nameLayoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            nameLayoutParams.gravity = Gravity.CENTER;

            container.addView(rectangle, imageLayoutParams);
            container.addView(acceptButton, acceptButtonLayoutParams);
            container.addView(declineButton, declineButtonLayoutParams);
            container.addView(user_info, nameLayoutParams);


            /*************** Button interactions *********/
            int request_id = Integer.parseInt((String)user.get("id"));

            acceptButton.setOnClickListener(view -> {
                db.approveRegistration(request_id);

                rectangleContainer.removeView(container);
            });

            int finalI = i;
            declineButton.setOnClickListener(view -> {
                db.rejectRegistrationRequest(request_id);
                rectangleContainer.removeView(container);

                //add request to rejected List
                ArrayList<Map<String, Object>> rejected_user= new ArrayList<>();
                rejected_user.add(requests.get(finalI));
                addToRejectionList(rejected_user);
            });

            rectangleContainer.addView(container);
        }

    }

    public void addToRejectionList(ArrayList<Map<String, Object>> requests){
        for(int i =0; i <requests.size(); i++){
            FrameLayout container = new FrameLayout(this);

            ImageView rectangle = new ImageView(this);
            rectangle.setImageResource(R.drawable.curve);

            FrameLayout.LayoutParams imageLayoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            imageLayoutParams.gravity = Gravity.TOP | Gravity.START;

            /*************** Accept *********/
            Button acceptButton = new Button(this);
            acceptButton.setText("Accept");

            FrameLayout.LayoutParams acceptButtonLayoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            acceptButtonLayoutParams.gravity = Gravity.TOP | Gravity.START;

            /*************** Add user info to view *********/
            Map<String, Object> user = requests.get(i);
            TextView user_info = new TextView(this);
            StringBuilder info = new StringBuilder();

            for(String col:user.keySet()){
                info.append(col+": "+ user.get(col) +"\n");
            }

            user_info.setText(info);

            FrameLayout.LayoutParams nameLayoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            nameLayoutParams.gravity = Gravity.CENTER;

            container.addView(rectangle, imageLayoutParams);
            container.addView(acceptButton, acceptButtonLayoutParams);
            container.addView(user_info, nameLayoutParams);


            /*************** Button interactions *********/
            int request_id = Integer.parseInt((String)user.get("id"));

            acceptButton.setOnClickListener(view -> {
                db.approveRegistration(request_id);

                rectangleContainer1.removeView(container);
            });

            rectangleContainer1.addView(container);
        }

    }

}