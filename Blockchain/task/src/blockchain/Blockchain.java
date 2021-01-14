package blockchain;

import java.util.*;

public class Blockchain {
    private final LinkedList<Block> blocks = new LinkedList<>();
    private String prefix = "";
    private static final Blockchain instance = new Blockchain();
    private long timeAddLast = System.currentTimeMillis();

    private Blockchain(){}

    public synchronized long[] addBlock(Block newBlock){
        if((blocks.isEmpty() || newBlock.getPreviousHash().equals(blocks.getLast().getHash()))
                && newBlock.getHash().indexOf(prefix) == 0){
            blocks.add(newBlock);
            long[] result = new long[2];
            if(System.currentTimeMillis() - timeAddLast < 5000){
                prefix = prefix.concat("0");
                result[1] = prefix.length();
            }
            if(System.currentTimeMillis() - timeAddLast > 60000 && !prefix.isEmpty()){
                prefix = prefix.substring(1);
                result[1] = -1;
            }
            result[0] = System.currentTimeMillis() - timeAddLast;
            timeAddLast = System.currentTimeMillis();
            return result;
        }
        return null;
    }

    public LinkedList<Block> getBlocks(){
        return blocks;
    }

    public String getPrefix() {
        return prefix;
    }

    public static Blockchain getInstance() {
        return instance;
    }

    public Block getLastElement(){
        return blocks.getLast();
    }

    public int size(){
        return blocks.size();
    }

    public boolean isRight(){
        if(blocks.size() < 2){
            return true;
        } else if(blocks.size() == 2){
            return blocks.getFirst().getHash().equals(blocks.get(1).getPreviousHash());
        } else {
            Iterator<Block> it = blocks.descendingIterator();
            Block now, prev;
            now = it.next();
            prev = it.next();
            while (it.hasNext()) {
                if (!now.getPreviousHash().equals(prev.getHash())){
                    return false;
                }
                now = prev;
                prev = it.next();
            }
            return true;
        }
    }
}
