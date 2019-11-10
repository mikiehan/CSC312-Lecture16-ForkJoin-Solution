import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.Random;

public class ForkJoinSumArray {
    public static void main(String[] args) {
        SimpleTimer timer = new SimpleTimer();

        long[] input = makeInput(500000);
        //System.out.println("The input is " + java.util.Arrays.toString(input) + ".");
        timer.start();
        System.out.println("Correct answer is " + sequentialSum(input, 0, input.length) + ".");
        timer.stop();
        timer.start();
        System.out.println("My answer is " + sum(input) + ".");
        timer.stop();
    }

    public static final Random RAND = new Random();
    public static long[] makeInput(int length) {
        long arr[] = new long[length];
        for (int i = 0; i < length; i++) {
            arr[i] = RAND.nextInt(Integer.MAX_VALUE / 10000);
        }
        return arr;
    }

    public static long sequentialSum(long[] arr, int lo, int hi) {
        long result = 0;
        for (int i = lo; i < Math.min(arr.length, hi); i++) {
            result += arr[i];
        }
        return result;
    }

	/*
	 To sum an array, sum the first part with the rest
	 */

//    public static long sum(long[] arr, int lo) {
//        if (lo >= arr.length) {
//            return 0L;
//        }
//        long mySum = sequentialSum(arr, lo, lo + arr.length / 4);
//        long theRest = sum(arr, lo + arr.length / 4);
//        return mySum + theRest;
//    }

    private static final int CUTOFF = 10000;

    static class SumTask extends RecursiveTask<Long> {
        long[] arr;
        int lo, hi;

        public SumTask(long[] arr, int lo, int hi) {
            this.arr = arr;
            this.lo = lo;
            this.hi = hi;
        }

        public Long compute() {
            if (hi - lo <= CUTOFF) {
                return sequentialSum(arr, lo, hi);
            }

            int mid = lo +  (hi - lo) / 2;

            SumTask left = new SumTask(arr, lo, mid);
            SumTask right = new SumTask(arr, mid, hi);

            right.fork(); //start another thread for the other half of the job (right kinda job)

            long leftResult = left.compute(); //I do the half of the job myself (left kinda job)
            long rightResult = right.join(); //I wait for the other thread's work for the other half (right kinda job)

            return leftResult + rightResult; //I combine the result
        }

    }

    private static ForkJoinPool POOL = new ForkJoinPool();

    public static long sum(long[] arr) {
        return POOL.invoke(new SumTask(arr, 0, arr.length));
    }
}