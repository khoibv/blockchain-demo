package vn.com.khoibv.blockchain.wallet;

import vn.com.khoibv.blockchain.common.StringUtil;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Transaction {

  public String transactionId; // this is also the hash of the transaction.
  public PublicKey sender; // senders address/public key.
  public PublicKey recipient; // Recipients address/public key.
  public double value;
  public byte[] signature; // this is to prevent anybody else from spending funds in our wallet.

  public List<TransactionInput> inputs = new ArrayList<>();
  public List<TransactionOutput> outputs = new ArrayList<>();

  private static int sequence = 0; // a rough count of how many transactions have been generated.

  // Constructor:
  public Transaction(PublicKey from, PublicKey to, double value, List<TransactionInput> inputs) {
    this.sender = from;
    this.recipient = to;
    this.value = value;
    this.inputs = inputs;
  }

  // This Calculates the transaction hash (which will be used as its Id)
  private String calculateHash() {
    sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
    return StringUtil.applySha256(
        StringUtil.getStringFromKey(sender) +
            StringUtil.getStringFromKey(recipient) +
            Double.toString(value) + sequence
    );
  }

  //Signs all the data we dont wish to be tampered with.
  public void generateSignature(PrivateKey privateKey) {
    String data =
        StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Double
            .toString(value);
    signature = StringUtil.applyECDSASig(privateKey, data);
  }

  //Verifies the data we signed hasnt been tampered with
  public boolean verifySignature() {
    String data =
        StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Double
            .toString(value);
    return StringUtil.verifyECDSASig(sender, data, signature);
  }

  //Returns true if new transaction could be created.
  public boolean processTransaction() {

    if (verifySignature() == false) {
      System.out.println("#Transaction Signature failed to verify");
      return false;
    }

    //gather transaction inputs (Make sure they are unspent):
    for (TransactionInput i : inputs) {
      i.UTXO = WalletApp.UTXOs.get(i.transactionOutputId);
    }

    //check if transaction is valid:
    if (getInputsValue() < WalletApp.minimumTransaction) {
      System.out.println("#Transaction Inputs to small: " + getInputsValue());
      return false;
    }

    //generate transaction outputs:
    double leftOver = getInputsValue() - value; //get value of inputs then the left over change:
    transactionId = calculateHash();
    outputs
        .add(new TransactionOutput(this.recipient, value, transactionId)); //send value to recipient
    outputs.add(new TransactionOutput(this.sender, leftOver,
        transactionId)); //send the left over 'change' back to sender

    //add outputs to Unspent list
    for (TransactionOutput o : outputs) {
      WalletApp.UTXOs.put(o.id, o);
    }

    //remove transaction inputs from UTXO lists as spent:
    for (TransactionInput i : inputs) {
      if (i.UTXO == null) {
        continue; //if Transaction can't be found skip it
      }
      WalletApp.UTXOs.remove(i.UTXO.id);
    }

    return true;
  }

  //returns sum of inputs(UTXOs) values
  public double getInputsValue() {
    double total = 0;
    for (TransactionInput i : inputs) {
      if (i.UTXO == null) {
        continue; //if Transaction can't be found skip it
      }
      total += i.UTXO.value;
    }
    return total;
  }

  //returns sum of outputs:
  public double getOutputsValue() {
    double total = 0;
    for (TransactionOutput o : outputs) {
      total += o.value;
    }
    return total;
  }
}
