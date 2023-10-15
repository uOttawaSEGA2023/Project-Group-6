package com.example.seg2105_project;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

public class Home extends AppCompatActivity {
    private UserType userType;
    private TextView userTypeText;
    private Button logoutBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        userTypeText = findViewById(R.id.user_type_text);
        logoutBtn = findViewById(R.id.logout_button);

        Intent intent = getIntent();
        if (intent.hasExtra("userType")) {
            userType = UserType.fromString(intent.getStringExtra("userType"));
            userTypeText.append(userType.type);
        }

        logoutBtn.setOnClickListener(view -> {
            finish();
        });
    }
}
