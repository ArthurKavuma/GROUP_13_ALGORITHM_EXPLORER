package com.group13.algorithmexplorer.algorithms;

import com.group13.algorithmexplorer.datastructures.CustomArrayList;
import com.group13.algorithmexplorer.datastructures.CustomList;
import com.group13.algorithmexplorer.datastructures.CustomQueue;
import com.group13.algorithmexplorer.datastructures.CustomStack;
import com.group13.algorithmexplorer.dto.MergeTreeDto;
import com.group13.algorithmexplorer.dto.PointerDto;
import com.group13.algorithmexplorer.dto.StepDto;

/**
 * Stateful execution recorder used by all five Module C sorting algorithms.
 * <ul>
 *   <li>Stores and mutates numbers inside a custom array-backed list ({@link CustomArrayList}).</li>
 *   <li>Collects every {@link StepDto} in FIFO order inside a {@link CustomQueue}.</li>
 *   <li>Pushes every array snapshot onto a {@link CustomStack} to maintain LIFO undo states.</li>
 *   <li>Tracks total comparisons and swaps/moves with zero built-in {@code java.util} collection imports.</li>
 * </ul>
 */
public class StepTracker {

    private final CustomList<Integer> list;
    private final CustomQueue<StepDto> stepQueue;
    private final CustomStack<int[]> undoStack;
    private final boolean[] sortedFlags;
    private StepDto initialStep;
    private int comparisons;
    private int swaps;
    private int stepCounter;

    public StepTracker(int[] input) {
        this.stepQueue = new CustomQueue<>();
        this.undoStack = new CustomStack<>();
        this.sortedFlags = new boolean[input.length];
        this.comparisons = 0;
        this.swaps = 0;
        this.stepCounter = 0;
        this.list = new CustomArrayList<>(input.length);
        for (int i = 0; i < input.length; i++) {
            this.list.add(input[i]);
        }
        recordInitialStep(input.length);
    }

    private void recordInitialStep(int count) {
        int[] snapshot = list.toIntArray();
        undoStack.push(snapshot);

        StepDto step = new StepDto(
            snapshot,
            "Initial array (" + count + " elements): " + formatIntArray(snapshot),
            "Initial state: " + formatIntArray(snapshot),
            new PointerDto[0],
            new int[0],
            null,
            new int[0],
            new int[0],
            new int[0],
            true,
            0,
            0,
            null
        );
        this.initialStep = step;
        stepQueue.enqueue(step);
    }

    public void updateInitialMergeTree(MergeTreeDto initialTree) {
        if (this.initialStep != null) {
            this.initialStep.setMergeTree(initialTree);
        }
    }

    public CustomList<Integer> getList() {
        return list;
    }

    public int size() {
        return list.size();
    }

    public int get(int index) {
        return list.get(index);
    }

    public void set(int index, int value) {
        list.set(index, value);
    }

    public int compare(int i, int j) {
        return compareValues(list.get(i), list.get(j));
    }

    public int compareValues(int a, int b) {
        comparisons++;
        return Integer.compare(a, b);
    }

    public void swap(int i, int j) {
        if (i == j) {
            return;
        }
        int temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
        swaps++;
    }

    public void countShift() {
        swaps++;
    }

    public void markSorted(int index) {
        if (index >= 0 && index < sortedFlags.length) {
            sortedFlags[index] = true;
        }
    }

    public void markRangeSorted(int lo, int hi) {
        for (int i = lo; i <= hi; i++) {
            markSorted(i);
        }
    }

    public void record(String description, String trace, PointerDto[] pointers, int[] active) {
        record(description, trace, pointers, active, null, new int[0], new int[0], false, null);
    }

    public void recordPassBoundary(String description, String trace, PointerDto[] pointers, int[] active) {
        record(description, trace, pointers, active, null, new int[0], new int[0], true, null);
    }

    public void record(
        String description,
        String trace,
        PointerDto[] pointers,
        int[] active,
        Integer pivot
    ) {
        record(description, trace, pointers, active, pivot, new int[0], new int[0], false, null);
    }

    public void record(
        String description,
        String trace,
        PointerDto[] pointers,
        int[] active,
        Integer pivot,
        MergeTreeDto mergeTree
    ) {
        record(description, trace, pointers, active, pivot, new int[0], new int[0], false, mergeTree);
    }

    public void recordPassBoundary(
        String description,
        String trace,
        PointerDto[] pointers,
        int[] active,
        Integer pivot,
        MergeTreeDto mergeTree
    ) {
        record(description, trace, pointers, active, pivot, new int[0], new int[0], true, mergeTree);
    }

    public void recordYellowGreen(
        String description,
        String trace,
        PointerDto[] pointers,
        int[] active,
        Integer pivot,
        int[] yellow,
        int[] green,
        boolean passBoundary
    ) {
        record(description, trace, pointers, active, pivot, yellow, green, passBoundary, null);
    }

    public void record(
        String description,
        String trace,
        PointerDto[] pointers,
        int[] active,
        Integer pivot,
        int[] yellow,
        int[] green,
        boolean passBoundary,
        MergeTreeDto mergeTree
    ) {
        stepCounter++;
        int[] snapshot = list.toIntArray();
        undoStack.push(snapshot);

        StepDto step = new StepDto(
            snapshot,
            description,
            trace != null ? trace : description,
            pointers != null ? pointers : new PointerDto[0],
            active != null ? active : new int[0],
            pivot,
            currentSortedIndices(),
            yellow != null ? yellow : new int[0],
            green != null ? green : new int[0],
            passBoundary,
            comparisons,
            swaps,
            mergeTree
        );
        stepQueue.enqueue(step);
    }

    public void finish() {
        finish(null);
    }

    public void finish(MergeTreeDto finalMergeTree) {
        markRangeSorted(0, sortedFlags.length - 1);
        int[] snapshot = list.toIntArray();
        undoStack.push(snapshot);
        stepCounter++;

        String doneTrace = "Sorted: " + formatIntArray(snapshot);
        StepDto finalStep = new StepDto(
            snapshot,
            "Array sorted (" + comparisons + " comparisons, " + swaps + " swaps/moves)",
            doneTrace,
            new PointerDto[0],
            new int[0],
            null,
            currentSortedIndices(),
            new int[0],
            new int[0],
            true,
            comparisons,
            swaps,
            finalMergeTree
        );
        stepQueue.enqueue(finalStep);
    }

    /** Drains the internal FIFO {@link CustomQueue} into a {@link StepDto} array. */
    public StepDto[] drainSteps() {
        int total = stepQueue.size();
        StepDto[] result = new StepDto[total];
        for (int i = 0; i < total; i++) {
            result[i] = stepQueue.dequeue();
        }
        return result;
    }

    /** Pops and returns the most recent state from the LIFO {@link CustomStack}. */
    public int[] popUndoState() {
        return undoStack.isEmpty() ? new int[0] : undoStack.pop();
    }

    public int getUndoStackDepth() {
        return undoStack.size();
    }

    public int getComparisons() {
        return comparisons;
    }

    public int getSwaps() {
        return swaps;
    }

    public static PointerDto ptr(int index, String label) {
        return new PointerDto(index, label);
    }

    public static int[] range(int lo, int hi) {
        if (hi < lo) {
            return new int[0];
        }
        int len = hi - lo + 1;
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = lo + i;
        }
        return arr;
    }

    public static String formatIntArray(int[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(arr[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    private int[] currentSortedIndices() {
        int count = 0;
        for (int i = 0; i < sortedFlags.length; i++) {
            if (sortedFlags[i]) {
                count++;
            }
        }
        int[] indices = new int[count];
        int pos = 0;
        for (int i = 0; i < sortedFlags.length; i++) {
            if (sortedFlags[i]) {
                indices[pos++] = i;
            }
        }
        return indices;
    }
}
