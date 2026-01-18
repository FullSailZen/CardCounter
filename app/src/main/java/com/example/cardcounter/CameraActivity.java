package com.example.cardcounter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class CameraActivity extends AppCompatActivity {

    // this launcher handles the result from the image picker activity
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // image was successfully selected by the user
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        // if we have a valid image URI, start the ML Kit processing
                        processImageUri(imageUri);
                    } else {
                        Toast.makeText(this, "Failed to get image URI", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // this block runs if the user cancels the image selection
                    Toast.makeText(this, "No image selected.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Button uploadButton = findViewById(R.id.uploadButton);
        Button cameraBackButton = findViewById(R.id.cameraBackButton);

        // set the main button to open the image picker instead of taking a photo.
        uploadButton.setOnClickListener(v -> openImagePicker());

        // back button
        cameraBackButton.setOnClickListener(v -> finish());
    }

    private void openImagePicker() {
        // creates and launches an Intent to open the device photo gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void processImageUri(Uri imageUri) {
        try {
            // create an InputImage object from the file path of the selected image
            //  in the format ML Kit needs
            InputImage image = InputImage.fromFilePath(this, imageUri);

            // instantiating an instance of the text recognizer
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

            // process the image and add listeners for success or failure
            recognizer.process(image)
                    .addOnSuccessListener(visionText -> {
                        // ML Kit processing was successful.
                        String recognizedText = visionText.getText();
                        launchResultsActivity(recognizedText);
                    })
                    .addOnFailureListener(e -> {
                        // ML Kit processing failed.
                        Log.e("CameraActivity", "Text recognition failed", e);
                        Toast.makeText(this, "Text recognition failed.", Toast.LENGTH_SHORT).show();
                        launchResultsActivity("Text recognition failed.");
                    });

        } catch (IOException e) {
            // This error happens if the app can't read the image file from the URI.
            Log.e("CameraActivity", "Failed to create InputImage from URI", e);
            Toast.makeText(this, "Failed to load image.", Toast.LENGTH_SHORT).show();
        }
    }

    // this method will launch the results activity and pass the recognized text to it
    private void launchResultsActivity(String recognizedText) {
        Intent intent = new Intent(this, ResultsActivity.class);
        // passing the text to the next activity using an Intent extra
        intent.putExtra("RECOGNIZED_TEXT", recognizedText);
        startActivity(intent);
    }
}
