package blockchain;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int poolSize = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        for(int i = 0; i < poolSize; i++) {
            int finalI = i;
            executor.submit(() -> {
                try {
                    while (Blockchain.getInstance().size() < 5) {
                        long[] log = Blockchain.getInstance().addBlock(new Block(finalI));
                        if (log != null && Blockchain.getInstance().size() < 6) {
                            synchronized (System.out) {
                                System.out.println(Blockchain.getInstance().getLastElement().toString());
                                System.out.printf("Block was generating for %d seconds\n", log[0] / 1000);
                                System.out.println(log[1] == 0 ? "N stays the same" : (log[1] < 0 ? "N was decreased by 1" :
                                        "N was increased to " + log[1]));
                                System.out.println();
                            }
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            });
        }
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }
}
