package vn.com.khoibv.blockchain.ledger;

import com.google.gson.GsonBuilder;
import vn.com.khoibv.blockchain.wallet.Transaction;
import vn.com.khoibv.blockchain.wallet.TransactionInput;
import vn.com.khoibv.blockchain.wallet.TransactionOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

  public static boolean checkHashes(Map<String, TransactionOutput> tempUTXOs, int difficulty,
      String hashTarget) {
    for (int i = 1; i < blockchain.size(); i++) {

      Block currentBlock = blockchain.get(i);
      Block previousBlock = blockchain.get(i - 1);
      //compare registered hash and calculated hash:
      if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
        System.out.println("#Current Hashes not equal");
        return false;
      }
      //compare previous hash and registered previous hash
      if (!previousBlock.hash.equals(currentBlock.previousHash)) {
        System.out.println("#Previous Hashes not equal");
        return false;
      }
      //check if hash is solved
      if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
        System.out.println("#This block hasn't been mined");
        return false;
      }

      //loop thru blockchains transactions:
      TransactionOutput tempOutput;
      for (int t = 0; t < currentBlock.transactions.size(); t++) {
        Transaction currentTransaction = currentBlock.transactions.get(t);

        if (!currentTransaction.verifySignature()) {
          System.out.println("#Signature on Transaction(" + t + ") is Invalid");
          return false;
        }
        if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
          System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
          return false;
        }

        for (TransactionInput input : currentTransaction.inputs) {
          tempOutput = tempUTXOs.get(input.transactionOutputId);

          if (tempOutput == null) {
            System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
            return false;
          }

          if (input.UTXO.value != tempOutput.value) {
            System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
            return false;
          }

          tempUTXOs.remove(input.transactionOutputId);
        }

        for (TransactionOutput output : currentTransaction.outputs) {
          tempUTXOs.put(output.id, output);
        }

        if (currentTransaction.outputs.get(0).recipient != currentTransaction.recipient) {
          System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
          return false;
        }
        if (currentTransaction.outputs.get(1).recipient != currentTransaction.sender) {
          System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
          return false;
        }

      }
    }

    return true;
  }
}
