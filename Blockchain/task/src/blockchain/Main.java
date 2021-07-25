package blockchain;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {

        BlockChain blockChain = BlockChain.getInstance();

        int numberOfThreads = 8;

        blockChain.doTransaction("user1" , "user2", 10);

        while (blockChain.getBlockID() < 16) {
            CountDownLatch countDownLatch = new CountDownLatch(1);

            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

            for (int i = 0; i < numberOfThreads; i++){
                executorService.submit(new Miner(countDownLatch));
            }
            try {
                countDownLatch.await();
                executorService.shutdownNow();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
