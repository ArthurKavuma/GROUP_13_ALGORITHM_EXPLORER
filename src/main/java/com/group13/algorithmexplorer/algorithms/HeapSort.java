package com.group13.algorithmexplorer.algorithms; 

import static com.group13.algorithmexplorer.algorithms.StepTracker.ptr;
import com.group13.algorithmexplorer.dto.PointerDto;

/** 
* Heap Sort implementation (max-heap construction + iterative sift-down 
extraction). 
* Powers the frontend's Binary Heap Tree visualizer. Zero built-in collection 
imports. 
*/ 
public class HeapSort { 
    public void sort(StepTracker r) { 
        int n = r.size(); 
        r.record( 
            "Building a max-heap from the input", 
            "Build max-heap", 
            new PointerDto[0], 
            new int[0] 
        ); 

        for (int i = (n / 2) - 1; i >= 0; i--) { 
            siftDown(r, i, n); 
        }

        for (int end = n - 1; end > 0; end--) { 
            int max = r.get(0); 
            r.swap(0, end); 
            r.markSorted(end); 
            r.record( 
                "Moving max " + max + " to its final index " + end, 
                "Swap index 0 and " + end, 
                new PointerDto[] { ptr(0, "root"), ptr(end, "end") }, 
                new int[] { 0, end } 
            ); 
            siftDown(r, 0, end); 
        } 
        
        r.finish(); 
    } 
    private void siftDown(StepTracker r, int root, int size) { 
        while (2 * root + 1 < size) { 
            int left = 2 * root + 1; 
            int right = left + 1; 
            int largest = root;

            if (r.compare(left, largest) > 0) { 
                largest = left; 
            } 

            if (right < size && r.compare(right, largest) > 0) {
                largest = right; 
            } 

            PointerDto[] pointers = right < size 
                ? new PointerDto[] { ptr(root, "root"), ptr(left, "L"), ptr(right, "R") } 
                : new PointerDto[] { ptr(root, "root"), ptr(left, "L") }; 
            int[] active = right < size 
                ? new int[] { root, left, right } 
                : new int[] { root, left }; 
            r.record( 
                "Heapify at index " + root + ": largest of node and children is " 
                + r.get(largest), 
                "Compare node " + root + " with children", 
                pointers, 
                active 
            ); 

            if (largest == root) { 
                return; 
            } 

            int a = r.get(root); 
            int b = r.get(largest); 
            r.swap(root, largest); 
            r.record( 
                "Sift down: swap " + a + " and " + b, 
                "Swap index " + root + " and " + largest, 
                new PointerDto[] { ptr(root, "root"), ptr(largest, "child") }, 
                new int[] { root, largest } 
            ); 
            root = largest; 
        } 
    } 
} 
