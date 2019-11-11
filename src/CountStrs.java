import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class CountStrs {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    private static final int CUTOFF = 1;

    public static void main(String[] args) {
        String[] arr = new String[] {"h", "ee", "llll", "llll", "oo", "llll"};
        System.out.println(parallel(arr, "llll"));
        System.out.println(parallel(arr, "h"));
    }

    public static int sequential(String[] arr, String str, int lo, int hi) {
        int count = 0;
        for (int i = lo; i < hi; i++) {
            if (arr[i].equals(str)) {
                count += 1;
            }
        }
        return count;
    }

    public static int parallel(String[] arr, String str) {
        return POOL.invoke(new CountStrsTask(arr, 0, arr.length, str));
    }


    private static class CountStrsTask extends RecursiveTask<Integer> {
        String[] arr;
        String str;
        int lo, hi;

        public CountStrsTask(String[] arr, int lo, int hi, String str) {
            this.arr = arr;
            this.lo = lo;
            this.hi = hi;
            this.str = str;
        }

        @Override
        protected Integer compute() {
            if (hi - lo <= CUTOFF) {
                return sequential(arr, str, lo, hi);
            }

            int mid = lo + (hi - lo) / 2;

            CountStrsTask left = new CountStrsTask(arr, lo, mid, str);
            CountStrsTask right = new CountStrsTask(arr, mid, hi, str);

            left.fork();

            int rightResult = right.compute();
            int leftResult = left.join();

            return leftResult + rightResult;
        }
    }
}