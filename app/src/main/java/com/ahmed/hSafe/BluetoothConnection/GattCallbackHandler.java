package com.ahmed.hSafe.BluetoothConnection;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static com.ahmed.hSafe.BluetoothConnection.UUIDs.AUTH_CHAR_KEY;
import static com.ahmed.hSafe.BluetoothConnection.UUIDs.CHAR_AUTH;
import static com.ahmed.hSafe.BluetoothConnection.UUIDs.CHAR_BATTERY;
import static com.ahmed.hSafe.BluetoothConnection.UUIDs.CHAR_HEART_RATE_CONTROL;
import static com.ahmed.hSafe.BluetoothConnection.UUIDs.CHAR_HEART_RATE_MEASURE;
import static com.ahmed.hSafe.BluetoothConnection.UUIDs.CHAR_SENSOR;
import static com.ahmed.hSafe.BluetoothConnection.UUIDs.CHAR_STEPS;
import static com.ahmed.hSafe.BluetoothConnection.UUIDs.NOTIFICATION_DESC;
import static com.ahmed.hSafe.BluetoothConnection.UUIDs.SERVICE1;
import static com.ahmed.hSafe.BluetoothConnection.UUIDs.SERVICE2;
import static com.ahmed.hSafe.BluetoothConnection.UUIDs.SERVICE_HEART_RATE;

public class GattCallbackHandler extends BluetoothGattCallback {

    private BluetoothGattCharacteristic authChar;
    private BluetoothGattDescriptor authDesc;

    private BluetoothGattCharacteristic hrCtrlChar;
    private BluetoothGattDescriptor hrDescChar;
    private BluetoothGattCharacteristic batteryChar;
    private Context context;
    private HeartBeatMeasurer heartBeatMeasurer;

    public GattCallbackHandler(Context applicationContext, HeartBeatMeasurer heartBeatMeasurer){
        this.heartBeatMeasurer = heartBeatMeasurer;
        this.context = applicationContext;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        switch (newState) {
            case BluetoothGatt.STATE_DISCONNECTED:
                Log.d("Info", "Device disconnected");
                break;
            case BluetoothGatt.STATE_CONNECTED: {
                Log.d("Info", "Connected with device");
                Log.d("Info", "Discovering services");
                gatt.discoverServices();
            }
            break;
        }
    }

    private void init(BluetoothGatt gatt) {
        BluetoothGattService service1 = gatt.getService(UUID.fromString(SERVICE1));
        BluetoothGattService service2 = gatt.getService(UUID.fromString(SERVICE2));
        BluetoothGattService heartService = gatt.getService(UUID.fromString(SERVICE_HEART_RATE));

        authChar = service2.getCharacteristic(UUID.fromString(CHAR_AUTH));
        hrCtrlChar = heartService.getCharacteristic(UUID.fromString(CHAR_HEART_RATE_CONTROL));
        BluetoothGattCharacteristic hrMeasureChar = heartService.getCharacteristic(UUID.fromString(CHAR_HEART_RATE_MEASURE));
        batteryChar = service1.getCharacteristic(UUID.fromString(CHAR_BATTERY));
        hrDescChar = hrMeasureChar.getDescriptor(UUID.fromString(NOTIFICATION_DESC));
        authDesc = authChar.getDescriptor(UUID.fromString(NOTIFICATION_DESC));
    }
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        init(gatt);
        if (gatt.getDevice().getBondState() != gatt.getDevice().BOND_BONDED) {
            authoriseMiBand(gatt);
            Log.d("need","Bonding..."+gatt.getDevice().getBondState());
        }
        else {
            BluetoothGattService service1 = gatt.getService(UUID.fromString(SERVICE1));
            BluetoothGattCharacteristic stepsChar = service1.getCharacteristic(UUID.fromString(CHAR_STEPS));
            gatt.readCharacteristic(stepsChar);
        }
    }

    private void authoriseMiBand(BluetoothGatt gatt) {
        Log.d("INFO","Enabling Auth Service notifications status...");
        gatt.setCharacteristicNotification(authChar, true);                 // 1
        Log.d("INFO", "Found NOTIFICATION BluetoothGattDescriptor: " + authDesc.toString());
        authDesc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);       // 2
        gatt.writeDescriptor(authDesc);                                             // 2
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        Log.d("INFO", "onCharacteristicChanged char uuid: " + characteristic.getUuid().toString()
                + " value: " + Arrays.toString(characteristic.getValue()));

        byte[] charValue = Arrays.copyOfRange(characteristic.getValue(), 0, 3);
        switch (characteristic.getUuid().toString()){
            case CHAR_AUTH:{
                switch (Arrays.toString(charValue)){
                    case "[16, 1, 1]":{
                        authChar.setValue(new byte[]{0x02, 0x00}); //4
                        gatt.writeCharacteristic(authChar); //4
                        break;
                    }
                    case "[16, 3, 4]":
                    case "[16, 2, 1]": {
                        executeAuthorisationSequence(gatt, characteristic); //5
                        break;
                    }
                    case "[16, 3, 1]":{
                        Log.d("INFO", "Authentication has been passed successfully"); // 7

                        heartBeatMeasurer.updateHrChars(gatt);

                        BluetoothGattService service1 = gatt.getService(UUID.fromString(SERVICE1));
                        BluetoothGattCharacteristic stepsChar = service1.getCharacteristic(UUID.fromString(CHAR_STEPS));
                        gatt.readCharacteristic(stepsChar);
                    }
                }
                break;
            }
            case CHAR_HEART_RATE_MEASURE:{
                heartBeatMeasurer.handleHeartRateData(characteristic);
                Log.d("Hello","CHAR_HEART_RATE_MEASURE");

                break;
            }
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.i("INFO", "onCharacteristicRead uuid: " + characteristic.getUuid().toString()
                + " value: " + Arrays.toString(characteristic.getValue()) + " status: " + status);
        byte[] value = characteristic.getValue();
        switch (characteristic.getUuid().toString()) {
            case CHAR_STEPS: {
                Log.d("MiBand3","STEPS");
                String steps =  bytesToHex(value).substring(8,10) + bytesToHex(value).substring(6,8) + bytesToHex(value).substring(4,6) + bytesToHex(value).substring(2,4) ;
                String distance = bytesToHex(value).substring(16,18) + bytesToHex(value).substring(14,16)+ bytesToHex(value).substring(12,14) + bytesToHex(value).substring(10,12) ;
                String calories = bytesToHex(value).substring(24,26) + bytesToHex(value).substring(22,24)+bytesToHex(value).substring(20,22) + bytesToHex(value).substring(18,20) ;
                long decimalstep = Long.parseLong(steps,16);
                long decimalCalories = Long.parseLong(calories,16);
                long decimalDistance = Long.parseLong(distance,16);

                Intent intent = new Intent("stepsReceiver");
                intent.putExtra("steps", Long.toString(decimalstep));
                intent.putExtra("calories", Long.toString(decimalCalories));
                intent.putExtra("distance", Long.toString(decimalDistance));
                context.sendBroadcast(intent);

                Log.d("MiBand3","STEPS = "+decimalstep);
                Log.d("MiBand3","Calories = "+decimalCalories);
                Log.d("MiBand3","Distance = "+decimalDistance);
                gatt.readCharacteristic(batteryChar);
                break;
            }
            case CHAR_BATTERY: {
                //infoReceiver.handleBatteryData(characteristic.getValue());
                Log.d("MiBand3","BATTERY = "+value[1]);

            }
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){
        switch (characteristic.getUuid().toString()) {
            case CHAR_SENSOR: {
                switch (Arrays.toString(characteristic.getValue())){
                    // for real time HR measurement [1, 3, 19] was sent actually but [1, 3, 25]
                    // is written. Magic?
                    case "[1, 3, 25]":{
                        hrDescChar.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(hrDescChar);
                    }
                }
            }
            default:
                Log.d("INFO", "onCharacteristicWrite uuid: " + characteristic.getUuid().toString()
                        + " value: " + Arrays.toString(characteristic.getValue()) + " status: " + status);
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        switch (descriptor.getCharacteristic().getUuid().toString()){
            case CHAR_AUTH:{
                byte[] authKey = ArrayUtils.addAll(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, AUTH_CHAR_KEY);
                authChar.setValue(authKey);             // 3
                gatt.writeCharacteristic(authChar);     // 3
                break;
            }
            case CHAR_HEART_RATE_MEASURE:{
                switch (Arrays.toString(descriptor.getValue())){
                    case "[1, 0]":{
                        hrCtrlChar.setValue(new byte[]{0x15, 0x01, 0x01});
                        Log.d("INFO","hrCtrlChar: " + gatt.writeCharacteristic(hrCtrlChar));
                    }
                    default:
                        Log.d("INFO", "onDescriptorWrite uuid: " + descriptor.getUuid().toString()
                                + " value: " + Arrays.toString(descriptor.getValue()));
                }
            }
        }
    }

    private void executeAuthorisationSequence(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();
        if (value[0] == 16 && value[1] == 1 && value[2] == 1) {
            characteristic.setValue(new byte[]{0x02, 0x8});
            gatt.writeCharacteristic(characteristic);
        } else if (value[0] == 16 && value[1] == 2 && value[2] == 1) {
            try {
                byte[] tmpValue = Arrays.copyOfRange(value, 3, 19);
                String CIPHER_TYPE = "AES/ECB/NoPadding";
                Cipher cipher = Cipher.getInstance(CIPHER_TYPE);

                String CIPHER_NAME = "AES";
                SecretKeySpec key = new SecretKeySpec(AUTH_CHAR_KEY, CIPHER_NAME);
                cipher.init(Cipher.ENCRYPT_MODE, key);
                byte[] bytes = cipher.doFinal(tmpValue);
                byte[] rq = ArrayUtils.addAll(new byte[]{0x03, 0x00}, bytes);
                characteristic.setValue(rq);
                gatt.writeCharacteristic(characteristic); // 6
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (value[0] == 16 && value[1] == 3 && value[2] == 4) {
            authoriseMiBand(gatt);
        }
    }
    final protected  char[] hexArray = "0123456789ABCDEF".toCharArray();

    public  String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        Log.d("onDescriptorRead", descriptor.getUuid().toString() + " Read" + "status: " + status);
    }


}
