# PoochPlay BLE(Bluetoothy low energy) device Integration & device details.
An Android library that solves Poochplay Android Bluetooth Low Energy problems. 
Class exposes high level API for connecting and communicating with Bluetooth LE peripherals.
The API is clean and easy to read.

## Features (Version 0.0.1)

**BleService** class provides the following features:

1. Get real time moves.
2. Fetch all offline ble device data.
3. Connecting with bluetooth devices.

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
implementation 'com.github.aafak7:poochplay-ble:1.0.0'
```

## How to Use this module after seting up all gradle.

First bind service and evaluate in Application class.

        PoochPlayApplication.context = getApplicationContext();
        Constant.appcontext = getApplicationContext();
        Log.e(TAG,"on create called");
        Intent gattServiceIntent = new Intent(this, BleService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        if (instance == null) {
            instance = this;
        }

Registered service in AndroidMainfest.xml

        <service
            android:name="com.amit.poochplayble.BleService"
            android:enabled="true" />
           
  Put below code in your class

       public BleService mBluetoothLeService;
       public BroadcastReceiver broadcastReceiver;
       public Funtion funtion = new Funtion();
       private final Handler getOldData = new Handler();
       private final Handler countRealSteps = new Handler();
 
  
  in oncreate
  
     mBluetoothLeService = Constant.bleService;
   
   
  for connect ble device.
   
     mBluetoothLeService.connect(mDeviceAddress);
   
   
  Registered reciever in onstart method
   
      @Override
      protected void onStart() {
      super.onStart();
      registerReceiver(broadcastReceiver, makeGattUpdateIntentFilter());
    }

Initialize method in oncreate 

        ProtocolHanderManager.TimerStart();
        SysHanderManager.TimerStart();
        BleDecodeData.initializeAlarmInfo();
        BleDecodeRtData.sethandler(datahandler);
        broadcastReceiver = new BleReceiver(ConnectHandler);
        
        
For getting current moves use datahandler method below

             Handler datahandler = new Handler(Looper.getMainLooper()) {
                   @SuppressLint("LongLogTag")
                   @Override
                   public void handleMessage(Message msg) {
                     super.handleMessage(msg);
                      try {
                          if (msg.what == 1) {
                              Calendar cal = Calendar.getInstance();
                               int second = cal.get(Calendar.SECOND);
                                int minute = cal.get(Calendar.MINUTE);
                                int hour = cal.get(Calendar.HOUR);
                                    if (Array.RtCtrlData.totalSteps > 0) {
                                        tvSteps.setText("Total steps :-"+Array.RtCtrlData.totalSteps);
                                        }
                                    }
                                if (msg.what == 2) {
                                    String dataString = msg.obj.toString().replace(" ", "")
                                            .toUpperCase();
                                    byte[] data = Util.hexStringToByte(dataString);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                      }
                     };


Put intent filter method in your class

         private static IntentFilter makeGattUpdateIntentFilter() {
             final IntentFilter intentFilter = new IntentFilter();
             intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
             intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
             intentFilter.addAction(BleService.ACTION_GATT_SERVICES_DISCOVERED);
             intentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);
             intentFilter.addAction("com.wrist.ble.SUCCESSSETINFO");
             intentFilter.addAction("com.wrist.ble.NRTDATAEND");
             return intentFilter;
          }
    
For tracker connectivity use connect handler below code.

Received message 

1 represent service discovered.
2 represents data received
3 represents connected
4 represents disconnected
5 represents that new protocol device has completed necessary
6 data syncing completed


     @SuppressLint("HandlerLeak")
       private Handler ConnectHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
              case 1:
                    try {
                        funtion.setdecideProtocol();
                        if (Array.DecideProtocol == 1) {
                            boolean issuccess = funtion.opennewdecideProtocol();
                            BleHelper.checkinfo();
                        } else if (Array.DecideProtocol == 2) {
                            boolean issuccess = funtion.openoldrtdecideProtocol();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                    
                case 2:
                    try {
                        if (Array.DecideProtocol == 1) {
                            BleData bledata = (BleData) msg.getData().getSerializable("data");
                            String uuid = bledata.getUuid();
                            String value = bledata.getValue();
                            String dataString = value.toString().replace(" ", "")
                                    .toUpperCase();
                            byte[] data = Util.hexStringToByte(dataString);
                            if (uuid.equals(SampleGattAttributes.NOTIFY_UUID)) {
                                BleTransLayer.TranslayerRecievePkt(data);
                            } else {
                                Message message = new Message();
                                message.what = 2;
                                message.obj = value;
                                datahandler.sendMessage(message);
                            }
                        } else if (Array.DecideProtocol == 2) {
                            BleData bledata = (BleData) msg.getData().getSerializable("data");
                            String uuid = bledata.getUuid();
                            String value = bledata.getValue();
                            Nrtanalysis.setdata(value, mBluetoothLeService, datahandler);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                case 3:
                    mProgressHUD.dismiss();
                    btnConnect.setText(" Connected- Tap here to disconnect");
                    if (mLeDevicesConnected != null) {
                        for (BluetoothDevice device : mLeDevicesConnected) {
                            if (device != null && device.getAddress().equals(mDeviceAddress))
                                mDevice = device;
                        }

                        new BluetoothTask(getApplicationContext()).execute();
                    }
                     try {
                         countRealSteps.postDelayed(countRealStepsHandler, 3000);

                         //TODO battery indication
                          /*  Intent gattServiceIntent = new Intent(getContext(), BluetoothLeService.class);
                            getActivity().startService(gattServiceIntent);
                            getActivity().bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);*/
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    return;
                case 4:
                    mProgressHUD.dismiss();
                    btnConnect.setText("Disconnected - Tap here to connect");
                    return;
                    
                case 5:
                  
                    try {
                        WaterSetInfo info = null;
            //			BleHelper.SetUTC();
            //			BleHelper.setUserSleep();
            //			BleHelper.setUserBodyInfo();
            //          BleHelper.RTSwitch();
            //			BleHelper.setAlarmPlan();
                        BleHelper.setWaterInfo(info);
            //			BleHelper.setMoveInfo();
            //			BleHelper.setRemindInfo();
                        } 
                        catch (Exception e) {
                           e.printStackTrace();
                        }
                           return;
                case 6:
                
                   for (int i=0;i<Array.liststep.size();i++) {
                       StepData stepData=Array.liststep.get(i);
                       lstDevicesName.add(stepData.getSteptime() + " - " + stepData.getStepdata());
                   }
                    btnOldData.setText("DATA RECEIVED");
                    ListAdapter adapter = new ArrayAdapter<>(TrackerActivity.this, android.R.layout.simple_list_item_1, lstDevicesName);
                    listView.setAdapter(adapter);
                    return;
            }
        }
    };


  
## Andorid Required Permission
    
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.INTERNET" />
    
## Version 0.0.1

The BLE library v 0.0.1 is supported.

