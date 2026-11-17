# Algorithm Explorer — CSC2107 Group 13

**Algorithm Explorer** is an interactive full-stack educational toolkit for **Recursion**, **Data Structures**, **Sorting Algorithms**, and **Time Complexity Analysis**, built from first principles without using standard `java.util` collection classes or built-in sort/search routines.

---

## Team Members (Group 13)

| Name |
| :--- |
| Tendo Kavuma Arthur C. |
| Keith Rowland |
| Joshua Odaga |
| Manzi Joel |
| Conrad Mugisha |

---

## Languages, Frameworks, and Tools

* **Java 17 & Spring Boot 3.2.5 (`spring-boot-starter-web`):** Backend engine implementing all recursive algorithms, custom data structures, sorting algorithms, step-tracking instrumentation, experimental benchmarking, and REST API endpoints.
* **Vanilla JavaScript (ES6+), HTML5, and CSS3:** Single-page interactive frontend with a 4-module navigation bar, animated Tower of Hanoi pegs/disks, boxes-and-arrows Data Structure canvas, Yellow/Green Quicksort partition & Merge Sort tree visualizer, and SVG complexity line charts.
* **JUnit 5 (`spring-boot-starter-test`) & Maven Wrapper (`mvnw`):** Automated unit and integration test suite.

---

## Implemented Modules (Matching CSC2107 Project Specification)

### Module A: Recursion Lab (`/api/recursion/*`)
1. **(a) Fibonacci:** Computes $F(n)$ in three ways:
   * **(i) Naive Recursion** ($O(2^n)$ time, capped at $n \le 30$ with on-screen explanation of overlapping subproblem explosion),
   * **(ii) Memoized Recursion** ($O(n)$ time using a fixed-size `long[]` lookup array),
   * **(iii) Bottom-Up Dynamic Programming** ($O(n)$ time using iterative table construction).
   Displays the result, function call / loop iteration counts, and execution time.
2. **(b) Factorial:** Computes $n!$ both **recursively** and **iteratively**, displaying the result, call/iteration counts, and time. Handles $n = 0$ ($0! = 1$) and rejects negative input ($n < 0$).
3. **(c) Tower of Hanoi (Animated):** Supports $1..8$ disks and user-selectable **Source**, **Auxiliary**, and **Destination** pegs (`A`, `B`, `C`). Solves the puzzle recursively, lists every move (`"Move disk 1 from A to C"`), animates the disks with **Start/Pause**, **Step Back/Forward**, **Reset**, and **Speed** controls, and confirms that total moves equal $2^n - 1$.

### Module B: Data Structures Lab (`/api/ds/*`)
1. **(a) Stack (Array-Based, Fixed Capacity Set by User) & Bracket Balance Application:**
   * Fixed-size array stack (`FixedArrayStack`) supporting `push`, `pop`, `peek`, `size`, `is_empty`, `is_full`, and `display` with explicit overflow and underflow messages.
   * **Bracket Balance Checker (`BracketBalanceChecker`):** Uses `FixedArrayStack` to check whether a bracket expression such as `{ ( [ ] ) }` is balanced, displaying the stack state after each symbol.
2. **(b) Queues (Fixed-Size Array):**
   * **(i) Linear Queue (`LinearArrayQueue`):** Supports `enqueue`, `dequeue`, `front`, `size`, `is_empty`, `is_full`, and `display`. Demonstrates the **"False Full" problem** when `rear == capacity - 1` while `front > 0` leaves wasted empty slots at the front.
   * **(ii) Circular Queue (`CircularArrayQueue`):** Supports the same operations and displays `front` and `rear` indices wrapping around to the start of the array via `(rear + 1) % capacity`.
3. **(c) Singly & Doubly Linked Lists (`SinglyLinkedList`, `DoublyLinkedList`):**
   * Built from custom `SinglyNode` (`next` only) and `DoublyNode` (`prev` and `next`) classes.
   * Supports insertion at the beginning, at the end, before a given element, and after a given element; deletion at the beginning, at the end, before a given element, after a given element, and deletion of a given element; search for an element, `size`, and display (**forward** for both lists, and **backward** via `prev` pointers for the doubly linked list).

### Module C: Sorting Lab (`/api/sort`, `/api/sort/compare-all`)
* Implements **Bubble Sort**, **Selection Sort**, **Insertion Sort**, **Merge Sort**, and **Quicksort** (both the **first-element pivot with "yellow/green" partition taught in class** and the **randomised-pivot** version).
* Shows the array after every pass or merge/partition step (with a toggle between pass-only steps and detailed comparison/swap steps), **Step** and **Auto-Play** controls, adjustable **Speed**, and color-coded highlights.
* Displays comparisons, swaps/moves, and execution time, plus a **"Compare All"** button that runs all sorting algorithms on the same input and presents the results side by side in one table.
* Displays each algorithm's best-, average-, and worst-case time complexity, extra space, and stability.

### Module D: Time Complexity Analysis (`/api/benchmark/run`)
* Times every sorting algorithm for $n \in \{100, 500, 1000, 2000, 5000, 10000\}$ (and $n \in \{50000, 100000\}$ for Merge Sort and Quicksort) across **Random**, **Already Sorted**, and **Reverse Sorted** inputs, repeating each run at least 3 times and reporting the average.
* Plots running time against $n$ (one chart for $O(n^2)$ sorts and one for $O(n \log n)$ sorts), presents full tables (with CSV export), and discusses agreement with theoretical Big-O complexities.

---

## How to Run the Application

### 1. Start the Spring Boot Server
On **Windows (PowerShell or Command Prompt)**:
```powershell
.\mvnw.cmd spring-boot:run
```
On **macOS / Linux**:
```bash
./mvnw spring-boot:run
```

### 2. Open in Your Browser
Navigate to:
```text
http://localhost:8080
```

### 3. Run the Automated JUnit 5 Test Suite
```powershell
.\mvnw.cmd test
```
