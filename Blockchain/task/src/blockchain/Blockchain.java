package blockchain;

import java.util.*;
import java.util.function.UnaryOperator;

public class Blockchain {
    private final LinkedList<Block> blocks = new LinkedList<>();
    private String prefix = "";
    private static final Blockchain instance = new Blockchain();
    private long timeAddLast = System.currentTimeMillis();
    private Set<String> users = new HashSet<>();
    private long oldNum = 0;

    private Blockchain(){}

    public synchronized Optional<long[]> addBlock(Block newBlock){
        if((blocks.isEmpty() ||
                (newBlock.getPreviousHash().equals(blocks.getLast().getHash()) && newBlock.getId() == blocks.getLast().getId() + 1))
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
            return Optional.of(result);
        }
        return Optional.empty();
    }

    public LinkedList<Block> getBlocks(){
        return blocks;
    }

    public String getPrefix() {
        return prefix;
    }

    public static synchronized Blockchain getInstance() {
        return instance;
    }

    public synchronized Block getLastElement(){
        return blocks.getLast();
    }

    public synchronized int size(){
        return blocks.size();
    }

    public Optional<UnaryOperator<String>> getOpenKey(String user) {
        if (users.contains(user)) {
            return Optional.empty();
        } else {
            return Optional.of(data -> {
                StringBuilder binary = new StringBuilder(data.concat(Long.toString(nextNumber()) + "#").chars()
                        .mapToObj(Integer::toBinaryString)
                        .map(i -> "0".repeat(8 - i.length()) + i)
                        .reduce("", (sum, now) -> sum + now));
                String[] chars = new String[(int) Math.ceil((float) binary.length() / 8)];
                for (int i = 0, end; i < chars.length; i++) {
                    end = Math.min(binary.length(), 6);
                    chars[i] = binary.substring(0, end);
                    binary.delete(0, end);
                }
                return Arrays.stream(chars)
                        .map(i -> Integer.parseInt(i, 2))
                        .map(i -> String.valueOf((char) i.intValue()))
                        .reduce("", (res, now) -> res + now);
            });
        }
    }

    private long nextNumber(){
        return (long) (Math.random() * 100 + oldNum);
    }

    private Optional<String> privateKey(String message){
        return Optional.empty();
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
