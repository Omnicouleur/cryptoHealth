package com.ahmed.hSafe.BluetoothConnection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmed.hSafe.LoginActivity;
import com.ahmed.hSafe.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class GattMainActivity extends AppCompatActivity {

    private BluetoothGatt bluetoothGatt;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private GattCallbackHandler gattCallbackHandler;
    private Context applicationContext;
    private HeartBeatMeasurer heartBeatMeasurer;
    TextView stepsTextView;
    TextView caloriesTextView;
    TextView distanceTextView;
    TextView heartRateTextView;
    ImageView heartBeatMeasureBtn;
    ImageView pauseMeasureHeartRateBtn;
    ImageView measuringHeartRateIcon;
    Context context;
    Button logoutBtn;
    private final BroadcastReceiver stepsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String steps = (String) intent.getSerializableExtra("steps");
            String calories = (String) intent.getSerializableExtra("calories");
            String distance = (String) intent.getSerializableExtra("distance");
            Log.d("MThesisLog", "From Main Activity : Steps : " + steps);
            stepsTextView.setText(steps);
            caloriesTextView.setText(calories);
            distanceTextView.setText(distance);
        }
    };
    private final BroadcastReceiver heartRateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String heartRate = (String) intent.getSerializableExtra("heartRate");
            Log.d("MThesisLog", "From Main Activity : Steps : " + heartRate);
            heartRateTextView.setText(heartRate);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){

         super.onCreate(savedInstanceState);
         this.context = getApplicationContext();
         //setContentView(R.layout.activity_main);
         setContentView(R.layout.activity_home);
         heartBeatMeasurer = new HeartBeatMeasurer(context);
        gattCallbackHandler = new GattCallbackHandler(context, heartBeatMeasurer, args -> {
        });

         // Register services to get data from mi band
         registerReceiver(stepsReceiver,new IntentFilter("stepsReceiver"));
         registerReceiver(heartRateReceiver,new IntentFilter("heartRateReceiver"));

         Context mainContext = this.getApplicationContext();
        bluetoothAdapter = ((BluetoothManager) Objects.requireNonNull(mainContext
                .getSystemService(BLUETOOTH_SERVICE)))
                    .getAdapter();
         applicationContext = getApplicationContext();


         if (!bluetoothAdapter.isEnabled()) {
             ((AppCompatActivity)mainContext)

                     .startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),

                             1);
         }

//         final ScanCallback deviceScanCallback = new DeviceScanCallback(this);
//         BluetoothLeScanner bluetoothScanner = bluetoothAdapter.getBluetoothLeScanner();
//         if(bluetoothScanner != null){
//             bluetoothScanner.startScan(deviceScanCallback);
//         }
//         final int DISCOVERY_TIME_DELAY_IN_MS = 120000;
//        new Handler().postDelayed(() -> bluetoothAdapter.getBluetoothLeScanner().stopScan(deviceScanCallback), DISCOVERY_TIME_DELAY_IN_MS);

        logoutBtn = findViewById(R.id.logoutButton);
        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(GattMainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.heart_anim);
        measuringHeartRateIcon = findViewById(R.id.measuringHeartRateIcon);

        stepsTextView = findViewById(R.id.stepsTextView);
        heartRateTextView = findViewById(R.id.heartRateTextView);
        distanceTextView = findViewById(R.id.distanceTextView);
        caloriesTextView = findViewById(R.id.caloriesTextView);
        pauseMeasureHeartRateBtn = findViewById(R.id.pauseMeasureHeartRateBtn);
        heartBeatMeasureBtn = findViewById(R.id.measureHeartRateBtn);

        heartBeatMeasureBtn.setOnClickListener(v -> {
            heartBeatMeasureBtn.setVisibility(View.GONE);
            pauseMeasureHeartRateBtn.setVisibility(View.VISIBLE);
            measuringHeartRateIcon.startAnimation(animation);
            if (bluetoothGatt != null) {
                heartBeatMeasurer.updateHrChars(bluetoothGatt);
                heartBeatMeasurer.startHrCalculation();
            }

        });

        pauseMeasureHeartRateBtn.setOnClickListener(v -> {
            pauseMeasureHeartRateBtn.setVisibility(View.GONE);
            measuringHeartRateIcon.clearAnimation();
            if (bluetoothGatt != null) {
                heartBeatMeasurer.stopHrCalculation();
            }
            measuringHeartRateIcon.setVisibility(View.INVISIBLE);
            heartBeatMeasureBtn.setVisibility(View.VISIBLE);
        });
//        getHrBtn.setOnClickListener(v -> {
//            heartBeatMeasurer.updateHrChars(bluetoothGatt);
//            heartBeatMeasurer.getHeartRate("70");
//            Log.d("MThesisLog","Button getHeartRate works");
//        });
    }

    @Override
    protected void onDestroy(){
            super.onDestroy();
            unregisterReceiver(stepsReceiver);
            unregisterReceiver(heartRateReceiver);
        }

    void connectDevice() {
        Context mainContext = this.getApplicationContext();
//        bluetoothDevice.createBond();
        bluetoothGatt = bluetoothDevice.connectGatt(mainContext, true, gattCallbackHandler);
        if (bluetoothGatt.getDevice().getBondState() == BluetoothDevice.BOND_BONDED) {
            Log.d("MThesisLog", "Bonded");
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
