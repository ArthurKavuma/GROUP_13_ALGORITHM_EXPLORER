package com.group13.algorithmexplorer.controller;

import com.group13.algorithmexplorer.algorithms.BubbleSort;
import com.group13.algorithmexplorer.algorithms.InsertionSort;
import com.group13.algorithmexplorer.algorithms.MergeSort;
import com.group13.algorithmexplorer.algorithms.QuickSort;
import com.group13.algorithmexplorer.algorithms.SelectionSort;
import com.group13.algorithmexplorer.algorithms.StepTracker;
import com.group13.algorithmexplorer.dto.CompareAllItemDto;
import com.group13.algorithmexplorer.dto.CompareAllResponseDto;
import com.group13.algorithmexplorer.dto.HealthResponseDto;
import com.group13.algorithmexplorer.dto.MetricsDto;
import com.group13.algorithmexplorer.dto.SortRequestDto;
import com.group13.algorithmexplorer.dto.StepDto;
import com.group13.algorithmexplorer.dto.TrackingObjectDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller exposing {@code POST /api/sort} and {@code POST /api/sort/compare-all}
 * for Module C: Sorting Lab.
 * <p>
 * Implements strictly the five required sorting algorithms:
 * Bubble Sort, Selection Sort, Insertion Sort, Merge Sort, and Quicksort
 * (both First-Element Yellow/Green Partition and Randomised-Pivot Yellow/Green Partition).
 * Zero {@code java.util} collection imports.
 * </p>
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AlgorithmController {

    private static final int MIN_ELEMENTS = 2;
    private static final int MAX_ELEMENTS = 16;
    private static final int MIN_VALUE = -999;
    private static final int MAX_VALUE = 999;

    private final BubbleSort bubbleSort = new BubbleSort();
    private final SelectionSort selectionSort = new SelectionSort();
    private final InsertionSort insertionSort = new InsertionSort();
    private final MergeSort mergeSort = new MergeSort();
    private final QuickSort quickSort = new QuickSort();

    @PostMapping("/sort")
    public ResponseEntity<TrackingObjectDto> sort(@RequestBody SortRequestDto request) {
        validateRequest(request);

        int[] inputCopy = copyArray(request.getArray());
        String algorithm = normalizeAlgorithm(request.getAlgorithm());

        StepTracker tracker = new StepTracker(inputCopy);

        long startNs = System.nanoTime();
        executeSort(algorithm, tracker);
        long runningTimeNs = System.nanoTime() - startNs;

        int[] output = tracker.getList().toIntArray();
        StepDto[] steps = tracker.drainSteps();
        MetricsDto metrics = new MetricsDto(
            runningTimeNs,
            tracker.getComparisons(),
            tracker.getSwaps()
        );

        TrackingObjectDto response = new TrackingObjectDto(
            algorithm,
            inputCopy,
            output,
            metrics,
            steps
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Module C(c) — Runs all five sorting algorithms (including both Quicksort pivot options)
     * on identical copies of the user's input array and returns side-by-side results in one table.
     */
    @PostMapping("/sort/compare-all")
    public ResponseEntity<CompareAllResponseDto> compareAll(@RequestBody SortRequestDto request) {
        validateRequest(request);
        int[] inputCopy = copyArray(request.getArray());

        String[] keys = new String[] {
            "BUBBLE_SORT",
            "SELECTION_SORT",
            "INSERTION_SORT",
            "MERGE_SORT",
            "QUICK_SORT",
            "QUICK_SORT_RANDOM"
        };
        String[] names = new String[] {
            "Bubble Sort",
            "Selection Sort",
            "Insertion Sort",
            "Merge Sort",
            "Quicksort (First-Element Yellow/Green)",
            "Quicksort (Randomised Pivot)"
        };
        String[] bestCases = new String[] {
            "O(n)",
            "O(n²)",
            "O(n)",
            "O(n log n)",
            "O(n log n)",
            "O(n log n)"
        };
        String[] avgCases = new String[] {
            "O(n²)",
            "O(n²)",
            "O(n²)",
            "O(n log n)",
            "O(n log n)",
            "O(n log n)"
        };
        String[] worstCases = new String[] {
            "O(n²)",
            "O(n²)",
            "O(n²)",
            "O(n log n)",
            "O(n²)",
            "O(n²)"
        };
        String[] extraSpaces = new String[] {
            "O(1)",
            "O(1)",
            "O(1)",
            "O(n)",
            "O(log n)",
            "O(log n)"
        };
        boolean[] stabilities = new boolean[] {
            true,
            false,
            true,
            true,
            false,
            false
        };

        CompareAllItemDto[] items = new CompareAllItemDto[keys.length];
        int[] sortedOutput = new int[0];

        for (int i = 0; i < keys.length; i++) {
            int[] runInput = copyArray(inputCopy);
            StepTracker tracker = new StepTracker(runInput);
            long startNs = System.nanoTime();
            executeSort(keys[i], tracker);
            long elapsedNs = System.nanoTime() - startNs;
            int[] out = tracker.getList().toIntArray();
            if (i == 0) {
                sortedOutput = out;
            }
            items[i] = new CompareAllItemDto(
                keys[i],
                names[i],
                tracker.getComparisons(),
                tracker.getSwaps(),
                elapsedNs,
                bestCases[i],
                avgCases[i],
                worstCases[i],
                extraSpaces[i],
                stabilities[i],
                out
            );
        }

        return ResponseEntity.ok(new CompareAllResponseDto(inputCopy, sortedOutput, items));
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponseDto> health() {
        return ResponseEntity.ok(new HealthResponseDto("UP", "Algorithm Explorer Backend (Group 13)"));
    }

    private void executeSort(String algorithm, StepTracker tracker) {
        switch (algorithm) {
            case "BUBBLE_SORT":
                bubbleSort.sort(tracker);
                break;
            case "SELECTION_SORT":
                selectionSort.sort(tracker);
                break;
            case "INSERTION_SORT":
                insertionSort.sort(tracker);
                break;
            case "MERGE_SORT":
                mergeSort.sort(tracker);
                break;
            case "QUICK_SORT":
                quickSort.sort(tracker, false);
                break;
            case "QUICK_SORT_RANDOM":
                quickSort.sort(tracker, true);
                break;
            default:
                throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        }
    }

    private static void validateRequest(SortRequestDto request) {
        if (request == null || request.getArray() == null) {
            throw new IllegalArgumentException("Request body must include a non-null 'array' of integers.");
        }
        int[] arr = request.getArray();
        if (arr.length < MIN_ELEMENTS || arr.length > MAX_ELEMENTS) {
            throw new IllegalArgumentException(
                "Input array must contain between " + MIN_ELEMENTS + " and " + MAX_ELEMENTS
                    + " elements (received " + arr.length + ")."
            );
        }
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < MIN_VALUE || arr[i] > MAX_VALUE) {
                throw new IllegalArgumentException(
                    "Element at index " + i + " (" + arr[i] + ") is outside the allowed range ["
                        + MIN_VALUE + ", " + MAX_VALUE + "]."
                );
            }
        }
    }

    private static String normalizeAlgorithm(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return "QUICK_SORT";
        }
        String key = raw.trim().toUpperCase();
        switch (key) {
            case "BUBBLE_SORT":
            case "SELECTION_SORT":
            case "INSERTION_SORT":
            case "MERGE_SORT":
            case "QUICK_SORT":
            case "QUICK_SORT_RANDOM":
                return key;
            default:
                throw new IllegalArgumentException(
                    "Unknown algorithm '" + raw + "'. Expected one of: BUBBLE_SORT, SELECTION_SORT, "
                        + "INSERTION_SORT, MERGE_SORT, QUICK_SORT, QUICK_SORT_RANDOM."
                );
        }
    }

    private static int[] copyArray(int[] source) {
        int[] copy = new int[source.length];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i];
        }
        return copy;
    }
}
