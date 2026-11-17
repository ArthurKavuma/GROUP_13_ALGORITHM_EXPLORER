package com.group13.algorithmexplorer.dto;

/**
 * Snapshot of a single subarray node (e.g. {@code "0-3"}) in the Merge Sort recursion tree.
 * Built without any {@code java.util} collection dependencies.
 */
public class MergeTreeNodeDto {

    private String id;
    private int[] values;
    private String status;

    public MergeTreeNodeDto() {
    }

    public MergeTreeNodeDto(String id, int[] values, String status) {
        this.id = id;
        this.values = values;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int[] getValues() {
        return values;
    }

    public void setValues(int[] values) {
        this.values = values;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
