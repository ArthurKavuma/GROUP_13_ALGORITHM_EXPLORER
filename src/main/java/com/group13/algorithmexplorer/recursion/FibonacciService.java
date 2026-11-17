package com.group13.algorithmexplorer.recursion;

import com.group13.algorithmexplorer.datastructures.CustomQueue;
import com.group13.algorithmexplorer.dto.RecursionDtos.FibonacciResponseDto;
import com.group13.algorithmexplorer.dto.RecursionDtos.MethodResultDto;

/**
 * Module A(a) — Fibonacci service computing the n-th Fibonacci number in three ways:
 * <ol>
 *   <li><b>Naive Recursion:</b> O(2^n) time, O(n) call stack space (limited to {@code n <= 30}).</li>
 *   <li><b>Memoized Recursion (Top-Down DP):</b> O(n) time, O(n) space using a fixed-size {@code long[]} table.</li>
 *   <li><b>Bottom-Up Dynamic Programming:</b> O(n) time, O(n) table space (or O(1) state) with loop iterations.</li>
 * </ol>
 */
public class FibonacciService {

    public static final int NAIVE_MAX_N = 30;
    public static final int DP_MAX_N = 92; // F(92) is the largest Fibonacci number fitting in signed 64-bit long
    private static final int MAX_LOGGED_TRACE_STEPS = 40;

    private static final String NAIVE_EXPLANATION =
        "Naive recursion recomputes overlapping subproblems (e.g. fib(n-2) is recomputed in both branches), "
        + "causing the recursion tree to grow exponentially as T(n) = T(n-1) + T(n-2) + 1 = 2*F(n+1) - 1 = O(2^n). "
        + "At n = 30, naive recursion makes 2,692,537 function calls; by n = 50 it would require over 40 billion calls "
        + "and freeze the program. Limiting naive recursion to n <= 30 keeps execution responsive while Memoized "
        + "Recursion and Bottom-Up DP solve up to n = 92 in O(n) linear time.";

    /**
     * Computes F(n) using all three required approaches and returns their results,
     * call/iteration counts, and execution times.
     *
     * @param n non-negative Fibonacci index (0 <= n <= 92; naive runs only when n <= 30)
     * @return comparison payload for Module A(a)
     */
    public FibonacciResponseDto computeAll(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Fibonacci input n must be non-negative (received " + n + ").");
        }
        if (n > DP_MAX_N) {
            throw new IllegalArgumentException(
                "Input n = " + n + " exceeds 64-bit integer limit (maximum supported n is " + DP_MAX_N
                    + "; for naive recursion, n must be <= " + NAIVE_MAX_N + ")."
            );
        }

        MethodResultDto naiveDto;
        if (n <= NAIVE_MAX_N) {
            naiveDto = runNaive(n);
        } else {
            naiveDto = new MethodResultDto(
                "NAIVE_RECURSION",
                "Naive Recursion",
                false,
                "Skipped because n = " + n + " exceeds the safe limit n <= " + NAIVE_MAX_N
                    + " (would require ~2^" + n + " calls).",
                -1L,
                0L,
                "Function Calls",
                0L,
                "O(2^n)",
                "O(n) stack",
                new String[] {
                    "Naive recursion disabled for n = " + n + " > " + NAIVE_MAX_N + " to prevent exponential hang.",
                    NAIVE_EXPLANATION
                }
            );
        }

        MethodResultDto memoDto = runMemoized(n);
        MethodResultDto dpDto = runBottomUp(n);

        return new FibonacciResponseDto(
            n,
            NAIVE_MAX_N,
            NAIVE_EXPLANATION,
            naiveDto,
            memoDto,
            dpDto
        );
    }

    /**
     * (i) Naive recursive Fibonacci: {@code fib(n) = fib(n-1) + fib(n-2)}.
     * Time Complexity: O(2^n), Space Complexity: O(n) call stack depth.
     */
    private MethodResultDto runNaive(int n) {
        long[] calls = new long[] { 0L };
        CustomQueue<String> trace = new CustomQueue<>();

        long startNs = System.nanoTime();
        long result = fibNaiveRecursive(n, calls, trace);
        long elapsedNs = System.nanoTime() - startNs;

        if (calls[0] > MAX_LOGGED_TRACE_STEPS) {
            trace.enqueue("... (" + (calls[0] - MAX_LOGGED_TRACE_STEPS) + " additional recursive calls omitted) ...");
        }
        trace.enqueue("Result: F(" + n + ") = " + result + " in " + calls[0] + " function calls.");

        return new MethodResultDto(
            "NAIVE_RECURSION",
            "Naive Recursion",
            true,
            null,
            result,
            calls[0],
            "Function Calls",
            elapsedNs,
            "O(2^n)",
            "O(n) stack",
            drainQueue(trace)
        );
    }

    private long fibNaiveRecursive(int k, long[] calls, CustomQueue<String> trace) {
        calls[0]++;
        if (calls[0] <= MAX_LOGGED_TRACE_STEPS) {
            trace.enqueue("Call #" + calls[0] + ": fib(" + k + ")");
        }
        if (k <= 1) {
            return k;
        }
        return fibNaiveRecursive(k - 1, calls, trace) + fibNaiveRecursive(k - 2, calls, trace);
    }

    /**
     * (ii) Memoized recursive Fibonacci using a pre-allocated fixed-size array {@code memo[0..n]}.
     * Time Complexity: O(n) (at most 2n - 1 calls), Space Complexity: O(n) memo array + O(n) call stack.
     */
    private MethodResultDto runMemoized(int n) {
        long[] memo = new long[n + 1];
        for (int i = 0; i <= n; i++) {
            memo[i] = -1L;
        }
        long[] calls = new long[] { 0L };
        CustomQueue<String> trace = new CustomQueue<>();

        long startNs = System.nanoTime();
        long result = fibMemoRecursive(n, memo, calls, trace);
        long elapsedNs = System.nanoTime() - startNs;

        trace.enqueue("Result: F(" + n + ") = " + result + " in " + calls[0] + " memoized function calls.");

        return new MethodResultDto(
            "MEMOIZED_RECURSION",
            "Memoized Recursion (Top-Down DP)",
            true,
            null,
            result,
            calls[0],
            "Function Calls",
            elapsedNs,
            "O(n)",
            "O(n) memo + stack",
            drainQueue(trace)
        );
    }

    private long fibMemoRecursive(int k, long[] memo, long[] calls, CustomQueue<String> trace) {
        calls[0]++;
        if (memo[k] != -1L) {
            if (calls[0] <= MAX_LOGGED_TRACE_STEPS) {
                trace.enqueue("Call #" + calls[0] + ": fib(" + k + ") -> cache hit = " + memo[k]);
            }
            return memo[k];
        }
        if (k <= 1) {
            memo[k] = k;
            if (calls[0] <= MAX_LOGGED_TRACE_STEPS) {
                trace.enqueue("Call #" + calls[0] + ": fib(" + k + ") -> base case = " + k);
            }
            return k;
        }
        if (calls[0] <= MAX_LOGGED_TRACE_STEPS) {
            trace.enqueue("Call #" + calls[0] + ": computing fib(" + k + ") = fib(" + (k - 1) + ") + fib(" + (k - 2) + ")");
        }
        memo[k] = fibMemoRecursive(k - 1, memo, calls, trace) + fibMemoRecursive(k - 2, memo, calls, trace);
        return memo[k];
    }

    /**
     * (iii) Bottom-up Dynamic Programming Fibonacci using iterative table construction.
     * Time Complexity: O(n), Space Complexity: O(n) table.
     */
    private MethodResultDto runBottomUp(int n) {
        CustomQueue<String> trace = new CustomQueue<>();
        long iterations = 0L;

        long startNs = System.nanoTime();
        long result;
        if (n <= 1) {
            iterations = 1L;
            result = n;
            trace.enqueue("Base case: dp[" + n + "] = " + n + " (1 step)");
        } else {
            long[] dp = new long[n + 1];
            dp[0] = 0L;
            dp[1] = 1L;
            trace.enqueue("Initialize dp[0] = 0, dp[1] = 1");
            for (int i = 2; i <= n; i++) {
                iterations++;
                dp[i] = dp[i - 1] + dp[i - 2];
                if (i <= MAX_LOGGED_TRACE_STEPS) {
                    trace.enqueue("Iteration " + (i - 1) + ": dp[" + i + "] = dp[" + (i - 1) + "] + dp[" + (i - 2) + "] = " + dp[i]);
                }
            }
            result = dp[n];
        }
        long elapsedNs = System.nanoTime() - startNs;

        trace.enqueue("Result: F(" + n + ") = " + result + " in " + iterations + " loop iterations.");

        return new MethodResultDto(
            "BOTTOM_UP_DP",
            "Bottom-Up Dynamic Programming",
            true,
            null,
            result,
            iterations,
            "Loop Iterations",
            elapsedNs,
            "O(n)",
            "O(n) table",
            drainQueue(trace)
        );
    }

    private static String[] drainQueue(CustomQueue<String> queue) {
        int size = queue.size();
        String[] arr = new String[size];
        for (int i = 0; i < size; i++) {
            arr[i] = queue.dequeue();
        }
        return arr;
    }
}
