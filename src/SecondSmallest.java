import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.Arrays;

public class SecondSmallest {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    private static final int CUTOFF = 1;

    public static void main(String[] args) {
        System.out.println(parallel(new int[]{1, 7, 4, 3, 6}));
        System.out.println(parallel(new int[]{6, 1, 4, 3, 5, 2, 1}));
        System.out.println(parallel(new int[]{6, 1, 1, 1, 1, 1, 1, 2, 1}));
    }

    private static class TwoSmallest {
        public int smallest;
        public int secondSmallest;

        public TwoSmallest() {
            this.smallest = Integer.MAX_VALUE;
            this.secondSmallest = Integer.MAX_VALUE;
        }
    }

    public static TwoSmallest sequential(int[] arr, int lo, int hi) {
        TwoSmallest result = new TwoSmallest();

        for (int i = lo; i < hi; i++) {
            TwoSmallest temp = new TwoSmallest();
            temp.smallest = arr[i];
            result = combine(result, temp);
        }
        return result;
    }

    public static int parallel(int[] arr) {
        TwoSmallest result = POOL.invoke(new SecondSmallestTask(arr, 0, arr.length));
        return result.secondSmallest;
    }

    public static TwoSmallest combine(TwoSmallest a, TwoSmallest b) {
        int[] values = new int[] {a.smallest, b.smallest, a.secondSmallest, b.secondSmallest};
        Arrays.sort(values);

        a.smallest = values[0];
        a.secondSmallest = values[1];

        // What happens if the array contains duplicates?
        // This loop makes sure that a.smallest and a.secondSmallest are
        // not the same.
        for (int i = 2; a.smallest == a.secondSmallest && i < values.length; i++) {
            a.secondSmallest = values[i];
        }

        return a;
    }

    private static class SecondSmallestTask extends RecursiveTask<TwoSmallest> {
        int[] arr;
        int lo, hi;

        public SecondSmallestTask(int[] arr, int lo, int hi) {
            this.arr = arr;
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        protected TwoSmallest compute() {
            if (hi - lo <= CUTOFF) {
                return sequential(arr, lo, hi);
            }

            int mid = lo + (hi - lo) / 2;

            SecondSmallestTask left = new SecondSmallestTask(arr, lo, mid);
            SecondSmallestTask right = new SecondSmallestTask(arr, mid, hi);

            left.fork();

            return combine(right.compute(), left.join());
        }
    }
}