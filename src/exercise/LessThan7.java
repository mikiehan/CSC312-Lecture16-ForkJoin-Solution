package exercise;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class LessThan7 {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    private static final int CUTOFF = 1;

    public static void main(String[] args) {
        int[] arr = new int[] {21, 7, 6, 8, 17, 1, 7, 7, 1, 1, 7};
        System.out.println(parallel(arr));
    }

    public static int sequential(int[] arr, int lo, int hi) {
        int count = 0;
        for (int i = lo; i < hi; i++) {
            if (arr[i] < 7) {
                count += 1;
            }
        }
        return count;
    }

    public static int parallel(int[] arr) {
        return POOL.invoke(new LessThan7Task(arr, 0, arr.length));
    }

    private static class LessThan7Task extends RecursiveTask<Integer> {
        int[] arr;
        int lo, hi;

        public LessThan7Task(int[] arr, int lo, int hi) {
            this.arr = arr;
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        protected Integer compute() {
            if (hi - lo <= CUTOFF) {
                return sequential(arr, lo, hi);
            }

            int mid = lo + (hi - lo) / 2;

            LessThan7Task left = new LessThan7Task(arr, lo, mid);
            LessThan7Task right = new LessThan7Task(arr, mid, hi);

            left.fork();

            int rightResult = right.compute();
            int leftResult = left.join();

            return leftResult + rightResult;
        }

    }
}