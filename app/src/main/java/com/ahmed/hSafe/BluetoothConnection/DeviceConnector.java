package com.ahmed.hSafe.BluetoothConnection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import static android.content.Context.BLUETOOTH_SERVICE;


/**
 * Declares main set of methods which will be used by react UI during data fetching procedure.
 * Last one includes only device connection. Make sure your miband device has
 * "Allow 3-rd party connect" option ON
 *
 * @author Spayker
 * @version 1.0
 * @since 06/01/2019
 */
public class DeviceConnector {

    private final HeartBeatMeasurer heartBeatMeasurer;
    // Bluetooth variable section
    private BluetoothGatt bluetoothGatt;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private GattCallbackHandler gattCallback;

    private Context mainContext;

    public DeviceConnector(Context context, HeartBeatMeasurer heartBeatMeasurer, Callback callback) {

        mainContext = context;
        this.heartBeatMeasurer = heartBeatMeasurer;
        //heartBeatMeasurer = new HeartBeatMeasurer(context);
        gattCallback = new GattCallbackHandler(context, heartBeatMeasurer, callback);
        enableBTAndDiscover(args -> {
        });
    }

    /**
     * Enables Bluetooth module on smart phone and starts device discovering process.
     *
     * @param successCallback - a Callback instance that will be needed in the end of discovering
     *                        process to send back a result of work.
     */
    private void enableBTAndDiscover(Callback successCallback) {
        bluetoothAdapter = ((BluetoothManager) Objects.requireNonNull(mainContext
                .getSystemService(BLUETOOTH_SERVICE)))
                .getAdapter();

        if (!bluetoothAdapter.isEnabled()) {
            ((AppCompatActivity) mainContext)
                    .startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                            1);
        }

        final ScanCallback deviceScanCallback = new DeviceScanCallback(this, successCallback);
        BluetoothLeScanner bluetoothScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (bluetoothScanner != null) {
            bluetoothScanner.startScan(deviceScanCallback);
        }

        final int DISCOVERY_TIME_DELAY_IN_MS = 120000;
        new Handler().postDelayed(() -> bluetoothAdapter.getBluetoothLeScanner().stopScan(deviceScanCallback), DISCOVERY_TIME_DELAY_IN_MS);
    }

    /**
     * Tries to connect a found miband device with the app. In case of succeed a bound level value
     * will be send back to be displayed on UI.
     *
     * @param successCallback - a Callback instance that will be needed in the end of discovering
     *                        process to send back a result of work.
     */
    void connectDevice(Callback successCallback) {
        //bluetoothDevice.createBond();
        bluetoothDevice.createBond();
        bluetoothGatt = bluetoothDevice.connectGatt(mainContext, true, gattCallback);
        if (bluetoothGatt.getDevice().getBondState() == BluetoothDevice.BOND_BONDED) {
            Log.d("CryptoHealthLog", "Bonded");
        }
        heartBeatMeasurer.updateBluetoothConfig(bluetoothGatt);

    }

    void disconnectDevice(Callback successCallback) {
        bluetoothGatt.disconnect();
        bluetoothGatt = null;
        bluetoothDevice = null;
        bluetoothAdapter = null;
    }

    void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

}
