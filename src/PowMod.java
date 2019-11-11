import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class PowMod {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    private static final int CUTOFF = 1;

    public static void main(String[] args) {
        int[] arr = new int[] {1, 7, 4, 3, 6};
        parallel(arr, 6, 5000);
        System.out.println(Arrays.toString(arr));
    }

    public static void sequential(int[] arr, int pow, int mod, int lo, int hi) {
        for (int i = lo; i < hi; i++) {
            int result = 1;
            for (int p = 0; p < pow; p++) {
                result = (result * arr[i]) % mod;
            }
            arr[i] = result;
        }
    }

    public static void parallel(int[] arr, int pow, int mod) {
        POOL.invoke(new PowModTask(arr, pow, mod, 0, arr.length));
    }

    private static class PowModTask extends RecursiveAction {
        int[] arr;
        int lo, hi;
        int pow, mod;

        public PowModTask(int[] arr, int pow, int mod, int lo, int hi) {
            this.arr = arr;
            this.lo = lo;
            this.hi = hi;
            this.pow = pow;
            this.mod = mod;
        }

        @Override
        protected void compute() {
            if (hi - lo <= CUTOFF) {
                sequential(arr, pow, mod, lo, hi);
                return;
            }

            int mid = lo + (hi - lo) / 2;

            PowModTask left = new PowModTask(arr, pow, mod, lo, mid);
            PowModTask right = new PowModTask(arr, pow, mod, mid, hi);

            left.fork();
            right.compute();
            left.join();
        }
    }
}