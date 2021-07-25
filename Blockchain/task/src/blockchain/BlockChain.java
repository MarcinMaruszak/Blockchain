package blockchain;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockChain {
    private long blockID;
    private List<Block> minedBlocks;
    private int n;
    private static BlockChain INSTANCE;
    private List<Transaction> nextBlockTransactions;
    private List<Transaction> currentMessages;
    private long transactionId;
    private Map<String, Double> balances;

    private BlockChain() {
        minedBlocks = new ArrayList<>();
        nextBlockTransactions = new ArrayList<>();
        currentMessages = new ArrayList<>();
        transactionId = 1;
        blockID = 1;
        n = 0;
        balances = new HashMap<>();
    }

    public static BlockChain getInstance() {
        if (INSTANCE == null) {
            synchronized (BlockChain.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BlockChain();
                }
            }
        }
        return INSTANCE;
    }

    public List<Block> getMinedBlocks() {
        return minedBlocks;
    }

    public void setMinedBlocks(List<Block> minedBlocks) {
        this.minedBlocks = minedBlocks;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public long getBlockID() {
        return blockID;
    }

    public void setBlockID(long blockID) {
        this.blockID = blockID;
    }

    public List<Transaction> getNextBlockMessages() {
        return nextBlockTransactions;
    }

    public void setNextBlockMessages(List<Transaction> nextBlockTransactions) {
        this.nextBlockTransactions = nextBlockTransactions;
    }

    public List<Transaction> getCurrentMessages() {
        return currentMessages;
    }

    public void setCurrentMessages(List<Transaction> currentTransactions) {
        this.currentMessages = currentTransactions;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public synchronized boolean validBlockChain() {
        boolean valid = true;
        for (int i = 0; i < minedBlocks.size(); i++) {
            String previousHash = i == 0 ? "0" : minedBlocks.get(i - 1).getHash();
            Block block = minedBlocks.get(i);
            try {
                valid = previousHash.equals(block.getPreviousHash()) &&
                        block.getHash().equals(block.generateHash())
                        && validTransaction(block.getTransactions(), i == 0 ?
                        new ArrayList<>() : minedBlocks.get(i - 1).getTransactions());
            } catch (Exception e) {
                valid = false;
            }
            if (!valid) {
                break;
            }
        }
        return valid;
    }

    public boolean validTransaction(List<Transaction> currentTransactions,
                                    List<Transaction> prevTransactions) throws Exception {
        long currentMax = currentTransactions
                .stream()
                .map(Transaction::getId)
                .max(Long::compareTo)
                .orElse(transactionId);
        long prevMax = prevTransactions
                .stream()
                .map(Transaction::getId)
                .max(Long::compareTo).orElse(-1L);
        boolean valid = transactionId >= currentMax && currentMax > prevMax;

        if (valid) {
            for (Transaction transaction : currentMessages) {
                if (!Encryptor.verifySignature(transaction.getData(), transaction.getSignature(), transaction.getPublicKey())) {
                    valid = false;
                    break;
                }
            }
        }
        return valid;
    }


    public Block getBlockDataToMine() {
        String previousHash = "0";
        if (blockID > 1) {
            previousHash = minedBlocks.get((int) blockID - 2).getHash();
        }
        return new Block(blockID, previousHash, n, currentMessages);
    }

    public void doTransaction(String sender, String receiver, int amount) {
        balances.putIfAbsent(sender, 100d);
        balances.putIfAbsent(receiver, 100d);
        double senderBalance = balances.get(sender);
        if (amount > senderBalance) {
            System.out.println("Not enough balance");
        } else {
            double receiverBalance = balances.get(receiver);
            balances.put(sender, senderBalance - amount);
            balances.put(receiver, receiverBalance + amount);
            String data = sender + " sent " + amount + " VC to " + receiver;
            try {
                KeyPair keyPair = Encryptor.generateKeys();
                byte[] encryptedData = Encryptor.encryptMessage(data.getBytes(StandardCharsets.UTF_8), keyPair.getPrivate());
                byte[] sign = Encryptor.singMessage(encryptedData, keyPair.getPrivate());
                nextBlockTransactions.add(new Transaction(encryptedData, sign, keyPair.getPublic(), transactionId));
                transactionId++;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    public synchronized void submitBlock(Block block) {
        minedBlocks.add(block);
        if (validBlockChain()) {
            blockID++;
            currentMessages = nextBlockTransactions;
            nextBlockTransactions = new ArrayList<>();
            balances.putIfAbsent(block.getMiner(), 100d);
            double balance = balances.get(block.getMiner());
            balances.put(block.getMiner(), balance + 100d);
            long generationTime = block.getGenerationTime();
            System.out.println(block);
            if (generationTime > 60 && n > 0) {
                n--;
                System.out.println("N was decreased by 1");
            } else if (generationTime < 10) {
                n++;
                System.out.println("N was increased to " + n);
            } else {
                System.out.println("N stays the same");
            }
            System.out.println();
        } else {
            minedBlocks.remove(block);
        }
    }
}
