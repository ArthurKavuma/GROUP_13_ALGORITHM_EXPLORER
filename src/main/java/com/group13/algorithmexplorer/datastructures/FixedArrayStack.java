package com.group13.algorithmexplorer.datastructures;

/**
 * Module B(a) — Array-based LIFO Stack with a fixed capacity set by the user.
 * <p>
 * Built from first principles on a fixed-size {@code int[]} array accessed by index {@code top}.
 * Supports {@code push}, {@code pop}, {@code peek}, {@code size}, {@code is_empty}, {@code is_full},
 * and {@code display}, with explicit Stack Overflow and Stack Underflow messages.
 * </p>
 * <ul>
 *   <li><b>push / pop / peek / size / is_empty / is_full:</b> O(1) time, O(1) extra space</li>
 *   <li><b>display:</b> O(n) time</li>
 * </ul>
 */
public class FixedArrayStack {

    private final int capacity;
    private final int[] array;
    private int top;

    public FixedArrayStack(int capacity) {
        if (capacity < 1 || capacity > 32) {
            throw new IllegalArgumentException("Stack capacity must be between 1 and 32 (received " + capacity + ").");
        }
        this.capacity = capacity;
        this.array = new int[capacity];
        this.top = -1;
    }

    /**
     * Pushes {@code value} onto the top of the stack in O(1) time.
     * Throws {@link IllegalStateException} on Stack Overflow when {@code isFull()} is true.
     */
    public void push(int value) {
        if (isFull()) {
            throw new IllegalStateException(
                "Stack Overflow: Stack is full (capacity = " + capacity + ", top = " + top + "). Cannot push " + value + "."
            );
        }
        array[++top] = value;
    }

    /**
     * Removes and returns the top element of the stack in O(1) time.
     * Throws {@link IllegalStateException} on Stack Underflow when {@code isEmpty()} is true.
     */
    public int pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack Underflow: Stack is empty (top = -1). Cannot pop.");
        }
        return array[top--];
    }

    /**
     * Returns (without removing) the top element of the stack in O(1) time.
     * Throws {@link IllegalStateException} when {@code isEmpty()} is true.
     */
    public int peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack Underflow: Stack is empty (top = -1). Nothing to peek.");
        }
        return array[top];
    }

    /** Returns the number of elements currently in the stack in O(1) time. */
    public int size() {
        return top + 1;
    }

    /** Returns {@code true} if the stack contains no elements (O(1) time). */
    public boolean isEmpty() {
        return top == -1;
    }

    /** Returns {@code true} if the stack has reached its fixed capacity (O(1) time). */
    public boolean isFull() {
        return top == capacity - 1;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getTopIndex() {
        return top;
    }

    /**
     * Returns all elements currently in the stack from bottom (index 0) to top (index {@code top}).
     * Time Complexity: O(n).
     */
    public int[] display() {
        int count = top + 1;
        int[] elements = new int[count];
        for (int i = 0; i < count; i++) {
            elements[i] = array[i];
        }
        return elements;
    }

    /**
     * Returns a snapshot of all fixed array slots (with {@code null} for unoccupied slots above {@code top}).
     */
    public Integer[] getSlotsSnapshot() {
        Integer[] slots = new Integer[capacity];
        for (int i = 0; i <= top; i++) {
            slots[i] = array[i];
        }
        return slots;
    }
}
