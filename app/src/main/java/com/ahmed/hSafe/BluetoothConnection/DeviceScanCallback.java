package com.ahmed.hSafe.BluetoothConnection;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.util.Log;

public class DeviceScanCallback extends ScanCallback {

    private GattMainActivity deviceConnector;

    DeviceScanCallback(GattMainActivity deviceConnector){
        this.deviceConnector = deviceConnector;
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        String deviceName = result.getDevice().getName();
        if (deviceName != null && deviceName.equalsIgnoreCase("Mi Band 3")){
            Log.d("TAG", "Device found " + result.getDevice().getAddress() +
                    " " + deviceName);
            deviceConnector.setBluetoothDevice(result.getDevice());
            deviceConnector.getBluetoothAdapter()
                    .getBluetoothLeScanner()
                    .stopScan(this);
            deviceConnector.connectDevice();
        }
    }

}
