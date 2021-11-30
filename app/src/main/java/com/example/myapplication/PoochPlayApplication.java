package com.example.myapplication;
/*
 *
 *  * 12/2/2021
 *  * Poochplay
 *  * Created by Isha Nagar on 12/2/21 11:47 AM
 *  * Copyright (c) POOCHPLAY Limited. All rights reserved
 *
 */
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.amit.poochplayble.BleService;
import com.amit.poochplayble.Constant;
import com.amit.poochplayble.GattServices;
import com.amit.poochplayble.SysApplication;

import java.util.HashMap;


public class PoochPlayApplication extends Application {
    private static Context context;
    public static final String TAG = "SysApplication";
    private static PoochPlayApplication instance;

    public static BleService mBluetoothLeService = null;
    public static GattServices gattServices;

    private String imagePath;
    private String fileName;

    private int currentIndex = 0;
    public HashMap<String,Integer> reminderMapping = new HashMap<>();

    public static PoochPlayApplication getInstance() {
        return instance;
    }



    public static GattServices getGattServices() {
        return gattServices;
    }

    public static void setGattServices(GattServices gattServices) {
        SysApplication.gattServices = gattServices;
    }



    public static BleService getmBluetoothLeService() {
        return mBluetoothLeService;
    }

    public static void setmBluetoothLeService(BleService mBluetoothLeService) {
        SysApplication.mBluetoothLeService = mBluetoothLeService;
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName,IBinder service) {
            try {
                mBluetoothLeService = ((BleService.LocalBinder)service)
                        .getService();
                Constant.bleService = ((BleService.LocalBinder) service)
                        .getService();
                // private JSONObject object,object1,jsonobject;

                gattServices = new GattServices(mBluetoothLeService);
                Constant.mgattServices = gattServices;

                Log.e("ly", "onServiceConnected");

                if (!mBluetoothLeService.initialize()) {
                    Log.e("", "Unable to initialize Bluetooth");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // mBluetoothLeService = null;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        //  Fabric.with(this, new Crashlytics());
        PoochPlayApplication.context = getApplicationContext();
        Constant.appcontext = getApplicationContext();
        Log.e(TAG,"on create called");
        Intent gattServiceIntent = new Intent(this, BleService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        if (instance == null) {
            instance = this;
        }
    }

    public static Context getAppContext() {
        return PoochPlayApplication.context;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }


}
