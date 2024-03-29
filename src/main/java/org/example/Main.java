package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {
    private static final ArrayBlockingQueue<String> queueCountMaxA = new ArrayBlockingQueue<>(100);
    private static final ArrayBlockingQueue<String> queueCountMaxB = new ArrayBlockingQueue<>(100);
    private static final ArrayBlockingQueue<String> queueCountMaxC = new ArrayBlockingQueue<>(100);


    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static int countCharElementInStr(String str, char chr) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == chr) {
                count++;
            }
        }
        return count;
    }

    public static String findStringWithMaxChar(ArrayBlockingQueue<String> str, char ch) {
        String strResult = "";
        int countResult = 0;
        for (int i = 0; i < 100; i++) {
            try {
                int counter = countCharElementInStr(str.take(), ch);
                if (counter > countResult) {
                    countResult = counter;
                    strResult = str.take();
                }
            } catch (InterruptedException e) {
                return "";
            }
        }
        return strResult;
    }

    public static void main(String[] args) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        String[] texts = new String[10_000];
        Thread threadGenerator = new Thread(() ->
        {
            while (!Thread.interrupted()) {
                for (int i = 0; i < texts.length; i++) {
                    texts[i] = generateText("abc", 100_000);
                    try {
                        queueCountMaxA.put(texts[i]);
                    } catch (InterruptedException e) {
                        return;
                    }
                    try {
                        queueCountMaxB.put(texts[i]);
                    } catch (InterruptedException e) {
                        return;
                    }
                    try {
                        queueCountMaxC.put(texts[i]);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
        );
        threadGenerator.start();
        Thread threadCountMaxA = new Thread(() ->
                System.out.println("В строке содержится максимальное количество символов 'a':" + "\n" + findStringWithMaxChar(queueCountMaxA, 'a'))
        );
        threads.add(threadCountMaxA);
        threadCountMaxA.start();

        Thread threadCountMaxB = new Thread(() ->
                System.out.println("В строке содержится максимальное количество символов 'b':" + "\n" + findStringWithMaxChar(queueCountMaxB, 'b'))
        );
        threads.add(threadCountMaxB);
        threadCountMaxB.start();

        Thread threadCountMaxC = new Thread(() ->
                System.out.println("В строке содержится максимальное количество символов 'c':" + "\n" + findStringWithMaxChar(queueCountMaxC, 'c'))
        );
        threads.add(threadCountMaxC);
        threadCountMaxC.start();

        for (Thread thread : threads) {
            thread.join();
        }
        threadGenerator.interrupt();
    }
}