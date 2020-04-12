package com.ahmed.hSafe.SmartContract;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.infura.InfuraHttpService;
import org.web3j.tx.ChainId;
import org.web3j.tx.Contract;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;
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
            Log.d("WriteContract","Hello");
            writeToContract();
        } catch (Exception e) {
            Log.d("WriteContract","Error : "+e.toString());

            e.printStackTrace();
        }
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void writeToContract() throws Exception {

        // endpoint url provided by infura
        String url = "https://rinkeby.infura.io/v3/175c599b149742d8a917fd6962e81788";
        // web3j infura instance
        Web3j web3 = Web3j.build(new InfuraHttpService(url));

        Web3j web3j_ganache = Web3j.build(new HttpService("http://10.0.2.2:8545"));
        // gas limit
        BigInteger gasLimit = BigInteger.valueOf(5_300_000);
        // gas price
        BigInteger gasPrice = BigInteger.valueOf(1000000000);
        // contract address
        String contractAddress = "0x3eb4a1f2516edb8a44d1c719a1399886c5f4bbd3";
        // create credentials w/ your private key
        Credentials credentials = Credentials.create("f9319fe162c31947c0ca8fd649a536b7ca311b5f210afdc48b62fd7d18ce53e4");

        String contractAddressGanache = "0x5dd1e9781cc5bee88e3753faf52f706250724812";
        Credentials credentialsGanache = Credentials.create("17fcb7e2dcca0e752c8e3c77df0562e396056e4ee73a8e7b18744ee114327c65");

        Log.d("WriteContract","Hello2 Creds : "+credentials.getAddress());
        TransactionManager transactionManager = new RawTransactionManager(
                web3j_ganache, credentials, ChainId.RINKEBY);
        ContractGasProvider contractGasProvider = new StaticGasProvider(gasPrice,gasLimit);

        eHealthContract = EHealth.load(contractAddressGanache, web3j_ganache, credentialsGanache,gasPrice,gasLimit);

        Log.d("WriteContract","Hello C: "+eHealthContract.getContractAddress());
        readContract(eHealthContract);
        // write to contract
//        TransactionReceipt transactionReceipt = eHealthContract.createPatient("Ahmed",new BigInteger(String.valueOf(20)),new BigInteger(String.valueOf(1))).send();
//        Log.d("WriteContract","Transaction Hash : "+transactionReceipt.toString());


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected String readContract (EHealth greeter) {

        try {
            Future<String> greeting = greeter.getPatientName().sendAsync();
            String convertToString = "";
            convertToString += " || " +  greeting.get();
            Log.d("Hello", "Read from contract :" + convertToString);
            return convertToString;
        }
        catch (Exception e){
            Log.d("Hello", "Exception 2 :" + e.getMessage());
        }
        finally {
            return "no";
        }

    }

}
