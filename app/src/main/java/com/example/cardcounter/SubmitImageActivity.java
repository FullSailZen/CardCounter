package com.example.cardcounter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SubmitImageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submitimage);

        Button scanButton = findViewById(R.id.submitImageButton);
        Button goBackButton = findViewById(R.id.goBackButton);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // this starts CameraActivity, which now handles file selection
                Intent intent = new Intent(SubmitImageActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Closes this activity.
                finish();
            }
        });
    }
}
