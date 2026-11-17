package com.group13.algorithmexplorer.algorithms;

import com.group13.algorithmexplorer.dto.PointerDto;
import static com.group13.algorithmexplorer.algorithms.StepTracker.ptr;

public class BubbleSort {
    public void sort(StepTracker r) {
 int n = r.size();
 for (int pass = 0; pass < n - 1; pass++) {
 boolean swapped = false;
 for (int j = 0; j < n - pass - 1; j++) {
 int a = r.get(j);
 int b = r.get(j + 1);
 boolean outOfOrder = r.compare(j, j + 1) > 0;
 PointerDto[] pointers = new PointerDto[] { ptr(j, "j"), ptr(j + 1,
"j + 1") };
 int[] active = new int[] { j, j + 1 };
 r.record(
 "Pass " + (pass + 1) + ": comparing " + a + " and " + b,
 "Compare index " + j + " and " + (j + 1),
 pointers,
 active
 );
 if (outOfOrder) {
 r.swap(j, j + 1);
 swapped = true;
 r.record(
 a + " > " + b + ", swapping them",
 "Swap index " + j + " and " + (j + 1),
 pointers,
 active
 );
 }
 }
 r.markSorted(n - pass - 1);
 if (!swapped) {
 break;
 }
 }
 r.finish();
 }
}

