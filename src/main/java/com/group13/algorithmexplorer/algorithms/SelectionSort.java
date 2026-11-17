package com.group13.algorithmexplorer.algorithms;

import com.group13.algorithmexplorer.dto.PointerDto;
import static com.group13.algorithmexplorer.algorithms.StepTracker.ptr;

public class SelectionSort {
   public void sort(StepTracker r) {
 int n = r.size();
 for (int i = 0; i < n - 1; i++) {
 int min = i;
 for (int j = i + 1; j < n; j++) {
 int a = r.get(j);
 int currentMin = r.get(min);
 boolean isSmaller = r.compare(j, min) < 0;
 r.record(
 "Comparing " + a + " with current minimum " + currentMin,
 "Compare index " + j + " and " + min,
 new PointerDto[] { ptr(i, "i"), ptr(j, "j"), ptr(min, "min")
},
 new int[] { j, min }
 );
 if (isSmaller) {
 min = j;
 }
 }
 if (min != i) {
 int value = r.get(min);
 r.swap(i, min);
 r.record(
 "Placing minimum " + value + " at index " + i,
 "Swap index " + i + " and " + min,
 new PointerDto[] { ptr(i, "i"), ptr(min, "min") },
 new int[] { i, min }
 );
 }
 r.markSorted(i);
 }
 r.finish();
 } 
}
