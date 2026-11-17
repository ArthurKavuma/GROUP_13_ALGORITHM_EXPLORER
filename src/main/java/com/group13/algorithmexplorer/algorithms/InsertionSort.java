package com.group13.algorithmexplorer.algorithms;

import com.group13.algorithmexplorer.dto.PointerDto;
import static com.group13.algorithmexplorer.algorithms.StepTracker.ptr;

public class InsertionSort {
    public void sort(StepTracker r) {
    int n = r.size();
        for (int i = 1; i < n; i++) {
            int key = r.get(i);
            r.record(
                "Inserting key " + key + " into the sorted prefix [0.." + (i - 1) + "]",
                "Select key at index " + i,
                new PointerDto[] { ptr(i, "key") },
                new int[] { i }
            );
            int j = i;
            while (j > 0) {
                int left = r.get(j - 1);
                boolean shouldShift = r.compare(j - 1, j) > 0;
                if (!shouldShift) {
                    r.record(
                        left + " <= " + key + ", key " + key + " stays at index " + j,
                        "Compare index " + (j - 1) + " and " + j,
                        new PointerDto[] { ptr(j - 1, "j"), ptr(j, "key") },
                        new int[] { j - 1, j }
                    );
                    break;
                }
                r.swap(j - 1, j);
                r.record(
                    left + " > " + key + ", shifting " + left + " one position right",
                    "Shift index " + (j - 1) + " -> " + j,
                    new PointerDto[] { ptr(j - 1, "key"), ptr(j, "j") },
                    new int[] { j - 1, j }
                );
                j--;
            }
        }
        r.finish();
    }
}

