package com.group13.algorithmexplorer.algorithms;

import com.group13.algorithmexplorer.dto.PointerDto; 

import static com.group13.algorithmexplorer.algorithms.StepTracker.ptr; 

/** 
* Recursive Quick Sort using the Lomuto partition scheme (last element as pivot). 
* Operates on {@code CustomList<Integer>} via {@link StepTracker} with zero 
built-in collection imports. 
*/ 
public class QuickSort {
    public void sort(StepTracker r) { 
        if (r.size() > 1) { 
            quickSort(r, 0, r.size() - 1); 
        } 
        r.finish(); 
    } 

    private void quickSort(StepTracker r, int lo, int hi) { 
        if (lo > hi) { 
            return; 
        } 
        if (lo == hi) { 
            r.markSorted(lo); 
            return; 
        } 
        int p = partition(r, lo, hi); 
        quickSort(r, lo, p - 1); 
        quickSort(r, p + 1, hi); 
    } 

    private int partition(StepTracker r, int lo, int hi) { 
        int pivot = r.get(hi); 
        int i = lo - 1; 

        r.record( 
            "Partitioning [" + lo + ".." + hi + "] around pivot " + pivot, 
            "Pivot = " + pivot + " (index " + hi + ")", 
            new PointerDto[] { ptr(hi, "pivot") }, 
            new int[0], 
            hi 
        );

        for (int j = lo; j < hi; j++) { 
            int value = r.get(j); 
            boolean isLess = r.compare(j, hi) < 0; 

            PointerDto[] pointers = i >= lo 
                ? new PointerDto[] { ptr(i, "i"), ptr(j, "j"), ptr(hi, "pivot") } 
                : new PointerDto[] { ptr(j, "j"), ptr(hi, "pivot") }; 
                
            r.record( 
                        "Partitioning around pivot " + pivot + ": is " + value + " < " + 
                        pivot + "? " + (isLess ? "Yes" : "No"), 
                        "Compare index " + j + " with pivot", 
                        pointers, 
                        new int[] { j }, 
                        hi 
            ); 

            if (isLess) { 
                i++; 
                if (i != j) { 
                    int a = r.get(i); 
                    r.swap(i, j); 
                    r.record( 
                        "Partitioning around pivot " + pivot + ": swap " + a + "and " + value, 
                        "Swap index " + i + " and " + j, 
                        new PointerDto[] { ptr(i, "i"), ptr(j, "j"), ptr(hi, "pivot") }, 
                        new int[] { i, j }, 
                        hi 
                    ); 
                } 
            } 
        } 
        int p = i + 1; 
        r.swap(p, hi); 
        r.markSorted(p); 
        r.record( 
            "Pivot " + pivot + " placed at its final index " + p, 
            p != hi ? "Swap index " + p + " and " + hi : "Pivot stays at index " + 
            p, 
            new PointerDto[] { ptr(p, "pivot") }, 
            new int[0], 
            p 
        ); 
        return p; 
    }
}
 
