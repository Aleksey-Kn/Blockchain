package blockchain;

import java.util.Date;
import java.util.Random;

public class Block{
    private final long ownerId;
    private int id;
    private final long timestamp;
    private String previousHash;
    private int magicConstant;
    private String hash;

    public Block(long owner){
        ownerId = owner;
        timestamp = new Date().getTime();
        Random random = new Random();
        do {
            if(Blockchain.getInstance().size() == 0){
                id = 1;
                previousHash = "0";
            } else {
                id = Blockchain.getInstance().getLastElement().getId() + 1;
                previousHash = Blockchain.getInstance().getLastElement().getHash();
            }
            magicConstant = random.nextInt();
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
                "\nHash of the block:\n" + hash;
    }

    private String getString(){
        return ownerId + id + previousHash + magicConstant + timestamp;
    }

    public int getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }
}