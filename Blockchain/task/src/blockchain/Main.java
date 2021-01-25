package blockchain;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        final int poolSize = 5;
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        ExecutorService messenger = Executors.newFixedThreadPool(poolSize);
        for (int i = 0; i < poolSize; i++) {
            String finalI = String.valueOf(i);
            messenger.submit(() -> {
                Random random = new Random();
                UnaryOperator<String> publicKey = Blockchain.getInstance().getOpenKey();
                while (true) {
                    Blockchain.getInstance().addMessage(publicKey.apply(
                            Block.createMessage(finalI, random.nextInt(500), Integer.toString(random.nextInt(poolSize))))
                    );
                    Thread.sleep(100);
                }
            });
        }
        for (int i = 0; i < poolSize; i++) {
            String finalI = String.valueOf(i);
            executor.submit(() -> {
                try {
                    while (Blockchain.getInstance().size() < 15) {
                        Blockchain.getInstance().addBlock(new Block(finalI));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        executor.awaitTermination(14, TimeUnit.SECONDS);
        messenger.shutdownNow();
    }
}
