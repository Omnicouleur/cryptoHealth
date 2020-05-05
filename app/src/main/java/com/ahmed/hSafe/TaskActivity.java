package com.ahmed.hSafe;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ahmed.hSafe.BluetoothConnection.DeviceConnector;
import com.ahmed.hSafe.BluetoothConnection.HeartBeatMeasurer;
import com.ahmed.hSafe.SmartContract.WriteToContract;
import com.ahmed.hSafe.entities.HealthInfo;

import java.util.Date;


public class TaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String contractAddress = intent.getStringExtra("contractAddress");
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d("MThesisLog", "Starting task... ");
        HeartBeatMeasurer heartBeatMeasurer = new HeartBeatMeasurer(getApplicationContext());
        DeviceConnector deviceConnector = new DeviceConnector(getApplicationContext(), heartBeatMeasurer, args -> {
            Log.d("MThesisLog", "Steps : " + args[1] + " || Distance : " + args[2]);
            heartBeatMeasurer.updateHrChars();
            heartBeatMeasurer.startHrCalculation(args1 -> {
                Log.d("MThesisLog", "HR c : " + args1[1]);
                heartBeatMeasurer.stopHrCalculation();
                HealthInfo healthInfo = new HealthInfo(new Date().toString(), args[3].toString(), args[1].toString(), args1[1].toString());
                new WriteToContract(contractAddress, healthInfo, getApplicationContext()).execute();
            });
        });
    }
}
