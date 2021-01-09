package blockchain;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.print("Enter how many zeros the hash must start with:");
        Blockchain blockchain = new Blockchain(new Scanner(System.in).nextInt());
        long[] time = new long[5];
        for(int i = 0; i < 5; i++){
            time[i] = blockchain.addBlock();
        }
        List<Blockchain.Block> list = blockchain.getBlocks();
        int i = 0;
        for(Blockchain.Block block: list){
            System.out.println(block.toString());
            System.out.println("Block was generating for " + time[i++] + " seconds");
            System.out.println();
        }
    }
}
