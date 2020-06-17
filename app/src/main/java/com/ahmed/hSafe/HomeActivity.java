package com.ahmed.hSafe;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ahmed.hSafe.BluetoothConnection.DeviceConnector;
import com.ahmed.hSafe.BluetoothConnection.HeartBeatMeasurer;
import com.ahmed.hSafe.SmartContract.WriteHRtoSM;
import com.ahmed.hSafe.SmartContract.WriteToContract;
import com.ahmed.hSafe.entities.CryptoAccount;
import com.ahmed.hSafe.entities.HealthInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.timqi.sectorprogressview.ColorfulRingProgressView;

import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    FirebaseUser currentUser;
    String walletFilePath;
    String contractAddress;
    FirebaseAuth mAuth;
    private PendingIntent pendingIntent;
    private AlarmManager manager;
    HeartBeatMeasurer heartBeatMeasurer;

    TextView stepsTextView;
    TextView caloriesTextView;
    TextView distanceTextView;
    TextView heartRateTextView;
    ImageView heartBeatMeasureBtn;
    ImageView pauseMeasureHeartRateBtn;
    ImageView measuringHeartRateIcon;
    Context context;
    ImageView logoutBtn;
    Button syncDataWithSM;
    ImageView syncDataWBand;
    ColorfulRingProgressView stepProgressView;
    Button setAlarmBtn;

    @Override
    protected void onCreate(Bundle bundle) {

        super.onCreate(bundle);
        setContentView(R.layout.activity_home_v2);
        context = getApplicationContext();

        // Firebase setup
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        // Fetches Contract address and Wallet file path from database and creates the pending intent
        getWalletFilePathFromDB();

        //initiate our Heart beat measurer. it will be used inside syncData()
        heartBeatMeasurer = new HeartBeatMeasurer(getApplicationContext());

        setAlarmBtn = findViewById(R.id.setAlarmButton);
        logoutBtn = findViewById(R.id.logoutButton);
        measuringHeartRateIcon = findViewById(R.id.measuringHeartRateIcon);
        stepsTextView = findViewById(R.id.stepsTextView);
        heartRateTextView = findViewById(R.id.heartRateTextView);
        distanceTextView = findViewById(R.id.distanceTextView);
        caloriesTextView = findViewById(R.id.caloriesTextView);
        pauseMeasureHeartRateBtn = findViewById(R.id.pauseMeasureHeartRateBtn);
        heartBeatMeasureBtn = findViewById(R.id.measureHeartRateBtn);
        syncDataWBand = findViewById(R.id.syncDataWithBand);
        syncDataWithSM = findViewById(R.id.syncDataWithSM);
        stepProgressView = findViewById(R.id.stepProgressView);

        // Set Buttons listeners
        setProfileBtnListener();
        setLogoutBtnListener();
        setAlarmBtn.setOnClickListener(this::startAlarm);
        // Set Sync data button Listener
        syncDataWBand.setOnClickListener(v -> syncData(false));
        syncDataWithSM.setOnClickListener(v -> syncData(true));
        // Start Measuring Heart Rate
        setStartHRMeasureListener();
        //Stop measuring heart rate
        setStopHRMeasureListener();
        //syncData();
    }


    public void syncData(Boolean uploadToContract) {

        Log.d("MThesisLog", "Syncing data with band ...");
        DeviceConnector deviceConnector = new DeviceConnector(getApplicationContext(), heartBeatMeasurer, args -> {
            heartBeatMeasurer.updateHrChars();
            Log.d("MThesisLog", "S : " + args[1] + "ll" + args[2] + "ll" + args[3]);

            heartBeatMeasurer.startHrCalculation(args1 -> {
                Log.d("MThesisLog", "HR c : " + args1[1]);
                heartBeatMeasurer.stopHrCalculation();
                HealthInfo healthInfo = new HealthInfo(new Date().toString(), args[3].toString(), args[1].toString(), args1[1].toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stepsTextView.setText(healthInfo.getSteps());
                        float prc = (float) Integer.parseInt(healthInfo.getSteps()) / 100;
                        Log.d("MThesisLog", "Pourcentage : " + prc);
                        stepProgressView.setPercent(prc);
                        heartRateTextView.setText(healthInfo.getHeartBeat());
                        distanceTextView.setText(args[2].toString());
                        caloriesTextView.setText(healthInfo.getCalories());
                        if (uploadToContract)
                            new WriteToContract(contractAddress, healthInfo, getApplicationContext()).execute();
                        heartBeatMeasurer.stopHrCalculation();
                    }
                });
            });
        });
    }
    public void startAlarm(View view) {
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 14400000;
        assert manager != null;
//        manager.setExactAndAllowWhileIdle();
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
        //manager.cancel(pendingIntent);
    }

    public void retrieveActivity(String contractAddress) {
        // Retrieve a PendingIntent that will perform a broadcast
        Intent alarmIntent = new Intent(this, TaskActivity.class);
        alarmIntent.putExtra("contractAddress", contractAddress);
        pendingIntent = PendingIntent.getActivity(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void getWalletFilePathFromDB() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("cryptoAccounts/");

        myRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CryptoAccount cryptoAccount = dataSnapshot.getValue(CryptoAccount.class);
                if (cryptoAccount != null) {
                    walletFilePath = cryptoAccount.walletFilePath;
                    contractAddress = cryptoAccount.contractAddress;
                    try {
                        retrieveActivity(contractAddress);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
    }

    private void setStartHRMeasureListener() {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.heart_anim);
        heartBeatMeasureBtn.setOnClickListener(v -> {
            if (heartBeatMeasurer.bleGattExist()) {
                heartBeatMeasureBtn.setVisibility(View.GONE);
                pauseMeasureHeartRateBtn.setVisibility(View.VISIBLE);
                measuringHeartRateIcon.startAnimation(animation);

                heartBeatMeasurer.startHrCalculation(args1 -> {
                    Log.d("MThesisLog", "HR c : " + args1[1]);
                    heartBeatMeasurer.stopHrCalculation();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pauseMeasureHeartRateBtn.setVisibility(View.GONE);
                            measuringHeartRateIcon.clearAnimation();
                            measuringHeartRateIcon.setVisibility(View.INVISIBLE);
                            heartBeatMeasureBtn.setVisibility(View.VISIBLE);
                            heartRateTextView.setText(args1[1].toString());
                            int hr = Integer.parseInt(args1[1].toString());
                            if (hr > 0)
                                new WriteHRtoSM(contractAddress, args1[1].toString(), getApplicationContext()).execute();
                        }
                    });
                });
            }
        });
    }

    private void setStopHRMeasureListener() {
        //Stop measuring heart rate
        pauseMeasureHeartRateBtn.setOnClickListener(v -> {
            pauseMeasureHeartRateBtn.setVisibility(View.GONE);
            measuringHeartRateIcon.clearAnimation();
            measuringHeartRateIcon.setVisibility(View.INVISIBLE);
            heartBeatMeasureBtn.setVisibility(View.VISIBLE);
            if (heartBeatMeasurer.bleGattExist()) {
                heartBeatMeasurer.stopHrCalculation();
            }

        });
    }

    private void setProfileBtnListener() {
        ImageView profileBtn = findViewById(R.id.profile_icon);
        profileBtn.setOnClickListener(v -> {
            Intent profileActivity = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(profileActivity);
        });
    }

    private void setLogoutBtnListener() {
        logoutBtn.setOnClickListener(v -> {
            Log.d("MThesisLog", "Sign out");
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
