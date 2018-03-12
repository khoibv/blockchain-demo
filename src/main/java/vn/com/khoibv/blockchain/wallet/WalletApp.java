package vn.com.khoibv.blockchain.wallet;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import vn.com.khoibv.blockchain.common.StringUtil;
import vn.com.khoibv.blockchain.ledger.Block;
import vn.com.khoibv.blockchain.ledger.BlockChain;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalletApp {

  public static Map<String, TransactionOutput> UTXOs = new HashMap<>(); //list of all unspent transactions.
  public static int difficulty = 5;
  public static Wallet walletA;
  public static Wallet walletB;
  public static double minimumTransaction = 0.000_1d;

  private static BlockChain blockchain = new BlockChain();

  public static void main(String[] args) {

    //Setup Bouncey castle as a Security Provider
    Security.addProvider(new BouncyCastleProvider());

//    test01();
    test02();

  }

  private static void test02() {
    //Create wallets:
    walletA = new Wallet();
    walletB = new Wallet();
    Wallet coinbase = new Wallet();

    //create genesis transaction, which sends 100 DemoCoin to walletA:
    Transaction genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100d, null);
    genesisTransaction.generateSignature(coinbase.privateKey);   //manually sign the genesis transaction
    genesisTransaction.transactionId = "0"; //manually set the transaction id
    //manually add the Transactions Output
    genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.transactionId));
    UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.

    System.out.println("Creating and Mining Genesis block... ");
    Block genesis = new Block("0");
    genesis.addTransaction(genesisTransaction);
    addBlock(genesis);
    printStatus(walletA, walletB);
    System.out.println("===================\n");

    //testing
    Block block1 = new Block(genesis.hash);
    System.out.println("WalletA is attempting to send funds (40) to WalletB...");
    block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
    addBlock(block1);
    printStatus(walletA, walletB);
    System.out.println("===================\n");

    Block block2 = new Block(block1.hash);
    System.out.println("WalletA is attempting to send more funds (1000) than it has...");
    block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
    addBlock(block2);
    printStatus(walletA, walletB);
    System.out.println("===================\n");

    Block block3 = new Block(block2.hash);
    System.out.println("WalletB is attempting to send funds (20) to WalletA...");
    block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20));
    printStatus(walletA, walletB);
    System.out.println("===================\n");

    isChainValid(genesisTransaction);
  }

  private static void printStatus(Wallet walletA, Wallet walletB) {
    System.out.println(String.format("WalletA: %1$,.2f$, WalletB: %2$,.2f$", walletA.getBalance(), walletB.getBalance()));
  }


  public static Boolean isChainValid(Transaction genesisTransaction) {
    String hashTarget = new String(new char[difficulty]).replace('\0', '0');
    Map<String, TransactionOutput> tempUTXOs = new HashMap<>(); //a temporary working list of unspent transactions at a given block state.
    tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

    //loop through blockchain to check hashes:
    if (!blockchain.checkHashes(tempUTXOs, difficulty, hashTarget)) {
      return false;
    }

    System.out.println("Blockchain is valid");
    return true;
  }

  public static void addBlock(Block newBlock) {
    newBlock.mineBlock(difficulty);
    blockchain.add(newBlock);
  }

  private static void test01() {
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
