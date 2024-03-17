import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {

    public static ArrayBlockingQueue<String> queue1 = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> queue2 = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> queue3 = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) {

        Thread textGeneratorThread = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                String text = generateText("abc", 100_000);
                try {
                    queue1.put(text);
                    queue2.put(text);
                    queue3.put(text);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        textGeneratorThread.start();

        generateThread('a', queue1).start();
        generateThread('b', queue2).start();
        generateThread('c', queue3).start();
    }

    public static Thread generateThread(char letter, ArrayBlockingQueue<String> queue) {
        return new Thread(() -> {
            int max = 0;
            String maxText = "";
            for (int i = 0; i < 10_000; i++) {
                int counter = 0;
                int maxCounter = 0;
                try {
                    String text = queue.take();
                    for (int j = 0; j < text.length(); j++) {
                        if (text.charAt(j) == letter) counter++;
                        else {
                            if (counter > maxCounter) maxCounter = counter;
                            counter = 0;
                        }
                    }
                    if (maxCounter > max) {
                        max = maxCounter;
                        maxText = text;
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
            System.out.printf("Текст, содержащий символ %c %d раз подряд -> %s \n",
                    letter, max, maxText.substring(0, 1000));

        });
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