package com.group13.algorithmexplorer;

import com.group13.algorithmexplorer.algorithms.RecursiveBinarySearch; 
import com.group13.algorithmexplorer.controller.AlgorithmController; 
import com.group13.algorithmexplorer.datastructures.CustomArrayList; 
import com.group13.algorithmexplorer.datastructures.CustomLinkedList; 
import com.group13.algorithmexplorer.datastructures.CustomQueue; 
import com.group13.algorithmexplorer.datastructures.CustomStack; 
import com.group13.algorithmexplorer.dto.SortRequestDto; 
import com.group13.algorithmexplorer.dto.StepDto; 
import com.group13.algorithmexplorer.dto.TrackingObjectDto; 
import org.junit.jupiter.api.Test; 
import org.springframework.http.ResponseEntity; 

import static org.junit.jupiter.api.Assertions.assertArrayEquals; 
import static org.junit.jupiter.api.Assertions.assertEquals; 
import static org.junit.jupiter.api.Assertions.assertFalse; 
import static org.junit.jupiter.api.Assertions.assertNotNull; 
import static org.junit.jupiter.api.Assertions.assertTrue; 

/** 
* Unit tests verifying all custom data structures, sorting algorithms, recursive 
binary search, 
* and the {@link AlgorithmController} REST contract. 
*/ 
class AlgorithmExplorerTests { 
    private static final String[] ALGORITHMS = { 
        "BUBBLE_SORT", 
        "SELECTION_SORT", 
        "INSERTION_SORT", 
        "MERGE_SORT", 
        "QUICK_SORT", 
        "HEAP_SORT" 
    };

    private static final String[] STRUCTURES = { 
        "CUSTOM_ARRAYLIST", 
        "CUSTOM_LINKEDLIST", 
        "CUSTOM_STACK", 
        "CUSTOM_QUEUE" 
    }; 

    @Test 
    void customDataStructuresWorkCorrectly() { 
        CustomArrayList<Integer> arrayList = new CustomArrayList<>(2); 
        for (int i = 1; i <= 10; i++) { 
        arrayList.add(i * 10); 
        } 
        assertEquals(10, arrayList.size()); 
        assertEquals(50, arrayList.get(4)); 
        arrayList.set(4, 99); 
        assertEquals(99, arrayList.get(4));

        CustomLinkedList<Integer> linkedList = new CustomLinkedList<>(); 
        linkedList.add(30); 
        linkedList.add(10); 
        linkedList.add(20); 
        linkedList.set(1, 15); 
        assertArrayEquals(new int[] { 30, 15, 20 }, linkedList.toIntArray()); 

        CustomStack<Integer> stack = new CustomStack<>(); 
        stack.push(1); 
        stack.push(2); 
        stack.push(3); 
        assertEquals(3, stack.pop()); 
        assertEquals(2, stack.peek()); 
        assertEquals(2, stack.size());

        CustomQueue<Integer> queue = new CustomQueue<>(); 
        queue.enqueue(10); 
        queue.enqueue(20); 
        queue.enqueue(30); 
        assertEquals(10, queue.dequeue()); 
        assertEquals(20, queue.peek()); 
        assertEquals(2, queue.size()); 
    } 

    @Test 
    void allAlgorithmsAndDataStructuresSortCorrectly() { 
        AlgorithmController controller = new AlgorithmController(); 
        int[] sample = { 45, 12, 89, 33, 7, 21, 54, 18 }; 
        int[] expected = { 7, 12, 18, 21, 33, 45, 54, 89 };

        for (String algo : ALGORITHMS) { 
            for (String ds : STRUCTURES) { 
            SortRequestDto req = new SortRequestDto(sample, algo, ds); 
            ResponseEntity<TrackingObjectDto> res = controller.sort(req); 
            TrackingObjectDto body = res.getBody(); 
            assertNotNull(body); 
            assertArrayEquals(expected, body.getOutput(), "Failed output for " + algo + " with " + ds); 
            assertTrue(body.getSteps().length >= 2); 
            StepDto lastStep = body.getSteps()[body.getSteps().length - 1]; 
            assertArrayEquals(expected, lastStep.getArray());

            if ("MERGE_SORT".equals(algo)) { 
                assertNotNull(lastStep.getMergeTree()); 
                assertNotNull(lastStep.getMergeTree().getNodes().get("0-7")); 
                } 
            } 
        } 
    }

    @Test 
    void recursiveBinarySearchFindsAllElementsAndLogsCalls() { 
        CustomArrayList<Integer> sorted = new CustomArrayList<>(); 
        int[] values = { 7, 12, 18, 21, 33, 45, 54, 89 }; 
        for (int v : values) { 
            sorted.add(v); 
        } 

        RecursiveBinarySearch binarySearch = new RecursiveBinarySearch(); 
        for (int i = 0; i < values.length; i++) { 
            CustomQueue<String> log = new CustomQueue<>(); 
            int idx = binarySearch.search(sorted, values[i], log); 
            assertEquals(i, idx); 
            assertFalse(log.isEmpty()); 
        } 
        
        CustomQueue<String> missLog = new CustomQueue<>(); 
        assertEquals(-1, binarySearch.search(sorted, 100, missLog)); 
        assertFalse(missLog.isEmpty()); 
    } 
} 


