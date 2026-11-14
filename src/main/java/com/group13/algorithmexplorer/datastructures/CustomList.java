package com.group13.algorithmexplorer.datastructures; 
/** 
* Custom indexed list contract for the Algorithm Explorer engine. 
* Strictly avoids any built-in JDK List or Collection usage. 
* 
* @param <T> element type 
*/ 
public interface CustomList<T> { 
/** Appends an element to the end of this list. */ 
void add(T element); 
/** Returns the element at the specified 0-based index. */ 
T get(int index); 
/** Replaces the element at the specified 0-based index. */ 
void set(int index, T element); 
/** Returns the number of elements currently stored. */ 
int size(); 
/** Removes all elements from this list. */ 
void clear(); 
/** Exports the current elements as a primitive int[] snapshot. */ 
int[] toIntArray();
}