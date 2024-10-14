package org.sharnalk;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;

public class WalletFactory {

    private static WalletFactory instance;

    public HashMap<PublicKey, Wallet> wallets;

    private WalletFactory() {
        this.wallets = new HashMap<>();
    }

    public static WalletFactory getInstance(){
        if(instance == null) instance = new WalletFactory();
        return instance;
    }

    public void addWallet(Wallet wallet) {
        wallets.put(wallet.getPublicKey(), wallet);
    }

    public Wallet getWallet(PublicKey publicKey) {
        return wallets.get(publicKey);
    }

    public int getWalletSize(){
        return wallets.size();
    }

    public void addUTXOToWallet(UTXO utxo) {
        var publicKey = utxo.getPublicKey();
        var wallet = getWallet(publicKey);
        if (wallet == null) System.out.println("Wallet : \n"+ publicKey.getEncoded() +" \n---not found");
        wallet.addUTXO(utxo);
        System.out.println(utxo.getAmount() + " BTC added to wallet : " + publicKey.getEncoded());
    }
    public PublicKey getPbkFromConsole(String pbkStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] pbkBytes = Base64.getDecoder().decode(pbkStr);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pbkBytes);
        return keyFactory.generatePublic(keySpec);
    }
    public void getAllWalletsConsole(){
        wallets.values().forEach(e -> System.out.println("Wallet : " + Base64.getEncoder().encodeToString(e.getPublicKey().getEncoded()) + "\n" + e.getAllUnspentOutputsConsole() + "\n"));
    }
}
