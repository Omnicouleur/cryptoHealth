package com.ahmed.hSafe.Services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.ahmed.hSafe.SmartContract.EHealth;

import org.web3j.crypto.Credentials;


public class WriteHRtoSM extends AsyncTask<String, Void, EHealth> {

    private String heartRate;
    private EHealth eHealthContract;
    private Credentials credentials;
    private String contractAddress;
    @SuppressLint("StaticFieldLeak")
    private Context context;

    public WriteHRtoSM(String contractAddress, String heartRate, Context context) {
        this.heartRate = heartRate;
        this.contractAddress = contractAddress;
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected EHealth doInBackground(String... strings) {
            credentials = WalletServices.loadCredentialsFromDevice(context);

        try {
            writeHeartRateToContract();
        } catch (Exception e) {
            Log.d("CryptoHealthLog", "Saving Heart Rate info to Smart Contract Exception:" + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void writeHeartRateToContract() throws Exception {
        //Load the deployed contract
        eHealthContract = WalletServices.loadContract("ropsten", credentials, contractAddress);
        WalletServices.addHeartRateInfo(eHealthContract, heartRate);
    }
}
