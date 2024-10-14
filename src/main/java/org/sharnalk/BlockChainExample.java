package org.sharnalk;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BlockChainExample {
    public static void main(String[] args) throws Exception {
        var walletFactory = WalletFactory.getInstance();
        var validator = Miner.getInstance();
        List<Block> blockList = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        while(true) {
            printMainMenu(walletFactory.getWalletSize());
            String choice = scanner.nextLine();
            handleMenuSelection(choice, walletFactory, validator, blockList, scanner);
        }
    }

    private static void printMainMenu(int walletSize) {
        if (walletSize <= 1) {
            System.out.println("**************************************\n"
                    + "Java Minimalist Cryptocurrency System\n"
                    + "**************************************\n\n"
                    + "[1] Create a wallet\n"
                    + "[2] See all wallet\n"
                    + "[3] See blockchain\n");
        } else {
            System.out.println("\n**************************************\n"
                    + "Java Minimalist Cryptocurrency System\n"
                    + "**************************************\n\n"
                    + "[1] Create a wallet\n"
                    + "[2] See all wallet\n"
                    + "[3] See blockchain\n"
                    + "[4] Create transaction\n"
                    + "[5] See funds from wallet\n");
        }
    }

    private static void handleMenuSelection(String choice, WalletFactory walletFactory, Miner validator,
                                            List<Block> blockList, Scanner scanner) throws Exception {
        switch (choice) {
            case "1":
                createWallet(walletFactory, scanner);
                break;
            case "2":
                walletFactory.getAllWalletsConsole();
                break;
            case "3":
                showBlockchain(blockList);
                break;
            case "4":
                createTransaction(walletFactory, validator, blockList, scanner);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void createWallet(WalletFactory walletFactory, Scanner scanner) throws Exception {
        System.out.println("Choose an amount: ");
        String amount = scanner.nextLine();
        var wallet = new Wallet(Integer.parseInt(amount));
        walletFactory.addWallet(wallet);
    }
    private static void createTransaction(WalletFactory walletFactory, Miner validator, List<Block> blockList,
                                          Scanner scanner) throws Exception {
        System.out.println("Choose a sender wallet: ");
        walletFactory.getAllWalletsConsole();
        String pbkSender = scanner.nextLine();
        System.out.println("Select a recipient wallet: ");
        String pbkRecipient = scanner.nextLine();
        System.out.println("Select an amount: ");
        String amountTx = scanner.nextLine();

        var tx = walletFactory.getWallet(walletFactory.getPbkFromConsole(pbkSender))
                .createTransactions(walletFactory.getPbkFromConsole(pbkRecipient), Double.parseDouble(amountTx));

        if (validator.verifyTransaction(tx)) {
            Block block = (blockList.isEmpty()) ? new Block() :
                    new Block(blockList.get(blockList.size() - 1).CalculateBlockHash());
            blockList.add(block);
            block.addTransaction(tx);
            tx.getOutputs().forEach(walletFactory::addUTXOToWallet);
        } else {
            System.out.println("Transaction not validated by validators.");
        }
    }

    private static void showBlockchain(List<Block> blockList) {
        if (blockList.isEmpty()) {
            System.out.println("Blockchain is empty.");
        } else {
            blockList.forEach(block -> {
                try {
                    System.out.println("Block: " + block.CalculateBlockHash());
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}