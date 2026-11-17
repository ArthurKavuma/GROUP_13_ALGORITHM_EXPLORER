package com.group13.algorithmexplorer.dto;

/**
 * Response payload for {@code POST /api/sort/compare-all}, returning side-by-side results
 * for all five sorting algorithms (including both Quicksort pivot options) on the same input.
 */
public class CompareAllResponseDto {

    private int[] input;
    private int[] sortedOutput;
    private CompareAllItemDto[] results;

    public CompareAllResponseDto() {
    }

    public CompareAllResponseDto(int[] input, int[] sortedOutput, CompareAllItemDto[] results) {
        this.input = input;
        this.sortedOutput = sortedOutput;
        this.results = results;
    }

    public int[] getInput() {
        return input;
    }

    public void setInput(int[] input) {
        this.input = input;
    }

    public int[] getSortedOutput() {
        return sortedOutput;
    }

    public void setSortedOutput(int[] sortedOutput) {
        this.sortedOutput = sortedOutput;
    }

    public CompareAllItemDto[] getResults() {
        return results;
    }

    public void setResults(CompareAllItemDto[] results) {
        this.results = results;
    }
}
