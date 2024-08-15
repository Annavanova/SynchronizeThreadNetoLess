package MultyThread.Synchronize.SecondThread;

import java.util.*;

public class Main {

    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    public static final int THREADS_COUNT = 1000;

    public static void main(String[] args) throws InterruptedException {
        //String[] text = new String[10];

        Thread textThredSecond = new Thread(() -> {
            synchronized (sizeToFreq) {
                while (!Thread.interrupted()) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                    int maxCountInMoment = sizeToFreq.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue)).get().getKey();
                    System.out.printf("Максимум в мапе: %d встретился %d раз \n", maxCountInMoment, sizeToFreq.get(maxCountInMoment));
                }
            }
        });
        textThredSecond.start();

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < THREADS_COUNT; i++) {
            Thread textThread = new Thread(() -> {
                String textGenerate = generateText("abaca", 100);
                int countCharIntext = (int) textGenerate.chars().filter(ch -> ch == 'a').count();
                //System.out.println(textGenerate + " -> " + countCharIntext);
                synchronized (sizeToFreq) {
                    if (sizeToFreq.containsKey(countCharIntext)) {
                        sizeToFreq.put(countCharIntext, sizeToFreq.get(countCharIntext) + 1);
                    } else {
                        sizeToFreq.put(countCharIntext, 1);
                    }
                    sizeToFreq.notify();
                }
            });
            textThread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        textThredSecond.interrupt();

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
