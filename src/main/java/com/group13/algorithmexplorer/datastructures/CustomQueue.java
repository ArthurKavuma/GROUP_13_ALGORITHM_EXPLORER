package com.group13.algorithmexplorer.datastructures;

/** 
* FIFO (First-In, First-Out) queue backed by head and tail {@link Node} pointers. 
* Used both for CUSTOM_QUEUE data ingestion and by StepTracker to collect 
execution steps in order. 
* 
* @param <T> element type 
*/ 
public class CustomQueue<T> { 
    private Node<T> head; 
    private Node<T> tail; 
    private int size; 
    public CustomQueue() { 
        this.head = null; 
        this.tail = null; 
        this.size = 0; 
    } 

    /** Adds an element to the back (tail) of this queue in O(1) time. */ 
    public void enqueue(T element) { 
        Node<T> newNode = new Node<>(element); 
        if (tail == null) { 
            head = newNode; 
            tail = newNode; 
        } else { 
            tail.setNext(newNode); 
            tail = newNode; 
        } 
        size++; 
    } 

    /** Removes and returns the front (head) element of this queue in O(1) time. 
    */ 
    public T dequeue() { 
        if (isEmpty()) { 
            throw new IllegalStateException("Cannot dequeue from an empty CustomQueue."); 
        } 
        T value = head.getData(); 
        Node<T> next = head.getNext(); 
        head.setNext(null); 
        head = next; 
        if (head == null) { 
            tail = null; 
        } 
        size--; 
        return value; 
    } 
    
    /** Returns (without removing) the front element of this queue. */ 
    public T peek() { 
        if (isEmpty()) { 
            throw new IllegalStateException("Cannot peek an empty CustomQueue."); 
        } 
        return head.getData(); 
    } 
    public boolean isEmpty() { 
        return size == 0; 
    } 
    public int size() { 
        return size; 
    } 
    public void clear() { 
        while (!isEmpty()) { 
            dequeue(); 
        } 
    } 
} 
