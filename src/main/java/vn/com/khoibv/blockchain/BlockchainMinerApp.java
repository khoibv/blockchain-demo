package vn.com.khoibv.blockchain;

public class BlockchainMinerApp {


  public static int difficulty = 5;


  public static void main(String[] args) {

//add our blocks to the blockchain ArrayList:
    Block block1 = new Block("Hi im the first block", "0");
    BlockChain.add(block1);
    System.out.println("Trying to Mine block 1... ");
    block1.mineBlock(difficulty);

    Block block2 = new Block("Yo im the second block", block1.hash);
    BlockChain.add(block2);
    System.out.println("Trying to Mine block 2... ");
    block2.mineBlock(difficulty);

    Block block3 = new Block("Hey im the third block", block2.hash);
    BlockChain.add(block3);
    System.out.println("Trying to Mine block 3... ");
    block3.mineBlock(difficulty);

    System.out.println("\nBlockchain is Valid: " + BlockChain.isChainValid());

    String blockchainJson = BlockChain.toJson();
    System.out.println("\nThe block chain: ");
    System.out.println(blockchainJson);

  }
}
