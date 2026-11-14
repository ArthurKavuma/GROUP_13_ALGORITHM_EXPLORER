package com.group13.algorithmexplorer.datastructures; 
/** 
* Generic doubly-linked node used by {@link CustomLinkedList}, {@link 
CustomStack}, and {@link CustomQueue}. 
* Built from scratch with zero standard library collection dependencies. 
* 
* @param <T> payload type stored in the node 
*/ 
public class Node<T> { 
    private T data; 
    private Node<T> next; 
    private Node<T> prev;

    public Node(T data) { 
        this.data = data; 
        this.next = null; 
        this.prev = null; 
    } 

    public Node(T data, Node<T> prev, Node<T> next) { 
        this.data = data; 
        this.prev = prev; 
        this.next = next; 
    } 

    public T getData() { 
        return data; 
    } 

    public void setData(T data) { 
        this.data = data; 
    } 

    public Node<T> getNext() { 
        return next; 
    } 

    public void setNext(Node<T> next) { 
        this.next = next; 
    } 

    public Node<T> getPrev() { 
        return prev; 
    } 

    public void setPrev(Node<T> prev) { 
        this.prev = prev; 
    } 
}
