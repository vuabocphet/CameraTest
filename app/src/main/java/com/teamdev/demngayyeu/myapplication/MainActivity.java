package com.teamdev.demngayyeu.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.core.TorchState;
import androidx.camera.core.impl.VideoCaptureConfig;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.TextureView;
import android.view.View;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.util.Log.e;

public class MainActivity extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private Preview imagePreview;
    private ImageCapture imageCapture;
    private PreviewView previewView;
    private CameraControl cameraControl;
    private CameraInfo cameraInfo;
    private float linearZoom = 0f;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.previewView = findViewById(R.id.preview_view);
        this.cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        this.previewView.post(this::startCamera);
    }

    private void toggleTorch() {
        if (this.cameraInfo == null) {
            return;
        }
        if (this.cameraInfo.getTorchState() == null) {
            return;
        }
        if (this.cameraInfo.getTorchState().getValue() == null) {
            return;
        }

        if (this.cameraInfo.getTorchState().getValue() == TorchState.ON) {
            this.cameraControl.enableTorch(false);
        } else {
            this.cameraControl.enableTorch(true);
        }
    }

    private void startCamera() {
        this.imagePreview = new Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setTargetAspectRatio(this.previewView.getDisplay().getRotation())
                .build();
        this.imageCapture = new ImageCapture.Builder()
                .build();

        final CameraSelector build = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();
        this.cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider processCameraProvider = cameraProviderFuture.get();
                processCameraProvider.unbindAll();
                Camera camera = processCameraProvider.bindToLifecycle(
                        MainActivity.this,
                        build,
                        imagePreview,
                        imageCapture);
                previewView.setPreferredImplementationMode(PreviewView.ImplementationMode.TEXTURE_VIEW);
                imagePreview.setSurfaceProvider(previewView.createSurfaceProvider(camera.getCameraInfo()));
                cameraControl = camera.getCameraControl();
                cameraInfo = camera.getCameraInfo();

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        }, ContextCompat.getMainExecutor(this));

    }

    public void getView(View view) {
        TextureView childAt = (TextureView) previewView.getChildAt(0);
        Bitmap bitmap = childAt.getBitmap();
        try {
            File savebitmap = savebitmap(bitmap, this);
            e("TinhNv", ": "+savebitmap.getAbsolutePath() );
        } catch (IOException e) {
            e.printStackTrace();
            e("TinhNv", "getView: "+e.toString() );
        }
    }

    public static File savebitmap(Bitmap bmp, Context context) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        File f = new File(context.getFilesDir().getAbsoluteFile()
                + File.separator + "testimage.jpg");
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        fo.close();
        return f;
    }
}
