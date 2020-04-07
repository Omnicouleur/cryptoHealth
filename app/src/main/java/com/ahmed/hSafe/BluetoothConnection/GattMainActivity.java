package com.ahmed.hSafe.BluetoothConnection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmed.hSafe.R;

public class GattMainActivity extends AppCompatActivity {

    private BluetoothGatt bluetoothGatt;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private GattCallbackHandler gattCallbackHandler;
    private Context applicationContext;
    private HeartBeatMeasurer heartBeatMeasurer;

    @Override
    protected void onCreate(Bundle savedInstanceState){

         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);
         heartBeatMeasurer = new HeartBeatMeasurer();
         gattCallbackHandler = new GattCallbackHandler(heartBeatMeasurer);

        Context mainContext = this.getApplicationContext();
         bluetoothAdapter = ((BluetoothManager) mainContext
                    .getSystemService(BLUETOOTH_SERVICE))
                    .getAdapter();
         applicationContext = getApplicationContext();


         if (!bluetoothAdapter.isEnabled()) {
             ((AppCompatActivity)mainContext)

                     .startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),

                             1);
         }


         final ScanCallback deviceScanCallback = new DeviceScanCallback(this);
         BluetoothLeScanner bluetoothScanner = bluetoothAdapter.getBluetoothLeScanner();
         if(bluetoothScanner != null){
             bluetoothScanner.startScan(deviceScanCallback);
         }

         final int DISCOVERY_TIME_DELAY_IN_MS = 120000;
         new Handler().postDelayed(() -> {
             bluetoothAdapter.getBluetoothLeScanner().stopScan(deviceScanCallback);
             }, DISCOVERY_TIME_DELAY_IN_MS);

        Button measureHRBtn = findViewById(R.id.measureHeartRate);
        measureHRBtn.setOnClickListener(v -> {
            heartBeatMeasurer.updateHrChars(bluetoothGatt);
            heartBeatMeasurer.startHrCalculation();

        });
        Button getHrBtn = findViewById((R.id.getHeartRate));
        getHrBtn.setOnClickListener(v -> {
            heartBeatMeasurer.updateHrChars(bluetoothGatt);
            heartBeatMeasurer.getHeartRate("70");
            Log.d("MiBand3","Button getHeartRate works");
        });
    }
        @Override
    protected void onDestroy(){
            super.onDestroy();
        }

    void connectDevice() {
        Context mainContext = this.getApplicationContext();
//        bluetoothDevice.createBond();
        bluetoothGatt = bluetoothDevice.connectGatt(mainContext, true, gattCallbackHandler);
        if (bluetoothGatt.getDevice().getBondState() <= 0) {
            Log.d("TAG","HeLlo");
            bluetoothDevice.createBond();
        }
        if (bluetoothGatt.getDevice().getBondState() == bluetoothDevice.BOND_BONDED) {
            Log.d("Hello", "Bonded");
            bluetoothGatt.discoverServices();
        }
//        getModuleStorage().getHeartBeatMeasurerPackage()
//                .getHeartBeatMeasurer()
//                .updateBluetoothConfig(bluetoothGatt);
//        getDeviceBondLevel(successCallback);

    }

    void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }


//    public Context getApplicationContext() {
//        return applicationContext;
//    }



}
