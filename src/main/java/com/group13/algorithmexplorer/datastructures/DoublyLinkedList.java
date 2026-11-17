package com.group13.algorithmexplorer.datastructures;

/**
 * Module B(c)(ii) — Doubly Linked List implemented from first principles using custom
 * {@link DoublyNode} objects with explicit {@code next} and {@code prev} references.
 * <p>
 * Supports all required operations:
 * <ul>
 *   <li>Insertion: at the beginning (O(1)), at the end (O(1)), before a given element (O(n)), after a given element (O(n))</li>
 *   <li>Deletion: at the beginning (O(1)), at the end (O(1)), before a given element (O(n)), after a given element (O(n)), and deletion of a given element (O(n))</li>
 *   <li>Search for an element (O(n)), size (O(1)), forward display (O(n) via {@code next}), and backward display (O(n) via {@code prev})</li>
 * </ul>
 * </p>
 */
public class DoublyLinkedList {

    /** Custom doubly-linked node with explicit {@code prev} and {@code next} references. */
    public static final class DoublyNode {
        int data;
        DoublyNode prev;
        DoublyNode next;

        DoublyNode(int data) {
            this.data = data;
            this.prev = null;
            this.next = null;
        }
    }

    private DoublyNode head;
    private DoublyNode tail;
    private int size;

    public DoublyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /** Inserts {@code value} at the beginning (head) of the list in O(1) time. */
    public void insertAtBeginning(int value) {
        DoublyNode newNode = new DoublyNode(value);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        size++;
    }

    /** Inserts {@code value} at the end (tail) of the list in O(1) time. */
    public void insertAtEnd(int value) {
        DoublyNode newNode = new DoublyNode(value);
        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }

    /** Inserts {@code newValue} immediately before the first occurrence of {@code targetValue} in O(n) time. */
    public void insertBefore(int targetValue, int newValue) {
        if (head == null) {
            throw new IllegalStateException("List is empty. Cannot insert before " + targetValue + ".");
        }
        DoublyNode curr = findNode(targetValue);
        if (curr == null) {
            throw new IllegalArgumentException("Element " + targetValue + " not found in the doubly linked list.");
        }
        if (curr == head) {
            insertAtBeginning(newValue);
            return;
        }
        DoublyNode newNode = new DoublyNode(newValue);
        DoublyNode prevNode = curr.prev;
        newNode.prev = prevNode;
        newNode.next = curr;
        prevNode.next = newNode;
        curr.prev = newNode;
        size++;
    }

    /** Inserts {@code newValue} immediately after the first occurrence of {@code targetValue} in O(n) time. */
    public void insertAfter(int targetValue, int newValue) {
        if (head == null) {
            throw new IllegalStateException("List is empty. Cannot insert after " + targetValue + ".");
        }
        DoublyNode curr = findNode(targetValue);
        if (curr == null) {
            throw new IllegalArgumentException("Element " + targetValue + " not found in the doubly linked list.");
        }
        if (curr == tail) {
            insertAtEnd(newValue);
            return;
        }
        DoublyNode newNode = new DoublyNode(newValue);
        DoublyNode nextNode = curr.next;
        newNode.prev = curr;
        newNode.next = nextNode;
        curr.next = newNode;
        nextNode.prev = newNode;
        size++;
    }

    /** Deletes and returns the first element (head) in O(1) time. */
    public int deleteAtBeginning() {
        if (head == null) {
            throw new IllegalStateException("Underflow: Doubly linked list is empty. Cannot delete at beginning.");
        }
        int removed = head.data;
        if (head == tail) {
            head = null;
            tail = null;
        } else {
            DoublyNode next = head.next;
            next.prev = null;
            head.next = null;
            head = next;
        }
        size--;
        return removed;
    }

    /** Deletes and returns the last element (tail) in O(1) time. */
    public int deleteAtEnd() {
        if (tail == null) {
            throw new IllegalStateException("Underflow: Doubly linked list is empty. Cannot delete at end.");
        }
        int removed = tail.data;
        if (head == tail) {
            head = null;
            tail = null;
        } else {
            DoublyNode prev = tail.prev;
            prev.next = null;
            tail.prev = null;
            tail = prev;
        }
        size--;
        return removed;
    }

    /** Deletes and returns the element immediately before {@code targetValue} in O(n) time. */
    public int deleteBefore(int targetValue) {
        if (head == null) {
            throw new IllegalStateException("Underflow: Doubly linked list is empty.");
        }
        DoublyNode curr = findNode(targetValue);
        if (curr == null) {
            throw new IllegalArgumentException("Element " + targetValue + " not found in the doubly linked list.");
        }
        if (curr.prev == null) {
            throw new IllegalArgumentException(
                "Element " + targetValue + " is at the head of the list; there is no element before it to delete."
            );
        }
        DoublyNode targetDelete = curr.prev;
        if (targetDelete == head) {
            return deleteAtBeginning();
        }
        int removed = targetDelete.data;
        DoublyNode beforeDelete = targetDelete.prev;
        beforeDelete.next = curr;
        curr.prev = beforeDelete;
        targetDelete.prev = null;
        targetDelete.next = null;
        size--;
        return removed;
    }

    /** Deletes and returns the element immediately after {@code targetValue} in O(n) time. */
    public int deleteAfter(int targetValue) {
        if (head == null) {
            throw new IllegalStateException("Underflow: Doubly linked list is empty.");
        }
        DoublyNode curr = findNode(targetValue);
        if (curr == null) {
            throw new IllegalArgumentException("Element " + targetValue + " not found in the doubly linked list.");
        }
        if (curr.next == null) {
            throw new IllegalArgumentException(
                "Element " + targetValue + " is at the tail of the list; there is no element after it to delete."
            );
        }
        DoublyNode targetDelete = curr.next;
        if (targetDelete == tail) {
            return deleteAtEnd();
        }
        int removed = targetDelete.data;
        DoublyNode afterDelete = targetDelete.next;
        curr.next = afterDelete;
        afterDelete.prev = curr;
        targetDelete.prev = null;
        targetDelete.next = null;
        size--;
        return removed;
    }

    /** Deletes the first occurrence of {@code targetValue} in O(n) time. */
    public int deleteElement(int targetValue) {
        if (head == null) {
            throw new IllegalStateException("Underflow: Doubly linked list is empty.");
        }
        DoublyNode curr = findNode(targetValue);
        if (curr == null) {
            throw new IllegalArgumentException("Element " + targetValue + " not found in the doubly linked list.");
        }
        if (curr == head) {
            return deleteAtBeginning();
        }
        if (curr == tail) {
            return deleteAtEnd();
        }
        int removed = curr.data;
        curr.prev.next = curr.next;
        curr.next.prev = curr.prev;
        curr.prev = null;
        curr.next = null;
        size--;
        return removed;
    }

    /**
     * Searches for {@code targetValue} from {@code head} in O(n) time.
     *
     * @return 0-based index of {@code targetValue}, or {@code -1} if not found
     */
    public int search(int targetValue) {
        DoublyNode curr = head;
        int idx = 0;
        while (curr != null) {
            if (curr.data == targetValue) {
                return idx;
            }
            curr = curr.next;
            idx++;
        }
        return -1;
    }

    /** Returns the number of nodes in the doubly linked list in O(1) time. */
    public int size() {
        return size;
    }

    /** Returns all elements in forward order (head to tail via {@code next}) in O(n) time. */
    public int[] displayForward() {
        int[] result = new int[size];
        DoublyNode curr = head;
        int i = 0;
        while (curr != null && i < size) {
            result[i++] = curr.data;
            curr = curr.next;
        }
        return result;
    }

    /** Returns all elements in backward order (tail to head via {@code prev}) in O(n) time. */
    public int[] displayBackward() {
        int[] result = new int[size];
        DoublyNode curr = tail;
        int i = 0;
        while (curr != null && i < size) {
            result[i++] = curr.data;
            curr = curr.prev;
        }
        return result;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    private DoublyNode findNode(int targetValue) {
        DoublyNode curr = head;
        while (curr != null) {
            if (curr.data == targetValue) {
                return curr;
            }
            curr = curr.next;
        }
        return null;
    }
}
