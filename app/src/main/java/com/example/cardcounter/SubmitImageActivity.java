package com.example.cardcounter;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SubmitImageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submitimage);

        Button goBackButton = findViewById(R.id.goBackButton);
        goBackButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}