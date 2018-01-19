package io.github.originalalex.ethereal.random;

public class Manager {

    private static long updateDuration = 5*60*1000; // 5 minutes
    private static ServerSecret currentSecret; // thread safe class

    private static volatile boolean RUNNING = true;

    /**
     * Update the secret key every x miliseconds
     */
    private static void loop() {
        Thread th = new Thread(() -> {
            while (RUNNING) {
                currentSecret = new ServerSecret();
                try {
                    Thread.sleep(updateDuration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }

    public static void begin() {
        loop();
    }

    public static void stop() {
        RUNNING = false;
    }

    public static ServerSecret getServerSecret() {
        return currentSecret;
    }

}
