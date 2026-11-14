package com.group13.algorithmexplorer.datastructures;

/** 
* Dynamic array implementation of {@link CustomList} backed by a raw {@code 
Object[]} buffer 
* that manually doubles in capacity when full. 
* 
* @param <T> element type 
*/ 
public class CustomArrayList<T> implements CustomList<T> { 
    private static final int DEFAULT_CAPACITY = 8; 
    private Object[] elements; 
    private int size; 

    public CustomArrayList() { 
        this(DEFAULT_CAPACITY); 
    } 

    public CustomArrayList(int initialCapacity) { 
        int cap = initialCapacity > 0 ? initialCapacity : DEFAULT_CAPACITY; 
        this.elements = new Object[cap]; 
        this.size = 0; 
    } 

    @Override 
    public void add(T element) { 
        if (size == elements.length) { 
        grow(); 
        } 
        elements[size++] = element; 
    } 

    @Override 
    @SuppressWarnings("unchecked") 
    public T get(int index) { 
        checkIndex(index); 
        return (T) elements[index]; 
    } 

    @Override 
    public void set(int index, T element) { 
        checkIndex(index); 
        elements[index] = element; 
    } 

    @Override 
    public int size() { 
        return size; 
    } 

    @Override 
    public void clear() { 
        for (int i = 0; i < size; i++) { 
            elements[i] = null; 
        } 
        size = 0; 
    } 

    @Override 
    public int[] toIntArray() { 
        int[] copy = new int[size]; 
        for (int i = 0; i < size; i++) { 
            Object val = elements[i]; 
            copy[i] = val instanceof Number ? ((Number) val).intValue() : 0; 
        } 
        return copy; 
    } 
    /** Manually doubles the internal buffer capacity without using built-in array 
    copy utilities. */ 

    private void grow() { 
        int newCapacity = elements.length == 0 ? DEFAULT_CAPACITY : 
        elements.length * 2; 
        Object[] expanded = new Object[newCapacity];

        for (int i = 0; i < size; i++) { 
            expanded[i] = elements[i]; 
        } 
        elements = expanded; 
    } 
    private void checkIndex(int index) { 
        if (index < 0 || index >= size) { 
        throw new IndexOutOfBoundsException( 
        "CustomArrayList index " + index + " out of bounds for size " + 
        size);
        }
    }
}
