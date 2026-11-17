package com.group13.algorithmexplorer;

import com.group13.algorithmexplorer.controller.AlgorithmController;
import com.group13.algorithmexplorer.controller.BenchmarkController;
import com.group13.algorithmexplorer.controller.DataStructureController;
import com.group13.algorithmexplorer.controller.RecursionController;
import com.group13.algorithmexplorer.datastructures.BracketBalanceChecker;
import com.group13.algorithmexplorer.datastructures.CircularArrayQueue;
import com.group13.algorithmexplorer.datastructures.DoublyLinkedList;
import com.group13.algorithmexplorer.datastructures.FixedArrayStack;
import com.group13.algorithmexplorer.datastructures.LinearArrayQueue;
import com.group13.algorithmexplorer.datastructures.SinglyLinkedList;
import com.group13.algorithmexplorer.dto.BenchmarkDtos.BenchmarkRequestDto;
import com.group13.algorithmexplorer.dto.BenchmarkDtos.BenchmarkResponseDto;
import com.group13.algorithmexplorer.dto.CompareAllResponseDto;
import com.group13.algorithmexplorer.dto.DataStructureDtos.BracketCheckResponseDto;
import com.group13.algorithmexplorer.dto.RecursionDtos.FactorialResponseDto;
import com.group13.algorithmexplorer.dto.RecursionDtos.FibonacciResponseDto;
import com.group13.algorithmexplorer.dto.RecursionDtos.HanoiRequestDto;
import com.group13.algorithmexplorer.dto.RecursionDtos.HanoiResponseDto;
import com.group13.algorithmexplorer.dto.RecursionDtos.IntInputRequestDto;
import com.group13.algorithmexplorer.dto.SortRequestDto;
import com.group13.algorithmexplorer.dto.StepDto;
import com.group13.algorithmexplorer.dto.TrackingObjectDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Comprehensive JUnit 5 test suite covering Modules A, B, C, and D of the CSC2107 Algorithm Explorer.
 */
class AlgorithmExplorerTests {

    private static final String[] SORT_ALGORITHMS = {
        "BUBBLE_SORT",
        "SELECTION_SORT",
        "INSERTION_SORT",
        "MERGE_SORT",
        "QUICK_SORT",
        "QUICK_SORT_RANDOM"
    };

    @Test
    void moduleA_fibonacciFactorialAndHanoiWorkCorrectly() {
        RecursionController rec = new RecursionController();

        // (a) Fibonacci: n = 10 -> 55 across all 3 methods
        FibonacciResponseDto fib10 = rec.fibonacci(new IntInputRequestDto(10)).getBody();
        assertNotNull(fib10);
        assertTrue(fib10.getNaiveRecursion().isExecuted());
        assertEquals(55L, fib10.getNaiveRecursion().getResult());
        assertEquals(55L, fib10.getMemoizedRecursion().getResult());
        assertEquals(55L, fib10.getBottomUpDp().getResult());
        assertEquals(177L, fib10.getNaiveRecursion().getOperationsCount()); // 2*F(11) - 1 = 177 calls

        // Naive skipped when n > 30, while Memoized and Bottom-Up compute F(40) = 102334155
        FibonacciResponseDto fib40 = rec.fibonacci(new IntInputRequestDto(40)).getBody();
        assertNotNull(fib40);
        assertFalse(fib40.getNaiveRecursion().isExecuted());
        assertEquals(102334155L, fib40.getMemoizedRecursion().getResult());
        assertEquals(102334155L, fib40.getBottomUpDp().getResult());

        // (b) Factorial: n = 0 -> 1, n = 6 -> 720, negative n rejected
        FactorialResponseDto fact0 = rec.factorial(new IntInputRequestDto(0)).getBody();
        assertNotNull(fact0);
        assertEquals(1L, fact0.getRecursive().getResult());
        assertEquals(1L, fact0.getIterative().getResult());

        FactorialResponseDto fact6 = rec.factorial(new IntInputRequestDto(6)).getBody();
        assertNotNull(fact6);
        assertEquals(720L, fact6.getRecursive().getResult());
        assertEquals(720L, fact6.getIterative().getResult());

        assertThrows(
            IllegalArgumentException.class,
            () -> rec.factorial(new IntInputRequestDto(-3))
        );

        // (c) Tower of Hanoi: 4 disks -> 2^4 - 1 = 15 moves
        HanoiResponseDto hanoi = rec.hanoi(new HanoiRequestDto(4, "A", "C", "B")).getBody();
        assertNotNull(hanoi);
        assertEquals(15, hanoi.getTotalMoves());
        assertEquals(15, hanoi.getFormulaExpectedMoves());
        assertTrue(hanoi.isMatchesFormula());
        assertArrayEquals(new int[] { 4, 3, 2, 1 }, hanoi.getSteps()[15].getPegC());
    }

    @Test
    void moduleB_fixedStackAndBracketCheckerWorkCorrectly() {
        FixedArrayStack stack = new FixedArrayStack(3);
        assertTrue(stack.isEmpty());
        stack.push(10);
        stack.push(20);
        stack.push(30);
        assertTrue(stack.isFull());
        assertEquals(3, stack.size());
        assertEquals(30, stack.peek());
        assertThrows(IllegalStateException.class, () -> stack.push(40)); // Overflow
        assertEquals(30, stack.pop());
        assertEquals(20, stack.pop());
        assertEquals(10, stack.pop());
        assertTrue(stack.isEmpty());
        assertThrows(IllegalStateException.class, stack::pop); // Underflow

        BracketBalanceChecker checker = new BracketBalanceChecker();
        BracketCheckResponseDto valid = checker.check("{ ( [ ] ) }");
        assertTrue(valid.isBalanced());

        BracketCheckResponseDto invalid = checker.check("{ ( [ ) ] }");
        assertFalse(invalid.isBalanced());
    }

    @Test
    void moduleB_linearQueueFalseFullAndCircularQueueWrapAroundWorkCorrectly() {
        LinearArrayQueue lq = new LinearArrayQueue(3);
        lq.enqueue(10);
        lq.enqueue(20);
        lq.enqueue(30);
        assertTrue(lq.isFull());
        assertEquals(10, lq.dequeue());
        // Now size is 2/3, front = 1, rear = 2 -> False Full!
        assertTrue(lq.isFull());
        assertTrue(lq.isFalseFull());
        assertThrows(IllegalStateException.class, () -> lq.enqueue(40));

        CircularArrayQueue cq = new CircularArrayQueue(3);
        cq.enqueue(10);
        cq.enqueue(20);
        cq.enqueue(30);
        assertEquals(10, cq.dequeue());
        assertFalse(cq.isFull());
        cq.enqueue(40); // Rear wraps around to index 0!
        assertEquals(0, cq.getRearIndex());
        assertEquals(1, cq.getFrontIndex());
        assertArrayEquals(new int[] { 20, 30, 40 }, cq.display());
    }

    @Test
    void moduleB_singlyAndDoublyLinkedListsSupportAllOperations() {
        SinglyLinkedList sll = new SinglyLinkedList();
        sll.insertAtBeginning(20);
        sll.insertAtBeginning(10);
        sll.insertAtEnd(40);
        sll.insertBefore(40, 30);
        sll.insertAfter(40, 50);
        assertArrayEquals(new int[] { 10, 20, 30, 40, 50 }, sll.displayForward());
        assertEquals(2, sll.search(30));
        assertEquals(20, sll.deleteBefore(30));
        assertEquals(40, sll.deleteAfter(30));
        assertEquals(30, sll.deleteElement(30));
        assertEquals(10, sll.deleteAtBeginning());
        assertEquals(50, sll.deleteAtEnd());
        assertEquals(0, sll.size());

        DoublyLinkedList dll = new DoublyLinkedList();
        dll.insertAtBeginning(20);
        dll.insertAtBeginning(10);
        dll.insertAtEnd(40);
        dll.insertBefore(40, 30);
        dll.insertAfter(40, 50);
        assertArrayEquals(new int[] { 10, 20, 30, 40, 50 }, dll.displayForward());
        assertArrayEquals(new int[] { 50, 40, 30, 20, 10 }, dll.displayBackward());
        assertEquals(20, dll.deleteBefore(30));
        assertEquals(40, dll.deleteAfter(30));
        assertEquals(30, dll.deleteElement(30));
        assertEquals(10, dll.deleteAtBeginning());
        assertEquals(50, dll.deleteAtEnd());
        assertEquals(0, dll.size());
    }

    @Test
    void moduleC_allSortingAlgorithmsAndCompareAllWorkCorrectly() {
        AlgorithmController controller = new AlgorithmController();
        int[] sample = { 45, 12, 89, 33, 7, 21, 54, 18 };
        int[] expected = { 7, 12, 18, 21, 33, 45, 54, 89 };

        for (String algo : SORT_ALGORITHMS) {
            SortRequestDto req = new SortRequestDto(sample, algo);
            ResponseEntity<TrackingObjectDto> res = controller.sort(req);
            TrackingObjectDto body = res.getBody();
            assertNotNull(body);
            assertArrayEquals(expected, body.getOutput(), "Failed output for " + algo);
            assertTrue(body.getSteps().length >= 2);
            StepDto lastStep = body.getSteps()[body.getSteps().length - 1];
            assertArrayEquals(expected, lastStep.getArray());

            if ("MERGE_SORT".equals(algo)) {
                assertNotNull(lastStep.getMergeTree());
                assertNotNull(lastStep.getMergeTree().findNode("0-7"));
            }
        }

        ResponseEntity<CompareAllResponseDto> compareRes = controller.compareAll(new SortRequestDto(sample, "QUICK_SORT"));
        assertNotNull(compareRes.getBody());
        assertEquals(6, compareRes.getBody().getResults().length);
    }

    @Test
    void moduleD_benchmarkControllerRunsCorrectly() {
        BenchmarkController bench = new BenchmarkController();
        ResponseEntity<BenchmarkResponseDto> res = bench.runBenchmark(new BenchmarkRequestDto(3, false));
        assertNotNull(res.getBody());
        assertEquals(3, res.getBody().getRepetitions());
        assertEquals(3, res.getBody().getDistributions().length);
    }

    @Test
    void invalidInputsThrowIllegalArgumentException() {
        AlgorithmController controller = new AlgorithmController();
        assertThrows(
            IllegalArgumentException.class,
            () -> controller.sort(new SortRequestDto(new int[] { 5 }, "QUICK_SORT"))
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> controller.sort(new SortRequestDto(new int[] { 5, 2000 }, "QUICK_SORT"))
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> controller.sort(new SortRequestDto(new int[] { 5, 2 }, "BOGOSORT"))
        );
    }
}
