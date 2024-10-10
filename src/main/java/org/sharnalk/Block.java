package org.sharnalk;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 *  Block represent a part of the "Blocks Chain"
 *  it's a hashCode of an object composed by transaction hash, previousHash (by the previous block), timestamp and Proof of Work
 *  We use SHA-256 hashing algorithm (expliquer pour le message digest et ce que ca fait dans le code)
 * */
public class Block {
    private byte[] previousHashCode;
    private List<Transaction> transactions;
    private final long timestamp;

    // Validation Proof of Work
    private int nonce;

    private byte[] blockHashCode;

    public Block(byte[] previousHashCode) {
        this.previousHashCode = previousHashCode;
        this.transactions = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
        this.nonce = 0;
    }

    public Block() {
        this.previousHashCode = new byte[32];
        this.transactions = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
        this.nonce = 0;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    /**
     * When Miner valid all the transaction
     * @return
     * @throws NoSuchAlgorithmException
     */
    public byte[] CalculateBlockHash() throws NoSuchAlgorithmException {
        ByteBuffer buffer = ByteBuffer.allocate(4096*4);
        buffer.put(previousHashCode);
        buffer.putLong(timestamp);
        for (Transaction transaction : transactions) {
            buffer.put(transaction.getData());
        }
        //MessageDigest used for creating the footprint of the Block
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(buffer.array());
    }

    public byte[] getPreviousHashCode() {
        return previousHashCode;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
