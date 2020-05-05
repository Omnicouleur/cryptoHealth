package com.ahmed.hSafe;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ahmed.hSafe.SmartContract.WalletServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.web3j.crypto.Bip39Wallet;

import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOWNLOADS;


public class CreateOrRestoreActivity extends AppCompatActivity {

    Button createNewWallet;
    Button goToRestoreUIBtn;
    Button restoreWalletBtn;
    EditText mnemonicEditText;
    EditText walletPasswordEditText;
    View restoreLayout;
    View createOrRestoreLayout;
    View loadingView;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_restore);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        createNewWallet = findViewById(R.id.create_new_wallet_btn);
        goToRestoreUIBtn = findViewById(R.id.go_to_restore_layout);
        restoreWalletBtn = findViewById(R.id.restore_wallet_btn);
        mnemonicEditText = findViewById(R.id.wallet_mnemonic_editText);
        walletPasswordEditText = findViewById(R.id.wallet_password_editText);
        createOrRestoreLayout = findViewById(R.id.restore_or_create_layout);
        restoreLayout = findViewById(R.id.restore_wallet_layout);
        loadingView = findViewById(R.id.loading_spinner);

        createNewWallet.setOnClickListener(v -> {
            Intent createWallet = new Intent(CreateOrRestoreActivity.this, CreateEthAccountActivity.class);
            startActivity(createWallet);
        });
        goToRestoreUIBtn.setOnClickListener(v -> {
            createOrRestoreLayout.setVisibility(View.GONE);
            loadingView.setVisibility(View.VISIBLE);
            (new Handler()).postDelayed(() -> {
                loadingView.setVisibility(View.GONE);
                restoreLayout.setVisibility(View.VISIBLE);
            }, 1000);
        });
        restoreWalletBtn.setOnClickListener(v -> {
            String mnemonic = mnemonicEditText.getText().toString();
            String password = walletPasswordEditText.getText().toString();
            try {
                Bip39Wallet bip39Wallet = WalletServices.restoreBipWallet(password, mnemonic);
                String path = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath() + "/" + bip39Wallet.getFilename();
                storePathInDB(path);

            } catch (Exception e) {
                Log.d("MThesisLog", "Error while restoring wallet : " + e.toString());
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void storePathInDB(String path) {
        Map<String, Object> pathUpdate = new HashMap<>();

        // Updates user's  wallet file path in DB
        FirebaseUser user = mAuth.getCurrentUser();
        pathUpdate.put("walletFilePath", path);
        if (user != null) {
            mDatabase.child("cryptoAccounts").child(user.getUid()).updateChildren(pathUpdate).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("MThesisLog", "Path updated ");
                }
            });
        } else {
            Log.d("MThesisLog", "Error in storing cyptoAccount in DB since no user is logged in, user is ");
        }
    }
}
