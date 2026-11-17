package com.group13.algorithmexplorer.datastructures;

/**
 * Module B(b)(ii) — Circular Queue implemented on a fixed-size array.
 * <p>
 * Solves the linear queue's "False Full" problem by advancing {@code rear} and {@code front}
 * modulo {@code capacity}:
 * {@code rear = (rear + 1) % capacity} and {@code front = (front + 1) % capacity}.
 * </p>
 * <ul>
 *   <li><b>enqueue / dequeue / front / size / is_empty / is_full:</b> O(1) time</li>
 *   <li><b>display:</b> O(n) time</li>
 * </ul>
 */
public class CircularArrayQueue {

    private final int capacity;
    private final Integer[] slots;
    private int front;
    private int rear;
    private int count;

    public CircularArrayQueue(int capacity) {
        if (capacity < 1 || capacity > 32) {
            throw new IllegalArgumentException("Circular Queue capacity must be between 1 and 32 (received " + capacity + ").");
        }
        this.capacity = capacity;
        this.slots = new Integer[capacity];
        this.front = 0;
        this.rear = -1;
        this.count = 0;
    }

    /**
     * Adds {@code value} at {@code (rear + 1) % capacity} in O(1) time.
     * Wraps {@code rear} around to index {@code 0} when space is available at the start of the array.
     */
    public void enqueue(int value) {
        if (isFull()) {
            throw new IllegalStateException(
                "Queue Overflow: Circular Queue is genuinely full (size = " + count + "/" + capacity
                    + ", front = " + front + ", rear = " + rear + "). Cannot enqueue " + value + "."
            );
        }
        if (count == 0) {
            front = 0;
            rear = 0;
        } else {
            rear = (rear + 1) % capacity;
        }
        slots[rear] = value;
        count++;
    }

    /**
     * Removes and returns the element at {@code front} in O(1) time, advancing
     * {@code front = (front + 1) % capacity}.
     */
    public int dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue Underflow: Circular Queue is empty. Cannot dequeue.");
        }
        int val = slots[front];
        slots[front] = null;
        count--;
        if (count == 0) {
            front = 0;
            rear = -1;
        } else {
            front = (front + 1) % capacity;
        }
        return val;
    }

    /**
     * Returns (without removing) the element at {@code front} in O(1) time.
     */
    public int front() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue Underflow: Circular Queue is empty. No front element.");
        }
        return slots[front];
    }

    /** Returns the number of elements currently in the circular queue in O(1) time. */
    public int size() {
        return count;
    }

    /** Returns {@code true} if the circular queue is empty in O(1) time. */
    public boolean isEmpty() {
        return count == 0;
    }

    /** Returns {@code true} only when all {@code capacity} slots are occupied in O(1) time. */
    public boolean isFull() {
        return count == capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getFrontIndex() {
        return front;
    }

    public int getRearIndex() {
        return rear;
    }

    /**
     * Returns the elements in logical FIFO order starting from {@code front} and wrapping around via modulo.
     * Time Complexity: O(n).
     */
    public int[] display() {
        int[] ordered = new int[count];
        for (int i = 0; i < count; i++) {
            int idx = (front + i) % capacity;
            ordered[i] = slots[idx];
        }
        return ordered;
    }

    public Integer[] getSlotsSnapshot() {
        Integer[] copy = new Integer[capacity];
        for (int i = 0; i < capacity; i++) {
            copy[i] = slots[i];
        }
        return copy;
    }
}
