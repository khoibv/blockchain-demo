package vn.com.khoibv.blockchain.ledger;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;
import vn.com.khoibv.blockchain.common.StringUtil;
import vn.com.khoibv.blockchain.wallet.Transaction;

@Data
public class Block {

  public String hash;
  public String previousHash;
  //  private String data; //our data will be a simple message.
  public String merkleRoot;
  private long timeStamp; //as number of milliseconds since 1/1/1970.

  private int nonce;

  public List<Transaction> transactions = new ArrayList<>(); //our data will be a simple message.

  //Block Constructor.
  public Block(String previousHash) {
    this.previousHash = previousHash;
    this.timeStamp = new Date().getTime();

    this.hash = calculateHash(); //Making sure we do this after we set the other values.
  }


  public String calculateHash() {
    String calculatedhash = StringUtil.applySha256(
        previousHash +
            Long.toString(timeStamp) +
            Long.toString(nonce) +
            merkleRoot
    );

    return calculatedhash;
  }

  public void mineBlock(int difficulty) {
    merkleRoot = StringUtil.getMerkleRoot(transactions);
    String target = new String(new char[difficulty])
        .replace('\0', '0'); //Create a string with difficulty * "0"

    Instant start = Instant.now();
    while (!hash.substring(0, difficulty).equals(target)) {
      nonce++;
      hash = calculateHash();
    }
    Instant end = Instant.now();

    System.out.println("Block Mined!!! : " + hash + ", after " + Duration.between(start, end));
  }

  //Add transactions to this block
  public boolean addTransaction(Transaction transaction) {
    //process transaction and check if valid, unless block is genesis block then ignore.
    if (transaction == null) {
      return false;
    }
    if ((previousHash != "0")) {
      if (!transaction.processTransaction()) {
        System.out.println("Transaction failed to process. Discarded.");
        return false;
      }
    }
    transactions.add(transaction);
    System.out.println("Transaction Successfully added to Block");
    return true;
  }
}
