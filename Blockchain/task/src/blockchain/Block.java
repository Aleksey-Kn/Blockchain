package blockchain;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

public class Block{
    private final static LinkedList<String> message = new LinkedList<>();
    private final long ownerId;
    private String[] blockData;
    private volatile int id;
    private long timestamp;
    private String previousHash;
    private int magicConstant;
    private String hash;

    public Block(long owner){
        ownerId = owner;
        Random random = new Random();
        do {
            if(Blockchain.getInstance().size() == 0){
                id = 1;
                previousHash = "0";
            } else {
                id = Blockchain.getInstance().getLastElement().getId() + 1;
                previousHash = Blockchain.getInstance().getLastElement().getHash();
                if(!message.isEmpty()) {
                    blockData = message.toArray(new String[0]);
                    message.clear();
                }
            }
            magicConstant = random.nextInt();
            timestamp = new Date().getTime();
            createHash();
        } while (hash.indexOf(Blockchain.getInstance().getPrefix()) != 0);
    }

    private void createHash(){
        hash = StringUtil.applySha256(getString());
    }

    @Override
    public String toString() {
        return "Block:" +
                "\nCreated by miner # " + ownerId +
                "\nId: " + id +
                "\nTimestamp: " + timestamp +
                "\nMagic number: " + magicConstant +
                "\nHash of the previous block: \n" + previousHash +
                "\nHash of the block:\n" + hash +
                "\nBlock data:";

    }

    private String getString(){
        return ownerId + Arrays.toString(blockData) + id + previousHash + magicConstant + timestamp;
    }

    int getId() {
        return id;
    }

    String getHash() {
        return hash;
    }

    String getPreviousHash() {
        return previousHash;
    }

    synchronized static void addMessage(String s){
        message.add(s);
    }

    String[] getBlockData() {
        return blockData;
    }
}