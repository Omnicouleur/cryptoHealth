package com.ahmed.hSafe;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ProfileActivity extends AppCompatActivity {

    private PendingIntent pendingIntent;
    private AlarmManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent alarmIntent = new Intent(this, TaskActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        startAlarm();
    }

    public void startAlarm() {
        Log.d("MThesisLog", "Starting alarm...");
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 14400000;
        assert manager != null;
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
        //manager.cancel(pendingIntent);
    }
}
