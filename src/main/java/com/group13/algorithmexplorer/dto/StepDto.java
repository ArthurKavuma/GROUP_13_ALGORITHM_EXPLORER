package com.group13.algorithmexplorer.dto;

/**
 * Immutable snapshot of one step in the Module C sorting visualization.
 * Supports pass/merge/partition step boundaries as well as fine-grained comparison/swap highlighting
 * and Quicksort "yellow/green" partition regions.
 */
public class StepDto {

    private int[] array;
    private String description;
    private String trace;
    private PointerDto[] pointers;
    private int[] active;
    private Integer pivot;
    private int[] sorted;
    private int[] yellow;
    private int[] green;
    private boolean passBoundary;
    private int comparisons;
    private int swaps;
    private MergeTreeDto mergeTree;

    public StepDto() {
    }

    public StepDto(
        int[] array,
        String description,
        String trace,
        PointerDto[] pointers,
        int[] active,
        Integer pivot,
        int[] sorted,
        int comparisons,
        int swaps,
        MergeTreeDto mergeTree
    ) {
        this(
            array,
            description,
            trace,
            pointers,
            active,
            pivot,
            sorted,
            new int[0],
            new int[0],
            false,
            comparisons,
            swaps,
            mergeTree
        );
    }

    public StepDto(
        int[] array,
        String description,
        String trace,
        PointerDto[] pointers,
        int[] active,
        Integer pivot,
        int[] sorted,
        int[] yellow,
        int[] green,
        boolean passBoundary,
        int comparisons,
        int swaps,
        MergeTreeDto mergeTree
    ) {
        this.array = array;
        this.description = description;
        this.trace = trace;
        this.pointers = pointers != null ? pointers : new PointerDto[0];
        this.active = active != null ? active : new int[0];
        this.pivot = pivot;
        this.sorted = sorted != null ? sorted : new int[0];
        this.yellow = yellow != null ? yellow : new int[0];
        this.green = green != null ? green : new int[0];
        this.passBoundary = passBoundary;
        this.comparisons = comparisons;
        this.swaps = swaps;
        this.mergeTree = mergeTree;
    }

    public int[] getArray() {
        return array;
    }

    public void setArray(int[] array) {
        this.array = array;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
    }

    public PointerDto[] getPointers() {
        return pointers;
    }

    public void setPointers(PointerDto[] pointers) {
        this.pointers = pointers;
    }

    public int[] getActive() {
        return active;
    }

    public void setActive(int[] active) {
        this.active = active;
    }

    public Integer getPivot() {
        return pivot;
    }

    public void setPivot(Integer pivot) {
        this.pivot = pivot;
    }

    public int[] getSorted() {
        return sorted;
    }

    public void setSorted(int[] sorted) {
        this.sorted = sorted;
    }

    public int[] getYellow() {
        return yellow;
    }

    public void setYellow(int[] yellow) {
        this.yellow = yellow;
    }

    public int[] getGreen() {
        return green;
    }

    public void setGreen(int[] green) {
        this.green = green;
    }

    public boolean isPassBoundary() {
        return passBoundary;
    }

    public void setPassBoundary(boolean passBoundary) {
        this.passBoundary = passBoundary;
    }

    public int getComparisons() {
        return comparisons;
    }

    public void setComparisons(int comparisons) {
        this.comparisons = comparisons;
    }

    public int getSwaps() {
        return swaps;
    }

    public void setSwaps(int swaps) {
        this.swaps = swaps;
    }

    public MergeTreeDto getMergeTree() {
        return mergeTree;
    }

    public void setMergeTree(MergeTreeDto mergeTree) {
        this.mergeTree = mergeTree;
    }
}
