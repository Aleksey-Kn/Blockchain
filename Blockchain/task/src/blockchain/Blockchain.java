package blockchain;

import java.util.*;
import java.util.function.UnaryOperator;

public class Blockchain {
    private final LinkedList<Block> blocks = new LinkedList<>();
    private final LinkedList<String> message = new LinkedList<>();
    private String prefix = "";
    private static final Blockchain instance = new Blockchain();
    private long timeAddLast = System.currentTimeMillis();
    private long oldNum = 0; // for openKey
    private final long[] oldLastNum = new long[3];
    private final LinkedList<long[]> dataTime = new LinkedList<>();
    private boolean needOutput = true;

    private Blockchain() {
    }

    public synchronized void addBlock(Block newBlock) {
        if ((blocks.isEmpty() ||
                (newBlock.getPreviousHash().equals(blocks.getLast().getHash()) && newBlock.getId() == blocks.getLast().getId() + 1))
                && newBlock.getHash().indexOf(prefix) == 0) {
            String[] list = newBlock.getBlockData();
            if (list != null) {
                for (String s : list) {
                    if (s != null && !trueMessage(s, false)) {
                        list = null;
                    }
                }
                if (list == null) {
                    for(String s: newBlock.getBlockData()){
                        if(trueMessage(s, false)){
                            message.add(s);
                        }
                    }
                    return;
                }
            }
            blocks.add(newBlock);
            long[] result = new long[2];
            if (System.currentTimeMillis() - timeAddLast < 200) {
                prefix = prefix.concat("0");
                result[1] = prefix.length();
            }
            if (System.currentTimeMillis() - timeAddLast > 1500 && !prefix.isEmpty()) {
                prefix = prefix.substring(1);
                result[1] = -1;
            }
            result[0] = System.currentTimeMillis() - timeAddLast;
            timeAddLast = System.currentTimeMillis();
            dataTime.add(result);
            if(needOutput) {
                System.out.println(newBlock.toString());
                if (newBlock.getBlockData() == null) {
                    System.out.println("no message");
                } else {
                    Arrays.stream(newBlock.getBlockData()).forEach(l -> {
                        if (l != null) {
                            System.out.println(privateKey(l, false, 2).get());
                        }
                    });
                }
                System.out.printf("Block was generating for %d seconds\n", result[0] / 1000);
                System.out.println(result[1] == 0 ? "N stays the same" : (result[1] < 0 ? "N was decreased by 1" :
                        "N was increased to " + result[1]));
                System.out.println();
                if(blocks.size() >= 12){
                    needOutput = false;
                }
            }
        }
    }

    public synchronized void print(int count) {
        Iterator<long[]> itTime = dataTime.iterator();
        Iterator<Block> itBlocks = blocks.iterator();
        long[] now;
        Block nowBlock;
        for (int i = 0; itBlocks.hasNext() && i < count; i++) {
            now = itTime.next();
            nowBlock = itBlocks.next();
            System.out.println(nowBlock.toString());
            if (nowBlock.getBlockData() == null) {
                System.out.println("no message");
            } else {
                Arrays.stream(nowBlock.getBlockData()).forEach(l -> {
                    System.out.println(privateKey(l, false, 2).get());
                });
            }
            System.out.printf("Block was generating for %d seconds\n", now[0] / 1000);
            System.out.println(now[1] == 0 ? "N stays the same" : (now[1] < 0 ? "N was decreased by 1" :
                    "N was increased to " + now[1]));
            System.out.println();
        }
    }

    public synchronized LinkedList<String> getMessage() {
        return message;
    }

    public String getPrefix() {
        return prefix;
    }

    public static Blockchain getInstance() {
        return instance;
    }

    public synchronized Block getLastElement() {
        return blocks.getLast();
    }

    public synchronized int size() {
        return blocks.size();
    }

    public synchronized void addMessage(String code) {
        if (trueMessage(code, true)) {
            message.add(code);
        }
    }

    private boolean trueMessage(String message, boolean newMessage) {
        Optional<String> decodingMessage = privateKey(message, true, (newMessage ? 0 : 1));
        if(decodingMessage.isPresent()) {
            return transaction(decodingMessage.get(), newMessage);
        }
        return false;
    }

    private synchronized boolean transaction(String decodingMessage, boolean newMessage) {
        if (isRight() || newMessage) {
            String[] nowData = decodingMessage.split(" ");
            String owner = nowData[0];
            String[] nowStr;
            long money = 100;
            money += blocks.parallelStream().filter(i -> i.getOwnerId().equals(nowData[0])).count() * 100;
            for(Block block: blocks){
                if(block.getBlockData() != null) {
                    for (String s : block.getBlockData()) {
                        if(s != null) {
                            nowStr = privateKey(s, false, 2).get().split(" ");
                            if (nowStr[0].equals(owner)) {
                                money -= Integer.parseInt(nowStr[2]);
                            } else if (nowStr[5].equals(owner)) {
                                money += Integer.parseInt(nowStr[2]);
                            }
                        }
                    }
                }
            }
            return money >= Integer.parseInt(nowData[2]);
        }
        return false;
    }

    public UnaryOperator<String> getOpenKey() {
        return data -> {
            StringBuilder binary = new StringBuilder(data.concat("#" + nextNumber()).chars()
                    .mapToObj(Integer::toBinaryString)
                    .map(i -> "0".repeat(8 - i.length()) + i)
                    .reduce("", (sum, now) -> sum + now));
            String[] chars = new String[(int) Math.ceil((float) binary.length() / 6)];
            for (int i = 0, end; i < chars.length; i++) {
                end = Math.min(binary.length(), 6);
                chars[i] = binary.substring(0, end);
                binary.delete(0, end);
            }
                /*
                if(chars[chars.length - 1].length() < 6){
                    int rep = 6 - chars[chars.length - 1].length();
                    chars[chars.length - 1] = "0".repeat(rep).concat(chars[chars.length - 1]);
                }

                 */
            return Arrays.stream(chars)
                    .map(i -> Integer.parseInt(i, 2))
                    .map(i -> String.valueOf((char) i.intValue()))
                    .reduce("", (res, now) -> res + now);
        };
    }

    private long nextNumber() {
        oldNum = (long) (Math.random() * 100 + oldNum);
        return oldNum;
    }

    private Optional<String> privateKey(String message, boolean check, int indOfVal) {
        StringBuilder binary = new StringBuilder(message.chars()
                .mapToObj(Integer::toBinaryString)
                .map(i -> "0".repeat(6 - i.length()) + i)
                .reduce("", (sum, now) -> sum + now));
        String[] chars = new String[(int) Math.ceil((float) binary.length() / 8)];
        for (int i = 0, end; i < chars.length; i++) {
            end = Math.min(binary.length(), 8);
            chars[i] = binary.substring(0, end);
            binary.delete(0, end);
        }
        String[] data = Arrays.stream(chars)
                .map(i -> Integer.parseInt(i, 2))
                .map(i -> String.valueOf((char) i.intValue()))
                .reduce("", (res, now) -> res + now).split("#");
        if (check) {
            int nowNum = Integer.parseInt(data[1].trim());
            if (nowNum <= oldLastNum[indOfVal]) {
                return Optional.empty();
            } else {
                oldLastNum[indOfVal] = nowNum;
                return Optional.of(data[0]);
            }
        } else {
            return Optional.of(data[0]);
        }
    }

    public boolean isRight() {
        if (blocks.size() < 2) {
            return true;
        } else {
            oldLastNum[2] = 0;
            for (Block b : blocks) {
                if(b.getBlockData() != null) {
                    for (String s : b.getBlockData()) {
                        if(s != null) {
                            Optional<String> opt = privateKey(s, true, 2);
                            if (opt.isEmpty()) {
                                return false;
                            }
                        }
                    }
                }
            }
            if (blocks.size() == 2) {
                return blocks.getFirst().getHash().equals(blocks.get(1).getPreviousHash());
            } else {
                Iterator<Block> it = blocks.descendingIterator();
                Block now, prev;
                now = it.next();
                prev = it.next();
                while (it.hasNext()) {
                    if (!now.getPreviousHash().equals(prev.getHash())) {
                        return false;
                    }
                    now = prev;
                    prev = it.next();
                }
                return true;
            }
        }
    }
}
