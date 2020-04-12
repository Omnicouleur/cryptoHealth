package com.ahmed.hSafe;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ahmed.hSafe.SmartContract.DeploySmartContractTask;
import com.ahmed.hSafe.SmartContract.EHealth;
import com.ahmed.hSafe.SmartContract.InitializeEthAccountTask;

public class CreateEthAccountActivity extends AppCompatActivity {

    private static final int ACCESS_LOCATION_REQUEST = 2;

    TextView welcomeText;
    TextView walletCreationText;
    TextView mnemonicPhrase;
    TextView contractAddressTextView;
    EditText walletPasswordEditText;
    Button createWalletButton;
    Button copyMnemonic;
    Button contractDeploySuccessBtn;
    Button nextStepDeploy;
    View displayWalletMnemonic;
    View loadingView;
    View getWalletPassowrdLayout;
    View contractDeploySuccessLayout;
    int shortAnimationDuration;
    AsyncTask<String, Void, EHealth> eHealthContract;
    String walletPassword;
    String mnemonic;
    String filePath;
    String contractAddress;
    private boolean hasPermissions() {
        int targetSdkVersion = getApplicationInfo().targetSdkVersion;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && targetSdkVersion >= Build.VERSION_CODES.Q) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST);
                return false;
            }
            if (getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ACCESS_LOCATION_REQUEST);
                return false;
            }
            if (getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_LOCATION_REQUEST);
                return false;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, ACCESS_LOCATION_REQUEST);
                return false;
            }
            if (getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },1);
                return false;
            }            if (getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },1);
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_eth_account);
        welcomeText = findViewById(R.id.welcomeText);
        walletCreationText = findViewById(R.id.wallet_creation_text);
        walletPasswordEditText = findViewById(R.id.editTextPassword);
        createWalletButton = findViewById(R.id.create_wallet_btn);
        getWalletPassowrdLayout = findViewById(R.id.create_wallet_card);
        displayWalletMnemonic = findViewById(R.id.display_wallet_mnemonic);
        loadingView = findViewById(R.id.loading_spinner);
        mnemonicPhrase = findViewById(R.id.mnemonic_phrase);
        nextStepDeploy = findViewById(R.id.next_deploy_contract);
        contractDeploySuccessLayout = findViewById(R.id.contract_deploy_success);
        contractDeploySuccessBtn = findViewById(R.id.contract_deploy_success_btn);
        contractAddressTextView = findViewById(R.id.contract_deploy_success_address);
        registerReceiver(cryptoAccountReceiver, new IntentFilter( "cryptoAccountReceiver" ));
        registerReceiver(contractReceiver, new IntentFilter( "contractReceiver" ));

        createWalletButton.setOnClickListener(v -> {
            initializeEthAccount();
        });


/*
web3j solidity generate -b eHealth.bin -a eHealth.abi -o . -p com.ahmed.hSafe
* */
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        unregisterReceiver(contractReceiver);
        unregisterReceiver(cryptoAccountReceiver);
    }
    private final BroadcastReceiver cryptoAccountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mnemonic = (String) intent.getSerializableExtra("mnemonic");
            filePath = (String) intent.getSerializableExtra("filePath");
            Log.d("Hello","From Main Activity : Steps/ Mnemonic : " + mnemonic+" <|> "+filePath);
            goToCreatedWalletView();
        }
    };
    private final BroadcastReceiver contractReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            contractAddress = (String) intent.getSerializableExtra("contractAddress");
            Log.d("Hello","From Main Activity : contract Steps address : " + contractAddress);
            contractAddressTextView.setText(contractAddress);
            goToDeployedContractView();
        }
    };
    public void initializeEthAccount(){
        Log.d("Hello","wallet password : "+ walletPasswordEditText.getText().toString());

        // Initially hide the content view.
        getWalletPassowrdLayout.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);

        // Retrieve and cache the system's default "short" animation time.
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        if (hasPermissions()){
            walletPassword = walletPasswordEditText.getText().toString();
            Log.d("Hello","Password is from main Act : "+ walletPassword);

           new InitializeEthAccountTask(getApplicationContext()).execute(walletPassword);

        }
        else {
            Log.d("Hello","Pas de permission");

        }

    }
    public void goToCreatedWalletView(){

        loadingView.setVisibility(View.GONE);
        displayWalletMnemonic.setVisibility(View.VISIBLE);
        mnemonicPhrase.setText(mnemonic);

        nextStepDeploy.setOnClickListener(sv -> {
            displayWalletMnemonic.setVisibility(View.GONE);
            loadingView.setVisibility(View.VISIBLE);

            // Retrieve and cache the system's default "short" animation time.
            shortAnimationDuration = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            // Deploy Contract
            eHealthContract =  new DeploySmartContractTask(getApplicationContext()).execute(walletPassword,filePath);
        });
    }
    public  void goToDeployedContractView(){
        loadingView.setVisibility(View.GONE);
        contractDeploySuccessLayout.setVisibility(View.VISIBLE);
        goToHomeActivity();
    }
    public void goToHomeActivity(){
        Intent intent = new Intent(CreateEthAccountActivity.this, HomeActivity.class);//TODO Change this to GattMainActivity
        startActivity(intent);
    }

}
