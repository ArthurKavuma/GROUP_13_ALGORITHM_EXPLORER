package com.group13.algorithmexplorer.dto;

/**
 * Snapshot of the Merge Sort divide-and-conquer tree for a single execution step.
 * Uses a plain array of {@link MergeTreeNodeDto} with zero {@code java.util} collection imports.
 */
public class MergeTreeDto {

    private String focusId;
    private int[] activeIndices;
    private MergeTreeNodeDto[] nodes;

    public MergeTreeDto() {
    }

    public MergeTreeDto(String focusId, int[] activeIndices, MergeTreeNodeDto[] nodeEntries) {
        this.focusId = focusId;
        this.activeIndices = activeIndices != null ? activeIndices : new int[0];
        this.nodes = nodeEntries != null ? nodeEntries : new MergeTreeNodeDto[0];
    }

    public String getFocusId() {
        return focusId;
    }

    public void setFocusId(String focusId) {
        this.focusId = focusId;
    }

    public int[] getActiveIndices() {
        return activeIndices;
    }

    public void setActiveIndices(int[] activeIndices) {
        this.activeIndices = activeIndices;
    }

    public MergeTreeNodeDto[] getNodes() {
        return nodes;
    }

    public void setNodes(MergeTreeNodeDto[] nodes) {
        this.nodes = nodes;
    }

    /**
     * Finds a node by its interval id (e.g. {@code "0-7"}) using linear scan on the fixed array.
     *
     * @param id interval identifier
     * @return matching {@link MergeTreeNodeDto} or {@code null}
     */
    public MergeTreeNodeDto findNode(String id) {
        if (nodes == null || id == null) {
            return null;
        }
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null && id.equals(nodes[i].getId())) {
                return nodes[i];
            }
        }
        return null;
    }
}
