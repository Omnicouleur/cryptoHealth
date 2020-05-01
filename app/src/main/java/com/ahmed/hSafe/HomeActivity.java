package com.ahmed.hSafe;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ahmed.hSafe.SmartContract.AddHealthInfoTaskSchedule;
import com.ahmed.hSafe.entities.CryptoAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {
    Button logoutBtn;
    FirebaseUser currentUser;
    String walletFilePath;
    String contractAddress;
    FirebaseAuth mAuth;
    private PendingIntent pendingIntent;
    private AlarmManager manager;


//    public interface Callback {
//
//        /**
//         * Schedule javascript function execution represented by this {@link Callback} instance
//         *
//         * @param args arguments passed to javascript callback method via bridge
//         */
//        public void invoke(Object... args);
//    }

//    void helloThere(Callback callback){
//        callback.invoke("Ahmed"," Chaari");
//    }

    @Override
    protected void onCreate(Bundle bundle) {

        super.onCreate(bundle);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        getWalletFilePathFromDB();

//        helloThere((s) -> {
//            Log.d("Callback Test","Received from callback : "+s[0]+s[1]);
//        });

        Button setAlarmBtn = findViewById(R.id.setAlarmButton);
        setAlarmBtn.setOnClickListener(v -> {
            startAlarm(v);
        });


        logoutBtn = findViewById(R.id.logoutButton);
        logoutBtn.setOnClickListener(v -> {
            Log.d("Hello", "Sign out");
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    public void startAlarm(View view) {
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 14400000;
        interval = 1000;
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    public void retrieveReceiver(String walletFilePath, String password, String contractAddress) {
        // Retrieve a PendingIntent that will perform a broadcast
        Intent alarmIntent = new Intent(this, AddHealthInfoTaskSchedule.class);
        alarmIntent.putExtra("password", password);
        alarmIntent.putExtra("walletFilePath", walletFilePath);
        alarmIntent.putExtra("contractAddress", contractAddress);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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
//                        Credentials credentials = WalletServices.loadCredentials("123456",walletFilePath);
//                        new WriteToContract(credentials,contractAddress).execute();
                        retrieveReceiver(walletFilePath, "123456", contractAddress);
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

}
