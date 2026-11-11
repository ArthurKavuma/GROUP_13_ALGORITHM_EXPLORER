package com.group13.algorithmexplorer.dto;

/**
 * Request payload for {@code POST /api/sort}.
 */

public class SortRequestDto {
    private int[] array;
 private String algorithm;
 private String dataStructure;
 public SortRequestDto() {
 }
 public SortRequestDto(int[] array, String algorithm, String dataStructure) {
 this.array = array;
 this.algorithm = algorithm;
 this.dataStructure = dataStructure;
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
 public String getDataStructure() {
 return dataStructure;
 }
 public void setDataStructure(String dataStructure) {
 this.dataStructure = dataStructure;
 }
}

