package com.ahmed.hSafe.SmartContract;

import android.os.Environment;
import android.util.Log;

import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.ChainId;
import org.web3j.tx.Contract;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class Wallet {

    // gas limit
    public static BigInteger gasLimit = BigInteger.valueOf(5_300_000);
    // gas price
    public static BigInteger gasPrice = BigInteger.valueOf(1000000000);
    /* Load the new wallet file once it is created
     *  Replace the File path with newly created wallet file path
     */
    public static Credentials loadCredentials(String password, String path) throws Exception {
        Credentials credentials = WalletUtils.loadCredentials(password, path);
        Log.d("Hello", "Credentials loaded, Private KEY : " + credentials.getEcKeyPair().getPrivateKey().toString(16));
        return credentials;
    }

    public static Bip39Wallet createBipWallet(String password) throws Exception {
        String path = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath();
        Bip39Wallet bip39Wallet = WalletUtils.generateBip39Wallet(password, new File(path));
        return bip39Wallet;
    }

    public static String sendTransaction(Web3j web3, Credentials credentials, String toAdress) throws Exception {
        TransactionReceipt transferReceipt = Transfer.sendFunds(web3, credentials,
                toAdress,  // you can put any address here
                BigDecimal.valueOf(25), Convert.Unit.FINNEY)
                .send();
        return transferReceipt.getTransactionHash();
    }
    public static EHealth deployContract(Web3j web3,Credentials walletCredentials) throws Exception {
        TransactionManager transactionManager = new RawTransactionManager(
                web3, walletCredentials, ChainId.RINKEBY);
        Log.d("Hello","Account's Address : "+ walletCredentials.getAddress() );
        EHealth contract = EHealth.deploy(web3,transactionManager,gasPrice, gasLimit).send();
        Log.d("Hello","Contract deployed successfuly \nContract's Address : "+contract.getContractAddress());
        return contract;
    }

}
