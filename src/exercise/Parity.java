package exercise;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Parity {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    private static final int CUTOFF = 1;

    public static void main(String[] args) {
        System.out.println(parallel(new int[] {1, 7, 4, 3, 6}));
        System.out.println(parallel(new int[] {6, 5, 4, 3, 2, 1}));
    }

    private static boolean sequential(int[] arr, int lo, int hi) {
        int count = 0;
        for (int i = lo; i < hi; i++) {
            if (arr[i] % 2 == 0) {
                count += 1;
            }
        }
        return count % 2 == 0;
    }

    public static boolean parallel(int[] arr) {
        return POOL.invoke(new ParityTask(arr, 0, arr.length));
    }

    private static class ParityTask extends RecursiveTask<Boolean> {
        int[] arr;
        int lo, hi;

        public ParityTask(int[] arr, int lo, int hi) {
            this.arr = arr;
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        protected Boolean compute() {
            if (hi - lo <= CUTOFF) {
                return sequential(arr, lo, hi);
            }

            int mid = lo + (hi - lo) / 2;

            ParityTask left = new ParityTask(arr, lo, mid);
            ParityTask right = new ParityTask(arr, mid, hi);

            left.fork();

            boolean rightResult = right.compute();
            boolean leftResult = left.join();

            // Note: even + even = even and odd + odd = even
            // so we return true when both are true or both are false
            return (leftResult && rightResult) || (!leftResult && !rightResult);
        }
    }
}