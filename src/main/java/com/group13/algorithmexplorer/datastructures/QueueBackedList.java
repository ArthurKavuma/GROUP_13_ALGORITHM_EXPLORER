package com.group13.algorithmexplorer.datastructures;
/**
* {@link CustomList} adapter backed by a {@link CustomQueue}.
* Demonstrates loading and manipulating elements exclusively through FIFO
* {@code enqueue}/{@code dequeue} rotations when the user selects "Custom Queue"
in the UI.
*
* @param <T> element type
*/

public class QueueBackedList<T> implements CustomList<T> {
    private final CustomQueue<T> queue;
 public QueueBackedList() {
 this.queue = new CustomQueue<>();
 }
 @Override
 public void add(T element) {
 queue.enqueue(element);
 }
 @Override
 public T get(int index) {
 checkIndex(index);
 int n = queue.size();
 T target = null;
 for (int i = 0; i < n; i++) {
 T current = queue.dequeue();
 if (i == index) {
 target = current;
 }
 queue.enqueue(current);
 }
 return target;
 }
 @Override
 public void set(int index, T element) {
 checkIndex(index);
 int n = queue.size();
 for (int i = 0; i < n; i++) {
 T current = queue.dequeue();
 queue.enqueue(i == index ? element : current);
 }
 }
 @Override
 public int size() {
 return queue.size();
 }
 @Override
 public void clear() {
 queue.clear();
 }
 @Override
 public int[] toIntArray() {
 int n = queue.size();
 int[] result = new int[n];
 for (int i = 0; i < n; i++) {
 T current = queue.dequeue();
 result[i] = current instanceof Number ? ((Number) current).intValue()
: 0;
 queue.enqueue(current);
 }
 return result;
 }
 private void checkIndex(int index) {
 if (index < 0 || index >= queue.size()) {
 throw new IndexOutOfBoundsException(
 "QueueBackedList index " + index + " out of bounds for size " +
queue.size()
 );
 }
 }
}

