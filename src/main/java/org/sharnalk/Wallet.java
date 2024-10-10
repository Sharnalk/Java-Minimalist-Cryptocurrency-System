package org.sharnalk;

import java.nio.ByteBuffer;
import java.security.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a Wallet in a cryptocurrency system.
 * In a concrete cryptocurrency system, A wallet mean a User, it is defining by his publicKey and privateKey,
 * the publicKey is his footprint and privateKey permit it to send money in transaction
 * The privateKey always represent the sender in a transaction, it's the only way to validate it.
 * The publicKey permit to verify the signature made by the Transaction by everyone
 */
public class Wallet {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private List<UTXO> unspentOutputs = new ArrayList<>(); // List of unspent UTXO

    public Wallet(double initialAmount) throws Exception {
        GenerateKeys(); // Generates the RSA key pair (public/private)

        // Create an initial UTXO with the initial amount and a randomly generated txId
        var initialUTXO = new UTXO(initialAmount,this.publicKey);
        initialUTXO.setTxId(asBytes(UUID.randomUUID())); // Generate a random txId for initialization
        Miner.getInstance().setGlobalUnspentOutputs(initialUTXO);
        unspentOutputs.add(initialUTXO);
    }

    /**
     * Convert UUID into byte array
     * @param uuid The UUID to be converted.
     * @return The byte array representation of the UUID.
     */
    public static byte[] asBytes(UUID uuid) {
        var bb = ByteBuffer.allocate(16);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
    /**
     * Generates an RSA key pair (2048 bits) for the wallet.
     * This method sets the publicKey and privateKey fields.
     *
     * @throws NoSuchAlgorithmException
     */
    private void GenerateKeys() throws NoSuchAlgorithmException {
        var keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048, new SecureRandom());
        var keyPair = keyGen.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }

    class OutputData{
        List<UTXO> selectedOutputs = new ArrayList<>();
        double totalOutputs = 0;
        int indexOutputs = 0;
    }

    /**
     * Creates a new transaction from this wallet, sending a specified amount to the recipient's public key.
     * It selects UTXOs from the wallet to cover the amount and generates outputs for both the recipient and the change.
     * The transaction is signed using the wallet's private key.
     *
     * @param recipientPublicKey The public key of the recipient.
     * @param amount The amount to be sent.
     * @return The signed Transaction object.
     * @throws Exception If the wallet does not have enough unspent outputs to cover the amount.
     */
    public Transaction createTransactions(PublicKey recipientPublicKey, double amount) throws Exception {

        if (getAllUnspentOutputs() < amount) throw new Exception("Funds insufficient.");

        var outputData = new OutputData();

        // Select UTXOs from the wallet to cover the transaction amount
        selectOutputs(outputData, amount);

        // Create a new Transaction with the sender's and recipient's public keys
        var transaction = new Transaction(this.publicKey, recipientPublicKey);

        // If the total selected UTXOs exceed the amount, create an output for the change
        if (outputData.totalOutputs > amount){
            var exchangeAmount = outputData.totalOutputs - amount;
            transaction.addToOutputs(new UTXO(exchangeAmount,this.publicKey));
        }

        // Add the recipient's output to the transaction
        transaction.addToOutputs(new UTXO(amount, recipientPublicKey));

        // Add selected UTXOs as inputs for the transaction
        for (var utxo : outputData.selectedOutputs) {
            transaction.addToInputs(utxo);
        }

        // Generate the byte representation of the transaction data
        byte[] txDataBytes = transaction.getTransactionDataBytes();

        // Mark the transaction's outputs with the transaction ID (derived from the hash of txDataBytes)
        transaction.markOutputsWithTxId(txDataBytes);

        txDataBytes = transaction.getTransactionDataBytes();

        // Sign the transaction with the sender's private key
        transaction.setSignature(signData(txDataBytes));

        return transaction;
    }

    /**
     * Get the sum of all unspent amount
     */
    private double getAllUnspentOutputs(){
        double sumUnspentOutputs = 0;
        for (var utxo : unspentOutputs){
            sumUnspentOutputs += utxo.getAmount();
        }
        return sumUnspentOutputs;
    }

    /**
     * Selects the sender's outputs to match the amount or more
     */
    private void selectOutputs (OutputData outputData, double amount){
        for (var utxo : new ArrayList<>(unspentOutputs)){
            //Here we count the amount of UTXOs we have to take from the wallet
            outputData.totalOutputs += utxo.getAmount();
            outputData.selectedOutputs.add(utxo);
            utxo.setOutputIndex(outputData.indexOutputs++);
            if (outputData.totalOutputs >= amount) break;
        }
        // Remove selected UTXOs from the list of unspent outputs
        unspentOutputs.removeAll(outputData.selectedOutputs);
    }

    /**
     * Signs the transaction using the sender's private key (RSA with SHA-256).
     * The transaction data is hashed and then signed.
     *
     * @param data The byte array representing the transaction data.
     * @return The signature of the transaction.
     * @throws SignatureException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private byte[] signData(byte[] data) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        var signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey); // Initialize signature with private key
        signature.update(data);         // We add the data to sign
        return signature.sign();        // Generate the digital signature
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    protected void addUTXO(UTXO utxo){
        unspentOutputs.add(utxo);
    }
}