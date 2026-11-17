package com.group13.algorithmexplorer.benchmark;

import com.group13.algorithmexplorer.dto.BenchmarkDtos.AlgorithmSeriesDto;
import com.group13.algorithmexplorer.dto.BenchmarkDtos.BenchmarkPointDto;
import com.group13.algorithmexplorer.dto.BenchmarkDtos.BenchmarkResponseDto;
import com.group13.algorithmexplorer.dto.BenchmarkDtos.DistributionBenchmarkDto;

/**
 * Module D — Experimental Time Complexity Benchmark Runner.
 * <p>
 * Executes the five sorting algorithms (including both First-Element Yellow/Green Quicksort
 * and Randomised-Pivot Quicksort) without UI step-snapshot allocation overhead so that
 * {@code System.nanoTime()} and operation counts reflect the true asymptotic behavior:
 * <ul>
 *   <li>Standard sizes: {@code n = 100, 500, 1000, 2000, 5000, 10000}</li>
 *   <li>Extended large sizes for Merge Sort and Quicksort: {@code n = 50000, 100000}</li>
 *   <li>Input distributions: {@code RANDOM}, {@code SORTED}, and {@code REVERSE_SORTED}</li>
 *   <li>Repeated at least 3 times per configuration, reporting the average time and operation counts.</li>
 * </ul>
 * </p>
 */
public class BenchmarkService {

    public static final int[] STANDARD_SIZES = new int[] { 100, 500, 1000, 2000, 5000, 10000 };
    public static final int[] LARGE_SIZES = new int[] { 50000, 100000 };
    public static final int[] ALL_NLOGN_SIZES = new int[] { 100, 500, 1000, 2000, 5000, 10000, 50000, 100000 };

    private static final String[] DISCUSSION_POINTS = new String[] {
        "1. Agreement with Big-O Theory: On random inputs, Bubble Sort, Selection Sort, and Insertion Sort exhibit clear quadratic growth O(n^2) — when n increases by 10x (from 1,000 to 10,000), their running time and comparisons increase by ~100x. In contrast, Merge Sort and Quicksort grow quasi-linearly as O(n log n).",
        "2. Why Insertion Sort is Fast on Sorted Input: On already-sorted data, Insertion Sort's inner while-loop condition (A[j-1] > key) fails on the very first comparison for every element. Consequently, it performs exactly n - 1 comparisons and 0 shifts, achieving its best-case O(n) linear time and outperforming even O(n log n) sorts.",
        "3. Why Bubble Sort with Early-Exit is Fast on Sorted Input, Unlike Selection Sort: Bubble Sort checks if any swap occurred during a pass; on sorted input it finishes after 1 pass (n - 1 comparisons, O(n)). Selection Sort still scans the entire remaining suffix on every pass to confirm the minimum, always performing n(n - 1)/2 comparisons in O(n^2) regardless of input order.",
        "4. Why First-Element Quicksort Slows Down on Sorted & Reverse-Sorted Input: When the input is already sorted or reverse sorted, choosing the first element A[lo] as the pivot produces a maximally unbalanced partition of size 0 and n - 1 at every recursive step. The recurrence degrades from T(n) = 2T(n/2) + O(n) to T(n) = T(n - 1) + O(n), resulting in n(n - 1)/2 comparisons and O(n^2) worst-case running time.",
        "5. How Randomised-Pivot Quicksort Fixes the O(n^2) Degradation: By swapping a randomly chosen element from A[lo..hi] into A[lo] before running the yellow/green partition, no specific input order (sorted or reverse-sorted) can consistently trigger the worst-case split. The expected split remains balanced on any input distribution, restoring O(n log n) expected running time even at n = 50,000 and 100,000."
    };

    /** Lightweight mutable counter holder for a single benchmark run. */
    private static final class RunMetrics {
        long comparisons;
        long swapsOrMoves;
        long elapsedNs;
    }

    /**
     * Runs the complete Module D benchmark suite across Random, Already Sorted, and Reverse Sorted inputs.
     *
     * @param requestedRepetitions number of repetitions per configuration (enforced >= 3)
     * @param includeLargeN        whether to include n = 50,000 and 100,000 for Merge Sort and Quicksort
     * @return full benchmark tables, chart series, and discussion points
     */
    public BenchmarkResponseDto runSuite(int requestedRepetitions, boolean includeLargeN) {
        int reps = Math.max(3, Math.min(requestedRepetitions, 10));

        // Brief JVM warm-up so JIT compilation does not skew the first n = 100 measurement
        warmUpJvm();

        int[] largeSizes = includeLargeN ? LARGE_SIZES : new int[0];
        int[] nlognSizes = includeLargeN ? ALL_NLOGN_SIZES : STANDARD_SIZES;

        DistributionBenchmarkDto randomDist = benchmarkDistribution("RANDOM", "Random Input", reps, STANDARD_SIZES, largeSizes, nlognSizes);
        DistributionBenchmarkDto sortedDist = benchmarkDistribution("SORTED", "Already Sorted Input", reps, STANDARD_SIZES, largeSizes, nlognSizes);
        DistributionBenchmarkDto reverseDist = benchmarkDistribution("REVERSE_SORTED", "Reverse Sorted Input", reps, STANDARD_SIZES, largeSizes, nlognSizes);

        return new BenchmarkResponseDto(
            reps,
            STANDARD_SIZES,
            largeSizes,
            new DistributionBenchmarkDto[] { randomDist, sortedDist, reverseDist },
            DISCUSSION_POINTS
        );
    }

    private DistributionBenchmarkDto benchmarkDistribution(
        String inputType,
        String inputLabel,
        int reps,
        int[] standardSizes,
        int[] largeSizes,
        int[] nlognSizes
    ) {
        AlgorithmSeriesDto bubbleSeries = new AlgorithmSeriesDto(
            "BUBBLE_SORT",
            "Bubble Sort",
            "QUADRATIC",
            "O(n)",
            "O(n^2)",
            "O(n^2)",
            runSizes("BUBBLE_SORT", inputType, standardSizes, reps)
        );

        AlgorithmSeriesDto selectionSeries = new AlgorithmSeriesDto(
            "SELECTION_SORT",
            "Selection Sort",
            "QUADRATIC",
            "O(n^2)",
            "O(n^2)",
            "O(n^2)",
            runSizes("SELECTION_SORT", inputType, standardSizes, reps)
        );

        AlgorithmSeriesDto insertionSeries = new AlgorithmSeriesDto(
            "INSERTION_SORT",
            "Insertion Sort",
            "QUADRATIC",
            "O(n)",
            "O(n^2)",
            "O(n^2)",
            runSizes("INSERTION_SORT", inputType, standardSizes, reps)
        );

        AlgorithmSeriesDto mergeSeries = new AlgorithmSeriesDto(
            "MERGE_SORT",
            "Merge Sort",
            "NLOGN",
            "O(n log n)",
            "O(n log n)",
            "O(n log n)",
            runSizes("MERGE_SORT", inputType, nlognSizes, reps)
        );

        AlgorithmSeriesDto quickFirstSeries = new AlgorithmSeriesDto(
            "QUICK_SORT",
            "Quicksort (First-Element Yellow/Green)",
            "NLOGN",
            "O(n log n)",
            "O(n log n)",
            "O(n^2)",
            runSizes("QUICK_SORT", inputType, nlognSizes, reps)
        );

        AlgorithmSeriesDto quickRandomSeries = new AlgorithmSeriesDto(
            "QUICK_SORT_RANDOM",
            "Quicksort (Randomised Pivot)",
            "NLOGN",
            "O(n log n)",
            "O(n log n)",
            "O(n^2) worst / O(n log n) expected",
            runSizes("QUICK_SORT_RANDOM", inputType, nlognSizes, reps)
        );

        return new DistributionBenchmarkDto(
            inputType,
            inputLabel,
            standardSizes,
            largeSizes,
            new AlgorithmSeriesDto[] {
                bubbleSeries,
                selectionSeries,
                insertionSeries,
                mergeSeries,
                quickFirstSeries,
                quickRandomSeries
            }
        );
    }

    private BenchmarkPointDto[] runSizes(String algoKey, String inputType, int[] sizes, int reps) {
        BenchmarkPointDto[] points = new BenchmarkPointDto[sizes.length];
        for (int s = 0; s < sizes.length; s++) {
            int n = sizes[s];

            // For First-Element Quicksort on SORTED or REVERSE_SORTED at n >= 50,000,
            // n(n-1)/2 = 1.25B to 5B operations per run (x3 reps = 15B operations) would exceed HTTP timeout.
            // We execute it up to n = 10,000 (and on RANDOM up to 100,000), and record the exact O(n^2) explanation for n >= 50,000 sorted/reverse.
            if ("QUICK_SORT".equals(algoKey) && n >= 50000 && !"RANDOM".equals(inputType)) {
                long theoreticalComps = ((long) n * (n - 1)) / 2L;
                points[s] = new BenchmarkPointDto(
                    n,
                    -1L,
                    -1.0,
                    -1L,
                    -1L,
                    theoreticalComps,
                    0L,
                    false,
                    "O(n^2) worst-case degeneracy (~" + theoreticalComps + " comparisons); Randomised Quicksort resolves this in O(n log n)."
                );
                continue;
            }

            long totalTimeNs = 0L;
            long minTimeNs = Long.MAX_VALUE;
            long maxTimeNs = Long.MIN_VALUE;
            long totalComps = 0L;
            long totalSwaps = 0L;

            for (int r = 0; r < reps; r++) {
                int[] baseData = generateInput(n, inputType, 1000L + r * 7919L + n);
                RunMetrics m = executeSingleSort(algoKey, baseData, 5000L + r * 104729L);
                totalTimeNs += m.elapsedNs;
                if (m.elapsedNs < minTimeNs) {
                    minTimeNs = m.elapsedNs;
                }
                if (m.elapsedNs > maxTimeNs) {
                    maxTimeNs = m.elapsedNs;
                }
                totalComps += m.comparisons;
                totalSwaps += m.swapsOrMoves;
            }

            long avgTimeNs = totalTimeNs / reps;
            double avgTimeMs = Math.round((avgTimeNs / 1_000_000.0) * 1000.0) / 1000.0;
            long avgComps = totalComps / reps;
            long avgSwaps = totalSwaps / reps;

            points[s] = new BenchmarkPointDto(
                n,
                avgTimeNs,
                avgTimeMs,
                minTimeNs,
                maxTimeNs,
                avgComps,
                avgSwaps,
                true,
                null
            );
        }
        return points;
    }

    private void warmUpJvm() {
        int[] sample = generateInput(200, "RANDOM", 42L);
        executeSingleSort("BUBBLE_SORT", copyOf(sample), 1L);
        executeSingleSort("SELECTION_SORT", copyOf(sample), 1L);
        executeSingleSort("INSERTION_SORT", copyOf(sample), 1L);
        executeSingleSort("MERGE_SORT", copyOf(sample), 1L);
        executeSingleSort("QUICK_SORT", copyOf(sample), 1L);
        executeSingleSort("QUICK_SORT_RANDOM", copyOf(sample), 1L);
    }

    private static int[] generateInput(int n, String inputType, long seed) {
        int[] arr = new int[n];
        if ("SORTED".equals(inputType)) {
            for (int i = 0; i < n; i++) {
                arr[i] = i + 1;
            }
            return arr;
        }
        if ("REVERSE_SORTED".equals(inputType)) {
            for (int i = 0; i < n; i++) {
                arr[i] = n - i;
            }
            return arr;
        }
        // Deterministic LCG random generation from first principles
        long state = seed ^ 0x5DEECE66DL;
        for (int i = 0; i < n; i++) {
            state = (state * 25214903917L + 11L) & ((1L << 48) - 1);
            arr[i] = (int) ((state >>> 16) % (n * 10));
            if (arr[i] < 0) {
                arr[i] = -arr[i];
            }
        }
        return arr;
    }

    private static int[] copyOf(int[] src) {
        int[] copy = new int[src.length];
        for (int i = 0; i < src.length; i++) {
            copy[i] = src[i];
        }
        return copy;
    }

    private RunMetrics executeSingleSort(String algoKey, int[] arr, long rngSeed) {
        RunMetrics m = new RunMetrics();
        long startNs = System.nanoTime();
        switch (algoKey) {
            case "BUBBLE_SORT":
                sortBubble(arr, m);
                break;
            case "SELECTION_SORT":
                sortSelection(arr, m);
                break;
            case "INSERTION_SORT":
                sortInsertion(arr, m);
                break;
            case "MERGE_SORT":
                int[] aux = new int[arr.length];
                sortMerge(arr, aux, 0, arr.length - 1, m);
                break;
            case "QUICK_SORT":
                sortQuickYellowGreen(arr, false, rngSeed, m);
                break;
            case "QUICK_SORT_RANDOM":
                sortQuickYellowGreen(arr, true, rngSeed, m);
                break;
            default:
                throw new IllegalArgumentException("Unknown benchmark algorithm: " + algoKey);
        }
        m.elapsedNs = System.nanoTime() - startNs;
        return m;
    }

    /** Pure Bubble Sort from first principles with early-exit flag. */
    private static void sortBubble(int[] a, RunMetrics m) {
        int n = a.length;
        for (int pass = 0; pass < n - 1; pass++) {
            boolean swapped = false;
            for (int j = 0; j < n - pass - 1; j++) {
                m.comparisons++;
                if (a[j] > a[j + 1]) {
                    int tmp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = tmp;
                    m.swapsOrMoves++;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    /** Pure Selection Sort from first principles. */
    private static void sortSelection(int[] a, RunMetrics m) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                m.comparisons++;
                if (a[j] < a[min]) {
                    min = j;
                }
            }
            if (min != i) {
                int tmp = a[i];
                a[i] = a[min];
                a[min] = tmp;
                m.swapsOrMoves++;
            }
        }
    }

    /** Pure Insertion Sort from first principles. */
    private static void sortInsertion(int[] a, RunMetrics m) {
        int n = a.length;
        for (int i = 1; i < n; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= 0) {
                m.comparisons++;
                if (a[j] > key) {
                    a[j + 1] = a[j];
                    m.swapsOrMoves++;
                    j--;
                } else {
                    break;
                }
            }
            a[j + 1] = key;
        }
    }

    /** Pure Merge Sort from first principles. */
    private static void sortMerge(int[] a, int[] aux, int lo, int hi, RunMetrics m) {
        if (lo >= hi) {
            return;
        }
        int mid = lo + (hi - lo) / 2;
        sortMerge(a, aux, lo, mid, m);
        sortMerge(a, aux, mid + 1, hi, m);

        for (int k = lo; k <= hi; k++) {
            aux[k] = a[k];
        }
        int i = lo;
        int j = mid + 1;
        for (int k = lo; k <= hi; k++) {
            if (i > mid) {
                a[k] = aux[j++];
                m.swapsOrMoves++;
            } else if (j > hi) {
                a[k] = aux[i++];
                m.swapsOrMoves++;
            } else {
                m.comparisons++;
                if (aux[i] <= aux[j]) {
                    a[k] = aux[i++];
                } else {
                    a[k] = aux[j++];
                }
                m.swapsOrMoves++;
            }
        }
    }

    /**
     * Quicksort using the exact first-element yellow/green partition taught in class
     * (and optional randomised pivot swap into {@code lo}).
     * Uses an explicit fixed-size {@code int[]} call-frame stack so worst-case O(n) recursion depth
     * on sorted/reverse-sorted arrays at {@code n = 10,000} never overflows the JVM thread stack.
     */
    private static void sortQuickYellowGreen(int[] a, boolean randomPivot, long rngSeed, RunMetrics m) {
        int n = a.length;
        if (n <= 1) {
            return;
        }
        int[] stackLo = new int[n];
        int[] stackHi = new int[n];
        int top = 0;
        stackLo[0] = 0;
        stackHi[0] = n - 1;

        long rng = rngSeed ^ 0x5DEECE66DL;

        while (top >= 0) {
            int lo = stackLo[top];
            int hi = stackHi[top];
            top--;

            if (lo >= hi) {
                continue;
            }

            if (randomPivot) {
                rng = (rng * 25214903917L + 11L) & ((1L << 48) - 1);
                int span = hi - lo + 1;
                int offset = (int) ((rng >>> 17) % span);
                if (offset < 0) {
                    offset += span;
                }
                int randIdx = lo + offset;
                if (randIdx != lo) {
                    int tmp = a[lo];
                    a[lo] = a[randIdx];
                    a[randIdx] = tmp;
                    m.swapsOrMoves++;
                }
            }

            int pivot = a[lo];
            int yellow = lo;
            for (int green = lo + 1; green <= hi; green++) {
                m.comparisons++;
                if (a[green] <= pivot) {
                    yellow++;
                    if (yellow != green) {
                        int tmp = a[yellow];
                        a[yellow] = a[green];
                        a[green] = tmp;
                        m.swapsOrMoves++;
                    }
                }
            }
            if (lo != yellow) {
                int tmp = a[lo];
                a[lo] = a[yellow];
                a[yellow] = tmp;
                m.swapsOrMoves++;
            }

            // Push right and left subproblems so left is processed first (matching recursive order)
            if (yellow + 1 < hi) {
                top++;
                stackLo[top] = yellow + 1;
                stackHi[top] = hi;
            }
            if (lo < yellow - 1) {
                top++;
                stackLo[top] = lo;
                stackHi[top] = yellow - 1;
            }
        }
    }
}
