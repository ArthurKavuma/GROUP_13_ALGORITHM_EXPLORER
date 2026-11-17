package com.group13.algorithmexplorer.recursion;

import com.group13.algorithmexplorer.datastructures.CustomQueue;
import com.group13.algorithmexplorer.dto.RecursionDtos.FactorialResponseDto;
import com.group13.algorithmexplorer.dto.RecursionDtos.MethodResultDto;

/**
 * Module A(b) — Factorial service computing {@code n!} both recursively and iteratively.
 * <ul>
 *   <li>Handles {@code n = 0} ({@code 0! = 1}) and rejects negative inputs ({@code n < 0}).</li>
 *   <li><b>Recursive Time Complexity:</b> O(n), <b>Space Complexity:</b> O(n) call stack frames.</li>
 *   <li><b>Iterative Time Complexity:</b> O(n), <b>Space Complexity:</b> O(1) auxiliary space.</li>
 * </ul>
 */
public class FactorialService {

    public static final int MAX_FACTORIAL_N = 20; // 20! = 2,432,902,008,176,640,000 (max fitting in signed 64-bit long)

    /**
     * Computes {@code n!} recursively and iteratively, returning results, call/iteration counts, and timing.
     *
     * @param n non-negative integer (0 <= n <= 20)
     * @return factorial comparison payload
     */
    public FactorialResponseDto computeAll(int n) {
        if (n < 0) {
            throw new IllegalArgumentException(
                "Factorial is undefined for negative numbers (received n = " + n + "). Please enter n >= 0."
            );
        }
        if (n > MAX_FACTORIAL_N) {
            throw new IllegalArgumentException(
                "Input n = " + n + " overflows 64-bit integer (20! = 2,432,902,008,176,640,000). Please enter 0 <= n <= "
                    + MAX_FACTORIAL_N + "."
            );
        }

        MethodResultDto recursiveDto = runRecursive(n);
        MethodResultDto iterativeDto = runIterative(n);

        return new FactorialResponseDto(n, recursiveDto, iterativeDto);
    }

    /**
     * Computes {@code n!} recursively using {@code n! = n * (n - 1)!} with base case {@code 0! = 1}.
     * Time Complexity: O(n), Space Complexity: O(n) recursion stack.
     */
    private MethodResultDto runRecursive(int n) {
        long[] calls = new long[] { 0L };
        CustomQueue<String> trace = new CustomQueue<>();

        long startNs = System.nanoTime();
        long result = factorialRecursive(n, calls, trace);
        long elapsedNs = System.nanoTime() - startNs;

        return new MethodResultDto(
            "RECURSIVE_FACTORIAL",
            "Recursive Factorial",
            true,
            null,
            result,
            calls[0],
            "Function Calls",
            elapsedNs,
            "O(n)",
            "O(n) call stack",
            drainQueue(trace)
        );
    }

    private long factorialRecursive(int k, long[] calls, CustomQueue<String> trace) {
        calls[0]++;
        if (k == 0) {
            trace.enqueue("Call #" + calls[0] + ": factorial(0) -> base case returns 1");
            return 1L;
        }
        trace.enqueue("Call #" + calls[0] + ": factorial(" + k + ") = " + k + " * factorial(" + (k - 1) + ")");
        long sub = factorialRecursive(k - 1, calls, trace);
        long val = k * sub;
        trace.enqueue("Return factorial(" + k + ") = " + k + " * " + sub + " = " + val);
        return val;
    }

    /**
     * Computes {@code n!} iteratively using a {@code for} loop from {@code 1} to {@code n}.
     * Time Complexity: O(n), Space Complexity: O(1).
     */
    private MethodResultDto runIterative(int n) {
        CustomQueue<String> trace = new CustomQueue<>();
        long iterations = 0L;

        long startNs = System.nanoTime();
        long accumulator = 1L;
        if (n == 0) {
            trace.enqueue("n = 0: loop skipped, 0! = 1 (0 loop iterations)");
        } else {
            for (int i = 1; i <= n; i++) {
                iterations++;
                long prev = accumulator;
                accumulator *= i;
                trace.enqueue("Iteration " + i + ": " + prev + " * " + i + " = " + accumulator);
            }
        }
        long elapsedNs = System.nanoTime() - startNs;

        trace.enqueue("Result: " + n + "! = " + accumulator + " in " + iterations + " loop iterations.");

        return new MethodResultDto(
            "ITERATIVE_FACTORIAL",
            "Iterative Factorial",
            true,
            null,
            accumulator,
            iterations,
            "Loop Iterations",
            elapsedNs,
            "O(n)",
            "O(1)",
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
