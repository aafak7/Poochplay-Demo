package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.amit.poochplayble.BleCallBacks;
import com.amit.poochplayble.DataManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements BleCallBacks {

    private GoogleApiClient googleApiClient;
    int REQUEST_CHECK_SETTINGS = 100;
    Button btnScan;
    TextView tvSteps, tvBatteryPercentage;
    private static String[] PERMISSIONS_LOCATION = {android.Manifest.permission.ACCESS_FINE_LOCATION};
    private ListView listView;
    private List<BluetoothDevice> lstDevices = new ArrayList<>();
    private List<String> lstDevicesName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startBluetooth(true);
        // TODO: 16-11-2021 Enable GPS/Location for access bluetooth data. for detail check blog. https://developer.android.com/guide/topics/connectivity/bluetooth/permissions
        enableLoc();

        // TODO: 16-11-2021 After give all the access of GPS & runtime location permission, here we are initialising our sdk.
        DataManager.getInstance().setApplication(getApplication(), this);
        tvSteps = findViewById(R.id.tvSteps);
        tvBatteryPercentage = findViewById(R.id.tvBatteryPercentage);
        btnScan = findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 16-11-2021 Bluetooth scan method.
                DataManager.getInstance().scan();
            }
        });

        listView = findViewById(R.id.listView);

        listView.setOnItemClickListener((parent, view, position, id) -> {

            DataManager.getInstance().deviceConnect(lstDevices.get(position));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startBluetooth(false);
    }

    private boolean startBluetooth(boolean enable) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();
        if (enable && !isEnabled) {
            return bluetoothAdapter.enable();
        } else if (!enable && isEnabled) {
            return bluetoothAdapter.disable();
        }
        // No need to change bluetooth state
        return true;
    }

    @Override
    public void scanStart() {
        // TODO: 16-11-2021 Bluetooth Scanning start callback.
        lstDevicesName = new ArrayList<>();
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstDevicesName);
        listView.setAdapter(adapter);
    }

    @Override
    public void scanEnd() {
        // TODO: 16-11-2021 Bluetooth Scanning end callback.
    }

    @Override
    public void getDeviceList(List<BluetoothDevice> bluetoothDeviceList) {
        // TODO: 16-11-2021 Get poochplay device list here.
        if (bluetoothDeviceList != null && bluetoothDeviceList.size() > 0) {
//            DataManager.getInstance().deviceConnect(bluetoothDeviceList.get(0));
            Log.e(TAG, "getDeviceList: " + bluetoothDeviceList.size());
            lstDevices = new ArrayList<>(bluetoothDeviceList);
            lstDevicesName = new ArrayList<>();
            for (BluetoothDevice device : lstDevices) {
                lstDevicesName.add(device.getName() + "\n" + device.getAddress());
            }
            ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstDevicesName);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void getDevicePercentage(String batteryPercentage) {
        tvBatteryPercentage.setText("Battery " + batteryPercentage);
        Log.e(TAG, "getDevicePercentage: " + batteryPercentage);
    }

    @Override
    public void getTotalCalories(String totalCalories) {
        tvSteps.append("\nTotal Calories: " + totalCalories);
        Log.e(TAG, "getTotalCalories: " + totalCalories);
    }

    @Override
    public void getTotalSteps(String totalSteps) {
        tvSteps.setText("TotalSteps: " + totalSteps);
        Log.e(TAG, "getTotalSteps: " + totalSteps);
    }

    @Override
    public void startConnect() {
        Toast.makeText(this, "startConnect", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void connectFailed(BluetoothDevice device) {
        Toast.makeText(this, "connection Failed " + device, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void connectSuccess(String macAddress) {
        Toast.makeText(this, "Connect Success.", Toast.LENGTH_SHORT).show();
    }


    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();

        }
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        locationPermission();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);

//                                finish();
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {

            if (resultCode == RESULT_OK) {

                Toast.makeText(getApplicationContext(), "GPS enabled", Toast.LENGTH_LONG).show();
                locationPermission();

            } else {

                enableLoc();
            }

        }
    }

    // TODO: 16-11-2021 Runtime Location permission request.
    private void locationPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_LOCATION, 101);
    }
}