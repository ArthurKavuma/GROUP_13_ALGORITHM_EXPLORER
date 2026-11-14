package com.group13.algorithmexplorer.dto;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
* Snapshot of a single subarray node (e.g. {@code "0-3"}) in the Merge Sort
recursion tree.
*/

public class MergeTreeNodeDto {
    @JsonIgnore
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
