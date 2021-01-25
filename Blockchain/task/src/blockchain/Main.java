package blockchain;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        final int poolSize = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        ExecutorService messenger = Executors.newSingleThreadExecutor();
        messenger.submit(() -> {
            String name = "Danon";
            StringBuilder builder = new StringBuilder();
            Random random = new Random();
            UnaryOperator<String> publicKey = Blockchain.getInstance().getOpenKey(name);
            while (true){
                while (random.nextInt() % 20 != 0) {
                    builder.append((char)('a' + random.nextInt(23)));
                }
                Block.addMessage(publicKey.apply(name+ ":" + builder.toString()));
                builder = new StringBuilder();
                Thread.sleep(500);
            }
        });
        for(int i = 0; i < poolSize; i++) {
            int finalI = i;
            executor.submit(() -> {
                try {
                    while (Blockchain.getInstance().size() < 5) {
                        Blockchain.getInstance().addBlock(new Block(finalI));
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            });
        }
        executor.awaitTermination(14, TimeUnit.SECONDS);
        Blockchain.getInstance().print(5);
        messenger.shutdownNow();
    }
}
