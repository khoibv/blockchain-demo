package vn.com.khoibv.blockchain;

import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;

public class BlockChain {


  public static List<Block> blockchain = new ArrayList<>();

//
//  public static void main(String[] args) {
////    test01();
//    test02();
//  }
//
//  private static void test02() {
//    //add our blocks to the blockchain ArrayList:
//    blockchain.add(new Block("Hi im the first block", "0"));
//    blockchain.add(new Block("Yo im the second block", blockchain.get(blockchain.size() - 1).hash));
//    blockchain.add(new Block("Hey im the third block", blockchain.get(blockchain.size() - 1).hash));
//
//    String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
//    System.out.println(blockchainJson);
//  }
//
//  private static void test01() {
//    Block genesisBlock = new Block("Hi im the first block", "0");
//    System.out.println("Hash for block 1 : " + genesisBlock.hash);
//
//    Block secondBlock = new Block("Yo im the second block", genesisBlock.hash);
//    System.out.println("Hash for block 2 : " + secondBlock.hash);
//
//    Block thirdBlock = new Block("Hey im the third block", secondBlock.hash);
//    System.out.println("Hash for block 3 : " + thirdBlock.hash);
//  }

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
