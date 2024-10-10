package org.sharnalk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * Transactions
 * this class represent the Transaction between two Wallet
 *  RSA encryption (asymmetric cryptography algorithm, here "SHA256withRSA" see in org.sharnalk/Block.java what does SHA-256 is)
 *  is an asymmetric cryptography algorithm used to sign the fingerprint with the sender's private key.
 *
 * For using it, we need three key to validate a transaction
 *  the senderPrivateKey who approuve the money exchange
 *  and the two public key from the sender and the recipient to illustre the exchange in the blockchain
 *
 */
public class Transaction {
    private PublicKey senderPublicKey; //public address of sender
    private PublicKey recipientPublicKey; //public address of recipient
    private List<UTXO> inputs = new ArrayList<>();
    private List<UTXO> outputs = new ArrayList<>();
    private long timestamp;
    private byte[] data;
    private byte[] signature;

    public Transaction(PublicKey senderPublicKey, PublicKey recipientPublicKey) {
        this.senderPublicKey = senderPublicKey;
        this.recipientPublicKey = recipientPublicKey;
        this.timestamp = System.currentTimeMillis();
    }

    public void addToOutputs(UTXO utxo) {
        outputs.add(utxo);
    }
    public void addToInputs(UTXO utxo) {
        this.inputs.add(utxo);
    }

    public byte[] getTransactionDataBytes() {
        byte[] senderPublicKeyBytes = senderPublicKey.getEncoded();
        byte[] recipientBytes = recipientPublicKey.getEncoded();

        var outputStream = new ByteArrayOutputStream ();

        try{
            outputStream.write(senderPublicKeyBytes);
            outputStream.write(recipientBytes);

            var buffer = ByteBuffer.allocate(Long.BYTES);
            buffer.putLong(timestamp);
            outputStream.write(buffer.array());

            for (var utxo : inputs) {
                    outputStream.write(utxo.getBytes());
            }
            if (outputs.stream().anyMatch(utxo -> utxo.getTxId() != null)){
                for (var utxo : outputs) {
                    outputStream.write(utxo.getBytes());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error in generation of data to sign.", e);
        }
        this.data = outputStream.toByteArray();
        // return of hash data
        return data;
    }

    protected void markOutputsWithTxId(byte[] txId){
        for (var utxo : outputs) {
            utxo.setTxId(txId);
        }
    }

    protected void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public List<UTXO> getInputs() {
        return inputs;
    }

    public List<UTXO> getOutputs() {
        return outputs;
    }

    public PublicKey getSenderPublicKey() {
        return senderPublicKey;
    }

    public byte[] getData() {return data;}

    public byte[] getSignature() {return signature;}
}