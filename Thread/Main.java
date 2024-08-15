package MultyThread.Synchronize.Thread;

import java.util.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    public static final int THREADS_COUNT = 1000;

    public static void main(String[] args) throws InterruptedException {
        //String[] text = new String[10];
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < THREADS_COUNT; i++) {
            Thread textThread = new Thread(() -> {
                String textGenerate = generateText("RLRFR", 100);
                int countCharIntext = (int) textGenerate.chars().filter(ch -> ch == 'R').count();
                //System.out.println(textGenerate + " -> " + countCharIntext);
                synchronized (sizeToFreq) {
                    if (sizeToFreq.containsKey(countCharIntext)) {
                        sizeToFreq.put(countCharIntext, sizeToFreq.get(countCharIntext) + 1);
                    } else {
                        sizeToFreq.put(countCharIntext, 1);
                    }
                }
            });
            textThread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        Map.Entry<Integer, Integer> maxEntry = sizeToFreq.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue()).get();
        System.out.printf("Самая частая частота %d (встретился %d раз)\n", maxEntry.getKey(), maxEntry.getValue());
        System.out.println("Другие размеры:");
        sizeToFreq.entrySet()
                .stream()
                .filter(e -> !e.equals(maxEntry))
                .forEach(t -> System.out.printf("%d (%d раз)\n", t.getKey(), t.getValue()));
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
