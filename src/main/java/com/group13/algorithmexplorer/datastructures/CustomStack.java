package com.group13.algorithmexplorer.datastructures;

/** 
* LIFO (Last-In, First-Out) stack backed by linked {@link Node} instances. 
* Used both for CUSTOM_STACK data loading/manipulation and for StepTracker 
undo-state history. 
* 
* @param <T> element type 
*/ 
public class CustomStack<T> { 
    private Node<T> top; 
    private int size; 
    public CustomStack() { 
        this.top = null; 
        this.size = 0; 
    } 

    /** Pushes an element onto the top of this stack in O(1) time. */ 
    public void push(T element) { 
        Node<T> newNode = new Node<>(element); 
        newNode.setNext(top); 
        top = newNode; 
        size++; 
    } 

    /** Removes and returns the top element of this stack in O(1) time. */ 
    public T pop() { 
        if (isEmpty()) { 
            throw new IllegalStateException("Cannot pop from an empty CustomStack."); 
        } 
        T value = top.getData(); 
        Node<T> next = top.getNext(); 
        top.setNext(null); 
        top = next; 
        size--; 
        return value; 
    } 

    /** Returns (without removing) the top element of this stack. */ 
    public T peek() { 
        if (isEmpty()) { 
            throw new IllegalStateException("Cannot peek an empty CustomStack."); 
        } 
        return top.getData(); 
    } 
    public boolean isEmpty() { 
        return size == 0; 
    } 
    public int size() { 
        return size; 
    } 
    public void clear() { 
        while (!isEmpty()) { 
            pop(); 
        } 
    } 
} 
