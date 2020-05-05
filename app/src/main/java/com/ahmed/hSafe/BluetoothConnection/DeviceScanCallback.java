package com.ahmed.hSafe.BluetoothConnection;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.util.Log;

public class DeviceScanCallback extends ScanCallback {

    private final Callback callback;
    private DeviceConnector deviceConnector;

    DeviceScanCallback(DeviceConnector deviceConnector, Callback callback) {
        this.deviceConnector = deviceConnector;
        this.callback = callback;
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        String deviceName = result.getDevice().getName();
        if (deviceName != null && deviceName.equalsIgnoreCase("Mi Band 3")){
            Log.d("MThesisLog", "Device found " + result.getDevice().getAddress() +
                    " " + deviceName);
            deviceConnector.setBluetoothDevice(result.getDevice());
            deviceConnector.getBluetoothAdapter()
                    .getBluetoothLeScanner()
                    .stopScan(this);
            deviceConnector.connectDevice(callback);
        }
    }

}
