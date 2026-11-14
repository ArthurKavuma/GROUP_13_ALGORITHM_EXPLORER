package com.group13.algorithmexplorer.algorithms; 

import com.group13.algorithmexplorer.datastructures.CustomArrayList; 
import com.group13.algorithmexplorer.datastructures.CustomLinkedList; 
import com.group13.algorithmexplorer.datastructures.CustomList; 
import com.group13.algorithmexplorer.datastructures.CustomQueue; 
import com.group13.algorithmexplorer.datastructures.CustomStack; 
import com.group13.algorithmexplorer.datastructures.QueueBackedList; 
import com.group13.algorithmexplorer.datastructures.StackBackedList; 
import com.group13.algorithmexplorer.dto.MergeTreeDto;
import com.group13.algorithmexplorer.dto.PointerDto; 
import com.group13.algorithmexplorer.dto.StepDto; 

/** 
* Stateful execution recorder used by all sorting algorithms. 
* <ul> 
*   
<li>Loads and mutates numbers inside the user-selected {@link CustomList} 
implementation.</li> 
*   
<li>Collects every {@link StepDto} in FIFO order inside a {@link 
CustomQueue}.</li> 
*   
<li>Pushes every array snapshot onto a {@link CustomStack} to maintain LIFO 
undo states.</li> 
*   
<li>Zero built-in collection imports.</li> 
* </ul> 
*/ 
public class StepTracker { 
    private final String dataStructureKey; 
    private final CustomList<Integer> list; 
    private final CustomQueue<StepDto> stepQueue; 
    private final CustomStack<int[]> undoStack; 
    private final boolean[] sortedFlags; 
    private StepDto initialStep; 
    private int comparisons; 
    private int swaps; 
    private int stepCounter; 
    public StepTracker(int[] input, String dataStructureKey) { 
    this.dataStructureKey = dataStructureKey != null ? dataStructureKey : 
    "CUSTOM_ARRAYLIST"; 
        this.stepQueue = new CustomQueue<>(); 
        this.undoStack = new CustomStack<>(); 
        this.sortedFlags = new boolean[input.length]; 
        this.comparisons = 0; 
        this.swaps = 0; 
        this.stepCounter = 0; 
        this.list = createAndLoadStructure(input, this.dataStructureKey); 
        recordInitialStep(input.length); 
    } 
    /** 
    * Instantiates the requested custom data structure and populates it from 
    {@code input}. 
    * For {@code CUSTOM_STACK} and {@code CUSTOM_QUEUE}, elements are explicitly 
    transferred 
    * through {@link CustomStack#push}/{@link CustomStack#pop} or 
    * {@link CustomQueue#enqueue}/{@link CustomQueue#dequeue}. 
    */ 
    private CustomList<Integer> createAndLoadStructure(int[] input, String key) { 
        if ("CUSTOM_LINKEDLIST".equals(key)) { 
            CustomLinkedList<Integer> linked = new CustomLinkedList<>(); 
            for (int i = 0; i < input.length; i++) { 
                linked.add(input[i]); 
            } 
        return linked; 
        }

        if ("CUSTOM_STACK".equals(key)) { 
            // Transfer via LIFO CustomStack (push in reverse, pop in order into StackBackedList) 
            CustomStack<Integer> transferStack = new CustomStack<>(); 
            for (int i = input.length - 1; i >= 0; i--) { 
                transferStack.push(input[i]); 
            } 
            StackBackedList<Integer> stackList = new StackBackedList<>(); 
            while (!transferStack.isEmpty()) { 
                stackList.add(transferStack.pop()); 
            } 
            return stackList; 
        } 

        if ("CUSTOM_QUEUE".equals(key)) { 
            // Transfer via FIFO CustomQueue (enqueue each element, dequeue into QueueBackedList) 
            CustomQueue<Integer> ingestionQueue = new CustomQueue<>(); 
            for (int i = 0; i < input.length; i++) { 
                ingestionQueue.enqueue(input[i]); 
            } 
            QueueBackedList<Integer> queueList = new QueueBackedList<>(); 
            while (!ingestionQueue.isEmpty()) { 
                queueList.add(ingestionQueue.dequeue()); 
            } 
            return queueList; 
        } 

        CustomArrayList<Integer> arrayList = new CustomArrayList<>(input.length); 
        for (int i = 0; i < input.length; i++) { 
            arrayList.add(input[i]); 
        } 
        return arrayList; 
    } 

    private void recordInitialStep(int count) { 
        String label = structureLabel(dataStructureKey); 
        String loadTrace = initialTraceFor(dataStructureKey, count); 
        int[] snapshot = list.toIntArray(); 
        undoStack.push(snapshot); 

        StepDto step = new StepDto( 
            snapshot, 
            "Loaded " + count + " elements into " + label, 
            loadTrace, 
            new PointerDto[0], 
            new int[0], 
            null, 
            new int[0], 
            0, 
            0, 
            null 
        ); 
        this.initialStep = step; 
        stepQueue.enqueue(step); 
    } 

    public void updateInitialMergeTree(MergeTreeDto initialTree) { 
        if (this.initialStep != null) { 
            this.initialStep.setMergeTree(initialTree); 
        } 
    } 

    public CustomList<Integer> getList() { 
        return list; 
    }   

    public int size() { 
        return list.size(); 
    } 

    public int get(int index) { 
        return list.get(index); 
    } 

    public void set(int index, int value) { 
        list.set(index, value); 
    } 

    public int compare(int i, int j) { 
        return compareValues(list.get(i), list.get(j)); 
    } 

    public int compareValues(int a, int b) { 
        comparisons++;
        return a - b; 
    } 

    public void swap(int i, int j) { 
        if (i == j) { 
        return; 
        } 
        int temp = list.get(i); 
        list.set(i, list.get(j)); 
        list.set(j, temp); 
        swaps++; 
    } 

    public void countShift() { 
        swaps++; 
    } 

    public void markSorted(int index) { 
        if (index >= 0 && index < sortedFlags.length) { 
            sortedFlags[index] = true; 
        } 
    } 

    public void markRangeSorted(int lo, int hi) { 
        for (int i = lo; i <= hi; i++) { 
            markSorted(i); 
        } 
    } 

    public void record(String description, String trace, PointerDto[] pointers, int[] active) { 
        record(description, trace, pointers, active, null, null); 
    } 

    public void record( 
        String description, 
        String trace, 
        PointerDto[] pointers, 
        int[] active, 
        Integer pivot 
    ) { 
        record(description, trace, pointers, active, pivot, null); 
    } 

    public void record( 
        String description, 
        String trace, 
        PointerDto[] pointers,
        int[] active, 
        Integer pivot, 
        MergeTreeDto mergeTree 
    ) { 
        stepCounter++; 
        int[] snapshot = list.toIntArray(); 
        undoStack.push(snapshot);

        String formattedTrace = decorateTrace(trace != null ? trace : description, stepCounter); 
        StepDto step = new StepDto( 
            snapshot, 
            description, 
            formattedTrace, 
            pointers != null ? pointers : new PointerDto[0], 
            active != null ? active : new int[0], 
            pivot, 
            currentSortedIndices(), 
            comparisons, 
            swaps, 
            mergeTree 
        ); 
        stepQueue.enqueue(step); 
    } 

    public void finish() { 
        finish(null); 
    } 

    public void finish(MergeTreeDto finalMergeTree) { 
        markRangeSorted(0, sortedFlags.length - 1); 
        int[] snapshot = list.toIntArray(); 
        undoStack.push(snapshot); 
        stepCounter++;

        String doneTrace = "Done: " + formatIntArray(snapshot); 
        StepDto finalStep = new StepDto( 
            snapshot, 
            "Array sorted (" + comparisons + " comparisons, " + swaps + " 
            swaps/shifts)", 
            decorateTrace(doneTrace, stepCounter), 
            new PointerDto[0], 
            new int[0], 
            null, 
            currentSortedIndices(), 
            comparisons, 
            swaps, 
            finalMergeTree 
            ); 
            stepQueue.enqueue(finalStep); 
    } 

    /** Drains the internal FIFO {@link CustomQueue} into a {@link StepDto} array. 
    */ 
    public StepDto[] drainSteps() { 
            int total = stepQueue.size(); 
            StepDto[] result = new StepDto[total]; 
            for (int i = 0; i < total; i++) { 
                result[i] = stepQueue.dequeue(); 
            } 
            return result; 
    } 

    /** Pops and returns the most recent state from the LIFO {@link CustomStack}. */
    public int[] popUndoState() { 
        return undoStack.isEmpty() ? new int[0] : undoStack.pop(); 
    } 

    public int getUndoStackDepth() { 
        return undoStack.size(); 
    } 

    public int getComparisons() { 
        return comparisons; 
    }

    public int getSwaps() { 
        return swaps; 
    } 

    public static PointerDto ptr(int index, String label) { 
        return new PointerDto(index, label); 
    }

    public static int[] range(int lo, int hi) { 
        if (hi < lo) { 
            return new int[0]; 
        } 
        int len = hi - lo + 1; 
        int[] arr = new int[len]; 
        for (int i = 0; i < len; i++) { 
            arr[i] = lo + i; 
        } 
        return arr; 
    }

    public static String formatIntArray(int[] arr) { 
        StringBuilder sb = new StringBuilder("["); 
        for (int i = 0; i < arr.length; i++) { 
            if (i > 0) { 
                sb.append(", "); 
            } 
            sb.append(arr[i]); 
        } 
        sb.append("]"); 
        return sb.toString(); 
    }

    private int[] currentSortedIndices() { 
        int count = 0; 
        for (int i = 0; i < sortedFlags.length; i++) { 
            if (sortedFlags[i]) { 
                count++; 
            } 
        } 
        int[] indices = new int[count]; 
        int pos = 0; 
        for (int i = 0; i < sortedFlags.length; i++) { 
            if (sortedFlags[i]) { 
                indices[pos++] = i; 
            } 
        } 
        return indices; 
    } 

    private String decorateTrace(String rawTrace, int stepNum) { 
        if ("CUSTOM_STACK".equals(dataStructureKey)) { 
            return "Pushed state_" + stepNum + " to CustomStack | " + rawTrace; 
        } 
        if ("CUSTOM_QUEUE".equals(dataStructureKey)) { 
            return "Enqueued step_" + stepNum + " to CustomQueue | " + rawTrace; 
        } 
            return rawTrace; 
    } 

    private static String structureLabel(String key) { 
        if ("CUSTOM_LINKEDLIST".equals(key)) { 
        return "Custom LinkedList"; 
        } 
        if ("CUSTOM_STACK".equals(key)) { 
        return "Custom Stack"; 
        } 
        if ("CUSTOM_QUEUE".equals(key)) {
            return "Custom Queue"; 
        } 
        return "Custom ArrayList"; 
    }

    private static String initialTraceFor(String key, int n) { 
        if ("CUSTOM_LINKEDLIST".equals(key)) { 
            return "LinkedList built: " + n + " nodes"; 
        } 
        if ("CUSTOM_STACK".equals(key)) { 
            return "Pushed state_0 to CustomStack (" + n + " elements)"; 
        } 
        if ("CUSTOM_QUEUE".equals(key)) { 
            return "Enqueued step_0 to CustomQueue (" + n + " elements)"; 
        } 
            return "ArrayList.addAll(): " + n + " elements"; 
    } 
} 

