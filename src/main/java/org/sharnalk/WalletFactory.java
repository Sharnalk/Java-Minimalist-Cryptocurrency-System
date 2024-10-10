package org.sharnalk;

import java.security.PublicKey;
import java.util.HashMap;

public class WalletFactory {
    private HashMap<PublicKey, Wallet> wallets;

    public WalletFactory() {
        this.wallets = new HashMap<>();
    }

    public void addWallet(Wallet wallet) {
        wallets.put(wallet.getPublicKey(), wallet);
    }

    public Wallet getWallet(PublicKey publicKey) {
        return wallets.get(publicKey);
    }

    public void addUTXOToWallet(UTXO utxo) {
        var publicKey = utxo.getPublicKey();
        var wallet = getWallet(publicKey);
        if (wallet == null) System.out.println("Wallet : \n"+ publicKey.getEncoded() +" \n---not found");
        wallet.addUTXO(utxo);
        System.out.println(utxo.getAmount() + " BTC added to wallet : " + publicKey.getEncoded());
    }
}
