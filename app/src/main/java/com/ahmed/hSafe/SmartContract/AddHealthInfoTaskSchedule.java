package com.ahmed.hSafe.SmartContract;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


public class AddHealthInfoTaskSchedule extends BroadcastReceiver {

    String password;

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // For our recurring task, we'll just display a message
        password = arg1.getSerializableExtra("password").toString();
        Toast.makeText(arg0, "I'm running", Toast.LENGTH_SHORT).show();
        Log.d("WriteContract", "Alarm fi9 :p " + password);

    }
}
