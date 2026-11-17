package com.group13.algorithmexplorer.algorithms;

import com.group13.algorithmexplorer.dto.PointerDto;

import static com.group13.algorithmexplorer.algorithms.StepTracker.formatIntArray;
import static com.group13.algorithmexplorer.algorithms.StepTracker.ptr;

/**
 * Bubble Sort implementation from first principles.
 * <p>
 * Repeatedly steps through the array, compares adjacent elements, and swaps them if they are
 * out of order. Includes the early-termination flag when a pass performs zero swaps.
 * </p>
 * <ul>
 *   <li><b>Best-Case Time Complexity:</b> O(n) (already sorted input, 1 pass)</li>
 *   <li><b>Average-Case Time Complexity:</b> O(n^2)</li>
 *   <li><b>Worst-Case Time Complexity:</b> O(n^2) (reverse sorted input)</li>
 *   <li><b>Extra Space Complexity:</b> O(1) auxiliary space (in-place)</li>
 *   <li><b>Stability:</b> Stable (only swaps strictly greater adjacent elements {@code a > b})</li>
 * </ul>
 */
public class BubbleSort {

    /**
     * Sorts the array tracked by {@code r} in ascending order using Bubble Sort.
     * Records each comparison, swap, and end-of-pass array state.
     *
     * @param r step tracker holding the array and operation counters
     */
    public void sort(StepTracker r) {
        int n = r.size();
        for (int pass = 0; pass < n - 1; pass++) {
            boolean swapped = false;
            for (int j = 0; j < n - pass - 1; j++) {
                int a = r.get(j);
                int b = r.get(j + 1);
                boolean outOfOrder = r.compare(j, j + 1) > 0;
                PointerDto[] pointers = new PointerDto[] { ptr(j, "j"), ptr(j + 1, "j+1") };
                int[] active = new int[] { j, j + 1 };
                r.record(
                    "Pass " + (pass + 1) + ": comparing " + a + " and " + b,
                    "Pass " + (pass + 1) + " | Compare A[" + j + "]=" + a + " and A[" + (j + 1) + "]=" + b,
                    pointers,
                    active
                );
                if (outOfOrder) {
                    r.swap(j, j + 1);
                    swapped = true;
                    r.record(
                        "Pass " + (pass + 1) + ": " + a + " > " + b + ", swapped them",
                        "Pass " + (pass + 1) + " | Swap A[" + j + "] and A[" + (j + 1) + "]",
                        pointers,
                        active
                    );
                }
            }
            int settledIdx = n - pass - 1;
            r.markSorted(settledIdx);
            r.recordPassBoundary(
                "End of Pass " + (pass + 1) + ": element " + r.get(settledIdx) + " is in final position [" + settledIdx + "]",
                "After Pass " + (pass + 1) + ": " + formatIntArray(r.getList().toIntArray()),
                new PointerDto[] { ptr(settledIdx, "sorted") },
                new int[] { settledIdx }
            );
            if (!swapped) {
                break;
            }
        }
        r.finish();
    }
}
