package com.ahmed.hSafe.entities;

import com.ahmed.hSafe.SmartContract.WalletServices;

import org.web3j.crypto.Credentials;

import java.io.Serializable;

public class Creds implements Serializable {

    private String password;
    private String filePath;

    public Creds(String password, String filePath) {
        this.password = password;
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public Credentials getCredentials() throws Exception {
        return WalletServices.loadCredentials(password, filePath);
    }
}
