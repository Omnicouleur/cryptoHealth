package com.ahmed.hSafe.SmartContract;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.ahmed.hSafe.entities.CryptoAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.infura.InfuraHttpService;


import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOWNLOADS;



public class InitializeEthAccountTask extends AsyncTask<String, Void, Map<String,String>> {

    private Exception exception;
    private EHealth eHealthContract;
    String mnemonic;
    String filePath;
    Bip39Wallet bip39Wallet;
    Credentials walletCredentials;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    String walletPassword;
    Context context;
    Map<String,String> resMap;

    public InitializeEthAccountTask(Context applicationContext) {
        context = applicationContext;
    }


    protected Map<String,String> doInBackground(String... urls) {
            // setting up firebase
            walletPassword = urls[0];
            Log.d("Hello","Password is : "+walletPassword);
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();

            // endpoint url provided by infura
            String url = "https://rinkeby.infura.io/v3/175c599b149742d8a917fd6962e81788";
            // web3j infura instance
            Web3j web3 = Web3j.build(new InfuraHttpService(url));
            //Credentials adminCreds = Credentials.create("21E20BD99C3EA66B11F03087501FAE73935C17C6A616B6F5F5386406541C2EC0");
            Credentials adminCreds = Credentials.create("f9319fe162c31947c0ca8fd649a536b7ca311b5f210afdc48b62fd7d18ce53e4");

            resMap = new HashMap<String, String>();
            // Create wallet and send funds to it
            try {
                // Create New Wallet
                bip39Wallet =  Wallet.createBipWallet(walletPassword);
                mnemonic = bip39Wallet.getMnemonic();
                resMap.put("mnemonic",mnemonic);
                Log.d("Hello","Wallet created, Mnemonic : "+mnemonic);

                // Get the wallet file Path
                filePath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath() +"/"+bip39Wallet.getFilename();
                resMap.put("filePath",filePath);
                // Load credentials from the wallet file
                walletCredentials = Wallet.loadCredentials(walletPassword,filePath);
                Log.d("Hello","Account Address : "+ walletCredentials.getAddress()+"\n Wallet file Path : "+ filePath);

                resMap.put("address",walletCredentials.getAddress());
                // Send Funds to the new wallet since funds are needed to deploy the contract and use it's methods
                String transHash = Wallet.sendTransaction(web3,adminCreds,walletCredentials.getAddress());
                Log.d("Hello","Transaction Hash : "+ transHash);
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

    protected void onPostExecute(EHealth feed) {
        mnemonic = "Hello There";
        Log.d("Hello","OnPostExecute");

        // TODO: check this.exception
        // TODO: do something with the feed
    }
    public String Hello() {
        return "screw you";
    }

}
