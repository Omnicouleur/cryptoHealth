package com.ahmed.hSafe.Services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.ahmed.hSafe.SmartContract.EHealth;
import com.ahmed.hSafe.entities.HealthInfo;

import org.web3j.crypto.Credentials;

public class WriteHealthInfoToContract extends AsyncTask<String, Void, EHealth> {

    private HealthInfo healthInfo;
    private EHealth eHealthContract;
    private Credentials credentials;
    private String contractAddress;
    @SuppressLint("StaticFieldLeak")
    private Context context;

    public WriteHealthInfoToContract(String contractAddress, HealthInfo healthInfo, Context context) {

        this.healthInfo = healthInfo;
        this.contractAddress = contractAddress;
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected EHealth doInBackground(String... strings) {
        credentials = WalletServices.loadCredentialsFromDevice(context);


        try {
            writeHealthInfoToContract();
        } catch (Exception e) {
            Log.d("CryptoHealthLog", "Saving Health info to Smart Contract Exception: " + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void writeHealthInfoToContract() throws Exception {

        //Load the deployed contract
        eHealthContract = WalletServices.loadContract("ropsten", credentials, contractAddress);

        // Store a new healthInfo object in the Blockchain
        WalletServices.addStepsInfo(eHealthContract, healthInfo);
        WalletServices.addHeartRateInfo(eHealthContract, healthInfo.getHeartBeat());

    }


}
