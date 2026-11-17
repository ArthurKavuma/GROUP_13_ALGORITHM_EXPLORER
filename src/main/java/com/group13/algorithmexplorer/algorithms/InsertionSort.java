package com.group13.algorithmexplorer.algorithms;

import com.group13.algorithmexplorer.dto.PointerDto;

import static com.group13.algorithmexplorer.algorithms.StepTracker.formatIntArray;
import static com.group13.algorithmexplorer.algorithms.StepTracker.ptr;
import static com.group13.algorithmexplorer.algorithms.StepTracker.range;

/**
 * Insertion Sort implementation from first principles.
 * <p>
 * Builds the sorted prefix one element at a time by taking {@code key = A[i]} and shifting larger
 * elements in {@code A[0..i-1]} one position to the right until the correct insertion point is found.
 * </p>
 * <ul>
 *   <li><b>Best-Case Time Complexity:</b> O(n) (already sorted input — inner loop terminates immediately)</li>
 *   <li><b>Average-Case Time Complexity:</b> O(n^2)</li>
 *   <li><b>Worst-Case Time Complexity:</b> O(n^2) (reverse sorted input)</li>
 *   <li><b>Extra Space Complexity:</b> O(1) auxiliary space (in-place)</li>
 *   <li><b>Stability:</b> Stable (only shifts elements strictly greater than {@code key})</li>
 * </ul>
 */
public class InsertionSort {

    /**
     * Sorts the array tracked by {@code r} in ascending order using Insertion Sort.
     * Records each comparison, shift/move, and end-of-pass array snapshot.
     *
     * @param r step tracker holding the array and operation counters
     */
    public void sort(StepTracker r) {
        int n = r.size();
        for (int i = 1; i < n; i++) {
            int key = r.get(i);
            r.record(
                "Pass " + i + ": inserting key " + key + " into sorted prefix [0.." + (i - 1) + "]",
                "Pass " + i + " | Select key = " + key + " at index " + i,
                new PointerDto[] { ptr(i, "key") },
                new int[] { i }
            );
            int j = i;
            while (j > 0) {
                int left = r.get(j - 1);
                boolean shouldShift = r.compare(j - 1, j) > 0;
                if (!shouldShift) {
                    r.record(
                        "Pass " + i + ": " + left + " <= " + key + ", key " + key + " stays at index " + j,
                        "Pass " + i + " | Compare A[" + (j - 1) + "]=" + left + " <= key=" + key,
                        new PointerDto[] { ptr(j - 1, "j"), ptr(j, "key") },
                        new int[] { j - 1, j }
                    );
                    break;
                }
                r.swap(j - 1, j);
                r.record(
                    "Pass " + i + ": " + left + " > " + key + ", shifting " + left + " right to index " + j,
                    "Pass " + i + " | Shift " + left + " from index " + (j - 1) + " to " + j,
                    new PointerDto[] { ptr(j - 1, "key"), ptr(j, "j") },
                    new int[] { j - 1, j }
                );
                j--;
            }
            r.recordPassBoundary(
                "End of Pass " + i + ": prefix [0.." + i + "] is now sorted",
                "After Pass " + i + ": " + formatIntArray(r.getList().toIntArray()),
                new PointerDto[] { ptr(j, "inserted") },
                range(0, i)
            );
        }
        r.finish();
    }
}
