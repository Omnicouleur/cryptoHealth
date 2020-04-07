package com.ahmed.hSafe;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ahmed.hSafe.BluetoothConnection.GattMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        setContentView(R.layout.activity_login);
        Button newAccountButton = (Button) findViewById(R.id.newAccountButton);

        newAccountButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        Button profileButton = (Button) findViewById(R.id.profileButton);

        profileButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(LoginActivity.this, GattMainActivity.class);
                Intent intent = new Intent(LoginActivity.this, CreateEthAccountActivity.class);
                startActivity(intent);
            }
        });
        final EditText lMail = (EditText) findViewById(R.id.loginMail);
        final EditText lpassword = (EditText) findViewById(R.id.loginPassword);
        Button cirLoginButton = (Button) findViewById(R.id.cirLoginButton);
        cirLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG",lMail.getText().toString());
                signInUsers(lMail.getText().toString(),lpassword.getText().toString());
            }
        });
    }

    public void signInUsers(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAGX", "signInWithEmail:success");
                            Toast.makeText(LoginActivity.this, "Authentication Successful.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(LoginActivity.this, GattMainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAGX", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                    // ...
                });

    }

}
