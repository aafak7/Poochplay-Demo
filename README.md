# PoochPlay BLE(Bluetoothy low energy) device Integration & device details.
An Android library that solves Poochplay Android Bluetooth Low Energy problems. 
Class exposes high level API for connecting and communicating with Bluetooth LE peripherals.
The API is clean and easy to read.

## Features (Version 0.0.1)

**DataManager** class provides the following features:

1. Scan device.
2. Fetching all nearby poochplay bluetooth devices.
3. Connecting with bluetooth devices.
4. Poochplay device battery percentage, Total step counts, Total calories burn.

## Importing

#### Gradle dependency

The library may be found on Jitpack.io. 
Add it to your project gradle by adding the following dependency:

```Jipack maven
allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```

Add this gradle dependency into the app gradle file.
```gradle
implementation 'com.github.Siyatech1808:ble-pp:0.0.1'
```

## How to Use this module after seting up all gradle.

First we need to Initialize SDK into activity oncreate method, And for getting call back of every method, We need to implement **BleCallBacks**

    DataManager.getInstance().setApplication(getApplication(), this);

here **getapplication()** direct instance use for bluetooth method initializing. and **this** keyword is used to get call backs of methods after implementing **BleCallBacks**.

    @Override
    public void scanStart() {
       Log.e(TAG, "Scan Start: ");
    }

    @Override
    public void scanEnd() {
        Log.e(TAG, "Scan end: ");
    }

    @Override
    public void getDeviceList(List<BluetoothDevice> bluetoothDeviceList) {
        Log.e(TAG, "Device List: " + bluetoothDeviceList.size);
    }

    @Override
    public void getDevicePercentage(String batteryPercentage) {
        Log.e(TAG, "Device Percentage: " + batteryPercentage);
    }

    @Override
    public void getTotalCalories(String totalCalories) {
        Log.e(TAG, "getTotalCalories: " + totalCalories);
    }

    @Override
    public void getTotalSteps(String totalSteps) {
        Log.e(TAG, "getTotalSteps: " + totalSteps);
    }

    @Override
    public void startConnect() {
        Log.e(TAG, "startConnect: ");
    }

    @Override
    public void connectFailed(BluetoothDevice device) {
        Log.e(TAG, "Connection failed: ");
    }

    @Override
    public void connectSuccess(String macAddress) {
        Log.e(TAG, "Connect Successfully: ");
    }


For using methods into the app to start scanning bluetooth on any click event or start activity :

    DataManager.getInstance().scan();

After scannig all the nearyby devices, getting callback into **getDeviceList(List<BluetoothDevice> bluetoothDeviceList)** method. Clicking on any search device, We need to connect poochplay ble device with the application. We need to use this method : 
  
    DataManager.getInstance().deviceConnect(lstDevices.get(position)); // lstDevices.get(position) =  searched poochplay bluetooth device.
  
After successfully connectting with ble device, We will get the callback into following methods with the device battery percentage, total steps & total calories.
  
    @Override
    public void getDevicePercentage(String batteryPercentage) {
        Log.e(TAG, "getDevicePercentage: " + batteryPercentage);
    }

    @Override
    public void getTotalCalories(String totalCalories) {
        Log.e(TAG, "getTotalCalories: " + totalCalories);
    }

    @Override
    public void getTotalSteps(String totalSteps) {
        Log.e(TAG, "getTotalSteps: " + totalSteps);
    }
  
  
## Andorid Required Permission
    
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.INTERNET" />
    
## Version 0.0.1

The BLE library v 0.0.1 is supported.

