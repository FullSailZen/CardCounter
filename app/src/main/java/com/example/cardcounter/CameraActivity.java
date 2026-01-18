package com.example.cardcounter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.ImageCapture;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.concurrent.ExecutionException;
public class CameraActivity extends AppCompatActivity {

    private PreviewView previewView;
    private Button captureButton;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;

    // this handles the permission request and its result
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startCamera();
                } else {
                    Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show();
                    finish(); // close the activity if permission isn't granted
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.cameraPreview);
        captureButton = findViewById(R.id.captureButton);

        captureButton.setOnClickListener(v -> takePhoto());

        // checking for camera permission. if granted, we start the camera. if not, we request it
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }

    }

    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {

                // allowing us to get the camera stream once its ready via a "Future", throws an exception if it fails
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // setting up the preview use case to display the camera feed
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // setting up the image capture use case to capture photos
                imageCapture = new ImageCapture.Builder().build();

                // selecting the back camera as the default (camera is a constant, not new)
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                // unbinding before we rebind to avoid errors, we can only use the camera in one use case at a time
                cameraProvider.unbindAll();

                // rebinding the use cases to the camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                // throwing error if something goes wrong
                Toast.makeText(this, "Error starting camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        },      // setting the UI thread as our main executor, instead of the secondary thread that handles background processes.
                ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        // checking if image capture use case is bound to lifecycle (ready to use)
        if (imageCapture == null) {
            Toast.makeText(this, "Image capture not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        // asking camerax to capture the photo and to give us the result in memory
        imageCapture.takePicture(
                // running on the main thread, which handles UI/notifs (toast)
                ContextCompat.getMainExecutor(this),
                // custom callback that handles successful captures or errors
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {
                        // proof that photo capture is working before the rest of implementation
                        Toast.makeText(CameraActivity.this, "Photo captured!", Toast.LENGTH_SHORT).show();
                        @SuppressWarnings("ConstantConditions")
                        // get raw image buffer from camerax
                        Image mediaImage = imageProxy.getImage();

                        if (mediaImage != null) {
                            // telling ML Kit how the image is rotated to ensure
                            // quality data extraction (needs to make it upright)
                            int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();

                            // creating ML Kit compatible image and passing the correct rotation
                            InputImage image = InputImage.fromMediaImage(mediaImage, rotationDegrees);

                            // sending the correctly rotated image to the ML Kit for processing
                            // and data extraction
                            processImageWithMlKit(image);
                        }
                        // closing the image proxy after processing to avoid unexpected behavior
                        imageProxy.close();

                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        // logging the error if it occurs
                        Log.e("CameraActivity", "Photo capture failed: " + exception.getMessage());
                        Toast.makeText(CameraActivity.this, "Photo capture failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    // this method processes the submitted image using ML Kits Text Recognition
    private void processImageWithMlKit(InputImage image) {
        // getting an instance of the text recognizer
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        // process the image and add listeners for successes and failures
        recognizer.process(image).addOnSuccessListener(visionText -> {
            // task completed successfully
            String recognizedText = visionText.getText();
            launchResultsActivity(recognizedText);
        }).addOnFailureListener(e -> {
            // logging task failure error
            Log.e("CameraActivity", "Text recognition failed: " + e.getMessage());
            Toast.makeText(CameraActivity.this, "Text recognition failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            launchResultsActivity("Failed to recognize text.");
        });



    }


// this method will launch the results activity and pass the recognized text to it
    private void launchResultsActivity(String recognizedText) {
    Intent intent = new Intent(this, ResultsActivity.class);
    // passing the text to the next activity using an Intent extra
        intent.putExtra("RECOGNIZED_TEXT", recognizedText);
        startActivity(intent);
    }


}
