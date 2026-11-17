package com.group13.algorithmexplorer.dto;

public class StepDto {

    private int[] array;
    private String description;
    private String trace;
    private PointerDto[] pointers;
    private int[] highlightedIndices;
    private Integer pivot;
    private int[] sortedIndices;
    private int comparisons;
    private int swaps;
    private MergeTreeDto mergeTree;

    public StepDto() {
    }

    public StepDto(int[] array, String description, String trace,
                   PointerDto[] pointers, int[] highlightedIndices, Integer pivot, int[] sortedIndices,
                   int comparisons, int swaps, MergeTreeDto mergeTree) {
        this.array = array;
        this.description = description;
        this.trace = trace;
        this.pointers = pointers;
        this.highlightedIndices = highlightedIndices;
        this.pivot = pivot;
        this.sortedIndices = sortedIndices;
        this.comparisons = comparisons;
        this.swaps = swaps;
        this.mergeTree = mergeTree;
    }

    public int[] getArray() { return array; }
    public void setArray(int[] array) { this.array = array; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTrace() { return trace; }
    public void setTrace(String trace) { this.trace = trace; }

    public PointerDto[] getPointers() { return pointers; }
    public void setPointers(PointerDto[] pointers) { this.pointers = pointers; }

    public int[] getHighlightedIndices() { return highlightedIndices; }
    public void setHighlightedIndices(int[] highlightedIndices) { this.highlightedIndices = highlightedIndices; }

    public Integer getPivot() { return pivot; }
public void setPivot(Integer pivot) { this.pivot = pivot; }

    public int[] getSortedIndices() { return sortedIndices; }
    public void setSortedIndices(int[] sortedIndices) { this.sortedIndices = sortedIndices; }

    public int getComparisons() { return comparisons; }
    public void setComparisons(int comparisons) { this.comparisons = comparisons; }

    public int getSwaps() { return swaps; }
    public void setSwaps(int swaps) { this.swaps = swaps; }

    public MergeTreeDto getMergeTree() { return mergeTree; }
    public void setMergeTree(MergeTreeDto mergeTree) { this.mergeTree = mergeTree; }
}