package com.ahmed.hSafe.Services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.ahmed.hSafe.SmartContract.EHealth;
import com.ahmed.hSafe.entities.CryptoAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.web3j.crypto.Credentials;


public class DeploySmartContractTask extends AsyncTask<String, Void, EHealth> {

    private EHealth eHealthContract;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private String walletPassword;
    private Credentials walletCredentials;
    private String filePath;
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private String network;

    public DeploySmartContractTask(Context applicationContext) {
        context = applicationContext;
    }


    protected EHealth doInBackground(String... urls) {

        walletPassword = urls[0];
        filePath = urls[1];
        network = urls[2];
        try {
            walletCredentials = WalletServices.loadCredentials(walletPassword, filePath);
        } catch (Exception e) {
            Log.d("CryptoHealthLog", "Error while loading credentials : " + e.toString());
            e.printStackTrace();
        }

        // Setting up firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Deploy the contract
        try {
            eHealthContract = WalletServices.deployContract(walletCredentials, network);
            Log.d("CryptoHealthLog", network + " : Contract deployed successfully at " + eHealthContract.getContractAddress());
            storeDataInDB();
        } catch (Exception e) {
            Log.d("CryptoHealthLog", "Error while deploying contract : " + e.getMessage() + " || " + e.toString());
            e.printStackTrace();
        }
        return eHealthContract;
    }

    protected void onPostExecute(EHealth feed) {
    }

    // Store user's address + wallet filepath in DB
    private void storeDataInDB() {
        FirebaseUser user = mAuth.getCurrentUser();
        CryptoAccount cryptoAccount = new CryptoAccount(walletCredentials.getAddress(), filePath, eHealthContract.getContractAddress());
        if (user != null) {
            mDatabase.child("cryptoAccounts").child(user.getUid()).setValue(cryptoAccount).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent intent = new Intent("contractReceiver");
                    intent.putExtra("contractAddress", eHealthContract.getContractAddress());
                    context.sendBroadcast(intent);
                }
            });
        } else {
            Log.d("CryptoHealthLog", "Error in storing cyptoAccount in DB since no user is logged in");
        }

    }
}
