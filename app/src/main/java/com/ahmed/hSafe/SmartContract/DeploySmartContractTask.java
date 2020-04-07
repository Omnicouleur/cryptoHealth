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


        import static android.os.Environment.DIRECTORY_DOWNLOADS;



public class DeploySmartContractTask extends AsyncTask<String, Void, EHealth> {

    private Exception exception;
    private EHealth eHealthContract;


    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    String walletPassword;
    Credentials walletCredentials;
    String filePath;
    Context context;

    public DeploySmartContractTask(Context applicationContext) {
        context = applicationContext;
    }


    protected EHealth doInBackground(String... urls) {
        // setting up firebase
        walletPassword = urls[0];
        filePath = urls[1];

        try {
            walletCredentials = Wallet.loadCredentials(walletPassword,filePath);
        } catch (Exception e) {
            Log.d("Hello","Error while loading credentials : "+e.toString());
            e.printStackTrace();
        }

        Log.d("Hello","Password is : "+walletPassword);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // endpoint url provided by infura
        String url = "https://rinkeby.infura.io/v3/175c599b149742d8a917fd6962e81788";
        // web3j infura instance
        Web3j web3 = Web3j.build(new InfuraHttpService(url));

        // Deploy the contract
        try {
            eHealthContract = Wallet.deployContract(web3,walletCredentials);
            // Store user's address + wallet file path in DB
            FirebaseUser user = mAuth.getCurrentUser();
            CryptoAccount cryptoAccount = new CryptoAccount(walletCredentials.getAddress(),filePath,eHealthContract.getContractAddress());
            mDatabase.child("cyptoAccounts").child(user.getUid()).setValue(cryptoAccount).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Intent intent = new Intent("contractReceiver");
                    intent.putExtra("contractAddress", eHealthContract.getContractAddress());
                    context.sendBroadcast(intent);
                }
            });

        } catch (Exception e) {
            Log.d("Hello","Error while deploying contract : " + e.getMessage() +" || "+ e.toString());
            e.printStackTrace();
        }
        return eHealthContract ;
    }

    protected void onPostExecute(EHealth feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
    public String Hello() {
        return "screw you";
    }

}
