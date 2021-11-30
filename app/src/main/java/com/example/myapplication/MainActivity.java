package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.location.LocationManagerCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogInterface.OnCancelListener {


    private List<BluetoothDevice> lstDevices = new ArrayList<>();
    private List<String> lstDevicesName = new ArrayList<>();
    private ListView listView;
    private String deviseList = "";
    private TextView tvScanning, tvBatteryPercentage, tvSteps;
    private Button btnScan;


    private BluetoothAdapter mBluetoothAdapter;

    private static final int REQUEST_ENABLE_BT = 1;
    private Handler mHandler;
    private BluetoothLeScanner mLEScanner;

    private static final long SCAN_PERIOD = 10000;
    private List<ScanFilter> filters;
    private ScanSettings settings;


    public static String mDeviceAddress;

    ListAdapter adapter;

    private final static String BATTERY_UUID = "0000180f-0000-1000-8000-00805f9b34fb";
    private final static String BATTERY_LEVEL = "00002a19-0000-1000-8000-00805f9b34fb";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationPermission();



        listView = findViewById(R.id.listView);
        tvScanning = findViewById(R.id.tvScanning);
        tvBatteryPercentage = findViewById(R.id.tvBatteryPercentage);
        tvSteps = findViewById(R.id.tvSteps);
        btnScan = findViewById(R.id.btnScan);


        ListAdapter adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, lstDevicesName);
        listView.setAdapter(adapter);

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Check whether the Bluetooth device


        mHandler = new Handler();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


        checkLocationOn();


        // Initializes list view adapter.


        listView.setOnItemClickListener((parent, view, position, id) -> {


            BluetoothDevice device = lstDevices.get(position);
            mDeviceAddress=device.getAddress();
            Intent intent= new Intent(this, TrackerActivity.class);
            intent.putExtra("device",mDeviceAddress);
            startActivity(intent);
        });


        btnScan.setVisibility(View.INVISIBLE);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                lstDevicesName.clear();


                checkLocationOn();
            }
        });


    }

    private void locationPermission(){
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                                checkLocationOn();
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                                checkLocationOn();
                            } else {
                                // No location access granted.
                            }
                        }
                );

// ...

// Before you perform the actual permission request, check whether your app
// already has the permissions, and whether your app needs to show a permission
// rationale dialog. For more details, see Request permissions.

        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

    }

    private void checkLocationOn() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!LocationManagerCompat.isLocationEnabled(lm)) {
            scanLeDevice(false);


        } else {
            scanLeDevice(true);

        }
    }


    private void scanLeDevice(final boolean enable) {
        try {
            if (enable) {

                mHandler.postDelayed(() -> {
                    if (mLEScanner != null && mScanCallback != null &&
                            mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {

                        mLEScanner.stopScan(mScanCallback);

                    }
                }, SCAN_PERIOD);


                if (mScanCallback != null && mLEScanner != null) {
                    filters.add(new ScanFilter.Builder().setDeviceName("PoochPlay").build());

                    if (mScanCallback != null)
                        mLEScanner.startScan(filters, settings, mScanCallback);

                } else if (mLEScanner == null && mScanCallback != null) {
                    mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();

                    settings = new ScanSettings.Builder()
                            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                            .build();

                    filters = new ArrayList<>();
                    filters.add(new ScanFilter.Builder().setDeviceName("PoochPlay").build());

                    if (mScanCallback != null)
                        mLEScanner.startScan(filters, settings, mScanCallback);
                }
            } else {
                if (mScanCallback != null && mLEScanner != null) {
                    mLEScanner.stopScan(mScanCallback);
                }

            }
        } catch (Exception e) {
        }
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice btDevice;
            btDevice = result.getDevice();


            if (!deviseList.contains(btDevice.getAddress())) {
                deviseList = deviseList.concat(result.getDevice().getName() + " - " + result.getDevice().getAddress() + "\n");

                lstDevices.add(result.getDevice());
                lstDevicesName.add(result.getDevice().getName() + " - " + result.getDevice().getAddress());

                 adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, lstDevicesName);
                listView.setAdapter(adapter);
            }


//            adapter.addDevice(btDevice);
            Log.e("list",btDevice.getAddress());

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.e("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };


    @Override
    public void onCancel(DialogInterface dialogInterface) {

    }





}