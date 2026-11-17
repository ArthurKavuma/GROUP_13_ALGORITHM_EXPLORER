package com.group13.algorithmexplorer.algorithms;

import com.group13.algorithmexplorer.dto.PointerDto;

import static com.group13.algorithmexplorer.algorithms.StepTracker.formatIntArray;
import static com.group13.algorithmexplorer.algorithms.StepTracker.ptr;
import static com.group13.algorithmexplorer.algorithms.StepTracker.range;

/**
 * Recursive Quicksort using the <b>first-element pivot</b> and <b>"yellow/green" partition</b>
 * taught in class, plus an optional <b>randomised-pivot</b> second option.
 * <p>
 * <b>Yellow/Green Partition Convention (Taught in Class):</b>
 * <ul>
 *   <li>Pivot is at index {@code lo} ({@code pivot = A[lo]}). (In the randomised-pivot variant,
 *       a random index {@code rIdx} in {@code [lo..hi]} is first swapped with {@code lo}.)</li>
 *   <li>The <b>yellow region</b> {@code [lo + 1 .. yellow]} contains elements {@code <= pivot}.</li>
 *   <li>The <b>green region</b> {@code [yellow + 1 .. green - 1]} contains elements {@code > pivot}.</li>
 *   <li>Pointer {@code green} scans from {@code lo + 1} to {@code hi}:
 *     <ul>
 *       <li>If {@code A[green] <= pivot}, increment {@code yellow} and swap {@code A[yellow]} with {@code A[green]}
 *           to grow the yellow region.</li>
 *       <li>If {@code A[green] > pivot}, advance {@code green} to grow the green region.</li>
 *     </ul>
 *   </li>
 *   <li>Finally, swap the pivot {@code A[lo]} with {@code A[yellow]} so the pivot settles at {@code yellow}.</li>
 * </ul>
 * </p>
 * <ul>
 *   <li><b>Best-Case Time Complexity:</b> O(n log n)</li>
 *   <li><b>Average-Case Time Complexity:</b> O(n log n)</li>
 *   <li><b>Worst-Case Time Complexity:</b> O(n^2) (sorted/reverse-sorted input with first-element pivot; avoided in expectation by randomised pivot)</li>
 *   <li><b>Extra Space Complexity:</b> O(log n) average / O(n) worst-case recursion stack</li>
 *   <li><b>Stability:</b> Unstable</li>
 * </ul>
 */
public class QuickSort {

    /** Linear Congruential Generator state for reproducible/first-principles random pivot selection. */
    private long rngState = 0x5DEECE66DL;

    /**
     * Sorts using the standard first-element pivot and yellow/green partition.
     *
     * @param r step tracker holding the array and operation counters
     */
    public void sort(StepTracker r) {
        sort(r, false);
    }

    /**
     * Sorts using the yellow/green partition with either the first-element pivot
     * or a randomised pivot swapped into the first position.
     *
     * @param r               step tracker holding the array and operation counters
     * @param randomisedPivot {@code true} to select a random pivot in {@code [lo..hi]} before partitioning
     */
    public void sort(StepTracker r, boolean randomisedPivot) {
        this.rngState = System.nanoTime() ^ 0x5DEECE66DL;
        if (r.size() > 1) {
            quickSort(r, 0, r.size() - 1, randomisedPivot);
        }
        r.finish();
    }

    private void quickSort(StepTracker r, int lo, int hi, boolean randomisedPivot) {
        if (lo > hi) {
            return;
        }
        if (lo == hi) {
            r.markSorted(lo);
            return;
        }
        int p = partitionYellowGreen(r, lo, hi, randomisedPivot);
        quickSort(r, lo, p - 1, randomisedPivot);
        quickSort(r, p + 1, hi, randomisedPivot);
    }

    private int partitionYellowGreen(StepTracker r, int lo, int hi, boolean randomisedPivot) {
        if (randomisedPivot && hi > lo) {
            int randIdx = nextIntInRange(lo, hi);
            int chosenVal = r.get(randIdx);
            if (randIdx != lo) {
                r.swap(lo, randIdx);
                r.recordYellowGreen(
                    "Randomised pivot: selected A[" + randIdx + "]=" + chosenVal + " and swapped to first index " + lo,
                    "Random pivot A[" + randIdx + "]=" + chosenVal + " <-> A[" + lo + "]",
                    new PointerDto[] { ptr(lo, "pivot"), ptr(randIdx, "rand") },
                    new int[] { lo, randIdx },
                    lo,
                    new int[0],
                    new int[0],
                    false
                );
            } else {
                r.recordYellowGreen(
                    "Randomised pivot: selected first element A[" + lo + "]=" + chosenVal,
                    "Random pivot selected at index " + lo + " (" + chosenVal + ")",
                    new PointerDto[] { ptr(lo, "pivot") },
                    new int[] { lo },
                    lo,
                    new int[0],
                    new int[0],
                    false
                );
            }
        }

        int pivot = r.get(lo);
        int yellow = lo;

        r.recordYellowGreen(
            "Partitioning [" + lo + ".." + hi + "] with first-element pivot = " + pivot + " (yellow/green scheme)",
            "Start partition [" + lo + ".." + hi + "] | pivot = A[" + lo + "] = " + pivot,
            new PointerDto[] { ptr(lo, "pivot"), ptr(yellow, "yellow") },
            new int[] { lo },
            lo,
            new int[0],
            new int[0],
            false
        );

        for (int green = lo + 1; green <= hi; green++) {
            int value = r.get(green);
            boolean isLessOrEqual = r.compare(green, lo) <= 0;

            int[] currentYellow = range(lo + 1, yellow);
            int[] currentGreen = range(yellow + 1, green - 1);

            PointerDto[] comparePointers = new PointerDto[] {
                ptr(lo, "pivot"),
                ptr(yellow, "yellow"),
                ptr(green, "green")
            };

            r.recordYellowGreen(
                "Yellow/Green scan: is A[" + green + "]=" + value + " <= pivot " + pivot + "? "
                    + (isLessOrEqual ? "Yes (belongs to Yellow <= pivot)" : "No (stays in Green > pivot)"),
                "Compare A[" + green + "]=" + value + " with pivot=" + pivot,
                comparePointers,
                new int[] { green, lo },
                lo,
                currentYellow,
                currentGreen,
                false
            );

            if (isLessOrEqual) {
                yellow++;
                int[] nextYellow = range(lo + 1, yellow);
                int[] nextGreen = range(yellow + 1, green);
                if (yellow != green) {
                    int swappedOut = r.get(yellow);
                    r.swap(yellow, green);
                    r.recordYellowGreen(
                        "Advance yellow to " + yellow + " and swap " + swappedOut + " <-> " + value + " into Yellow region",
                        "Swap A[" + yellow + "] and A[" + green + "] (expand Yellow)",
                        new PointerDto[] {
                            ptr(lo, "pivot"),
                            ptr(yellow, "yellow"),
                            ptr(green, "green")
                        },
                        new int[] { yellow, green },
                        lo,
                        nextYellow,
                        nextGreen,
                        false
                    );
                } else {
                    r.recordYellowGreen(
                        "Advance yellow to " + yellow + ": " + value + " is already at end of Yellow region",
                        "Expand Yellow region to index " + yellow,
                        new PointerDto[] {
                            ptr(lo, "pivot"),
                            ptr(yellow, "yellow"),
                            ptr(green, "green")
                        },
                        new int[] { yellow },
                        lo,
                        nextYellow,
                        nextGreen,
                        false
                    );
                }
            }
        }

        int[] finalYellow = range(lo + 1, yellow);
        int[] finalGreen = range(yellow + 1, hi);

        if (lo != yellow) {
            r.swap(lo, yellow);
        }
        r.markSorted(yellow);

        r.recordYellowGreen(
            "End of Partition [" + lo + ".." + hi + "]: swapped pivot " + pivot + " from index " + lo
                + " into final position " + yellow,
            "After Partition [" + lo + ".." + hi + "] (pivot at " + yellow + "): "
                + formatIntArray(r.getList().toIntArray()),
            new PointerDto[] { ptr(yellow, "pivot") },
            new int[] { yellow },
            yellow,
            range(lo, yellow - 1),
            finalGreen,
            true
        );

        return yellow;
    }

    private int nextIntInRange(int lo, int hi) {
        rngState = (rngState * 25214903917L + 11L) & ((1L << 48) - 1);
        int span = hi - lo + 1;
        int offset = (int) ((rngState >>> 17) % span);
        if (offset < 0) {
            offset += span;
        }
        return lo + offset;
    }
}
