package com.ahmed.hSafe.SmartContract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tuples.generated.Tuple5;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.5.16.
 */
@SuppressWarnings("rawtypes")
public class EHealth extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b50600180546001600160a01b031916331790556111cc806100326000396000f3fe608060405234801561001057600080fd5b50600436106100a95760003560e01c806382d609561161007157806382d6095614610493578063870be7dd146105375780638da5cb5b146105b4578063a3661747146105d8578063b8e94977146105e0578063be93f4c2146105e0576100a9565b80630869cfbc146100ae578063340460c51461019a578063401ac2c81461024b5780635131c620146103315780637f15546e146103ea575b600080fd5b6100d4600480360360208110156100c457600080fd5b50356001600160a01b0316610684565b60405180866001600160a01b03166001600160a01b0316815260200180602001856001600160a01b03166001600160a01b0316815260200184815260200183600181111561011e57fe5b60ff168152602001828103825286818151815260200191508051906020019080838360005b8381101561015b578181015183820152602001610143565b50505050905090810190601f1680156101885780820380516001836020036101000a031916815260200191505b50965050505050505060405180910390f35b610249600480360360808110156101b057600080fd5b810190602081018135600160201b8111156101ca57600080fd5b8201836020820111156101dc57600080fd5b803590602001918460018302840111600160201b831117156101fd57600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295505082359350505060208101359060400135610759565b005b6102536107ec565b604051808060200180602001838103835285818151815260200191508051906020019080838360005b8381101561029457818101518382015260200161027c565b50505050905090810190601f1680156102c15780820380516001836020036101000a031916815260200191505b50838103825284518152845160209182019186019080838360005b838110156102f45781810151838201526020016102dc565b50505050905090810190601f1680156103215780820380516001836020036101000a031916815260200191505b5094505050505060405180910390f35b61035d6004803603604081101561034757600080fd5b506001600160a01b038135169060200135610823565b6040518080602001858152602001848152602001838152602001828103825286818151815260200191508051906020019080838360005b838110156103ac578181015183820152602001610394565b50505050905090810190601f1680156103d95780820380516001836020036101000a031916815260200191505b509550505050505060405180910390f35b6102496004803603606081101561040057600080fd5b810190602081018135600160201b81111561041a57600080fd5b82018360208201111561042c57600080fd5b803590602001918460018302840111600160201b8311171561044d57600080fd5b91908080601f01602080910402602001604051908101604052809392919081815260200183838082843760009201919091525092955050823593505050602001356108f1565b610249600480360360208110156104a957600080fd5b810190602081018135600160201b8111156104c357600080fd5b8201836020820111156104d557600080fd5b803590602001918460018302840111600160201b831117156104f657600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550610a1c945050505050565b61053f610a7c565b6040805160208082528351818301528351919283929083019185019080838360005b83811015610579578181015183820152602001610561565b50505050905090810190601f1680156105a65780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b6105bc610b24565b604080516001600160a01b039092168252519081900360200190f35b61053f610b33565b610249600480360360208110156105f657600080fd5b810190602081018135600160201b81111561061057600080fd5b82018360208201111561062257600080fd5b803590602001918460018302840111600160201b8311171561064357600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550610a65945050505050565b6003602090815260009182526040918290208054600180830180548651600261010094831615949094026000190190911692909204601f81018690048602830186019096528582526001600160a01b0390921694929390929083018282801561072e5780601f106107035761010080835404028352916020019161072e565b820191906000526020600020905b81548152906001019060200180831161071157829003601f168201915b505050506003830154600484015460059094015492936001600160a01b039091169290915060ff1685565b610761610fd5565b50604080516080810182528581526020808201869052818301859052606082018490523360009081526002825292832080546001810180835591855293829020835180519495929486946004909402909201926107c392849290910190610ffd565b506020820151816001015560408201518160020155606082015181600301555050505050505050565b6040805180820182526003808252620c5cdd60ea1b6020808401919091528351808501909452908352620c9b9960ea1b9083015291565b6002602052816000526040600020818154811061083c57fe5b60009182526020918290206004919091020180546040805160026001841615610100026000190190931692909204601f8101859004850283018501909152808252919450925083918301828280156108d55780601f106108aa576101008083540402835291602001916108d5565b820191906000526020600020905b8154815290600101906020018083116108b857829003601f168201915b5050505050908060010154908060020154908060030154905084565b600060606108fd61107b565b6040518060c00160405280336001600160a01b03168152602001878152602001838152602001846001600160a01b031681526020018681526020018560001461094757600161094a565b60005b600181111561095557fe5b9052336000908152600360209081526040909120825181546001600160a01b0319166001600160a01b03909116178155828201518051939450849391926109a492600185019290910190610ffd565b50604082015180516109c09160028401916020909101906110cd565b5060608201516003820180546001600160a01b0319166001600160a01b039092169190911790556080820151600482015560a082015160058201805460ff191660018381811115610a0d57fe5b02179055505050505050505050565b6001546001600160a01b03163314610a655760405162461bcd60e51b815260040180806020018281038252602b81526020018061116d602b913960400191505060405180910390fd5b8051610a78906000906020840190610ffd565b5050565b33600090815260036020908152604091829020600190810180548451600293821615610100026000190190911692909204601f81018490048402830184019094528382526060939192909190830182828015610b195780601f10610aee57610100808354040283529160200191610b19565b820191906000526020600020905b815481529060010190602001808311610afc57829003601f168201915b505050505090505b90565b6001546001600160a01b031681565b604080516020808201835260008083523381526002909152918220546060925b81811015610d6b573360009081526002602052604090208054610c9b91859184908110610b7c57fe5b6000918252602091829020600490910201805460408051601f6002600019610100600187161502019094169390930492830185900485028101850190915281815292830182828015610c0f5780601f10610be457610100808354040283529160200191610c0f565b820191906000526020600020905b815481529060010190602001808311610bf257829003601f168201915b5050505050604051806040016040528060038152602001621f099f60ea1b815250610c7a60026000336001600160a01b03166001600160a01b031681526020019081526020016000208681548110610c6357fe5b906000526020600020906004020160010154610d73565b604051806040016040528060038152602001621f099f60ea1b815250610e39565b3360009081526002602052604090208054919450610d61918591610cda9185908110610cc357fe5b906000526020600020906004020160020154610d73565b604051806040016040528060038152602001621f099f60ea1b815250610d4060026000336001600160a01b03166001600160a01b031681526020019081526020016000208681548110610d2957fe5b906000526020600020906004020160030154610d73565b604051806040016040528060038152602001621f08df60ea1b815250610e39565b9250600101610b53565b509091505090565b60608180610d9a5750506040805180820190915260018152600360fc1b6020820152610e34565b8060005b8115610db257600101600a82049150610d9e565b6060816040519080825280601f01601f191660200182016040528015610ddf576020820181803883390190505b50905060001982015b8415610e2d57600a850660300160f81b82828060019003935081518110610e0b57fe5b60200101906001600160f81b031916908160001a905350600a85049450610de8565b5093505050505b919050565b606085858585856040516020018086805190602001908083835b60208310610e725780518252601f199092019160209182019101610e53565b51815160209384036101000a600019018019909216911617905288519190930192880191508083835b60208310610eba5780518252601f199092019160209182019101610e9b565b51815160209384036101000a600019018019909216911617905287519190930192870191508083835b60208310610f025780518252601f199092019160209182019101610ee3565b51815160209384036101000a600019018019909216911617905286519190930192860191508083835b60208310610f4a5780518252601f199092019160209182019101610f2b565b51815160209384036101000a600019018019909216911617905285519190930192850191508083835b60208310610f925780518252601f199092019160209182019101610f73565b6001836020036101000a03801982511681845116808217855250505050505090500195505050505050604051602081830303815290604052905095945050505050565b6040518060800160405280606081526020016000815260200160008152602001600081525090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061103e57805160ff191683800117855561106b565b8280016001018555821561106b579182015b8281111561106b578251825591602001919060010190611050565b5061107792915061112e565b5090565b6040518060c0016040528060006001600160a01b03168152602001606081526020016060815260200160006001600160a01b0316815260200160008152602001600060018111156110c857fe5b905290565b828054828255906000526020600020908101928215611122579160200282015b8281111561112257825182546001600160a01b0319166001600160a01b039091161782556020909201916001909101906110ed565b50611077929150611148565b610b2191905b808211156110775760008155600101611134565b610b2191905b808211156110775780546001600160a01b031916815560010161114e56fe5374616666206d656d626572732063616e206f6e6c792063726561746564206279207468652061646d696ea265627a7a723158207cfb1407d4816c8fea57c2b8b631d322f4a322ff743b2925b9f9abb5f739d28a64736f6c63430005100032";

    public static final String FUNC_ADDHEALTHINFOS = "addHealthInfos";

    public static final String FUNC_CHECKHEALTHINFOS = "checkHealthInfos";

    public static final String FUNC_CREATEPATIENT = "createPatient";

    public static final String FUNC_CREATESTAFF = "createStaff";

    public static final String FUNC_GETFAZA = "getFaza";

    public static final String FUNC_GETPATIENTNAME = "getPatientName";

    public static final String FUNC_HEALTHINFOMAP = "healthInfoMap";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_PATIENTS = "patients";

    public static final String FUNC_UPDATEPATIENT = "updatePatient";

    public static final String FUNC_UPDATESTAFF = "updateStaff";

    @Deprecated
    protected EHealth(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected EHealth(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected EHealth(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected EHealth(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> addHealthInfos(String _date, BigInteger _heartBeat, BigInteger _o2Level, BigInteger _calories) {
        final Function function = new Function(
                FUNC_ADDHEALTHINFOS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_date), 
                new org.web3j.abi.datatypes.generated.Uint256(_heartBeat), 
                new org.web3j.abi.datatypes.generated.Uint256(_o2Level), 
                new org.web3j.abi.datatypes.generated.Uint256(_calories)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> checkHealthInfos() {
        final Function function = new Function(FUNC_CHECKHEALTHINFOS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> createPatient(String _name, BigInteger _age, BigInteger _gender) {
        final Function function = new Function(
                FUNC_CREATEPATIENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_name), 
                new org.web3j.abi.datatypes.generated.Uint256(_age), 
                new org.web3j.abi.datatypes.generated.Uint256(_gender)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> createStaff(String _greeting) {
        final Function function = new Function(
                FUNC_CREATESTAFF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_greeting)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple2<String, String>> getFaza() {
        final Function function = new Function(FUNC_GETFAZA, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        return new RemoteFunctionCall<Tuple2<String, String>>(function,
                new Callable<Tuple2<String, String>>() {
                    @Override
                    public Tuple2<String, String> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<String, String>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<String> getPatientName() {
        final Function function = new Function(FUNC_GETPATIENTNAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Tuple4<String, BigInteger, BigInteger, BigInteger>> healthInfoMap(String param0, BigInteger param1) {
        final Function function = new Function(FUNC_HEALTHINFOMAP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0), 
                new org.web3j.abi.datatypes.generated.Uint256(param1)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple4<String, BigInteger, BigInteger, BigInteger>>(function,
                new Callable<Tuple4<String, BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple4<String, BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<String, BigInteger, BigInteger, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue());
                    }
                });
    }

    public RemoteFunctionCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Tuple5<String, String, String, BigInteger, BigInteger>> patients(String param0) {
        final Function function = new Function(FUNC_PATIENTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint8>() {}));
        return new RemoteFunctionCall<Tuple5<String, String, String, BigInteger, BigInteger>>(function,
                new Callable<Tuple5<String, String, String, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple5<String, String, String, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple5<String, String, String, BigInteger, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> updatePatient(String _greeting) {
        final Function function = new Function(
                FUNC_UPDATEPATIENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_greeting)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> updateStaff(String _greeting) {
        final Function function = new Function(
                FUNC_UPDATESTAFF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_greeting)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static EHealth load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new EHealth(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static EHealth load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new EHealth(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static EHealth load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new EHealth(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static EHealth load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new EHealth(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<EHealth> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(EHealth.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<EHealth> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(EHealth.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<EHealth> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(EHealth.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<EHealth> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(EHealth.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
