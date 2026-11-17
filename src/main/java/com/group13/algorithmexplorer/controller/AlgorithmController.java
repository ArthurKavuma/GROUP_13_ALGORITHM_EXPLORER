package com.group13.algorithmexplorer.controller;

import com.group13.algorithmexplorer.algorithms.BubbleSort;
import com.group13.algorithmexplorer.algorithms.HeapSort;
import com.group13.algorithmexplorer.algorithms.InsertionSort;
import com.group13.algorithmexplorer.algorithms.MergeSort;
import com.group13.algorithmexplorer.algorithms.QuickSort;
import com.group13.algorithmexplorer.algorithms.RecursiveBinarySearch;
import com.group13.algorithmexplorer.algorithms.SelectionSort;
import com.group13.algorithmexplorer.algorithms.StepTracker;
import com.group13.algorithmexplorer.datastructures.CustomQueue;
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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * REST Controller exposing {@code POST /api/sort} for the Algorithm Explorer
 * frontend.
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
    private final HeapSort heapSort = new HeapSort();
    private final RecursiveBinarySearch binarySearch = new RecursiveBinarySearch();

    @PostMapping("/sort")
    public ResponseEntity<TrackingObjectDto> sort(@RequestBody SortRequestDto request) {
        validateRequest(request);

        int[] inputCopy = copyArray(request.getArray());
        String algorithm = normalizeAlgorithm(request.getAlgorithm());
        String dataStructure = normalizeDataStructure(request.getDataStructure());

        StepTracker tracker = new StepTracker(inputCopy, dataStructure);

        long startNs = System.nanoTime();
        executeSort(algorithm, tracker);
        long runningTimeNs = System.nanoTime() - startNs;

        // Exercise RecursiveBinarySearch on the sorted CustomList<Integer> to verify
        // order
        if (tracker.size() > 0) {
            int medianTarget = tracker.get(tracker.size() / 2);
            CustomQueue<String> searchLog = new CustomQueue<>();
            binarySearch.search(tracker.getList(), medianTarget, searchLog);
        }

        int[] output = tracker.getList().toIntArray();
        StepDto[] steps = tracker.drainSteps();
        MetricsDto metrics = new MetricsDto(
                runningTimeNs,
                tracker.getComparisons(),
                tracker.getSwaps());

        TrackingObjectDto response = new TrackingObjectDto(
                algorithm,
                dataStructure,
                inputCopy,
                output,
                metrics,
                steps);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("status", "UP");
        body.put("service", "Algorithm Explorer Backend (Group 13)");
        return ResponseEntity.ok(body);
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
                quickSort.sort(tracker);
                break;
            case "HEAP_SORT":
                heapSort.sort(tracker);
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
                            + " elements (received " + arr.length + ").");
        }
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < MIN_VALUE || arr[i] > MAX_VALUE) {
                throw new IllegalArgumentException(
                        "Element at index " + i + " (" + arr[i] + ") is outside the allowed range ["
                                + MIN_VALUE + ", " + MAX_VALUE + "].");
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
            case "HEAP_SORT":
                return key;
            default:
                throw new IllegalArgumentException(
                        "Unknown algorithm '" + raw + "'. Expected one of: BUBBLE_SORT, SELECTION_SORT, "
                                + "INSERTION_SORT, MERGE_SORT, QUICK_SORT, HEAP_SORT.");
        }
    }

    private static String normalizeDataStructure(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return "CUSTOM_ARRAYLIST";
        }
        String key = raw.trim().toUpperCase();
        switch (key) {
            case "CUSTOM_ARRAYLIST":
            case "CUSTOM_LINKEDLIST":
            case "CUSTOM_STACK":
            case "CUSTOM_QUEUE":
                return key;
            default:
                throw new IllegalArgumentException(
                        "Unknown dataStructure '" + raw + "'. Expected one of: CUSTOM_ARRAYLIST, "
                                + "CUSTOM_LINKEDLIST, CUSTOM_STACK, CUSTOM_QUEUE.");
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
