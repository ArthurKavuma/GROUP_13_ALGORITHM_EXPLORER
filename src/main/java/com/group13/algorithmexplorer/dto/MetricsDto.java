package com.group13.algorithmexplorer.dto;

/**
 * Aggregate performance metrics returned inside {@link TrackingObjectDto}.
 */

public class MetricsDto {
    private long runningTimeNs;
 private int comparisons;
 private int swaps;
 public MetricsDto() {
 }
 public MetricsDto(long runningTimeNs, int comparisons, int swaps) {
 this.runningTimeNs = runningTimeNs;
 this.comparisons = comparisons;
 this.swaps = swaps;
 }
 public long getRunningTimeNs() {
 return runningTimeNs;
 }
  public void setRunningTimeNs(long runningTimeNs) {
 this.runningTimeNs = runningTimeNs;
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
}


