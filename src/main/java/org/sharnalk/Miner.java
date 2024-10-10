package org.sharnalk;

import java.security.PublicKey;
import java.security.Signature;
import java.util.HashSet;
import java.util.List;

/**
 * Represents a Miner in a cryptocurrency system.
 * The Miner class is responsible for verifying the validity of transactions on the blockchain.
 * It checks if the UTXOs used in the transaction are valid, verifies the transaction's signature,
 * and maintains a record of all available UTXOs (unspent outputs) on the blockchain.
 */
public class Miner {
    private static Miner instance;

    public HashSet<UTXO> globalUnspentOutputs;

    private Miner(){
            this.globalUnspentOutputs = new HashSet<>();
    }

    public static Miner getInstance(){
        if(instance == null) instance = new Miner();
        return instance;
    }

    public boolean verifyTransaction(Transaction tx) throws Exception {
        if(verifyUTXOValidity(tx.getInputs())){
            System.out.println("UTXOs valid");
            if (verifySignature(tx.getSenderPublicKey(), tx.getData(), tx.getSignature())){
                System.out.println("Signature valid");
                addTransaction(tx.getInputs(), tx.getOutputs());
                return true;
            }
        }
        System.out.println("Transaction not valid.");
        return false;
    }

    private boolean verifyUTXOValidity(List<UTXO> utxoList){
        for(var utxo : utxoList){
            if (globalUnspentOutputs.contains(utxo)) return true;
        }
        return false;
    }

    private void addTransaction(List<UTXO> inputs, List<UTXO> outputs){
        //Input consommé
        for (var input : inputs){
            globalUnspentOutputs.remove(input);
        }
        //Output valide
        for(var output : outputs){
            globalUnspentOutputs.add(output);
        }
    }

    /**
     * Verify the signature with the senderPublicKey always with RSA encryption
     * Return true is the signature is valid
     * Will permit us to verify it when we transfer money between two Wallet
     * */
    private boolean verifySignature(PublicKey senderPublicKey, byte[] data, byte[] signatureToVerify) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(senderPublicKey);
        signature.update(data);
        return signature.verify(signatureToVerify);
    }

    public void setGlobalUnspentOutputs(UTXO utxo) throws Exception {
        if (utxo == null) throw new Exception();
        globalUnspentOutputs.add(utxo);
    }

}
