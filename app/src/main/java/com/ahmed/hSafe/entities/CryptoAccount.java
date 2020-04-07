package com.ahmed.hSafe.entities;

public class CryptoAccount {

    public String accountAddress;
    public String walletFilePath;
    public String contractAddress;

    public CryptoAccount(String accountAddress, String walletFilePath, String contractAddress) {
        this.accountAddress = accountAddress;
        this.walletFilePath = walletFilePath;
        this.contractAddress = contractAddress;
    }
}
