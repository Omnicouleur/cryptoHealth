package com.ahmed.hSafe.SmartContract;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

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
    private Context context;
    private String network;

    public DeploySmartContractTask(Context applicationContext) {
        context = applicationContext;
    }


    protected EHealth doInBackground(String... urls) {
        // setting up firebase
        walletPassword = urls[0];
        filePath = urls[1];
        network = urls[2];
        try {
            walletCredentials = Wallet.loadCredentials(walletPassword,filePath);
        } catch (Exception e) {
            Log.d("Hello","Error while loading credentials : "+e.toString());
            e.printStackTrace();
        }

        Log.d("Hello","Password is : "+walletPassword);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Deploy the contract
        try {
            eHealthContract = Wallet.deployContract(walletCredentials, network);
            Log.d("Hello", network + " : Contract deployed at " + eHealthContract.getContractAddress());
            storeDataInDB();
        } catch (Exception e) {
            Log.d("Hello", "Error while deploying contract : " + e.getMessage() + " || " + e.toString());
            e.printStackTrace();
        }
        return eHealthContract;
    }

    protected void onPostExecute(EHealth feed) {
    }

    private void storeDataInDB() {
        // Store user's address + wallet file path in DB
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
            Log.d("Hello", "Error in storing cyptoAccount in DB since no user is logged in, user is ");
        }

    }
}
