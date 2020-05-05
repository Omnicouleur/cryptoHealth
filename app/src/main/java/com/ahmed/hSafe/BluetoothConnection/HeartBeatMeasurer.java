package com.ahmed.hSafe.BluetoothConnection;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import java.util.UUID;

import static com.ahmed.hSafe.BluetoothConnection.UUIDs.CHAR_HEART_RATE_CONTROL;
import static com.ahmed.hSafe.BluetoothConnection.UUIDs.CHAR_HEART_RATE_MEASURE;
import static com.ahmed.hSafe.BluetoothConnection.UUIDs.CHAR_SENSOR;
import static com.ahmed.hSafe.BluetoothConnection.UUIDs.SERVICE1;
import static com.ahmed.hSafe.BluetoothConnection.UUIDs.SERVICE_HEART_RATE;


/**
 *  Declares main set of methods which will be used by react UI during data fetching procedure
 * Last one includes only heart beat measurement.
 * @author  Spayker
 * @version 1.0
 * @since   06/01/2019
 */
public class HeartBeatMeasurer  {

    private BluetoothGattService service1;
    private BluetoothGattService heartService;
    private BluetoothGattCharacteristic hrCtrlChar;
    private BluetoothGattCharacteristic hrMeasureChar;
    private BluetoothGattCharacteristic sensorChar;

    /**
     * Public API for the Bluetooth GATT Profile.
     * This class provides Bluetooth GATT functionality to enable communication with Bluetooth
     * Smart or Smart Ready devices.
     * To connect to a remote peripheral device, create a BluetoothGattCallback and call
     * BluetoothDevice#connectGatt to get a instance of this class. GATT capable devices can be
     * discovered using the Bluetooth device discovery or BLE scan process.
     */
    private BluetoothGatt btGatt;
    private Context context;
    private Callback callback;
    /**
     * keeps current heart beat value taken from miband device
     */
    private String heartRateValue = "0";

    public HeartBeatMeasurer(Context context) {
        this.context = context;

    }

    void updateHrChars(BluetoothGatt gatt) {
        //this.btGatt = gatt;
        service1 = btGatt.getService(UUID.fromString(SERVICE1));
        heartService = btGatt.getService(UUID.fromString(SERVICE_HEART_RATE));

        hrCtrlChar = heartService.getCharacteristic(UUID.fromString(CHAR_HEART_RATE_CONTROL));
        hrMeasureChar = heartService.getCharacteristic(UUID.fromString(CHAR_HEART_RATE_MEASURE));
        sensorChar = service1.getCharacteristic(UUID.fromString(CHAR_SENSOR));

        btGatt.setCharacteristicNotification(hrCtrlChar, true);
        btGatt.setCharacteristicNotification(hrMeasureChar, true);
    }

    public void updateHrChars() {
        //this.btGatt = gatt;
        service1 = btGatt.getService(UUID.fromString(SERVICE1));
        heartService = btGatt.getService(UUID.fromString(SERVICE_HEART_RATE));

        hrCtrlChar = heartService.getCharacteristic(UUID.fromString(CHAR_HEART_RATE_CONTROL));
        hrMeasureChar = heartService.getCharacteristic(UUID.fromString(CHAR_HEART_RATE_MEASURE));
        sensorChar = service1.getCharacteristic(UUID.fromString(CHAR_SENSOR));

        btGatt.setCharacteristicNotification(hrCtrlChar, true);
        btGatt.setCharacteristicNotification(hrMeasureChar, true);
    }

    /**
     * Reads recieved data from miband device with current heart beat state.
     * @param characteristic GATT characteristic is a basic data element used
     *                       to construct a GATT service
     */
    void handleHeartRateData(final BluetoothGattCharacteristic characteristic) {
        byte currentHrValue = characteristic.getValue()[1];
        heartRateValue = String.valueOf(currentHrValue);
        callback.invoke(null, heartRateValue);
//        Intent intent = new Intent("heartRateReceiver");
//        intent.putExtra("heartRate", heartRateValue);
//        context.sendBroadcast(intent);

        Log.d("MThesisLog", "(Handle) Heart Rate Value = " + heartRateValue);
    }

    /**
     * Starts heartBeat data fetching from miband device.
     */
    public void startHrCalculation(Callback callback) {
        this.callback = callback;
        sensorChar.setValue(new byte[]{0x01, 0x03, 0x19});
        btGatt.writeCharacteristic(sensorChar);
    }

    void startHrCalculation() {
        sensorChar.setValue(new byte[]{0x01, 0x03, 0x19});
        btGatt.writeCharacteristic(sensorChar);
    }

    public void stopHrCalculation() {
        hrCtrlChar.setValue(new byte[]{0x15, 0x01, 0x00});
        btGatt.writeCharacteristic(hrCtrlChar);
        //Log.d("MThesisLog","hrCtrlChar: " + btGatt.writeCharacteristic(hrCtrlChar));
    }



    /**
     * Re-inits BluetoothGatt instance in case bluetooth connection was interrupted somehow.
     * @param bluetoothGatt instance to be re-initialized
     */
    void updateBluetoothConfig(BluetoothGatt bluetoothGatt) {
        this.btGatt = bluetoothGatt;
    }


}
