package vn.com.khoibv.blockchain.wallet;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import vn.com.khoibv.blockchain.common.StringUtil;
import vn.com.khoibv.blockchain.ledger.Block;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalletApp {

//    public static List<Block> blockchain = new ArrayList<>();
    public static Map<String, TransactionOutput> UTXOs = new HashMap<>(); //list of all unspent transactions.
    public static int difficulty = 5;
    public static Wallet walletA;
    public static Wallet walletB;
    public static double minimumTransaction = 0.000_1d;

    public static void main(String[] args) {

        //Setup Bouncey castle as a Security Provider
        Security.addProvider(new BouncyCastleProvider());

        //Create the new wallets
        walletA = new Wallet();
        walletB = new Wallet();

        //Test public and private keys
        System.out.println("Private key: " + StringUtil.getStringFromKey(walletA.privateKey));
        System.out.println("Public key: " + StringUtil.getStringFromKey(walletA.publicKey));

        //Create a test transaction from WalletA to walletB
        Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
        transaction.generateSignature(walletA.privateKey);

        //Verify the signature works and verify it from the public key
        System.out.println("Is signature verify: " + transaction.verifySignature());
    }

}
