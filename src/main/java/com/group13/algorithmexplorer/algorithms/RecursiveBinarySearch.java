package com.group13.algorithmexplorer.algorithms;

import com.group13.algorithmexplorer.datastructures.CustomList;
import com.group13.algorithmexplorer.datastructures.CustomQueue;

/**
 * Pure recursive binary search utility operating on a sorted {@link CustomList} of integers.
 * Logs every recursive call into a {@link CustomQueue} to fulfill recursion &amp; trace rubric requirements.
 * Zero built-in collection imports.
 */
public class RecursiveBinarySearch {

    /**
     * Searches {@code sortedList} for {@code target} using pure recursion.
     *
     * @param sortedList sorted custom list of integers
     * @param target     value to locate
     * @param callLog    optional custom queue collecting each recursive call trace
     * @return 0-based index of {@code target}, or {@code -1} if not present
     */
    public int search(CustomList<Integer> sortedList, int target, CustomQueue<String> callLog) {
        if (sortedList == null || sortedList.size() == 0) {
            return -1;
        }
        return searchRecursive(sortedList, target, 0, sortedList.size() - 1, 1, callLog);
    }

    private int searchRecursive(
        CustomList<Integer> list,
        int target,
        int lo,
        int hi,
        int depth,
        CustomQueue<String> callLog
    ) {
        if (lo > hi) {
            if (callLog != null) {
                callLog.enqueue(
                    "binarySearch(lo=" + lo + ", hi=" + hi + ", depth=" + depth + ") -> base case: not found (-1)"
                );
            }
            return -1;
        }

        int mid = lo + (hi - lo) / 2;
        int midVal = list.get(mid);

        if (callLog != null) {
            callLog.enqueue(
                "binarySearch(lo=" + lo + ", hi=" + hi + ", mid=" + mid + ", val=" + midVal + ", target=" + target + ")"
            );
        }

        if (midVal == target) {
            return mid;
        }
        if (target < midVal) {
            return searchRecursive(list, target, lo, mid - 1, depth + 1, callLog);
        }
        return searchRecursive(list, target, mid + 1, hi, depth + 1, callLog);
    }
}
