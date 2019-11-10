public class SimpleTimer {
    private long startTime;

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void stop() {
        long stopTime = System.currentTimeMillis();
        System.out.println("I took: " + (stopTime - startTime) + " ms.");
    }
}