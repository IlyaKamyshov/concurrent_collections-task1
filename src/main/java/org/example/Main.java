package org.example;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    final static int TEXTS_COUNT = 10_000;
    final static int TEXT_LENGTH = 100_000;

    public static BlockingQueue<String> textsQueueA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> textsQueueB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> textsQueueC = new ArrayBlockingQueue<>(100);

    static AtomicInteger counterA = new AtomicInteger(0);
    static AtomicInteger counterB = new AtomicInteger(0);
    static AtomicInteger counterC = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {

        Random random = new Random();
        String[] texts = new String[TEXTS_COUNT];

        new Thread(() -> {
            for (int i = 0; i < texts.length; i++) {
                texts[i] = generateText("abc", TEXT_LENGTH);
                try {
                    textsQueueA.put(texts[i]);
                    textsQueueB.put(texts[i]);
                    textsQueueC.put(texts[i]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            counter('a', textsQueueA, counterA);
        }).start();

        new Thread(() -> {
            counter('b', textsQueueB, counterB);
        }).start();

        new Thread(() -> {
            counter('c', textsQueueC, counterC);
        }).start();

    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void counter(char letter, BlockingQueue<String> textsQueue, AtomicInteger counter) {
        for (int i = 0; i < TEXTS_COUNT; i++) {
            try {
                String text = textsQueue.take();
                long count = text.codePoints().filter(ch -> ch == letter).count();
                if (counter.get() < count) {
                    counter.set((int) count);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Наибольшее количество символа '" + letter + "': " + counter);
    }

}

