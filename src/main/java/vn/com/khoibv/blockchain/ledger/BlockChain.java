package vn.com.khoibv.blockchain.ledger;

import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;

public class BlockChain {


  public static List<Block> blockchain = new ArrayList<>();

  public static void add(Block block) {
    blockchain.add(block);
  }

  public static boolean isChainValid() {
    Block currentBlock;
    Block previousBlock;

    //loop through blockchain to check hashes:
    for (int i = 1; i < blockchain.size(); i++) {
      currentBlock = blockchain.get(i);
      previousBlock = blockchain.get(i - 1);
      //compare registered hash and calculated hash:
      if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
        System.out.println("Current Hashes not equal");
        return false;
      }
      //compare previous hash and registered previous hash
      if (!previousBlock.hash.equals(currentBlock.previousHash)) {
        System.out.println("Previous Hashes not equal");
        return false;
      }
    }
    return true;
  }

  public static String toJson() {
    return new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
  }
}
