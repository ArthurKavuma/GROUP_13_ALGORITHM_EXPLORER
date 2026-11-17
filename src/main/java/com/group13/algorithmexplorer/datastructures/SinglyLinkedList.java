package com.group13.algorithmexplorer.datastructures;

/**
 * Module B(c)(i) — Singly Linked List implemented from first principles using custom
 * {@link SinglyNode} objects with explicit {@code next} references only.
 * <p>
 * Supports all required operations:
 * <ul>
 *   <li>Insertion: at the beginning (O(1)), at the end (O(n)), before a given element (O(n)), after a given element (O(n))</li>
 *   <li>Deletion: at the beginning (O(1)), at the end (O(n)), before a given element (O(n)), after a given element (O(n)), and deletion of a given element (O(n))</li>
 *   <li>Search for an element (O(n)), size (O(1)), and forward display (O(n))</li>
 * </ul>
 * </p>
 */
public class SinglyLinkedList {

    /** Custom singly-linked node with explicit {@code next} reference only. */
    public static final class SinglyNode {
        int data;
        SinglyNode next;

        SinglyNode(int data) {
            this.data = data;
            this.next = null;
        }
    }

    private SinglyNode head;
    private int size;

    public SinglyLinkedList() {
        this.head = null;
        this.size = 0;
    }

    /** Inserts {@code value} at the beginning (head) of the list in O(1) time. */
    public void insertAtBeginning(int value) {
        SinglyNode newNode = new SinglyNode(value);
        newNode.next = head;
        head = newNode;
        size++;
    }

    /** Inserts {@code value} at the end (tail) of the list in O(n) time. */
    public void insertAtEnd(int value) {
        SinglyNode newNode = new SinglyNode(value);
        if (head == null) {
            head = newNode;
        } else {
            SinglyNode curr = head;
            while (curr.next != null) {
                curr = curr.next;
            }
            curr.next = newNode;
        }
        size++;
    }

    /** Inserts {@code newValue} immediately before the first occurrence of {@code targetValue} in O(n) time. */
    public void insertBefore(int targetValue, int newValue) {
        if (head == null) {
            throw new IllegalStateException("List is empty. Cannot insert before " + targetValue + ".");
        }
        if (head.data == targetValue) {
            insertAtBeginning(newValue);
            return;
        }
        SinglyNode prev = head;
        SinglyNode curr = head.next;
        while (curr != null && curr.data != targetValue) {
            prev = curr;
            curr = curr.next;
        }
        if (curr == null) {
            throw new IllegalArgumentException("Element " + targetValue + " not found in the singly linked list.");
        }
        SinglyNode newNode = new SinglyNode(newValue);
        newNode.next = curr;
        prev.next = newNode;
        size++;
    }

    /** Inserts {@code newValue} immediately after the first occurrence of {@code targetValue} in O(n) time. */
    public void insertAfter(int targetValue, int newValue) {
        if (head == null) {
            throw new IllegalStateException("List is empty. Cannot insert after " + targetValue + ".");
        }
        SinglyNode curr = head;
        while (curr != null && curr.data != targetValue) {
            curr = curr.next;
        }
        if (curr == null) {
            throw new IllegalArgumentException("Element " + targetValue + " not found in the singly linked list.");
        }
        SinglyNode newNode = new SinglyNode(newValue);
        newNode.next = curr.next;
        curr.next = newNode;
        size++;
    }

    /** Deletes and returns the first element (head) in O(1) time. */
    public int deleteAtBeginning() {
        if (head == null) {
            throw new IllegalStateException("Underflow: Singly linked list is empty. Cannot delete at beginning.");
        }
        int removed = head.data;
        SinglyNode next = head.next;
        head.next = null;
        head = next;
        size--;
        return removed;
    }

    /** Deletes and returns the last element in O(n) time. */
    public int deleteAtEnd() {
        if (head == null) {
            throw new IllegalStateException("Underflow: Singly linked list is empty. Cannot delete at end.");
        }
        if (head.next == null) {
            int removed = head.data;
            head = null;
            size--;
            return removed;
        }
        SinglyNode prev = head;
        SinglyNode curr = head.next;
        while (curr.next != null) {
            prev = curr;
            curr = curr.next;
        }
        int removed = curr.data;
        prev.next = null;
        size--;
        return removed;
    }

    /** Deletes and returns the element immediately before {@code targetValue} in O(n) time. */
    public int deleteBefore(int targetValue) {
        if (head == null) {
            throw new IllegalStateException("Underflow: Singly linked list is empty.");
        }
        if (head.data == targetValue) {
            throw new IllegalArgumentException(
                "Element " + targetValue + " is at the head of the list; there is no element before it to delete."
            );
        }
        if (head.next != null && head.next.data == targetValue) {
            return deleteAtBeginning();
        }
        SinglyNode grandPrev = head;
        SinglyNode prev = head.next;
        SinglyNode curr = prev != null ? prev.next : null;
        while (curr != null && curr.data != targetValue) {
            grandPrev = prev;
            prev = curr;
            curr = curr.next;
        }
        if (curr == null) {
            throw new IllegalArgumentException("Element " + targetValue + " not found in the singly linked list.");
        }
        int removed = prev.data;
        grandPrev.next = curr;
        prev.next = null;
        size--;
        return removed;
    }

    /** Deletes and returns the element immediately after {@code targetValue} in O(n) time. */
    public int deleteAfter(int targetValue) {
        if (head == null) {
            throw new IllegalStateException("Underflow: Singly linked list is empty.");
        }
        SinglyNode curr = head;
        while (curr != null && curr.data != targetValue) {
            curr = curr.next;
        }
        if (curr == null) {
            throw new IllegalArgumentException("Element " + targetValue + " not found in the singly linked list.");
        }
        if (curr.next == null) {
            throw new IllegalArgumentException(
                "Element " + targetValue + " is the last node; there is no element after it to delete."
            );
        }
        SinglyNode targetDelete = curr.next;
        int removed = targetDelete.data;
        curr.next = targetDelete.next;
        targetDelete.next = null;
        size--;
        return removed;
    }

    /** Deletes the first occurrence of {@code targetValue} in O(n) time. */
    public int deleteElement(int targetValue) {
        if (head == null) {
            throw new IllegalStateException("Underflow: Singly linked list is empty.");
        }
        if (head.data == targetValue) {
            return deleteAtBeginning();
        }
        SinglyNode prev = head;
        SinglyNode curr = head.next;
        while (curr != null && curr.data != targetValue) {
            prev = curr;
            curr = curr.next;
        }
        if (curr == null) {
            throw new IllegalArgumentException("Element " + targetValue + " not found in the singly linked list.");
        }
        int removed = curr.data;
        prev.next = curr.next;
        curr.next = null;
        size--;
        return removed;
    }

    /**
     * Searches for {@code targetValue} by linear traversal from {@code head} in O(n) time.
     *
     * @return 0-based index of {@code targetValue}, or {@code -1} if not found
     */
    public int search(int targetValue) {
        SinglyNode curr = head;
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

    /** Returns the number of nodes in the singly linked list in O(1) time. */
    public int size() {
        return size;
    }

    /** Returns all elements in forward order (head to tail) in O(n) time. */
    public int[] displayForward() {
        int[] result = new int[size];
        SinglyNode curr = head;
        int i = 0;
        while (curr != null && i < size) {
            result[i++] = curr.data;
            curr = curr.next;
        }
        return result;
    }

    public void clear() {
        head = null;
        size = 0;
    }
}
