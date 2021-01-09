package blockchain;

import java.util.*;

public class Blockchain {
    private final LinkedList<Block> blocks = new LinkedList<>();
    private final String prefix;
    private final Random random;

    public Blockchain(int countNull){
        prefix = "0".repeat(countNull);
        random = new Random();
    }

    public class Block{
        private final int id;
        private final long timestamp;
        private final String previousHash;
        private int magicConstant;
        private String hash;

        private Block(String prevHash, int prevId){
            id = prevId + 1;
            previousHash = prevHash;
            timestamp = new Date().getTime();
            do {
                magicConstant = random.nextInt();
                createHash();
            } while (hash.indexOf(prefix) != 0);
        }

        private void createHash(){
            hash = StringUtil.applySha256(getString());
        }

        @Override
        public String toString() {
            return "Block:" +
                    "\nId: " + id +
                    "\nTimestamp: " + timestamp +
                    "\nMagic number: " + magicConstant +
                    "\nHash of the previous block: \n" + previousHash +
                    "\nHash of the block:\n" + hash;
        }

        private String getString(){
            return id + previousHash + magicConstant + timestamp;
        }
    }

    public long addBlock(){
        long start = System.currentTimeMillis();
        if(blocks.isEmpty()){
            blocks.add(new Block("0", 0));
        } else {
            blocks.add(new Block(blocks.getLast().hash, blocks.getLast().id));
        }
        return System.currentTimeMillis() - start;
    }

    public List<Block> getBlocks(){
        return blocks;
    }

    public boolean isRight(){
        if(blocks.size() < 2){
            return true;
        } else if(blocks.size() == 2){
            return blocks.getFirst().hash.equals(blocks.get(1).previousHash);
        } else {
            Iterator<Block> it = blocks.descendingIterator();
            Block now, prev;
            now = it.next();
            prev = it.next();
            while (it.hasNext()) {
                if (!now.previousHash.equals(prev.hash)){
                    return false;
                }
                now = prev;
                prev = it.next();
            }
            return true;
        }
    }
}
