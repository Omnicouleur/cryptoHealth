package com.ahmed.hSafe.SmartContract;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.ahmed.hSafe.Utilities.InternalStorage;
import com.ahmed.hSafe.entities.Creds;
import com.ahmed.hSafe.entities.HealthInfo;

import org.web3j.crypto.Credentials;

import java.io.IOException;
import java.util.concurrent.Future;
public class WriteToContract extends AsyncTask<String, Void, EHealth> {

    private HealthInfo healthInfo;
    private EHealth eHealthContract;
    private Credentials credentials;
    private String contractAddress;
    @SuppressLint("StaticFieldLeak")
    private Context context;

    public WriteToContract(String contractAddress, HealthInfo healthInfo, Context context) {

        this.healthInfo = healthInfo;
        this.contractAddress = contractAddress;
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected EHealth doInBackground(String... strings) {
        try {
            Creds creds = (Creds) InternalStorage.readObject(context, "Credentials");
            credentials = creds.getCredentials();
            Log.d("MThesisLog", "Credentials loaded from internal storage " + creds.getFilePath());
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

        // Store a new healthInfo object in the Blockchain
        WalletServices.addHealthInfos(eHealthContract, healthInfo);

        //Read the health infos stored in the blockchain
        readContract(eHealthContract);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String readContract(EHealth greeter) {

        try {
            Future<String> greeting = greeter.checkHealthInfos().sendAsync();
            String convertToString = "";
            convertToString += " || " +  greeting.get();
            Log.d("MThesisLog", "Read from contract :" + convertToString);
            return convertToString;
        }
        catch (Exception e){
            Log.d("MThesisLog", "Exception read Contract :" + e.getMessage());
        }

        return "Error while reading";
    }

}
