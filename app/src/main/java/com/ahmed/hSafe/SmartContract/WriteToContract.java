package com.ahmed.hSafe.SmartContract;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.ahmed.hSafe.entities.HealthInfo;

import org.web3j.crypto.Credentials;

import java.util.Date;
import java.util.concurrent.Future;
public class WriteToContract extends AsyncTask<String, Void, EHealth> {

    private EHealth eHealthContract;
    private Credentials credentials;
    private String contractAddress;

    public WriteToContract(Credentials credentials, String contractAddress) {
        this.credentials = credentials;
        this.contractAddress = contractAddress;
    }
    public WriteToContract(){

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected EHealth doInBackground(String... strings) {
        try {
            writeToContract();
        } catch (Exception e) {
            Log.d("WriteContract", "Error while writing to contract : " + e.toString());

            e.printStackTrace();
        }
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void writeToContract() throws Exception {

        //Load the deployed contract
        eHealthContract = WalletServices.loadContract("ropsten", credentials, contractAddress);

        // Store a new healthInfo object in the Blockchain
        HealthInfo healthInfo = new HealthInfo(new Date().toString(), "489", "7897", "74");
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
            Log.d("WriteContract", "Read from contract :" + convertToString);
            return convertToString;
        }
        catch (Exception e){
            Log.d("WriteContract", "Exception read Contract :" + e.getMessage());
        }

        return "Error while reading";
    }

}
