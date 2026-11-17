package com.group13.algorithmexplorer.algorithms;

import com.group13.algorithmexplorer.datastructures.CustomArrayList;
import com.group13.algorithmexplorer.dto.MergeTreeDto;
import com.group13.algorithmexplorer.dto.MergeTreeNodeDto;
import com.group13.algorithmexplorer.dto.PointerDto;

import static com.group13.algorithmexplorer.algorithms.StepTracker.formatIntArray;
import static com.group13.algorithmexplorer.algorithms.StepTracker.ptr;
import static com.group13.algorithmexplorer.algorithms.StepTracker.range;

/**
 * Recursive Divide-and-Conquer Merge Sort operating on {@code CustomList<Integer>} via {@link StepTracker}.
 * Populates {@link MergeTreeDto} on every step so the frontend's SVG Merge Tree visualizer
 * renders the full recursion tree with node states ({@code "focus"}, {@code "split"}, {@code "merged"}, {@code "pending"}).
 * Zero built-in collection imports.
 */
public class MergeSort {

    /** Internal mutable tracker for a single interval node [lo..hi] in the recursion tree. */
    private static final class IntervalNode {
        final String id;
        final int lo;
        final int hi;
        int[] values;
        String status;

        IntervalNode(int lo, int hi, int[] initialValues, String status) {
            this.id = lo + "-" + hi;
            this.lo = lo;
            this.hi = hi;
            this.values = initialValues;
            this.status = status;
        }
    }

    public void sort(StepTracker r) {
        int n = r.size();
        if (n <= 1) {
            r.finish();
            return;
        }

        CustomArrayList<IntervalNode> nodes = new CustomArrayList<>(2 * n);
        buildIntervals(r, 0, n - 1, 0, nodes);

        String rootId = "0-" + (n - 1);
        r.updateInitialMergeTree(snapshotTree(nodes, rootId, new int[0]));

        mergeSort(r, 0, n - 1, nodes);

        // Mark all interval nodes as merged for the final step
        for (int i = 0; i < nodes.size(); i++) {
            IntervalNode node = nodes.get(i);
            node.values = slice(r, node.lo, node.hi);
            node.status = "merged";
        }
        r.finish(snapshotTree(nodes, rootId, new int[0]));
    }

    private void buildIntervals(
        StepTracker r,
        int lo,
        int hi,
        int depth,
        CustomArrayList<IntervalNode> nodes
    ) {
        String initialStatus = depth == 0 ? "focus" : "pending";
        nodes.add(new IntervalNode(lo, hi, slice(r, lo, hi), initialStatus));
        if (lo < hi) {
            int mid = (lo + hi) / 2;
            buildIntervals(r, lo, mid, depth + 1, nodes);
            buildIntervals(r, mid + 1, hi, depth + 1, nodes);
        }
    }

    private void mergeSort(
        StepTracker r,
        int lo,
        int hi,
        CustomArrayList<IntervalNode> nodes
    ) {
        String id = lo + "-" + hi;
        if (lo >= hi) {
            updateNode(nodes, id, new int[] { r.get(lo) }, "merged");
            return;
        }

        int mid = (lo + hi) / 2;
        String leftId = lo + "-" + mid;
        String rightId = (mid + 1) + "-" + hi;

        updateNode(nodes, id, slice(r, lo, hi), "split");
        updateNode(nodes, leftId, slice(r, lo, mid), lo == mid ? "merged" : "split");
        updateNode(nodes, rightId, slice(r, mid + 1, hi), (mid + 1) == hi ? "merged" : "split");

        int[] spanIndices = range(lo, hi);
        r.record(
            "Dividing [" + lo + ".." + hi + "] into [" + lo + ".." + mid + "] and [" + (mid + 1) + ".." + hi + "]",
            "Split [" + lo + ".." + hi + "] at " + mid,
            new PointerDto[] { ptr(lo, "lo"), ptr(mid, "mid"), ptr(hi, "hi") },
            spanIndices,
            null,
            snapshotTree(nodes, id, spanIndices)
        );

        mergeSort(r, lo, mid, nodes);
        mergeSort(r, mid + 1, hi, nodes);
        merge(r, lo, mid, hi, nodes);
    }

    private void merge(
        StepTracker r,
        int lo,
        int mid,
        int hi,
        CustomArrayList<IntervalNode> nodes
    ) {
        String id = lo + "-" + hi;
        int[] left = slice(r, lo, mid);
        int[] right = slice(r, mid + 1, hi);
        int[] merged = new int[left.length + right.length];
        int mergedLen = 0;
        int i = 0;
        int j = 0;

        while (i < left.length && j < right.length) {
            boolean takeLeft = r.compareValues(left[i], right[j]) <= 0;
            int value = takeLeft ? left[i++] : right[j++];
            if (!takeLeft) {
                r.countShift();
            }
            merged[mergedLen++] = value;

            // Keep working array a valid permutation: [merged so far | rest of left | rest of right]
            int writePos = lo;
            for (int m = 0; m < mergedLen; m++) {
                r.set(writePos++, merged[m]);
            }
            for (int l = i; l < left.length; l++) {
                r.set(writePos++, left[l]);
            }
            for (int rg = j; rg < right.length; rg++) {
                r.set(writePos++, right[rg]);
            }

            updateNode(nodes, id, slice(r, lo, hi), "focus");

            int k = lo + mergedLen - 1;
            PointerDto[] pointers = buildMergePointers(k, i, left.length, j, right.length);
            int[] active = new int[] { k };

            r.record(
                "Merging [" + lo + ".." + hi + "]: placed " + value + " at index " + k,
                "Write " + value + " → index " + k,
                pointers,
                active,
                null,
                snapshotTree(nodes, id, active)
            );
        }

        int[] finalSlice = slice(r, lo, hi);
        updateNode(nodes, id, finalSlice, "merged");
        int[] spanIndices = range(lo, hi);

        r.record(
            "Merged range [" + lo + ".." + hi + "] → " + formatIntArray(finalSlice),
            "Merged [" + lo + ".." + hi + "]",
            new PointerDto[] { ptr(lo, "lo"), ptr(hi, "hi") },
            spanIndices,
            null,
            snapshotTree(nodes, id, spanIndices)
        );
    }

    private static PointerDto[] buildMergePointers(int k, int i, int leftLen, int j, int rightLen) {
        boolean hasL = i < leftLen;
        boolean hasR = j < rightLen;
        int count = 1 + (hasL ? 1 : 0) + (hasR ? 1 : 0);
        PointerDto[] ptrs = new PointerDto[count];
        int p = 0;
        ptrs[p++] = ptr(k, "k");
        if (hasL) {
            ptrs[p++] = ptr(k + 1, "L");
        }
        if (hasR) {
            ptrs[p] = ptr(k + 1 + (leftLen - i), "R");
        }
        return ptrs;
    }

    private static int[] slice(StepTracker r, int lo, int hi) {
        int len = hi - lo + 1;
        int[] out = new int[len];
        for (int i = 0; i < len; i++) {
            out[i] = r.get(lo + i);
        }
        return out;
    }

    private static void updateNode(
        CustomArrayList<IntervalNode> nodes,
        String id,
        int[] values,
        String status
    ) {
        for (int i = 0; i < nodes.size(); i++) {
            IntervalNode node = nodes.get(i);
            if (node.id.equals(id)) {
                node.values = values;
                node.status = status;
                return;
            }
        }
    }

    private static MergeTreeDto snapshotTree(
        CustomArrayList<IntervalNode> nodes,
        String focusId,
        int[] activeIndices
    ) {
        int count = nodes.size();
        MergeTreeNodeDto[] entries = new MergeTreeNodeDto[count];
        for (int i = 0; i < count; i++) {
            IntervalNode n = nodes.get(i);
            int[] copy = new int[n.values.length];
            for (int k = 0; k < n.values.length; k++) {
                copy[k] = n.values[k];
            }
            entries[i] = new MergeTreeNodeDto(n.id, copy, n.status);
        }
        return new MergeTreeDto(focusId, activeIndices, entries);
    }
}
