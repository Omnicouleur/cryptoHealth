package com.ahmed.hSafe.SmartContract;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.web3j.crypto.Credentials;

import java.io.IOException;


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
        try {
            credentials = WalletServices.loadCredentialsFromDevice(context);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("MThesisLog", "Error1 while reading object from IS " + e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.d("MThesisLog", "Error2 while reading object from IS " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MThesisLog", "Error3 while reading object from IS " + e.getMessage());
        }
        try {
            writeToContract();
        } catch (Exception e) {
            Log.d("MThesisLog", "Error while writing to contract : " + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void writeToContract() throws Exception {
        //Load the deployed contract
        eHealthContract = WalletServices.loadContract("ropsten", credentials, contractAddress);
        WalletServices.addHeartRateInfo(eHealthContract, heartRate);
    }
}
