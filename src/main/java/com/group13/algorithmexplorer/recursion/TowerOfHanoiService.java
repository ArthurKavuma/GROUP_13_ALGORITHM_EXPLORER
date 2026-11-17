package com.group13.algorithmexplorer.recursion;

import com.group13.algorithmexplorer.datastructures.CustomQueue;
import com.group13.algorithmexplorer.dto.RecursionDtos.HanoiResponseDto;
import com.group13.algorithmexplorer.dto.RecursionDtos.HanoiStepDto;

/**
 * Module A(c) — Recursive Tower of Hanoi solver and peg-state recorder.
 * <p>
 * Solves the puzzle for {@code n} disks (1 to 8) across user-selected source, destination,
 * and auxiliary pegs ({@code A}, {@code B}, {@code C}), recording every disk transfer and the
 * exact peg state after each move so the frontend can animate the disks and verify that
 * total moves equal {@code 2^n - 1}.
 * </p>
 * <ul>
 *   <li><b>Time Complexity:</b> O(2^n) — exactly {@code 2^n - 1} moves and {@code 2^(n+1) - 1} recursive calls</li>
 *   <li><b>Space Complexity:</b> O(n) recursion stack depth</li>
 * </ul>
 */
public class TowerOfHanoiService {

    public static final int MIN_DISKS = 1;
    public static final int MAX_DISKS = 8;

    /** Fixed-size array peg model representing disks from bottom (index 0) to top (index top-1). */
    private static final class PegState {
        final int[] pegA;
        final int[] pegB;
        final int[] pegC;
        int sizeA;
        int sizeB;
        int sizeC;

        PegState(int capacity) {
            this.pegA = new int[capacity];
            this.pegB = new int[capacity];
            this.pegC = new int[capacity];
            this.sizeA = 0;
            this.sizeB = 0;
            this.sizeC = 0;
        }

        void push(char peg, int disk) {
            if (peg == 'A') {
                pegA[sizeA++] = disk;
            } else if (peg == 'B') {
                pegB[sizeB++] = disk;
            } else {
                pegC[sizeC++] = disk;
            }
        }

        int pop(char peg) {
            if (peg == 'A') {
                return pegA[--sizeA];
            } else if (peg == 'B') {
                return pegB[--sizeB];
            } else {
                return pegC[--sizeC];
            }
        }

        int[] snapshot(char peg) {
            int[] src = peg == 'A' ? pegA : (peg == 'B' ? pegB : pegC);
            int len = peg == 'A' ? sizeA : (peg == 'B' ? sizeB : sizeC);
            int[] copy = new int[len];
            for (int i = 0; i < len; i++) {
                copy[i] = src[i];
            }
            return copy;
        }
    }

    /**
     * Validates inputs and solves the Tower of Hanoi puzzle recursively.
     *
     * @param disks          number of disks (1..8)
     * @param sourcePeg      source peg label ("A", "B", or "C")
     * @param destinationPeg destination peg label ("A", "B", or "C")
     * @param auxiliaryPeg   auxiliary peg label ("A", "B", or "C")
     * @return full move list, peg snapshots, timing, and confirmation that total moves = 2^n - 1
     */
    public HanoiResponseDto solve(int disks, String sourcePeg, String destinationPeg, String auxiliaryPeg) {
        if (disks < MIN_DISKS || disks > MAX_DISKS) {
            throw new IllegalArgumentException(
                "Number of disks must be between " + MIN_DISKS + " and " + MAX_DISKS + " (received " + disks + ")."
            );
        }

        char src = parsePeg(sourcePeg, "Source");
        char dest = parsePeg(destinationPeg, "Destination");
        char aux = parsePeg(auxiliaryPeg, "Auxiliary");

        if (src == dest || src == aux || dest == aux) {
            throw new IllegalArgumentException(
                "Source (" + src + "), Destination (" + dest + "), and Auxiliary (" + aux
                    + ") pegs must be three distinct pegs (A, B, and C)."
            );
        }

        PegState pegs = new PegState(disks);
        // Load disks n (largest, bottom) down to 1 (smallest, top) onto the chosen source peg
        for (int d = disks; d >= 1; d--) {
            pegs.push(src, d);
        }

        CustomQueue<HanoiStepDto> steps = new CustomQueue<>();
        // Initial state (Move 0)
        steps.enqueue(new HanoiStepDto(
            0,
            0,
            String.valueOf(src),
            String.valueOf(src),
            "Initial state: " + disks + " disk(s) stacked on Peg " + src,
            pegs.snapshot('A'),
            pegs.snapshot('B'),
            pegs.snapshot('C')
        ));

        int[] moveCounter = new int[] { 0 };
        long[] callCounter = new long[] { 0L };

        long startNs = System.nanoTime();
        solveRecursive(disks, src, dest, aux, pegs, steps, moveCounter, callCounter);
        long elapsedNs = System.nanoTime() - startNs;

        int totalMoves = moveCounter[0];
        int expectedMoves = (1 << disks) - 1;
        boolean matches = totalMoves == expectedMoves;
        String confirmation = "Confirmed: Total moves = " + totalMoves + " = 2^" + disks + " - 1 (" + expectedMoves + ")";

        int count = steps.size();
        HanoiStepDto[] stepArray = new HanoiStepDto[count];
        for (int i = 0; i < count; i++) {
            stepArray[i] = steps.dequeue();
        }

        return new HanoiResponseDto(
            disks,
            String.valueOf(src),
            String.valueOf(dest),
            String.valueOf(aux),
            totalMoves,
            expectedMoves,
            matches,
            confirmation,
            callCounter[0],
            elapsedNs,
            stepArray
        );
    }

    private void solveRecursive(
        int n,
        char from,
        char to,
        char aux,
        PegState pegs,
        CustomQueue<HanoiStepDto> steps,
        int[] moveCounter,
        long[] callCounter
    ) {
        callCounter[0]++;
        if (n == 0) {
            return;
        }
        solveRecursive(n - 1, from, aux, to, pegs, steps, moveCounter, callCounter);

        int movedDisk = pegs.pop(from);
        pegs.push(to, movedDisk);
        moveCounter[0]++;
        String desc = "Move disk " + movedDisk + " from " + from + " to " + to;
        steps.enqueue(new HanoiStepDto(
            moveCounter[0],
            movedDisk,
            String.valueOf(from),
            String.valueOf(to),
            desc,
            pegs.snapshot('A'),
            pegs.snapshot('B'),
            pegs.snapshot('C')
        ));

        solveRecursive(n - 1, aux, to, from, pegs, steps, moveCounter, callCounter);
    }

    private static char parsePeg(String raw, String role) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new IllegalArgumentException(role + " peg must be specified (A, B, or C).");
        }
        char c = Character.toUpperCase(raw.trim().charAt(0));
        if (c != 'A' && c != 'B' && c != 'C') {
            throw new IllegalArgumentException(role + " peg must be A, B, or C (received '" + raw + "').");
        }
        return c;
    }
}
