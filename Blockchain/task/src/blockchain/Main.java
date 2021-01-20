package blockchain;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        final int poolSize = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        ExecutorService messenger = Executors.newSingleThreadExecutor();
        messenger.submit(() -> {
            int i;
            StringBuilder builder = new StringBuilder();
            Random random = new Random();
            while (true){
                while (random.nextInt() % 20 != 0) {
                    builder.append((char)('a' + random.nextInt(23)));
                }
                Block.addMessage(builder.toString());
                builder = new StringBuilder();
                Thread.sleep(500);
            }
        });
        for(int i = 0; i < poolSize; i++) {
            int finalI = i;
            executor.submit(() -> {
                try {
                    while (Blockchain.getInstance().size() < 5) {
                        Optional<long[]> log = Blockchain.getInstance().addBlock(new Block(finalI));
                        log.ifPresent(l -> {
                        if (Blockchain.getInstance().size() < 6) {
                            synchronized (System.out) {
                                System.out.println(Blockchain.getInstance().getLastElement().toString());
                                System.out.printf("Block was generating for %d seconds\n", l[0] / 1000);
                                System.out.println(l[1] == 0 ? "N stays the same" : (l[1] < 0 ? "N was decreased by 1" :
                                        "N was increased to " + l[1]));
                                System.out.println();
                            }
                        }});
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            });
        }
        executor.awaitTermination(10, TimeUnit.SECONDS);
        messenger.shutdownNow();
    }
}
