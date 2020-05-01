package com.ahmed.hSafe;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ahmed.hSafe.entities.CryptoAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    FirebaseUser currentUser;
    String walletFilePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d("Hello","user exists, cool : " + currentUser.getEmail());
            goToHomeActivity();
        }
        setContentView(R.layout.activity_login);
        Button newAccountButton = findViewById(R.id.newAccountButton);

        newAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        Button profileButton = findViewById(R.id.profileButton);

        profileButton.setOnClickListener(v -> {
            //Intent intent = new Intent(LoginActivity.this, CreateEthAccountActivity.class);
            Intent intent = new Intent(LoginActivity.this, CreateOrRestoreActivity.class);
            startActivity(intent);
        });
        final EditText lMail = findViewById(R.id.loginMail);
        final EditText lpassword = findViewById(R.id.loginPassword);
        Button cirLoginButton = findViewById(R.id.cirLoginButton);
        cirLoginButton.setOnClickListener(v -> {
            Log.d("TAG", lMail.getText().toString());
            signInUsers(lMail.getText().toString(), lpassword.getText().toString());
        });
    }

    public void signInUsers(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAGX", "signInWithEmail:success");
                        Toast.makeText(LoginActivity.this, "Authentication Successful.",
                                Toast.LENGTH_SHORT).show();
                        currentUser = mAuth.getCurrentUser();
                        getWalletFilePathFromDB();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAGX", "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void getWalletFilePathFromDB(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("cryptoAccounts/");

        myRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CryptoAccount cryptoAccount = dataSnapshot.getValue(CryptoAccount.class);
                if (cryptoAccount != null) {
                    walletFilePath = cryptoAccount.walletFilePath;
                    Log.d("Hello","Account : "+cryptoAccount.walletFilePath);
                    Log.d("Hello","file exists ? "+ isFilePresent(cryptoAccount.walletFilePath));
                    Log.d("Hello","file exists ? "+ isFilePresent(cryptoAccount.walletFilePath));
                    if (isFilePresent(cryptoAccount.walletFilePath)){
                        goToHomeActivity();
                        return;
                    }
                }
                Intent intent = new Intent(LoginActivity.this, CreateOrRestoreActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
    }
    public boolean isFilePresent(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }
    public void goToHomeActivity(){
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);//TODO Change this to GattMainActivity
        startActivity(intent);
    }
}
