package com.ahmed.hSafe.Services;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.ahmed.hSafe.SmartContract.EHealth;
import com.ahmed.hSafe.Utilities.InternalStorage;
import com.ahmed.hSafe.entities.Creds;
import com.ahmed.hSafe.entities.EthNetwork;
import com.ahmed.hSafe.entities.HealthInfo;

import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.infura.InfuraHttpService;
import org.web3j.tx.ChainIdLong;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class WalletServices {

    // gas limit
    private static BigInteger gasLimit = BigInteger.valueOf(5_300_000);
    // gas price
    private static BigInteger gasPrice = BigInteger.valueOf(1000000000);
    /* Load the new wallet file once it is created
     *  Replace the File path with newly created wallet file path
     */

    private static Web3j web3j_ganache = Web3j.build(new HttpService("http://10.0.2.2:8545"));
    // endpoint url provided by infura
    private static String url_rinkeby = "https://rinkeby.infura.io/v3/175c599b149742d8a917fd6962e81788";
    // web3j infura instance
    private static Web3j web3_rinkeby = Web3j.build(new InfuraHttpService(url_rinkeby));
    // endpoint url provided by infura
    private static String url_ropsten = "https://ropsten.infura.io/v3/175c599b149742d8a917fd6962e81788";
    // web3j infura instance
    private static Web3j web3_ropsten = Web3j.build(new InfuraHttpService(url_ropsten));
    private static ContractGasProvider contractGasProvider = new StaticGasProvider(gasPrice, gasLimit);
    private static Map<String, EthNetwork> web3js = new HashMap<>();

    private static void setWeb3js() {
        Credentials credentialsRi = Credentials.create("0xf9319fe162c31947c0ca8fd649a536b7ca311b5f210afdc48b62fd7d18ce53e4");
        Credentials credentialsG = Credentials.create("0x3013130b6a5f19d93abc67cad9c35e5ace338d1a06e2028da989fceae85af10b");
        Credentials credentialsRo = Credentials.create("0x21E20BD99C3EA66B11F03087501FAE73935C17C6A616B6F5F5386406541C2EC0");
        web3js.put("ganache", new EthNetwork(credentialsG, ChainIdLong.ROPSTEN, web3j_ganache));
        web3js.put("rinkeby", new EthNetwork(credentialsRi, ChainIdLong.RINKEBY, web3_rinkeby));
        web3js.put("ropsten", new EthNetwork(credentialsRo, ChainIdLong.ROPSTEN, web3_ropsten));
    }

    public static Credentials loadCredentials(String password, String path) throws Exception {
        Credentials credentials = WalletUtils.loadCredentials(password, path);
        Log.d("CryptoHealthLog", "Credentials loaded, Private KEY : " + credentials.getEcKeyPair().getPrivateKey().toString(16));
        Log.d("CryptoHealthLog", "Public Address : " + credentials.getAddress());
        return credentials;
    }

    static Bip39Wallet createBipWallet(String password) throws Exception {
        String path = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath();
        Bip39Wallet bip39Wallet = WalletUtils.generateBip39Wallet(password, new File(path));
        Log.d("CryptoHealthLog", "Wallet created, Mnemonic : " + bip39Wallet.getMnemonic());
        return bip39Wallet;
    }

    public static Bip39Wallet restoreBipWallet(String password, String mnemonic) throws Exception {
        String path = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath();
        Bip39Wallet bip39Wallet = WalletUtils.generateBip39WalletFromMnemonic(password, mnemonic, new File(path));
        Log.d("CryptoHealthLog", "Wallet resotred, Mnemonic : " + bip39Wallet.getMnemonic());
        return bip39Wallet;
    }

    static String sendTransaction(String toAdress, String network) throws Exception {
        if (web3js.isEmpty()) setWeb3js();

        EthNetwork net = web3js.get(network);
        assert net != null;
        TransactionReceipt transferReceipt = Transfer.sendFunds(net.web3Instance, net.adminCreds,
                toAdress,  // you can put any address here
                BigDecimal.valueOf(45), Convert.Unit.FINNEY)
                .send();
        return transferReceipt.getTransactionHash();
    }

    static EHealth deployContract(Credentials walletCredentials, String network) throws Exception {
        if (web3js.isEmpty()) setWeb3js();
        EthNetwork net = web3js.get(network);
        assert net != null;
        TransactionManager transactionManager = new RawTransactionManager(
                net.web3Instance, walletCredentials, net.chainId);
        EHealth contract = EHealth.deploy(net.web3Instance, transactionManager, contractGasProvider).send();
        Log.d("CryptoHealthLog", "Contract deployed successfully \nContract's Address : " + contract.getContractAddress());
        return contract;
    }


    public static EHealth loadContract(String network, Credentials credentials, String contractAddress) {
        if (web3js.isEmpty()) setWeb3js();
        EthNetwork net = web3js.get(network);
        assert net != null;
        EHealth eHealthContract = EHealth.load(contractAddress, net.web3Instance, credentials, contractGasProvider);
        Log.d("CryptoHealthLog", "Contract Loaded successfully: address : " + eHealthContract.getContractAddress());

        return eHealthContract;
    }

    static void addStepsInfo(EHealth eHealthContract, HealthInfo healthInfo) throws Exception {
        //write to contract
        TransactionReceipt transactionReceipt = eHealthContract.addStepsInfos(new Date().toString(), healthInfo.getCalories(), healthInfo.getSteps()).send();
        Log.d("CryptoHealthLog", "Health info successfully saved : Transaction Hash : " + transactionReceipt.toString());
    }

    static void addHeartRateInfo(EHealth eHealthContract, String heartRate) throws Exception {
        if (Integer.parseInt(heartRate) < 1) return;
        //write to contract
        TransactionReceipt transactionReceipt = eHealthContract.addHeartRate(new Date().toString(), heartRate).send();
        Log.d("CryptoHealthLog", "Heart Rate successfully saved : Transaction Hash : " + transactionReceipt.toString());
    }

    public static void addDoctor(EHealth eHealthContract, String doctorPublicAddress) throws Exception {
        //write to contract
        TransactionReceipt transactionReceipt = eHealthContract.addDoctor(doctorPublicAddress).send();
        Log.d("CryptoHealthLog", "Doctor successfully saved : Transaction Hash: " + transactionReceipt.toString());
    }

    public static Credentials loadCredentialsFromDevice(Context context) {

        Credentials credentials = null;
        Creds creds = null;
        try {
            creds = (Creds) InternalStorage.readObject(context, "Credentials");
            credentials = creds.getCredentials();
            Log.d("CryptoHealthLog", "Credentials loaded from internal storage " + creds.getFilePath());
            return credentials;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return credentials;
        }

    }

}
