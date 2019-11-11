package lecture;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import java.util.Arrays;
import java.util.Random;

public class ForkJoinLargestFactors {

    public static final int CUTOFF = 100;
    public static final ForkJoinPool POOL = new ForkJoinPool();
    public static final Random RAND = new Random();
    public static long[] makeInput(int length) {
        long arr[] = new long[length];
        for (int i = 0; i < length; i++) {
            arr[i] = RAND.nextInt(Integer.MAX_VALUE / 10000);
        }
        return arr;
    }
    private static long largestFactor(long num) {
        if (num < 2) {
            return num;
        }
        else if (num == 2) {
            return 2;
        }

        for (int i = 3; i * i <= num; i += 2) {
            if (num % i == 0) {
                return num / i;
            }
        }
        return num;
    }

    public static void sequentialFindLargestFactors(long[] arr, int lo, int hi) {
        for (int i = lo; i < hi; i++) {
            arr[i] = largestFactor(arr[i]);
        }
    }

    public static void sequentialFindLargestFactors(long[] arr) {
        sequentialFindLargestFactors(arr, 0, arr.length);
    }

    static class LargestFactorsTask extends RecursiveAction {
        int lo, hi;
        long[] arr;

        public LargestFactorsTask(long[] arr, int lo, int hi) {
            this.arr = arr;
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        protected void compute() {
            if (hi - lo <= CUTOFF) {
                sequentialFindLargestFactors(arr, lo, hi);
                return;
            }

            int mid = lo + (hi - lo) / 2;
            LargestFactorsTask left = new LargestFactorsTask(arr, lo, mid);
            LargestFactorsTask right = new LargestFactorsTask(arr, mid, hi);

            left.fork();
            right.compute();
            left.join();
        }
    }

    public static void findLargestFactors(long[] arr) {
        LargestFactorsTask task = new LargestFactorsTask(arr, 0, arr.length);
        POOL.invoke(task);
    }

    public static void main(String[] args) {
        SimpleTimer timer = new SimpleTimer();
        long[] inputSeq = makeInput(10000000);
        long[] inputPara = Arrays.copyOf(inputSeq, inputSeq.length);

        //System.out.println("Input:       " + Arrays.toString(inputSeq));

        System.out.println("Sequential:");
        timer.start();
        sequentialFindLargestFactors(inputSeq);
        timer.stop();
        //System.out.println("Seq Output:  " + Arrays.toString(inputSeq));

        System.out.println("");

        System.out.println("Parallel:");
        timer.start();
        findLargestFactors(inputPara);
        timer.stop();
        //System.out.println("Para Output: " + Arrays.toString(inputSeq));
    }
}