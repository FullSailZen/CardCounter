package com.example.cardcounter;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ResultsActivity extends AppCompatActivity {

    // declaring ui elements
    private TextView cardNameTextView;
    private TextView cardSetTextView;
    private TextView cardRollAvgTextView;
    private TextView cardEstRarityTextView;
    private Button resultsGoBackButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setting content view to the activity_results layout
        setContentView(R.layout.activity_results);

        // instantiating back button
        resultsGoBackButton = findViewById(R.id.resultsGoBackButton);

        // this is the start of displaying the results, we will start with name for now
        cardNameTextView = findViewById(R.id.cardName);

        // getting the recognized text from the intent
        Intent intent = getIntent();

        // if there is no data, we use default text (no text found)
        String recognizedText = intent.getStringExtra("RECOGNIZED_TEXT");

        // displaying the results
        // putting recognized text into the cardName TextView object for testing (for now)
        // adding a label for readability and clarity
        if (recognizedText != null && !recognizedText.isEmpty()) {
            cardNameTextView.setText("Card Name: " + recognizedText);
        } else {
            cardNameTextView.setText("No text was recognized.");
        }

        // go back button
        resultsGoBackButton.setOnClickListener(v -> {
            finish();
        });
    }
}
