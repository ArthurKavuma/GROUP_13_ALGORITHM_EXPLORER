package com.group13.algorithmexplorer.controller;

import com.group13.algorithmexplorer.algorithms.StepTracker;
import com.group13.algorithmexplorer.datastructures.BracketBalanceChecker;
import com.group13.algorithmexplorer.datastructures.CircularArrayQueue;
import com.group13.algorithmexplorer.datastructures.DoublyLinkedList;
import com.group13.algorithmexplorer.datastructures.FixedArrayStack;
import com.group13.algorithmexplorer.datastructures.LinearArrayQueue;
import com.group13.algorithmexplorer.datastructures.SinglyLinkedList;
import com.group13.algorithmexplorer.dto.DataStructureDtos.BracketCheckRequestDto;
import com.group13.algorithmexplorer.dto.DataStructureDtos.BracketCheckResponseDto;
import com.group13.algorithmexplorer.dto.DataStructureDtos.DsOperationRequestDto;
import com.group13.algorithmexplorer.dto.DataStructureDtos.LinkedListStateResponseDto;
import com.group13.algorithmexplorer.dto.DataStructureDtos.QueueStateResponseDto;
import com.group13.algorithmexplorer.dto.DataStructureDtos.StackStateResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Module B: Data Structures Lab.
 * Provides interactive operations for:
 * <ul>
 *   <li>(a) Array-based fixed-capacity Stack + Bracket Balance Checker application</li>
 *   <li>(b) Fixed-size array Linear Queue (including "False Full" demonstration) and Circular Queue</li>
 *   <li>(c) Singly Linked List and Doubly Linked List</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/ds")
@CrossOrigin(origins = "*")
public class DataStructureController {

    private FixedArrayStack stack = new FixedArrayStack(6);
    private LinearArrayQueue linearQueue = new LinearArrayQueue(5);
    private CircularArrayQueue circularQueue = new CircularArrayQueue(5);
    private final SinglyLinkedList singlyList = new SinglyLinkedList();
    private final DoublyLinkedList doublyList = new DoublyLinkedList();
    private final BracketBalanceChecker bracketChecker = new BracketBalanceChecker();

    public DataStructureController() {
        // Seed initial sample values so the structures are immediately visible on load
        stack.push(10);
        stack.push(25);
        stack.push(42);

        linearQueue.enqueue(15);
        linearQueue.enqueue(30);
        linearQueue.enqueue(45);

        circularQueue.enqueue(15);
        circularQueue.enqueue(30);
        circularQueue.enqueue(45);

        singlyList.insertAtEnd(12);
        singlyList.insertAtEnd(24);
        singlyList.insertAtEnd(36);

        doublyList.insertAtEnd(12);
        doublyList.insertAtEnd(24);
        doublyList.insertAtEnd(36);
    }

    @PostMapping("/stack")
    public synchronized ResponseEntity<StackStateResponseDto> stackOperation(
        @RequestBody DsOperationRequestDto req
    ) {
        String op = normalizeOp(req != null ? req.getOperation() : null);
        long startNs = System.nanoTime();
        boolean success = true;
        String message;
        int ops = 1;

        try {
            switch (op) {
                case "INIT": {
                    int cap = requirePositiveCapacity(req.getCapacity(), "Stack");
                    stack = new FixedArrayStack(cap);
                    message = "Initialized empty Stack with fixed capacity = " + cap + ".";
                    break;
                }
                case "PUSH": {
                    int val = requireValue(req.getValue(), "push");
                    stack.push(val);
                    message = "Pushed " + val + " onto the stack (top = " + stack.getTopIndex() + ").";
                    break;
                }
                case "POP": {
                    int popped = stack.pop();
                    message = "Popped " + popped + " from the stack.";
                    break;
                }
                case "PEEK": {
                    int topVal = stack.peek();
                    message = "Peek: Top element is " + topVal + " at index " + stack.getTopIndex() + ".";
                    break;
                }
                case "SIZE": {
                    message = "Stack size is " + stack.size() + " (capacity = " + stack.getCapacity() + ").";
                    break;
                }
                case "IS_EMPTY": {
                    message = "is_empty() -> " + stack.isEmpty()
                        + (stack.isEmpty() ? " (Stack is empty)" : " (Stack has " + stack.size() + " element(s))");
                    break;
                }
                case "IS_FULL": {
                    message = "is_full() -> " + stack.isFull()
                        + (stack.isFull() ? " (Stack is full)" : " (" + (stack.getCapacity() - stack.size()) + " slot(s) remaining)");
                    break;
                }
                case "DISPLAY":
                case "STATE": {
                    int[] all = stack.display();
                    ops = Math.max(1, all.length);
                    message = all.length == 0
                        ? "Display: Stack is empty []."
                        : "Display (bottom -> top): " + StepTracker.formatIntArray(all);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unsupported stack operation: " + op);
            }
        } catch (IllegalStateException ex) {
            success = false;
            message = ex.getMessage();
        }

        long elapsedNs = System.nanoTime() - startNs;
        return ResponseEntity.ok(new StackStateResponseDto(
            op,
            success,
            message,
            stack.getCapacity(),
            stack.size(),
            stack.getTopIndex(),
            stack.isEmpty(),
            stack.isFull(),
            stack.getSlotsSnapshot(),
            stack.display(),
            ops,
            elapsedNs
        ));
    }

    @PostMapping("/brackets")
    public ResponseEntity<BracketCheckResponseDto> checkBrackets(
        @RequestBody BracketCheckRequestDto req
    ) {
        if (req == null) {
            throw new IllegalArgumentException("Please provide a bracket expression.");
        }
        return ResponseEntity.ok(bracketChecker.check(req.getExpression()));
    }

    @PostMapping("/linear-queue")
    public synchronized ResponseEntity<QueueStateResponseDto> linearQueueOperation(
        @RequestBody DsOperationRequestDto req
    ) {
        String op = normalizeOp(req != null ? req.getOperation() : null);
        long startNs = System.nanoTime();
        boolean success = true;
        String message;
        int ops = 1;

        try {
            switch (op) {
                case "INIT": {
                    int cap = requirePositiveCapacity(req.getCapacity(), "Linear Queue");
                    linearQueue = new LinearArrayQueue(cap);
                    message = "Initialized empty Linear Queue with fixed capacity = " + cap + ".";
                    break;
                }
                case "ENQUEUE": {
                    int val = requireValue(req.getValue(), "enqueue");
                    linearQueue.enqueue(val);
                    message = "Enqueued " + val + " at rear = " + linearQueue.getRearIndex() + ".";
                    break;
                }
                case "DEQUEUE": {
                    int dequeued = linearQueue.dequeue();
                    message = "Dequeued " + dequeued + " (front is now " + linearQueue.getFrontIndex() + ").";
                    break;
                }
                case "FRONT": {
                    int frontVal = linearQueue.front();
                    message = "Front element is " + frontVal + " at index " + linearQueue.getFrontIndex() + ".";
                    break;
                }
                case "SIZE": {
                    message = "Linear Queue size is " + linearQueue.size() + " (capacity = " + linearQueue.getCapacity() + ").";
                    break;
                }
                case "IS_EMPTY": {
                    message = "is_empty() -> " + linearQueue.isEmpty();
                    break;
                }
                case "IS_FULL": {
                    if (linearQueue.isFalseFull()) {
                        message = "is_full() -> true (FALSE FULL: rear = " + linearQueue.getRearIndex()
                            + " is at capacity - 1, even though " + linearQueue.getFrontIndex()
                            + " front slot(s) are empty!).";
                    } else {
                        message = "is_full() -> " + linearQueue.isFull();
                    }
                    break;
                }
                case "DEMO_FALSE_FULL": {
                    int cap = Math.max(4, linearQueue.getCapacity());
                    linearQueue = new LinearArrayQueue(cap);
                    for (int i = 1; i <= cap; i++) {
                        linearQueue.enqueue(i * 10);
                    }
                    linearQueue.dequeue();
                    linearQueue.dequeue();
                    ops = cap + 2;
                    message = "Demonstrated 'False Full': Enqueued " + cap
                        + " items to fill indices [0.." + (cap - 1) + "], then dequeued 2 items. "
                        + "Now slots [0..1] are empty (size = " + linearQueue.size() + "/" + cap
                        + "), but rear = " + linearQueue.getRearIndex()
                        + " is stuck at the end! Try clicking Enqueue now.";
                    break;
                }
                case "DISPLAY":
                case "STATE": {
                    int[] all = linearQueue.display();
                    ops = Math.max(1, all.length);
                    message = all.length == 0
                        ? "Display: Linear Queue is empty []."
                        : "Display (front -> rear): " + StepTracker.formatIntArray(all);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unsupported linear queue operation: " + op);
            }
        } catch (IllegalStateException ex) {
            success = false;
            message = ex.getMessage();
        }

        long elapsedNs = System.nanoTime() - startNs;
        return ResponseEntity.ok(new QueueStateResponseDto(
            "LINEAR_QUEUE",
            op,
            success,
            message,
            linearQueue.getCapacity(),
            linearQueue.size(),
            linearQueue.getFrontIndex(),
            linearQueue.getRearIndex(),
            linearQueue.isEmpty(),
            linearQueue.isFull(),
            linearQueue.isFalseFull(),
            LinearArrayQueue.FALSE_FULL_EXPLANATION,
            linearQueue.getSlotsSnapshot(),
            linearQueue.display(),
            ops,
            elapsedNs
        ));
    }

    @PostMapping("/circular-queue")
    public synchronized ResponseEntity<QueueStateResponseDto> circularQueueOperation(
        @RequestBody DsOperationRequestDto req
    ) {
        String op = normalizeOp(req != null ? req.getOperation() : null);
        long startNs = System.nanoTime();
        boolean success = true;
        String message;
        int ops = 1;

        try {
            switch (op) {
                case "INIT": {
                    int cap = requirePositiveCapacity(req.getCapacity(), "Circular Queue");
                    circularQueue = new CircularArrayQueue(cap);
                    message = "Initialized empty Circular Queue with fixed capacity = " + cap + ".";
                    break;
                }
                case "ENQUEUE": {
                    int val = requireValue(req.getValue(), "enqueue");
                    int prevRear = circularQueue.getRearIndex();
                    circularQueue.enqueue(val);
                    int newRear = circularQueue.getRearIndex();
                    boolean wrapped = prevRear == circularQueue.getCapacity() - 1 && newRear == 0;
                    message = wrapped
                        ? "Enqueued " + val + ": rear wrapped around from " + prevRear + " to 0! ((rear + 1) % "
                            + circularQueue.getCapacity() + " = 0)"
                        : "Enqueued " + val + " at rear = " + newRear + " (front = " + circularQueue.getFrontIndex() + ").";
                    break;
                }
                case "DEQUEUE": {
                    int dequeued = circularQueue.dequeue();
                    message = "Dequeued " + dequeued + " (front = " + circularQueue.getFrontIndex()
                        + ", rear = " + circularQueue.getRearIndex() + ").";
                    break;
                }
                case "FRONT": {
                    int frontVal = circularQueue.front();
                    message = "Front element is " + frontVal + " at index " + circularQueue.getFrontIndex() + ".";
                    break;
                }
                case "SIZE": {
                    message = "Circular Queue size is " + circularQueue.size()
                        + " (capacity = " + circularQueue.getCapacity() + ").";
                    break;
                }
                case "IS_EMPTY": {
                    message = "is_empty() -> " + circularQueue.isEmpty();
                    break;
                }
                case "IS_FULL": {
                    message = "is_full() -> " + circularQueue.isFull();
                    break;
                }
                case "DEMO_WRAP_AROUND": {
                    int cap = Math.max(4, circularQueue.getCapacity());
                    circularQueue = new CircularArrayQueue(cap);
                    for (int i = 1; i <= cap; i++) {
                        circularQueue.enqueue(i * 10);
                    }
                    circularQueue.dequeue();
                    circularQueue.dequeue();
                    circularQueue.enqueue(99);
                    ops = cap + 3;
                    message = "Wrap-Around Demonstrated: Filled " + cap
                        + " slots, dequeued 2 from front (moving front to 2), and enqueued 99 -> rear wrapped around to index 0!";
                    break;
                }
                case "DISPLAY":
                case "STATE": {
                    int[] all = circularQueue.display();
                    ops = Math.max(1, all.length);
                    message = all.length == 0
                        ? "Display: Circular Queue is empty []."
                        : "Display (FIFO from front=" + circularQueue.getFrontIndex()
                            + " to rear=" + circularQueue.getRearIndex() + "): "
                            + StepTracker.formatIntArray(all);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unsupported circular queue operation: " + op);
            }
        } catch (IllegalStateException ex) {
            success = false;
            message = ex.getMessage();
        }

        long elapsedNs = System.nanoTime() - startNs;
        return ResponseEntity.ok(new QueueStateResponseDto(
            "CIRCULAR_QUEUE",
            op,
            success,
            message,
            circularQueue.getCapacity(),
            circularQueue.size(),
            circularQueue.getFrontIndex(),
            circularQueue.getRearIndex(),
            circularQueue.isEmpty(),
            circularQueue.isFull(),
            false,
            LinearArrayQueue.FALSE_FULL_EXPLANATION,
            circularQueue.getSlotsSnapshot(),
            circularQueue.display(),
            ops,
            elapsedNs
        ));
    }

    @PostMapping("/singly-list")
    public synchronized ResponseEntity<LinkedListStateResponseDto> singlyListOperation(
        @RequestBody DsOperationRequestDto req
    ) {
        String op = normalizeOp(req != null ? req.getOperation() : null);
        long startNs = System.nanoTime();
        boolean success = true;
        String message;
        int highlightedIdx = -1;
        int ops = 1;

        try {
            switch (op) {
                case "CLEAR": {
                    singlyList.clear();
                    message = "Cleared Singly Linked List.";
                    break;
                }
                case "INSERT_BEGINNING": {
                    int val = requireValue(req.getValue(), "insert at beginning");
                    singlyList.insertAtBeginning(val);
                    highlightedIdx = 0;
                    message = "Inserted " + val + " at the beginning (new head).";
                    break;
                }
                case "INSERT_END": {
                    int val = requireValue(req.getValue(), "insert at end");
                    ops = Math.max(1, singlyList.size());
                    singlyList.insertAtEnd(val);
                    highlightedIdx = singlyList.size() - 1;
                    message = "Inserted " + val + " at the end (index " + highlightedIdx + ").";
                    break;
                }
                case "INSERT_BEFORE": {
                    int val = requireValue(req.getValue(), "insert before");
                    int target = requireTarget(req.getTarget(), "insert before");
                    singlyList.insertBefore(target, val);
                    highlightedIdx = singlyList.search(val);
                    ops = Math.max(1, highlightedIdx + 1);
                    message = "Inserted " + val + " before element " + target + ".";
                    break;
                }
                case "INSERT_AFTER": {
                    int val = requireValue(req.getValue(), "insert after");
                    int target = requireTarget(req.getTarget(), "insert after");
                    singlyList.insertAfter(target, val);
                    highlightedIdx = singlyList.search(val);
                    ops = Math.max(1, highlightedIdx + 1);
                    message = "Inserted " + val + " after element " + target + ".";
                    break;
                }
                case "DELETE_BEGINNING": {
                    int removed = singlyList.deleteAtBeginning();
                    message = "Deleted " + removed + " from the beginning (head).";
                    break;
                }
                case "DELETE_END": {
                    ops = Math.max(1, singlyList.size());
                    int removed = singlyList.deleteAtEnd();
                    message = "Deleted " + removed + " from the end.";
                    break;
                }
                case "DELETE_BEFORE": {
                    int target = requireTarget(req.getTarget(), "delete before");
                    ops = Math.max(1, singlyList.size());
                    int removed = singlyList.deleteBefore(target);
                    message = "Deleted " + removed + " (which was before " + target + ").";
                    break;
                }
                case "DELETE_AFTER": {
                    int target = requireTarget(req.getTarget(), "delete after");
                    ops = Math.max(1, singlyList.size());
                    int removed = singlyList.deleteAfter(target);
                    message = "Deleted " + removed + " (which was after " + target + ").";
                    break;
                }
                case "DELETE_ELEMENT": {
                    int target = requireTarget(req.getTarget(), "delete element");
                    ops = Math.max(1, singlyList.size());
                    int removed = singlyList.deleteElement(target);
                    message = "Deleted element " + removed + " from the singly linked list.";
                    break;
                }
                case "SEARCH": {
                    int target = requireTarget(req.getTarget() != null ? req.getTarget() : req.getValue(), "search");
                    int idx = singlyList.search(target);
                    highlightedIdx = idx;
                    ops = idx >= 0 ? idx + 1 : Math.max(1, singlyList.size());
                    message = idx >= 0
                        ? "Found element " + target + " at index " + idx + " (" + ops + " comparison(s))."
                        : "Element " + target + " not found in the singly linked list (" + ops + " comparison(s)).";
                    break;
                }
                case "SIZE": {
                    message = "Singly Linked List size is " + singlyList.size() + " node(s).";
                    break;
                }
                case "DISPLAY_FORWARD":
                case "STATE": {
                    int[] fwd = singlyList.displayForward();
                    ops = Math.max(1, fwd.length);
                    message = fwd.length == 0
                        ? "Display Forward: List is empty (HEAD -> NULL)."
                        : "Display Forward: " + formatArrowList(fwd, " -> ") + " -> NULL";
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unsupported singly linked list operation: " + op);
            }
        } catch (IllegalStateException | IllegalArgumentException ex) {
            success = false;
            message = ex.getMessage();
        }

        long elapsedNs = System.nanoTime() - startNs;
        return ResponseEntity.ok(new LinkedListStateResponseDto(
            "SINGLY_LINKED_LIST",
            op,
            success,
            message,
            singlyList.size(),
            highlightedIdx,
            singlyList.displayForward(),
            new int[0],
            ops,
            elapsedNs
        ));
    }

    @PostMapping("/doubly-list")
    public synchronized ResponseEntity<LinkedListStateResponseDto> doublyListOperation(
        @RequestBody DsOperationRequestDto req
    ) {
        String op = normalizeOp(req != null ? req.getOperation() : null);
        long startNs = System.nanoTime();
        boolean success = true;
        String message;
        int highlightedIdx = -1;
        int ops = 1;

        try {
            switch (op) {
                case "CLEAR": {
                    doublyList.clear();
                    message = "Cleared Doubly Linked List.";
                    break;
                }
                case "INSERT_BEGINNING": {
                    int val = requireValue(req.getValue(), "insert at beginning");
                    doublyList.insertAtBeginning(val);
                    highlightedIdx = 0;
                    message = "Inserted " + val + " at the beginning (new head).";
                    break;
                }
                case "INSERT_END": {
                    int val = requireValue(req.getValue(), "insert at end");
                    doublyList.insertAtEnd(val);
                    highlightedIdx = doublyList.size() - 1;
                    message = "Inserted " + val + " at the end (new tail, index " + highlightedIdx + ").";
                    break;
                }
                case "INSERT_BEFORE": {
                    int val = requireValue(req.getValue(), "insert before");
                    int target = requireTarget(req.getTarget(), "insert before");
                    doublyList.insertBefore(target, val);
                    highlightedIdx = doublyList.search(val);
                    ops = Math.max(1, highlightedIdx + 1);
                    message = "Inserted " + val + " before element " + target + ".";
                    break;
                }
                case "INSERT_AFTER": {
                    int val = requireValue(req.getValue(), "insert after");
                    int target = requireTarget(req.getTarget(), "insert after");
                    doublyList.insertAfter(target, val);
                    highlightedIdx = doublyList.search(val);
                    ops = Math.max(1, highlightedIdx + 1);
                    message = "Inserted " + val + " after element " + target + ".";
                    break;
                }
                case "DELETE_BEGINNING": {
                    int removed = doublyList.deleteAtBeginning();
                    message = "Deleted " + removed + " from the beginning (head).";
                    break;
                }
                case "DELETE_END": {
                    int removed = doublyList.deleteAtEnd();
                    message = "Deleted " + removed + " from the end (tail).";
                    break;
                }
                case "DELETE_BEFORE": {
                    int target = requireTarget(req.getTarget(), "delete before");
                    ops = Math.max(1, doublyList.size());
                    int removed = doublyList.deleteBefore(target);
                    message = "Deleted " + removed + " (which was before " + target + ").";
                    break;
                }
                case "DELETE_AFTER": {
                    int target = requireTarget(req.getTarget(), "delete after");
                    ops = Math.max(1, doublyList.size());
                    int removed = doublyList.deleteAfter(target);
                    message = "Deleted " + removed + " (which was after " + target + ").";
                    break;
                }
                case "DELETE_ELEMENT": {
                    int target = requireTarget(req.getTarget(), "delete element");
                    ops = Math.max(1, doublyList.size());
                    int removed = doublyList.deleteElement(target);
                    message = "Deleted element " + removed + " from the doubly linked list.";
                    break;
                }
                case "SEARCH": {
                    int target = requireTarget(req.getTarget() != null ? req.getTarget() : req.getValue(), "search");
                    int idx = doublyList.search(target);
                    highlightedIdx = idx;
                    ops = idx >= 0 ? idx + 1 : Math.max(1, doublyList.size());
                    message = idx >= 0
                        ? "Found element " + target + " at index " + idx + " (" + ops + " comparison(s))."
                        : "Element " + target + " not found in the doubly linked list (" + ops + " comparison(s)).";
                    break;
                }
                case "SIZE": {
                    message = "Doubly Linked List size is " + doublyList.size() + " node(s).";
                    break;
                }
                case "DISPLAY_FORWARD":
                case "STATE": {
                    int[] fwd = doublyList.displayForward();
                    ops = Math.max(1, fwd.length);
                    message = fwd.length == 0
                        ? "Display Forward: List is empty (NULL)."
                        : "Display Forward (head -> tail): NULL <- " + formatArrowList(fwd, " <-> ") + " -> NULL";
                    break;
                }
                case "DISPLAY_BACKWARD": {
                    int[] bwd = doublyList.displayBackward();
                    ops = Math.max(1, bwd.length);
                    message = bwd.length == 0
                        ? "Display Backward: List is empty (NULL)."
                        : "Display Backward (tail -> head via prev): NULL <- " + formatArrowList(bwd, " <-> ") + " -> NULL";
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unsupported doubly linked list operation: " + op);
            }
        } catch (IllegalStateException | IllegalArgumentException ex) {
            success = false;
            message = ex.getMessage();
        }

        long elapsedNs = System.nanoTime() - startNs;
        return ResponseEntity.ok(new LinkedListStateResponseDto(
            "DOUBLY_LINKED_LIST",
            op,
            success,
            message,
            doublyList.size(),
            highlightedIdx,
            doublyList.displayForward(),
            doublyList.displayBackward(),
            ops,
            elapsedNs
        ));
    }

    private static String normalizeOp(String op) {
        if (op == null || op.trim().isEmpty()) {
            return "STATE";
        }
        return op.trim().toUpperCase();
    }

    private static int requireValue(Integer val, String opName) {
        if (val == null) {
            throw new IllegalArgumentException("Please enter a valid numeric value for '" + opName + "'.");
        }
        if (val < -9999 || val > 9999) {
            throw new IllegalArgumentException("Value must be between -9999 and 9999.");
        }
        return val;
    }

    private static int requireTarget(Integer target, String opName) {
        if (target == null) {
            throw new IllegalArgumentException("Please enter a target element value for '" + opName + "'.");
        }
        return target;
    }

    private static int requirePositiveCapacity(Integer cap, String structName) {
        if (cap == null || cap < 1 || cap > 20) {
            throw new IllegalArgumentException(structName + " capacity must be between 1 and 20.");
        }
        return cap;
    }

    private static String formatArrowList(int[] values, String sep) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                sb.append(sep);
            }
            sb.append("[").append(values[i]).append("]");
        }
        return sb.toString();
    }
}
