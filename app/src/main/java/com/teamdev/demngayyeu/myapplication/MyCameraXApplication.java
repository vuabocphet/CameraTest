package com.teamdev.demngayyeu.myapplication;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.CameraXConfig;

public class MyCameraXApplication extends Application implements CameraXConfig.Provider {

    public static MyCameraXApplication myCameraXApplication;


    @Override
    public void onCreate() {
        super.onCreate();
        myCameraXApplication = this;
    }

    @NonNull
    @Override
    public CameraXConfig getCameraXConfig() {
        return Camera2Config.defaultConfig();
    }
}
