package com.group13.algorithmexplorer.datastructures;

/**
* {@link CustomList} adapter backed by two {@link CustomStack} instances (primary
+ auxiliary).
* Demonstrates loading and manipulating elements exclusively through LIFO {@code
push}/{@code pop}
* operations when the user selects "Custom Stack" in the UI.
*
* @param <T> element type
*/

public class StackBackedList<T> implements CustomList<T> {
    private final CustomStack<T> primaryStack;
 private final CustomStack<T> auxStack;
 public StackBackedList() {
 this.primaryStack = new CustomStack<>();
 this.auxStack = new CustomStack<>();
 }
 @Override
 public void add(T element) {
 primaryStack.push(element);
 }
 @Override
 public T get(int index) {
 checkIndex(index);
 int popsNeeded = primaryStack.size() - 1 - index;
 for (int i = 0; i < popsNeeded; i++) {
 auxStack.push(primaryStack.pop());
 }
 T target = primaryStack.peek();
 while (!auxStack.isEmpty()) {
 primaryStack.push(auxStack.pop());
 }
 return target;
 }
 @Override
 public void set(int index, T element) {
 checkIndex(index);
 int popsNeeded = primaryStack.size() - 1 - index;
 for (int i = 0; i < popsNeeded; i++) {
 auxStack.push(primaryStack.pop());
 }
 primaryStack.pop();
 primaryStack.push(element);
 while (!auxStack.isEmpty()) {
 primaryStack.push(auxStack.pop());
 }
 }
 @Override
 public int size() {
 return primaryStack.size();
 }
 @Override
 public void clear() {
 primaryStack.clear();
 auxStack.clear();
 }
 @Override
 public int[] toIntArray() {
 int n = primaryStack.size();
 int[] result = new int[n];
 for (int i = n - 1; i >= 0; i--) {
 T val = primaryStack.pop();
 auxStack.push(val);
 result[i] = val instanceof Number ? ((Number) val).intValue() : 0;
 }
 while (!auxStack.isEmpty()) {
 primaryStack.push(auxStack.pop());
 }
 return result;
 }
Java
 private void checkIndex(int index) {
 if (index < 0 || index >= primaryStack.size()) {
 throw new IndexOutOfBoundsException(
 "StackBackedList index " + index + " out of bounds for size " +
primaryStack.size()
 );
 }
 }
}

