package com.ahmed.hSafe.BluetoothConnection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import static android.content.Context.BLUETOOTH_SERVICE;

public class DeviceConnector {

    private BluetoothGatt bluetoothGatt;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private GattCallbackHandler gattCallbackHandler;
    private Context applicationContext;
    private HeartBeatMeasurer heartBeatMeasurer;


    DeviceConnector(Context context) {
        applicationContext = context;
        heartBeatMeasurer = new HeartBeatMeasurer(applicationContext);
        gattCallbackHandler = new GattCallbackHandler(applicationContext, heartBeatMeasurer);

        bluetoothAdapter = ((BluetoothManager) applicationContext
                .getSystemService(BLUETOOTH_SERVICE))
                .getAdapter();


        if (!bluetoothAdapter.isEnabled()) {
            ((AppCompatActivity) applicationContext)

                    .startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),

                            1);
        }

//        final ScanCallback deviceScanCallback = new DeviceScanCallback(this);
//        BluetoothLeScanner bluetoothScanner = bluetoothAdapter.getBluetoothLeScanner();
//        if(bluetoothScanner != null){
//            bluetoothScanner.startScan(deviceScanCallback);
//        }
//        final int DISCOVERY_TIME_DELAY_IN_MS = 120000;
//        new Handler().postDelayed(() -> bluetoothAdapter.getBluetoothLeScanner().stopScan(deviceScanCallback), DISCOVERY_TIME_DELAY_IN_MS);

    }

    void connectDevice() {
//        bluetoothDevice.createBond();
        bluetoothGatt = bluetoothDevice.connectGatt(applicationContext, true, gattCallbackHandler);
        if (bluetoothGatt.getDevice().getBondState() == BluetoothDevice.BOND_BONDED) {
            Log.d("Hello", "Bonded");
            bluetoothGatt.discoverServices();
        }

    }

    void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }
}
