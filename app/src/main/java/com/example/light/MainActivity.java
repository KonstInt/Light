package com.example.light;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 50;
    private boolean flashLightStatus = false;
    private boolean hasCameraFlash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Sync().onCreate();

        hasCameraFlash
                = getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
        new ServiceThread().start();
    }

    private void flashLightOn() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
            flashLightStatus = true;
        } catch (CameraAccessException e) {
        }
    }

    private void flashLightOff() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
            flashLightStatus = false;
        } catch (CameraAccessException e) {
        }
    }

    class ServiceThread extends Thread {

        @Override
        public void run() {
            Timer timer = new Timer();

            try {
                sleep(1000 * 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    flashLightOn();
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    flashLightOff();
                }
            }, (System.currentTimeMillis() + (int) Sync.deltaT) % 1000, 1000);

        }
    }
}