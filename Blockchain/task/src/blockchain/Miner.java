package blockchain;

import java.util.concurrent.CountDownLatch;

public class Miner implements Runnable {
    private BlockChain blockChain = BlockChain.getInstance();
    private CountDownLatch latch;

    public Miner(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        Block block = blockChain.getBlockDataToMine();
        String hash = block.calculateHash(blockChain);
        if (block.getN() == blockChain.getN()) {
            block.setHash(hash);
            block.setMiner("miner" + Thread.currentThread().getId());
            blockChain.submitBlock(block);
            latch.countDown();

        }
    }
}
