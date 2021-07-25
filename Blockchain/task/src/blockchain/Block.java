package blockchain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Block {
    private long id;
    private long timeStamp;
    private String hash;
    private String previousHash;
    private int magicNumber;
    private long generationTime;
    private int n;
    private String miner;
    private List<Transaction> transactions;

    public Block(long id, String previousHash, int n, List<Transaction> transactions) {
        this.id = id;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.magicNumber = new Random().nextInt(Integer.MAX_VALUE);
        this.n = n;
        this.transactions = transactions;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public long getMagicNumber() {
        return magicNumber;
    }

    public void setMagicNumber(int magicNumber) {
        this.magicNumber = magicNumber;
    }

    public String getMiner() {
        return miner;
    }

    public void setMiner(String miner) {
        this.miner = miner;
    }

    public long getGenerationTime() {
        return generationTime;
    }

    public void setGenerationTime(long generationTime) {
        this.generationTime = generationTime;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    private String getDataForHashGeneration() {
        return id + Long.toString(timeStamp) + previousHash + magicNumber;
    }

    public String calculateHash(BlockChain blockChain) {
        Long start = new Date().getTime();
        String prefixString = new String(new byte[n]).replace("\0", "0");
        String hash = generateHash();
        while (!hash.substring(0, n).equals(prefixString)) {
            if(n != blockChain.getN()){
                break;
            }
            magicNumber = new Random().nextInt(Integer.MAX_VALUE);
            hash = generateHash();
        }
        Long end = new Date().getTime();
        this.generationTime = (end - start) / 1000;
        return hash;
    }

    public String generateHash() {
        String data = getDataForHashGeneration();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte element : hash) {
                hexString.append(String.format("%02x", element));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public String toString() {
        return "Block:\n" +
                "Created by " + miner + "\n" +
                miner + "gets 100 VC\n" +
                "Id: " + id + "\n" +
                "Timestamp: " + timeStamp + "\n" +
                "Magic number: " + magicNumber + "\n" +
                "Hash of the previous block:\n" +
                previousHash + "\n" +
                "Hash of the block:\n" +
                hash + "\n" +
                "Block data:" +
                (transactions.isEmpty() ? " no transactions" : "\n" + transactions.stream()
                        .map(transaction -> {
                            try {
                                return Encryptor.decryptMessage(transaction.getData(), transaction.getPublicKey());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .collect(Collectors.joining("\n")))+ "\n" +
                "Block was generating for " + generationTime + " seconds";

    }
}
