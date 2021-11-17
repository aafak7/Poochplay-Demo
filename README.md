# PoochPlay BLE device Integration & device details.
An Android library that solves Poochplay Android Bluetooth Low Energy problems. 
class exposes high level API for connecting and communicating with Bluetooth LE peripherals.
The API is clean and easy to read.

## Features (Version 0.0.1)

**DataManager** class provides the following features:

1. Scan device.
2. Fetch all neary by poochplay bluetooth devices.
3. Connection with bluetooth device.
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

Add this gradLe dependency into the app gradle file.
```gradle
implementation 'com.github.Siyatech1808:ble-pp:0.0.1'
```

## How to Use this module after setup all gradle.

First we need to Initialize SDK into activity oncreate method, And for get call back of every method we need to implement **BleCallBacks**

    DataManager.getInstance().setApplication(getApplication(), this);

here **getapplication()** application direct instance use for bluetooth method initializing. and **this** keyword used for get call backs of method after implement **BleCallBacks**.

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


For using methods into the app for start scan bluetooth on any click event or on start activity :

    DataManager.getInstance().scan();

After scannig all the nearyby device, get callback into **getDeviceList(List<BluetoothDevice> bluetoothDeviceList)** method. Click on any search device we need to connect poochplay ble device with the application. We need to use this method : 
  
    DataManager.getInstance().deviceConnect(lstDevices.get(position)); // lstDevices.get(position) =  searched poochplay bluetooth device.
  
After connect successfully with ble device, We will get the callback into following methods with the device battery percentage, total steps & total calories.
  
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
  
  
## Version 0.0.1

The BLE library v 0.0.1 is supported.

