package com.ahmed.hSafe.Services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.ahmed.hSafe.BluetoothConnection.Callback;
import com.ahmed.hSafe.SmartContract.EHealth;

import org.web3j.crypto.Credentials;

public class AddDoctorToSC extends AsyncTask<String, Void, EHealth> {

    public String doctorPublicAddress;
    private EHealth eHealthContract;
    private Credentials credentials;
    private String contractAddress;
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private Callback callback;
    public AddDoctorToSC(String contractAddress, String doctorPublicAddress, Context context) {

        this.doctorPublicAddress = doctorPublicAddress;
        this.contractAddress = contractAddress;
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected EHealth doInBackground(String... strings) {

        credentials = WalletServices.loadCredentialsFromDevice(context);

        try {
            addDoctor();
        } catch (Exception e) {
            Log.d("CryptoHealthLog", "Error while adding doctor to contract : " + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addDoctor() throws Exception {

        //Load the deployed contract
        eHealthContract = WalletServices.loadContract("ropsten", credentials, contractAddress);

        // Save doctor in the smart contract
        WalletServices.addDoctor(eHealthContract, doctorPublicAddress);
    }

}
