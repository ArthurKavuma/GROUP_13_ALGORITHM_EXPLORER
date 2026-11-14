package com.group13.algorithmexplorer.dto;
import java.util.LinkedHashMap;
import java.util.Map;

/**
* Snapshot of the Merge Sort divide-and-conquer tree for a single execution step.
* Constructed by {@code MergeSort} using raw arrays (zero java.util imports in
core logic)
* and serialized by Jackson at the DTO boundary into the exact {@code mergeTree}
JSON shape
* expected by {@code app.js}.
*/

public class MergeTreeDto {
    private String focusId;
 private int[] activeIndices;
 private Map<String, MergeTreeNodeDto> nodes;
 public MergeTreeDto() {
 }

 public MergeTreeDto(String focusId, int[] activeIndices, MergeTreeNodeDto[]
nodeEntries) {
 this.focusId = focusId;
 this.activeIndices = activeIndices != null ? activeIndices : new int[0];
 this.nodes = new LinkedHashMap<>();
 if (nodeEntries != null) {
 for (int i = 0; i < nodeEntries.length; i++) {
 MergeTreeNodeDto entry = nodeEntries[i];
 if (entry != null && entry.getId() != null) {
 this.nodes.put(entry.getId(), entry);
 }
 }
 }
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
 public Map<String, MergeTreeNodeDto> getNodes() {
 return nodes;
 }
 public void setNodes(Map<String, MergeTreeNodeDto> nodes) {
 this.nodes = nodes;
 }
}

