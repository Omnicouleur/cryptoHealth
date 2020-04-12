package com.ahmed.hSafe;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmed.hSafe.SmartContract.WriteToContract;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle bundle) {

        super.onCreate(bundle);
        setContentView(R.layout.activity_home);
        new WriteToContract().execute();

    }
}
