package org.sharnalk;

import java.nio.ByteBuffer;
import java.security.PublicKey;


public class UTXO {
    private double amount;
    private PublicKey publicKey;
    private int outputIndex;
    private byte[] txId; //ID of link Transaction

    public UTXO(double amount, PublicKey publicKey) {
        this.amount = amount;
        this.publicKey = publicKey;
    }

    //Here I get the size in Bytes of all of my argument to allocate
    // the size of the ByteBuffer in getBytes()
    private int getBytesSize(){
     return Double.BYTES
             + Integer.BYTES
             + txId.length
             + publicKey.getEncoded().length;
    }

    public byte[] getBytes() {
        // Allocate a ByteBuffer to get the exact size of byte[]
        var buffer = ByteBuffer.allocate(getBytesSize());
        buffer.putDouble(amount);
        buffer.putInt(outputIndex);
        buffer.put(txId);
        buffer.put(publicKey.getEncoded());

        return buffer.array();
    }

    //Getters
    public double getAmount() {
        return amount;
    }

    public void setOutputIndex(int outputIndex){
        this.outputIndex = outputIndex;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public byte[] getTxId() {return txId;}

    public void setTxId(byte[] txId) {
        if (this.txId != null) throw new UnsupportedOperationException("tx Id can be set only once.");
        this.txId = txId;
    }
}