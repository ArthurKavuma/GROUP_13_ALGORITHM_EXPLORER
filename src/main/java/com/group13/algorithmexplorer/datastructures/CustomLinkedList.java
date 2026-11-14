package com.group13.algorithmexplorer.datastructures;

/** 
* Doubly-linked list implementation of {@link CustomList} backed by {@link Node} 
pointers. 
* Supports bidirectional traversal from head or tail depending on index position. 
* 
* @param <T> element type 
*/ 
public class CustomLinkedList<T> implements CustomList<T> { 
    private Node<T> head; 
    private Node<T> tail; 
    private int size; 

    public CustomLinkedList() { 
        this.head = null; 
        this.tail = null; 
        this.size = 0; 
    } 

    @Override 
    public void add(T element) {
        Node<T> newNode = new Node<>(element, tail, null); 
        if (tail == null) { 
            head = newNode; 
            tail = newNode; 
        } else { 
            tail.setNext(newNode); 
            tail = newNode; 
        } 
        size++; 
    } 

    @Override 
    public T get(int index) { 
        return nodeAt(index).getData(); 
    } 

    @Override 
    public void set(int index, T element) { 
        nodeAt(index).setData(element); 
    } 

    @Override 
    public int size() { 
        return size; 
    } 

    @Override 
    public void clear() { 
        Node<T> current = head; 
        while (current != null) { 
            Node<T> next = current.getNext(); 
            current.setPrev(null); 
            current.setNext(null); 
            current.setData(null); 
            current = next; 
        } 
        head = null; 
        tail = null; 
        size = 0; 
    } 

    @Override 
    public int[] toIntArray() { 
        int[] result = new int[size]; 
        Node<T> current = head; 
        int i = 0; 
        while (current != null && i < size) { 
            T val = current.getData();
            result[i++] = val instanceof Number ? ((Number) val).intValue() : 0; 
            current = current.getNext(); 
        } 
        return result; 
    } 

    private Node<T> nodeAt(int index) { 
        if (index < 0 || index >= size) { 
            throw new IndexOutOfBoundsException( 
            "CustomLinkedList index " + index + " out of bounds for size " + 
            size 
            ); 
        } 
        if (index < (size >> 1)) { 
            Node<T> curr = head; 
            for (int i = 0; i < index; i++) { 
            curr = curr.getNext(); 
            } 
            return curr; 
        } else { 
            Node<T> curr = tail; 
            for (int i = size - 1; i > index; i--) { 
            curr = curr.getPrev(); 
            } 
            return curr;
        }
    }
}
