package blockchain;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class Block{
    private final String ownerId;
    private String[] blockData;
    private volatile int id;
    private long timestamp;
    private String previousHash;
    private int magicConstant;
    private String hash;

    public Block(String owner){
        ownerId = owner;
        Random random = new Random();
        do {
            if(Blockchain.getInstance().size() == 0){
                id = 1;
                previousHash = "0";
            } else {
                id = Blockchain.getInstance().getLastElement().getId() + 1;
                previousHash = Blockchain.getInstance().getLastElement().getHash();
                if(!Blockchain.getInstance().getMessage().isEmpty()) {
                    blockData = Blockchain.getInstance().getMessage().toArray(new String[0]);
                    Blockchain.getInstance().getMessage().clear();
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
                "\nCreated by: " + ownerId +
                "\n" + ownerId + "gets 100 VC" +
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

    public String getOwnerId() {
        return ownerId;
    }

    String getHash() {
        return hash;
    }

    String getPreviousHash() {
        return previousHash;
    }

    String[] getBlockData() {
        return blockData;
    }

    public static String createMessage(String in, int money, String out){
        return String.format("%s sent %d VC to %s", in, money, out);
    }
}