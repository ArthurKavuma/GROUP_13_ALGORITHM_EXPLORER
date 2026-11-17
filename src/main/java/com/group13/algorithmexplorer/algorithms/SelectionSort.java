package com.group13.algorithmexplorer.algorithms;

import com.group13.algorithmexplorer.dto.PointerDto;

import static com.group13.algorithmexplorer.algorithms.StepTracker.formatIntArray;
import static com.group13.algorithmexplorer.algorithms.StepTracker.ptr;

/**
 * Selection Sort implementation from first principles.
 * <p>
 * Divides the array into a sorted prefix {@code [0..i-1]} and an unsorted suffix {@code [i..n-1]}.
 * In each pass, scans the unsorted suffix to find the minimum element and swaps it into position {@code i}.
 * </p>
 * <ul>
 *   <li><b>Best-Case Time Complexity:</b> O(n^2)</li>
 *   <li><b>Average-Case Time Complexity:</b> O(n^2)</li>
 *   <li><b>Worst-Case Time Complexity:</b> O(n^2)</li>
 *   <li><b>Extra Space Complexity:</b> O(1) auxiliary space (in-place)</li>
 *   <li><b>Stability:</b> Unstable (long-range swaps can move equal elements past one another)</li>
 * </ul>
 */
public class SelectionSort {

    /**
     * Sorts the array tracked by {@code r} in ascending order using Selection Sort.
     * Records comparisons, minimum selection updates, swaps, and end-of-pass snapshots.
     *
     * @param r step tracker holding the array and operation counters
     */
    public void sort(StepTracker r) {
        int n = r.size();
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                int a = r.get(j);
                int currentMin = r.get(min);
                boolean isSmaller = r.compare(j, min) < 0;
                r.record(
                    "Pass " + (i + 1) + ": comparing " + a + " with current minimum " + currentMin,
                    "Pass " + (i + 1) + " | Compare A[" + j + "]=" + a + " with min A[" + min + "]=" + currentMin,
                    new PointerDto[] { ptr(i, "i"), ptr(j, "j"), ptr(min, "min") },
                    new int[] { j, min }
                );
                if (isSmaller) {
                    min = j;
                }
            }
            if (min != i) {
                int value = r.get(min);
                r.swap(i, min);
                r.record(
                    "Pass " + (i + 1) + ": swapping minimum " + value + " into index " + i,
                    "Pass " + (i + 1) + " | Swap A[" + i + "] and A[" + min + "] (" + value + ")",
                    new PointerDto[] { ptr(i, "i"), ptr(min, "min") },
                    new int[] { i, min }
                );
            }
            r.markSorted(i);
            r.recordPassBoundary(
                "End of Pass " + (i + 1) + ": minimum " + r.get(i) + " placed at index " + i,
                "After Pass " + (i + 1) + ": " + formatIntArray(r.getList().toIntArray()),
                new PointerDto[] { ptr(i, "sorted") },
                new int[] { i }
            );
        }
        r.finish();
    }
}
