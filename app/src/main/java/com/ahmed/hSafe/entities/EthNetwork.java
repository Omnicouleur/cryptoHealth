package com.ahmed.hSafe.entities;


import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

public class EthNetwork {
    public Credentials adminCreds;
    public int chainId;
    public Web3j web3Instance;

    public EthNetwork(Credentials adminCreds, long chainId, Web3j web3Instance) {
        this.adminCreds = adminCreds;
        this.chainId = (int) chainId;
        this.web3Instance = web3Instance;
    }
}
