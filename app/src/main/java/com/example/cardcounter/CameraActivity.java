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

        // proof that photo capture is working before the rest of implementation
        Toast.makeText(this, "Photo captured!", Toast.LENGTH_SHORT).show();

    }


}
