package org.sharnalk;


public class BlockChainExample {
    public static void main(String[] args) throws Exception {
        var walletFactory = new WalletFactory();
        var validator = Miner.getInstance();

        var user1 = new Wallet(100);
        var user2 = new Wallet(10);
        walletFactory.addWallet(user1);
        walletFactory.addWallet(user2);
        var block = new Block();

        Transaction tx = user1.createTransactions(user2.getPublicKey(), 10);
        if (validator.verifyTransaction(tx)){
            block.addTransaction(tx);
            for(UTXO utxo : tx.getOutputs()){
                walletFactory.addUTXOToWallet(utxo);
            }
        } else {
            System.out.println("Validators did not validate the transaction");
        }
    }
}