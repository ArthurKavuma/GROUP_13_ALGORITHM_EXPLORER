package com.group13.algorithmexplorer.dto;

/**
 * Request payload for {@code POST /api/sort} and {@code POST /api/sort/compare-all}.
 */
public class SortRequestDto {

    private int[] array;
    private String algorithm;

    public SortRequestDto() {
    }

    public SortRequestDto(int[] array, String algorithm) {
        this.array = array;
        this.algorithm = algorithm;
    }

    public int[] getArray() {
        return array;
    }

    public void setArray(int[] array) {
        this.array = array;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
}
