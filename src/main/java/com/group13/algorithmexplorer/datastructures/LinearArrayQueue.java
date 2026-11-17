package com.group13.algorithmexplorer.datastructures;

/**
 * Module B(b)(i) — Linear Queue implemented on a fixed-size array.
 * <p>
 * Maintains {@code front} and {@code rear} indices that only move forward ({@code 0 -> capacity - 1})
 * without wrapping around. Demonstrates the classic <b>"False Full" problem</b>: once {@code rear}
 * reaches {@code capacity - 1}, {@code isFull()} is {@code true} and no further elements can be
 * enqueued even if earlier dequeues freed slots {@code 0 .. front - 1}.
 * </p>
 * <ul>
 *   <li><b>enqueue / dequeue / front / size / is_empty / is_full:</b> O(1) time</li>
 *   <li><b>display:</b> O(n) time</li>
 * </ul>
 */
public class LinearArrayQueue {

    public static final String FALSE_FULL_EXPLANATION =
        "False Full Problem in Linear Queue: Because front and rear only advance rightward without wrapping around, "
        + "once rear reaches capacity - 1, no new element can be enqueued even when front > 0 and earlier slots "
        + "[0 .. front-1] are empty and wasted! A Circular Queue solves this by advancing pointers modulo capacity: "
        + "rear = (rear + 1) % capacity and front = (front + 1) % capacity, allowing rear to wrap around to index 0.";

    private final int capacity;
    private final Integer[] slots;
    private int front;
    private int rear;
    private int count;

    public LinearArrayQueue(int capacity) {
        if (capacity < 1 || capacity > 32) {
            throw new IllegalArgumentException("Queue capacity must be between 1 and 32 (received " + capacity + ").");
        }
        this.capacity = capacity;
        this.slots = new Integer[capacity];
        this.front = 0;
        this.rear = -1;
        this.count = 0;
    }

    /**
     * Adds {@code value} at {@code rear + 1} in O(1) time.
     * Throws {@link IllegalStateException} when {@code rear == capacity - 1}, explicitly distinguishing
     * between a true full queue and the "False Full" condition ({@code front > 0 && count < capacity}).
     */
    public void enqueue(int value) {
        if (isFull()) {
            if (isFalseFull()) {
                throw new IllegalStateException(
                    "FALSE FULL! Cannot enqueue " + value + ": rear has reached index " + rear
                        + " (capacity - 1), even though " + front + " empty slot(s) [0.." + (front - 1)
                        + "] are free at the front (size = " + count + "/" + capacity + "). "
                        + "Use the Circular Queue to wrap rear around to index 0!"
                );
            }
            throw new IllegalStateException(
                "Queue Overflow: Linear Queue is full (capacity = " + capacity + ", rear = " + rear + "). Cannot enqueue " + value + "."
            );
        }
        rear++;
        slots[rear] = value;
        count++;
    }

    /**
     * Removes and returns the front element at {@code slots[front]} in O(1) time.
     */
    public int dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue Underflow: Linear Queue is empty. Cannot dequeue.");
        }
        int val = slots[front];
        slots[front] = null;
        front++;
        count--;
        return val;
    }

    /**
     * Returns (without removing) the front element in O(1) time.
     */
    public int front() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue Underflow: Linear Queue is empty. No front element.");
        }
        return slots[front];
    }

    /** Returns the number of elements currently in the queue in O(1) time. */
    public int size() {
        return count;
    }

    /** Returns {@code true} if the queue contains 0 elements in O(1) time. */
    public boolean isEmpty() {
        return count == 0;
    }

    /**
     * In a fixed-array linear queue without shifting or wrap-around, the queue cannot accept new
     * elements once {@code rear == capacity - 1}.
     */
    public boolean isFull() {
        return rear == capacity - 1;
    }

    /**
     * Returns {@code true} when {@code rear == capacity - 1} (so no more elements can be enqueued)
     * yet {@code count < capacity} because {@code front > 0} slots at the start of the array are empty.
     */
    public boolean isFalseFull() {
        return rear == capacity - 1 && count < capacity;
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
     * Returns the active queue elements from {@code front} to {@code rear} in O(n) time.
     */
    public int[] display() {
        int[] active = new int[count];
        for (int i = 0; i < count; i++) {
            active[i] = slots[front + i];
        }
        return active;
    }

    public Integer[] getSlotsSnapshot() {
        Integer[] copy = new Integer[capacity];
        for (int i = 0; i < capacity; i++) {
            copy[i] = slots[i];
        }
        return copy;
    }
}
