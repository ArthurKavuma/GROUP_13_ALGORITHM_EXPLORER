package com.group13.algorithmexplorer.dto;

/**
* Represents a named index pointer rendered beneath an array card or heap node
* (e.g. {@code i}, {@code j}, {@code min}, {@code key}, {@code pivot}, {@code
root}, {@code L}, {@code R}).
*/

public class PointerDto {
    private int index;
 private String label;
 public PointerDto() {
 }
 public PointerDto(int index, String label) {
 this.index = index;
 this.label = label;
 }
 public int getIndex() {
 return index;
 }
 public void setIndex(int index) {
 this.index = index;
 }
 public String getLabel() {
 return label;
 }
 public void setLabel(String label) {
 this.label = label;
 }
}

