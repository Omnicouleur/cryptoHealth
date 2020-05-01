package com.ahmed.hSafe.SmartContract;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.Credentials;

import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOWNLOADS;



public class InitializeEthAccountTask extends AsyncTask<String, Void, Map<String,String>> {

    private Exception exception;
    private EHealth eHealthContract;
    private String mnemonic;
    private String filePath;
    private Bip39Wallet bip39Wallet;
    private Credentials walletCredentials;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String walletPassword;
    private Context context;
    private Map<String, String> resMap;
    private String network;

    public InitializeEthAccountTask(Context applicationContext) {
        context = applicationContext;

    }


    protected Map<String,String> doInBackground(String... urls) {
        walletPassword = urls[0];
        network = urls[1];
        resMap = new HashMap<>();

        // setting up firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Create wallet and send funds to it
        try {
            // Create New Wallet
            bip39Wallet = WalletServices.createBipWallet(walletPassword);
            mnemonic = bip39Wallet.getMnemonic();
            resMap.put("mnemonic", mnemonic);

            // Get the wallet file Path
            filePath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath() + "/" + bip39Wallet.getFilename();
            resMap.put("filePath", filePath);

            // Load credentials from the wallet file
            walletCredentials = WalletServices.loadCredentials(walletPassword, filePath);
            resMap.put("address", walletCredentials.getAddress());

            // Send funds to the newly created wallet
            String transHash = WalletServices.sendTransaction(walletCredentials.getAddress(), network);
            Log.d("Hello", "Transaction Hash : " + transHash);

            // Send data to main activity via broadcast
            Intent intent = new Intent("cryptoAccountReceiver");
            intent.putExtra("mnemonic", resMap.get("mnemonic"));
            intent.putExtra("address", resMap.get("address"));
            intent.putExtra("filePath", resMap.get("filePath"));
            context.sendBroadcast(intent);
            }
            catch (Exception e) {
                Log.d("Hello","Error  : " + e.getMessage() +" || "+ e.toString());
                e.printStackTrace();
            }
        return resMap ;
    }
}
